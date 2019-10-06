package com.dc.im.pojo;

public class Message {
    public static final int MAGIC_CODE = 0x1314;
    
    private String msgId;
    private String header;
	private byte[] body;
	
	/**
	 * 服务端接收时间
	 */
	private long serverRevTime;
	
	
	
    public byte[] getBody() {
        return body;
    }
    public void setBody(byte[] body) {
        this.body = body;
    }
    public String getHeader() {
        return header;
    }
    public void setHeader(String header) {
        this.header = header;
    }
    public long getServerRevTime() {
        return serverRevTime;
    }
    public void setServerRevTime(long serverRevTime) {
        this.serverRevTime = serverRevTime;
    }
    public String getMsgId() {
        return msgId;
    }
    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
