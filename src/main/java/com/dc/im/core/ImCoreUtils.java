package com.dc.im.core;

import com.alibaba.fastjson.JSON;
import com.dc.im.config.TransCode;
import com.dc.im.pojo.Header;
import com.dc.im.pojo.Message;

public class ImCoreUtils {
    public static Message getMessByCode(Integer statusCode) {
        Message msg = new Message();
        Header header = new Header();
        header.setStatusCode(statusCode);
        msg.setHeader(JSON.toJSONString(header));
        return msg;
    }
    public static Message getKeepaliveMess() {
        Message msg = new Message();
        Header header = new Header();
        header.setMsgType(TransCode.HEARTBEAT_ACTION);//心跳
        msg.setHeader(JSON.toJSONString(header));
        return msg;
    }
    public static Message getErrorMess() {
        Message msg = new Message();
        Header header = new Header();
        header.setMsgType(TransCode.ERROR);//心跳
        msg.setHeader(JSON.toJSONString(header));
        return msg;
    }
}
