package com.dc.im.pojo;

public class Message {
    public static final int MAGIC_CODE = 0x1314;
    private Header headerObj;
    private String header;
	private byte[] body;
	
    public Header getHeaderObj() {
		return headerObj;
	}
	public void setHeaderObj(Header headerObj) {
		this.headerObj = headerObj;
	}
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
}
