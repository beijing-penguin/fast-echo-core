package com.dc.echo.core;

import com.dc.echo.config.AppKeyConfig;
import com.dc.echo.config.EchoCode;
import com.dc.echo.pojo.Message;
import com.dc.echo.utils.EchoCoreUtils;

import io.netty.channel.ChannelHandlerContext;

public class InterceptorController {
	public static boolean interceptor(ChannelHandlerContext ctx, Message msg) {
		if(msg.getHeaderObj().getMsgType()==EchoCode.HEARTBEAT_ACTION) {
			return true;
		}
		if(AppKeyConfig.appKeySet.contains(msg.getHeaderObj().getAppKey())) {
			return true;
		}else {
			ctx.channel().writeAndFlush(EchoCoreUtils.getMessByCode(EchoCode.UNREG_APPKEY));
			ctx.channel().close();
			return false;
		}
	}
}
