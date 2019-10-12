package com.dc.echo.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.dc.echo.config.EchoCode;
import com.dc.echo.pojo.Header;
import com.dc.echo.pojo.Message;

import io.netty.channel.ChannelHandlerContext;

public class LoginController {
	public static Map<String,ChannelHandlerContext> user_channel_map = new ConcurrentHashMap<String, ChannelHandlerContext>();
	
	public static void login(ChannelHandlerContext ctx, Message msg) {
		Header header = msg.getHeaderObj();
		//1.认证登录参数
        //2.登录成功
        synchronized (user_channel_map) {
            if(user_channel_map.containsKey(header.getSender())) {//注册id已存在
                header.setStatusCode(EchoCode.USER_EXIST);
                msg.setHeader(JSON.toJSONString(header));
                ctx.channel().writeAndFlush(msg);
                ctx.close();
            }else {
                user_channel_map.put(header.getSender(), ctx);
                header.setStatusCode(EchoCode.SUCCESS);
                msg.setHeader(JSON.toJSONString(header));
                ctx.channel().writeAndFlush(msg);
            }
        }
	}
}
