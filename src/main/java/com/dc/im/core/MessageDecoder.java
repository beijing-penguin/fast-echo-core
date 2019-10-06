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



        //        // 可读长度必须大于基本长度  
        //        if (buffer.readableBytes() >= BASE_LENGTH) {
        //            // 防止socket字节流攻击  
        //            // 防止，客户端传来的数据过大  
        //            // 因为，太大的数据，是不合理的  
        //            if (buffer.readableBytes() > 2048) {  
        //                buffer.skipBytes(buffer.readableBytes());  
        //            }
        //
        //            // 记录包头开始的index  
        //            int beginReader;  
        //
        //            while (true) {  
        //                // 获取包头开始的index  
        //                beginReader = buffer.readerIndex();  
        //                // 标记包头开始的index  
        //                buffer.markReaderIndex();
        //                // 读到了协议的开始标志，结束while循环  
        //                if (buffer.readInt() == MessageProtocol.MAGIC_CODE) {  
        //                    break;
        //                }
        //                // 未读到包头，略过一个字节  
        //                // 每次略过，一个字节，去读取，包头信息的开始标记  
        //                buffer.resetReaderIndex();
        //                buffer.readByte();
        //
        //                // 当略过，一个字节之后，  
        //                // 数据包的长度，又变得不满足  
        //                // 此时，应该结束。等待后面的数据到达
        //                if (buffer.readableBytes() < BASE_LENGTH) {  
        //                    return;  
        //                }  
        //            }
        //
        //            // 消息的长度  
        //            int length = buffer.readInt();  
        //            // 判断请求数据包数据是否到齐  
        //            if (buffer.readableBytes() < length) {  
        //                // 还原读指针
        //                buffer.readerIndex(beginReader);
        //                return;  
        //            }  
        //
        //            // 读取data数据  
        //            byte[] data = new byte[length];  
        //            buffer.readBytes(data);  
        //
        //            MessageProtocol protocol = new MessageProtocol();
        //            protocol.setBody(data);
        //            System.err.println(new String(protocol.getBody()));
        //            out.add(protocol);  
    }
}
