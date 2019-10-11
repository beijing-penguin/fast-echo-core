package com.dc.im.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.dc.im.pojo.Header;
import com.dc.im.pojo.Message;
import com.dc.im.pojo.MessageEnum;

import io.netty.channel.ChannelHandlerContext;

public class LoginController {
	private static Map<String,ChannelHandlerContext> user_channel_map = new ConcurrentHashMap<String, ChannelHandlerContext>();
	
	public void login(ChannelHandlerContext ctx, Message msg) {
		Header header = msg.getHeaderObj();
		//1.认证登录参数
        //2.登录成功
        synchronized (user_channel_map) {
            if(user_channel_map.containsKey(header.getSender())) {//注册id已存在
            	header.setStatusCode(200);
                header.setMsgType(MessageEnum.USER_EXIST.getMsgType());
                msg.setHeader(JSON.toJSONString(header));
                ctx.channel().writeAndFlush(msg);
                ctx.close();
            }else {
                user_channel_map.put(header.getSender(), ctx);
                msg.setHeader(JSON.toJSONString(header));
                msg.setBody(new String("登录成功").getBytes());
                ctx.channel().writeAndFlush(msg);
            }
        }
	}
}
