package com.dc.echo.core;

import io.netty.channel.ChannelHandlerContext;

public interface MessageListener {
    public void callback(ChannelHandlerContext ctx, byte[] dataByteArr);
}
