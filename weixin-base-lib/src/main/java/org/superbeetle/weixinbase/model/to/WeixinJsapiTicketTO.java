package org.superbeetle.weixinbase.model.to;

/**
 * 获取jsapi 中的  ticket返回的实体
 * @author Weddorn
 *
 */
public class WeixinJsapiTicketTO extends WeixinBackTO {
	// token
	private String ticket;
	// 时间毫秒
	private int expires_in;
	
	public String getTicket() {
		return ticket;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	public int getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(int expires_in) {
		this.expires_in = expires_in;
	}
}
