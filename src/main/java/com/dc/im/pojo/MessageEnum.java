package com.dc.im.pojo;

public enum MessageEnum {
    SUCCESS(1,"成功"),USER_LOGIN(2,"用户登录"), MESSAGE_IM(3,"消息转发"),HEARTBEAT(120,"心跳"),USER_EXIST(501, "用户已存在");
    
    private int msgType;
    private String msgInfo;
    
    private MessageEnum( int msgType,String msgInfo) {  
        this.msgType = msgType;  
        this.msgInfo = msgInfo;  
    }
    public static String getMsgInfo(int msgType) {  
        for (MessageEnum c : MessageEnum.values()) {  
            if (c.getMsgType() == msgType) {  
                return c.msgInfo;  
            }  
        }  
        return null;  
    }
    
    public int getMsgType() {
        return msgType;
    }
    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }
    public String getMsgInfo() {
        return msgInfo;
    }
    public void setMsgInfo(String msgInfo) {
        this.msgInfo = msgInfo;
    }
    
}
