package com.superbeetle.weixinbase.util;

import com.superbeetle.weixinbase.model.to.WeixinJsapiConfigTO;
import com.superbeetle.weixinbase.model.to.WeixinJsapiTicketTO;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import rxframework.utility.cache.MCacheUtils;
import rxframework.utility.cache.MemcacheException;
import rxframework.utility.web.WebProxyUtils;
import rxplatform.system.cache.WebCache;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Formatter;
import java.util.UUID;

public class WeixinJsSdk {

//	private static MemCachedClient cachedClient = new MemCachedClient();

    /**
     * 获取 Jsapi的config
     *
     * @param accessToken
     * @param pageUrl     当前页面全路径
     * @return
     */
    public static WeixinJsapiConfigTO getJsapiConfig(String appId, String accessToken, String pageUrl,
                                                     Logger logger) {
        String jsapi_ticket = getJsapiTicket(appId, accessToken, logger);
        WeixinJsapiConfigTO result = sign(appId, jsapi_ticket, pageUrl);
        return result;
    }

    ;

    /**
     * 重设AccessToken
     */
    public static String reSetJsapiTicket(String appId, String accessToken) {
        String cacheKey = "wxcache" + appId;
        String key = cacheKey + ".JsapiTicket";
        String timeKey = cacheKey + ".JsapiTicketTime";
        Object timeValue = MCacheUtils.get(WebCache.getSystemParam("PLATFORM_NAMESPACE"), timeKey);
        //cachedClient.get(timeKey);
        Object jsapiTicketValue = MCacheUtils.get(WebCache.getSystemParam("PLATFORM_NAMESPACE"), key);
        //cachedClient.get(key);
        if (timeValue != null && jsapiTicketValue != null) {
            try {
                MCacheUtils.remove(WebCache.getSystemParam("PLATFORM_NAMESPACE"), timeKey);
                MCacheUtils.remove(WebCache.getSystemParam("PLATFORM_NAMESPACE"), key);
            } catch (MemcacheException e) {// 不能replace null，会设置失败，设置成"",get的时候 值就为null
                MCacheUtils.replace(WebCache.getSystemParam("PLATFORM_NAMESPACE"), timeKey, "");
                MCacheUtils.replace(WebCache.getSystemParam("PLATFORM_NAMESPACE"), key, "");
                e.printStackTrace();
            }
            //cachedClient.delete(timeKey);
            //cachedClient.delete(key);
        }

        return getJsapiTicket(appId, accessToken, null);
    }

    /**
     * 获取 jsapi_ticket
     *
     * @return
     */
    public static String getJsapiTicket(String appId, String accessToken, Logger logger) {
        String cacheKey = "wxcache" + appId;
        String key = cacheKey + ".JsapiTicket";
        String timeKey = cacheKey + ".JsapiTicketTime";
        Object timeValue = MCacheUtils.get(WebCache.getSystemParam("PLATFORM_NAMESPACE"), timeKey);
        Object jsapiTicketValue = MCacheUtils.get(WebCache.getSystemParam("PLATFORM_NAMESPACE"), key);
        //logger.error("timeValue:"+timeValue+" jsapiTicketValue:"+jsapiTicketValue);
        if (timeValue != null && jsapiTicketValue != null) {
            Long nowTime = (Long) timeValue;
            if (nowTime > (new Date().getTime() - 5400000l)) {// 5400000 = 1.5*60*60*1000 ，超过1.5小时更新一次
                return (String) jsapiTicketValue;
            }
        }

        String proxyUrl = String
                .format("https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=%s&type=jsapi",
                        accessToken);
        try {
            String json = WebProxyUtils.post(proxyUrl, null);

//			zjc.zsjswx.util.wx.WeixinJsapiTicketTO entity = new Gson().fromJson(json,
//					new TypeToken<zjc.zsjswx.util.wx.WeixinJsapiTicketTO>() {
//					}.getType());

            JSONObject tokenJsonObject = JSONObject.fromObject(json);
            WeixinJsapiTicketTO entity = (WeixinJsapiTicketTO) JSONObject.toBean(tokenJsonObject, WeixinJsapiTicketTO.class);

            //logger.error("json:"+json);
            if (entity != null && entity.getErrcode() == 0 && entity.getTicket() != null) {
                String JsapiTicket = entity.getTicket();
                //logger.error("new JsapiTicket:"+JsapiTicket);


                if (timeValue != null) {
                    MCacheUtils.replace(WebCache.getSystemParam("PLATFORM_NAMESPACE"),
                            timeKey, new Date().getTime());
                    //cachedClient.replace(timeKey, new Date().getTime());
                } else {
                    MCacheUtils.put(WebCache.getSystemParam("PLATFORM_NAMESPACE"),
                            timeKey, new Date().getTime());
                    //cachedClient.add(timeKey, new Date().getTime());
                }
                if (jsapiTicketValue != null) {
                    MCacheUtils.replace(WebCache.getSystemParam("PLATFORM_NAMESPACE"),
                            key, JsapiTicket);
                    //cachedClient.replace(key, JsapiTicket);
                } else {
                    MCacheUtils.put(WebCache.getSystemParam("PLATFORM_NAMESPACE"),
                            key, JsapiTicket);
                    //cachedClient.add(key, JsapiTicket);
                }

                return JsapiTicket;
            } else {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    private static WeixinJsapiConfigTO sign(String appId, String jsapi_ticket, String url) {
        WeixinJsapiConfigTO to = new WeixinJsapiConfigTO();
        String nonce_str = create_nonce_str();
        String timestamp = create_timestamp();
        String string1;
        String signature = "";

        //注意这里参数名必须全部小写，且必须有序，按照字母顺序
        string1 = "jsapi_ticket=" + jsapi_ticket +
                "&noncestr=" + nonce_str +
                "&timestamp=" + timestamp +
                "&url=" + url;
        System.out.println(string1);

        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        to.setAppId(appId);
        to.setUrl(url);
        to.setNonceStr(nonce_str);
        to.setTimestamp(timestamp);
        to.setSignature(signature);
        //to.setJsapiTicket(jsapi_ticket);

        return to;
    }

    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    private static String create_nonce_str() {
        return UUID.randomUUID().toString();
    }

    private static String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }
}
