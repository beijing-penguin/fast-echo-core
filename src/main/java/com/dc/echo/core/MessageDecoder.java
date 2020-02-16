package com.dc.echo.core;

import java.util.List;

import com.dc.echo.pojo.Message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class MessageDecoder extends ByteToMessageDecoder{
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in,  List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {//读取魔数,这里的8=4个int魔数+4个int的header_len
            return;
        }
        in.markReaderIndex();                  //标记一下当前的readIndex的位置

        int magic_code = in.readInt();
        if (magic_code != Message.MAGIC_CODE) {
            in.resetReaderIndex();
            return;
        }
        int dataByteArrLen = in.readInt();
        if (in.readableBytes() < (dataByteArrLen)) { //读到的消息体长度如果小于我们传送过来的消息长度，则resetReaderIndex. 这个配合markReaderIndex使用的。把readIndex重置到mark的地方
            in.resetReaderIndex();
            return;
        }
        byte[] dataByteArr = new byte[dataByteArrLen];
        in.readBytes(dataByteArr);
        System.err.println(new String(dataByteArr));
        out.add(dataByteArr);
    }
}
