package com.superbeetle.weixinbase.util;

import com.superbeetle.weixinbase.model.to.WeixinAccessTokenTO;
import com.superbeetle.weixinbase.model.to.WeixinTemplate;
import com.superbeetle.weixinbase.model.to.WeixinUserAutoGetTO;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import rxframework.utility.cache.MCacheUtils;
import rxframework.utility.cache.MemcacheException;
import rxframework.utility.web.WebProxyUtils;
import rxplatform.system.cache.WebCache;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 微信基础公共类
 *
 * @author Weddorn
 */
public class WeixinBase {

    private static final Logger logger = Logger.getLogger(WeixinBase.class);

    private static final String CacheBaseName = "wxcache";
    private static final String CacheBaseKeyName = "Accesstoken";
    private static final String CacheBaseKeyTimeName = "AccesstokenTime";

    //private static MemCachedClient cachedClient = new MemCachedClient();

    /**
     * 重设AccessToken
     */
    public static String reSetAccessToken(String appId, String secret) {
        String cacheKey = CacheBaseName + appId;
        String key = cacheKey + "." + CacheBaseKeyName;
        String timeKey = cacheKey + "." + CacheBaseKeyTimeName;
        //Object timeValue = cachedClient.get(timeKey);
        Object timeValue = MCacheUtils.get(WebCache.getSystemParam("PLATFORM_NAMESPACE"), timeKey);
        //Object acceccTokenValue = cachedClient.get(key);
        Object acceccTokenValue = MCacheUtils.get(WebCache.getSystemParam("PLATFORM_NAMESPACE"), key);
        if (timeValue != null || acceccTokenValue != null) {
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
            //cachedClient.replace(timeKey, "");// 不能replace null，会设置失败，设置成"",get的时候 值就为null
            //cachedClient.replace(key, "");
        }
        return getAccessToken(appId, secret);
    }

    /**
     * 获取AccessToken，这里需要判断是否过期（2小时有效，获取新的旧的就会失效）
     *
     * @return
     */
    public static String getAccessToken(String appId, String secret) {
        String cacheKey = CacheBaseName + appId;
        String key = cacheKey + "." + CacheBaseKeyName;
        String timeKey = cacheKey + "." + CacheBaseKeyTimeName;
        Object timeValue = MCacheUtils.get(WebCache.getSystemParam("PLATFORM_NAMESPACE"), timeKey);
        Object acceccTokenValue = MCacheUtils.get(WebCache.getSystemParam("PLATFORM_NAMESPACE"), key);
        //logger.error("timeValue:"+timeValue+" acceccTokenValue:"+acceccTokenValue);
        if (timeValue != null && acceccTokenValue != null) {
            Long nowTime = (Long) timeValue;
            if (nowTime > (new Date().getTime() - 5400000l)) {// 5400000 = 1.5*60*60*1000 ，超过1.5小时更新一次
                return (String) acceccTokenValue;
            }
        }

        String proxyUrl = String
                .format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s",
                        appId, secret);
        try {
            String json = WebProxyUtils.post(proxyUrl, null);

//			WeixinAccessTokenTO tokenEntity = new Gson().fromJson(json,
//					new TypeToken<WeixinAccessTokenTO>() {
//					}.getType());
            JSONObject tokenJsonObject = JSONObject.fromObject(json);
            WeixinAccessTokenTO tokenEntity = (WeixinAccessTokenTO) JSONObject.toBean(tokenJsonObject, WeixinAccessTokenTO.class);

            //logger.error("json1:"+json);
            if (tokenEntity != null && tokenEntity.getErrcode() == 0 && tokenEntity.getAccess_token() != null) {
                String AccessToken = tokenEntity.getAccess_token();
                //logger.error("AccessToken:"+AccessToken);
                if (timeValue != null) {
                    MCacheUtils.replace(WebCache.getSystemParam("PLATFORM_NAMESPACE"),
                            timeKey, new Date().getTime());
                    //cachedClient.replace(timeKey, new Date().getTime());
                } else {
                    MCacheUtils.put(WebCache.getSystemParam("PLATFORM_NAMESPACE"),
                            timeKey, new Date().getTime());
                    //cachedClient.add(timeKey, new Date().getTime());
                }
                if (acceccTokenValue != null) {
                    MCacheUtils.replace(WebCache.getSystemParam("PLATFORM_NAMESPACE"),
                            key, AccessToken);
                    //cachedClient.replace(key, AccessToken);
                } else {
                    MCacheUtils.put(WebCache.getSystemParam("PLATFORM_NAMESPACE"),
                            key, AccessToken);
                    //cachedClient.add(key, AccessToken);
                }

                return AccessToken;
            } else {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 通过code，自动获取用户openid
     *
     * @param code
     */
    public static String getOpenidByCode(String code, String appId, String secret) {
        String proxyUrl = String
                .format("https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code",
                        appId, secret, code);
        try {
            String json = WebProxyUtils.post(proxyUrl, null);

//			WeixinUserAutoGetTO tokenEntity = new Gson().fromJson(json,
//					new TypeToken<WeixinUserAutoGetTO>() {
//					}.getType());

            JSONObject tokenJsonObject = JSONObject.fromObject(json);
            WeixinUserAutoGetTO tokenEntity = (WeixinUserAutoGetTO) JSONObject.toBean(tokenJsonObject, WeixinAccessTokenTO.class);

            if (tokenEntity != null && tokenEntity.getErrcode() == 0 && tokenEntity.getAccess_token() != null) {
                return tokenEntity.getOpenid();
            } else {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }


    /**
     * 发送模板消息<br/>
     * 用户需要关注公众号<br/>
     * 文档地址：https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Template_Message_Interface.html<br/>
     *
     * @param weixinTemplate 模板内容
     * @return 状态码，详情见https://mp.weixin.qq.com/advanced/tmplmsg?action=faq&token=1345576893&lang=zh_CN
     */
    public static int sendTemplateMessage(String appId, String secret, WeixinTemplate weixinTemplate) {
        int result = 0;
        String token = getAccessToken(appId, secret);
        String sendUrl = String.format("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s", token);
        String json = JSONObject.fromObject(weixinTemplate).toString();
        try {
            String resultJson = WebProxyUtils.post(sendUrl, json);
            System.out.println(resultJson);
        } catch (IOException e) {
            logger.error(e);
            return -2;
        }
        return result;
    }

    /**
     * 一次性订阅消息<br/>
     * h5版，一次授权只接收一次消息，鸡肋功能
     * @return
     */
    public static int sendSubscribeMessage(String appId, String secret) {
        String token = getAccessToken(appId, secret);
        String sendUrl = String.format("https://api.weixin.qq.com/cgi-bin/message/template/subscribe?access_token=%s", token);

        Map<String, Object> map = new HashMap<>();
        map.put("touser", "oC9pxszK2QGhu2RiFpMplu9gdQCA");
        map.put("template_id", "5okuo91zXF43tjqzhRKfGvnzzAnSnRhWBeYNTuYLVqo");
        map.put("url", "http://aqjy.jiuwoding.com/");
        map.put("scene", 1000);
        map.put("title", "您有一个任务未处理");

        Map<String, Object> data = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        content.put("value", "未处理任务22");
        content.put("color", "#929232");
        data.put("content", content);

        map.put("data", data);

        String json = JSONObject.fromObject(map).toString();

        try {
            String resultJson = WebProxyUtils.post(sendUrl, json);
            System.out.println(resultJson);
        } catch (IOException e) {
            logger.error(e);
            return -2;
        }
        return 1;
    }
}
