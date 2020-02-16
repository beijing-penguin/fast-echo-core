package com.dc.echo.core;

import com.dc.echo.config.MsgCode;
import com.dc.echo.pojo.Message;

import io.netty.channel.ChannelHandlerContext;

public class InterceptorController {
	public static boolean interceptor(ChannelHandlerContext ctx, Message msg) {
		if(msg.getMsgCode()==MsgCode.HEARTBEAT_ACTION) {
			return true;
		}
//		if(AppKeyConfig.appKeySet.contains(msg.getHeaderObj().getAppKey())) {
//			return true;
//		}else {
//			ctx.channel().writeAndFlush(EchoCoreUtils.getMessByCode(EchoCode.UNREG_APPKEY));
//			ctx.channel().close();
//			return false;
//		}
		return true;
	}
}
