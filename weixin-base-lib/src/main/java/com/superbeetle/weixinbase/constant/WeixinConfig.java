package com.superbeetle.weixinbase.constant;

import rxframework.utility.other.PropertiesUtils;

public class WeixinConfig {
	// 微信APPID
	public static final String WEIXIN_APPID = PropertiesUtils.getAsString("sysConfig","weixin.appid");
	// 微信SECRET,appsecret
	public static final String WEIXIN_SECRET = PropertiesUtils.getAsString("sysConfig","weixin.secret");
	// 微信APPKEY
	public static final String WEIXIN_APPKEY = "";
	// 微信MCHID
	public static final String WEIXIN_MCHID = "";
}
