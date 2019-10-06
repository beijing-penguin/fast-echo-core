package com.dc.im.core;

import com.dc.im.pojo.Message;

import io.netty.channel.ChannelHandlerContext;

public interface MessageListener {
    public void callback(ChannelHandlerContext ctx, Message message);
}
