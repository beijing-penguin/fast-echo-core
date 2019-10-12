package com.dc.echo.core;

import com.dc.echo.pojo.Message;

import io.netty.channel.ChannelHandlerContext;

public interface MessageListener {
    public void callback(ChannelHandlerContext ctx, Message message);
}
