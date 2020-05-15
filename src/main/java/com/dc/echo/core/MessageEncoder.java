package com.dc.echo.core;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dc.echo.config.LoggerName;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder extends MessageToByteEncoder<byte[]>{
    private static Logger LOG = LoggerFactory.getLogger(LoggerName.CONSOLE);
    @Override
    protected void encode(ChannelHandlerContext ctx, byte[] dataByteArr, ByteBuf out) throws Exception {
//        try {
//            out.writeInt(Message.MAGIC_CODE);
//            out.writeInt(dataByteArr.length);
//            out.writeBytes(dataByteArr);
//        }catch (Exception e) {
//            LOG.error("encoder fail",e);
//            throw e;
//        }
    	try {
    		out.writeInt(dataByteArr.length);
			out.writeBytes(dataByteArr);
        } catch (Exception e) {
        	LOG.error("encoder fail",e);
        	throw e;
        }
    }
}
