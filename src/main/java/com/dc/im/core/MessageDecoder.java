package com.dc.im.core;

import java.util.List;

import com.dc.im.pojo.Message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class MessageDecoder extends ByteToMessageDecoder{
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in,  List<Object> out) throws Exception {
        if (in.readableBytes() < 8) {//读取魔数,这里的8=4个int魔数+4个int的header_len
            return;
        }
        in.markReaderIndex();                  //标记一下当前的readIndex的位置

        int magic_code = in.readInt();
        if (magic_code != Message.MAGIC_CODE) {
            in.resetReaderIndex();
            return;
        }
        int header_len = in.readInt();
        if (in.readableBytes() < (header_len+4)) { //读到的消息体长度如果小于我们传送过来的消息长度，则resetReaderIndex. 这个配合markReaderIndex使用的。把readIndex重置到mark的地方
            in.resetReaderIndex();
            return;
        }
        byte[] header = new byte[header_len];
        in.readBytes(header);
        int dataLength = in.readInt();//in位置已更新，接着读

        Message protocol = new Message();
        if(dataLength!=0) {
            if (in.readableBytes() < dataLength){ //读到的消息体长度如果小于我们传送过来的消息长度，则resetReaderIndex. 这个配合markReaderIndex使用的。把readIndex重置到mark的地方
                in.resetReaderIndex();
                return;
            }
            byte[] body = new byte[dataLength];
            in.readBytes(body);  //
            protocol.setBody(body);
        }
        protocol.setHeader(new String(header));
        out.add(protocol);
    }
}
