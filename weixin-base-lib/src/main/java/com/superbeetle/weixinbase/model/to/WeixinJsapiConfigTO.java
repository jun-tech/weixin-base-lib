package com.superbeetle.weixinbase.model.to;

/**
 * jssdk 使用时候的配置
 * @author Weddorn
 *
 */
public class WeixinJsapiConfigTO extends WeixinBackTO {
	// 必填，公众号的唯一标识
	private String appId;
	// 必填，生成签名的时间戳
	private String timestamp;
	// 必填，生成签名的随机串
	private String nonceStr;
	// 必填，签名，见附录1
	private String signature;
	// 当前页面url
	private String url;
	
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getNonceStr() {
		return nonceStr;
	}
	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
