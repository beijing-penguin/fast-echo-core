package com.dc.echo.pojo;


public class Message {
    public static final int MAGIC_CODE = 0x1314;
    
    private String msgId;
    private String info;
    private Integer msgCode;
    private String sender;
    private String[] receiver;
    private String encoding;
    private Long sendTime;
    
    //
    private String version;
    
    //
    /**
     * 服务端接收时间
     */
    private Long serverRevTime;
    
    
    /**
     * 应用秘钥
     */
    private String appKey;
    
	private String content;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }


    public Integer getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(Integer msgCode) {
        this.msgCode = msgCode;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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

    public Long getServerRevTime() {
        return serverRevTime;
    }

    public void setServerRevTime(Long serverRevTime) {
        this.serverRevTime = serverRevTime;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
	
	
}
