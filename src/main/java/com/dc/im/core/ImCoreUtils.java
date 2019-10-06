package com.dc.im.core;

import com.alibaba.fastjson.JSON;
import com.dc.im.pojo.Header;
import com.dc.im.pojo.Message;
import com.dc.im.pojo.MsgType;

public class ImCoreUtils {
    public static Message getSuccMess(int msgType) {
        Message msg = new Message();
        Header header = new Header();
        header.setMsgType(msgType);
        msg.setHeader(JSON.toJSONString(header));
        return msg;
    }
    public static Message getKeepaliveMess() {
        Message msg = new Message();
        Header header = new Header();
        header.setMsgType(MsgType.HEARTBEAT);//心跳
        msg.setHeader(JSON.toJSONString(header));
        return msg;
    }
}
