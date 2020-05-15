package com.dc.echo.core;

import java.nio.ByteBuffer;
import java.util.List;

import com.dc.echo.pojo.Message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class MessageDecoder extends LengthFieldBasedFrameDecoder{

	public MessageDecoder() {
		super(16777216, 0, 4, 0, 4);
	}

	//    @Override
	//    protected Object decode(ChannelHandlerContext ctx, ByteBuf in,  List<Object> out) throws Exception {
	//        if (in.readableBytes() < 4) {//读取魔数,这里的8=4个int魔数+4个int的header_len
	//            return;
	//        }
	//        in.markReaderIndex();                  //标记一下当前的readIndex的位置
	//
	//        int magic_code = in.readInt();
	//        if (magic_code != Message.MAGIC_CODE) {
	//            in.resetReaderIndex();
	//            return;
	//        }
	//        int dataByteArrLen = in.readInt();
	//        if (in.readableBytes() < (dataByteArrLen)) { //读到的消息体长度如果小于我们传送过来的消息长度，则resetReaderIndex. 这个配合markReaderIndex使用的。把readIndex重置到mark的地方
	//            in.resetReaderIndex();
	//            return;
	//        }
	//        byte[] dataByteArr = new byte[dataByteArrLen];
	//        in.readBytes(dataByteArr);
	//        out.add(dataByteArr);
	//    }


	@Override
	public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		ByteBuf frame = null;
		frame = (ByteBuf) super.decode(ctx, in);
		if (null == frame) {
			return null;
		}
		ByteBuffer byteBuffer = frame.nioBuffer();
		//		int length = byteBuffer.limit();
		//		int oriHeaderLen = byteBuffer.getInt();
		//		int headerLength = oriHeaderLen & 0xFFFFFF;
		//		
		//		byte[] headerData = new byte[headerLength];
		//        byteBuffer.get(headerData);
		//        

		//int length = byteBuffer.limit();

		int length = byteBuffer.limit();
//		byteBuffer.get(headerData);
//
//		int number = 0;
//		for(int i = 0; i < 4 ; i++){
//			number += headerData[i] << i*8;
//		}
//
//
//		System.err.println(number);
		byte[] bodyData = new byte[length];
		byteBuffer.get(bodyData);
		
		if (null != frame) {
            frame.release();
        }
		return bodyData;
	}
}
