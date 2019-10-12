package com.dc.im.core;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.dc.echo.core.EchoConnection;
import com.dc.echo.core.MessageListener;
import com.dc.echo.pojo.Message;

import io.netty.channel.ChannelHandlerContext;

public class NettyTest {
    public static void main(String[] args) throws Throwable {
        EchoConnection conn = new EchoConnection("localhost",8000).connect().setListener(new MessageListener() {
            @Override
            public void callback(ChannelHandlerContext ctx, Message message) {
                System.err.println(JSON.toJSONString(message));
            }
        });
        Message message = new Message();
        Map<String, Object> headerMap = new HashMap<String, Object>();
        headerMap.put("v", "1.0");
        headerMap.put("type", "1");//登录
        headerMap.put("receiver", "dc");
        headerMap.put("sender", "dc");
        message.setHeader(JSON.toJSONString(headerMap));
        conn.sendMessage(message);
        //send(conn);
    }
   static void send(EchoConnection conn) throws Throwable {
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            Message message = new Message();
            Map<String, Object> headerMap = new HashMap<String, Object>();
            headerMap.put("v", "1.0");
            headerMap.put("type", "2");
            headerMap.put("receiver", "dc");
            message.setHeader("1");
            //message.setBody(new byte[] {(byte)2});
            conn.sendMessage(message,new MessageListener() {
                @Override
                public void callback(ChannelHandlerContext ctx, Message message) {
                    System.err.println(JSON.toJSONString(message));
                }
            });
        }
        System.out.println("NettyTest执行耗时: " + (System.currentTimeMillis() - begin));
    }
}
