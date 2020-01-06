package com.superbeetle.weixinbase.util;

import com.superbeetle.weixinbase.model.to.WeixinAccessTokenTO;
import com.superbeetle.weixinbase.model.to.WeixinUserAutoGetTO;
import net.sf.json.JSONObject;
import rxframework.utility.cache.MCacheUtils;
import rxframework.utility.cache.MemcacheException;
import rxframework.utility.web.WebProxyUtils;
import rxplatform.system.cache.WebCache;

import java.io.IOException;
import java.util.Date;


/**
 * 微信基础公共类
 *
 * @author Weddorn
 */
public class WeixinBase {
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
        return getAccessToken(appId, secret, null);
    }

    /**
     * 获取AccessToken，这里需要判断是否过期（2小时有效，获取新的旧的就会失效）
     *
     * @return
     */
    public static String getAccessToken(String appId, String secret,
                                        org.apache.log4j.Logger logger) {
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
}
