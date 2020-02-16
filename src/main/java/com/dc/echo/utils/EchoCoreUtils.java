package com.dc.echo.utils;

import com.alibaba.fastjson.JSON;
import com.dc.echo.config.MsgCode;
import com.dc.echo.pojo.Message;

public class EchoCoreUtils {
    public static byte[] getMessByCode(Integer msgType) {
        Message msg = new Message();
        msg.setMsgCode(msgType);
        return JSON.toJSONString(msg).getBytes();
    }
    public static byte[] getKeepaliveMess() {
        Message msg = new Message();
        msg.setMsgCode(MsgCode.HEARTBEAT_ACTION);
        return JSON.toJSONString(msg).getBytes();
    }
    public static Message getErrorMess() {
        Message msg = new Message();
        msg.setMsgCode(MsgCode.ERROR);
        return msg;
    }
    public static Message byteToMessage(byte[] dataByteArr) {
        return JSON.parseObject(new String(dataByteArr), Message.class);
    }
    public static byte[] messageToByteArr(Message msg) {
        return JSON.toJSONString(msg).getBytes();
    }
}
