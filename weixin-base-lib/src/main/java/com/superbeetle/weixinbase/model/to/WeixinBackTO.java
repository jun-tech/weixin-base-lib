package com.superbeetle.weixinbase.model.to;

/**
 * 调用微信接口之后，返回的对象
 * @author Weddorn
 *
 */
public class WeixinBackTO {
	// 错误代码
	private int errcode;
	// 错误信息
	private String errmsg;
	
	public int getErrcode() {
		return errcode;
	}
	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}
	public String getErrmsg() {
		return errmsg;
	}
	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
}
