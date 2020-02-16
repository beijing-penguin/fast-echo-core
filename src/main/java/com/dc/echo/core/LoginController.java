package com.dc.echo.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dc.echo.config.MsgCode;
import com.dc.echo.pojo.Message;
import com.dc.echo.utils.EchoCoreUtils;

import io.netty.channel.ChannelHandlerContext;

public class LoginController {
	public static Map<String,ChannelHandlerContext> user_channel_map = new ConcurrentHashMap<String, ChannelHandlerContext>();
	
	public static void login(ChannelHandlerContext ctx, Message msg) {
		//1.认证登录参数
        //2.登录成功
        synchronized (msg.getSender()) {
            if(user_channel_map.containsKey(msg.getSender())) {//注册id已存在
                msg.setMsgCode(MsgCode.USER_EXIST);
                ctx.channel().writeAndFlush(EchoCoreUtils.messageToByteArr(msg));
            }else {
                user_channel_map.put(msg.getSender(), ctx);
                msg.setMsgCode(MsgCode.SUCCESS);
                ctx.channel().writeAndFlush(EchoCoreUtils.messageToByteArr(msg));
            }
        }
	}
}
