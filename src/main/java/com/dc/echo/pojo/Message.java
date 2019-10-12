package com.dc.echo.pojo;

import com.alibaba.fastjson.annotation.JSONField;

public class Message {
    public static final int MAGIC_CODE = 0x1314;
    
    @JSONField(serialize=false)
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
