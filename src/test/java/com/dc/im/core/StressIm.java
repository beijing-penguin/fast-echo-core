package com.dc.im.core;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.dc.im.pojo.Message;
import com.dc.im.pojo.MsgType;

import io.netty.channel.ChannelHandlerContext;

public class StressIm {
    public static long begin;
    public static void main(String[] args) throws Throwable {
        NettyConnection conn = new NettyConnection("47.104.77.145",8000).setSync(false).connect().setListener(new MessageListener() {
            @Override
            public void callback(ChannelHandlerContext ctx, Message message) {
                System.err.println((System.currentTimeMillis()-begin) +"=="+JSON.toJSONString(message));
            }
        });
        
        Message message = new Message();
        Map<String, Object> headerMap = new HashMap<String, Object>();
        headerMap.put("v", "1.0");
        headerMap.put("msgType", MsgType.USER_LOGIN);// 登录
        headerMap.put("sender", "dc");
        message.setHeader(JSON.toJSONString(headerMap));
        conn.sendMessage(message);//登录
        
        begin = System.currentTimeMillis();
        for (int i = 0; i < 30000; i++) {
            Message message2 = new Message();
            Map<String, Object> headerMap2 = new HashMap<String, Object>();
            headerMap2.put("v", "1.0");
            headerMap2.put("msgType", MsgType.MESSAGE_IM);//发送消息
            headerMap2.put("receiver", new String[] {"dc"});
            headerMap2.put("sender", "dc");
            
            message2.setHeader(JSON.toJSONString(headerMap2));
            message2.setBody("Test".getBytes());
            conn.sendMessage(message2);//发送消息
        }
    }
}
