package com.dc.im.pojo;

public class Header {
	
	private String msgId;
    private Integer statusCode;
    private String info;
    private Integer msgType;
    private String sender;
    private String[] receiver;
    private String encoding;
    private Long sendTime;
    
    /**
	 * 服务端接收时间
	 */
	private Long serverRevTime;
	
	

	
	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public Integer getMsgType() {
		return msgType;
	}

	public void setMsgType(Integer msgType) {
		this.msgType = msgType;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String[] getReceiver() {
		return receiver;
	}

	public void setReceiver(String[] receiver) {
		this.receiver = receiver;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public Long getSendTime() {
		return sendTime;
	}

	public void setSendTime(Long sendTime) {
		this.sendTime = sendTime;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public Long getServerRevTime() {
		return serverRevTime;
	}

	public void setServerRevTime(Long serverRevTime) {
		this.serverRevTime = serverRevTime;
	}
}
