package com.dc.im.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dc.im.config.LoggerName;
import com.dc.im.pojo.Message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder extends MessageToByteEncoder<Message>{
    private static Logger LOG = LoggerFactory.getLogger(LoggerName.CONSOLE);
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        try {
            out.writeInt(Message.MAGIC_CODE);
            out.writeInt(msg.getHeader().getBytes().length);
            out.writeBytes(msg.getHeader().getBytes());
            if(msg.getBody()!=null) {
                out.writeInt(msg.getBody().length);
                out.writeBytes(msg.getBody());
            }else {
                out.writeInt(0);
            }
        }catch (Exception e) {
            LOG.error("encoder fail",e);
            throw e;
        }
    }
}
