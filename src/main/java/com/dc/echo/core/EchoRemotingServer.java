package com.dc.echo.core;

import java.net.InetSocketAddress;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dc.echo.config.LoggerName;
import com.dc.echo.config.MsgCode;
import com.dc.echo.pojo.Message;
import com.dc.echo.utils.EchoCoreUtils;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

public class EchoRemotingServer {

	private static Logger LOG = LoggerFactory.getLogger(LoggerName.CONSOLE);
	private static Logger LOG_RECEIVE = LoggerFactory.getLogger(LoggerName.RECEIVE);
	private ServerBootstrap serverBootstrap;
	private  NioEventLoopGroup boss;
	private  NioEventLoopGroup work;
	private int port;
	public EchoRemotingServer(int port) {
		this.port = port;
	}
	public void start() throws Throwable {
		if(serverBootstrap==null) {
			serverBootstrap = new ServerBootstrap();
		}
		if(boss==null) {
			boss = new NioEventLoopGroup();
		}
		if(work==null) {
			//1w并发下
			//8 1005
			//16 986 默认线程个数，推荐
			//128 1080毫秒
			//5000 954毫秒
			work = new NioEventLoopGroup();
		}
		serverBootstrap.group(boss, work)
		.channel(NioServerSocketChannel.class)
		.option(ChannelOption.SO_BACKLOG, 4096)
		//.option(ChannelOption.SO_KEEPALIVE, true)
		//启用ByteBuf重用
		//.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
		//.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
		.childOption(ChannelOption.TCP_NODELAY, true)
		.childOption(ChannelOption.SO_SNDBUF, 65535*10)
		.childOption(ChannelOption.SO_RCVBUF, 65535*10)
		.localAddress(new InetSocketAddress(port))
		.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {

				ch.config().setAllowHalfClosure(true);
				ChannelPipeline pipeline = ch.pipeline();
				//IdleStateHandler 与客户端链接后，根据超出配置的时间自动触发userEventTriggered
				//readerIdleTime服务端长时间没有读到数据，则为读空闲，触发读空闲监听，并自动关闭链路连接，周期性按readerIdleTime的超时间触发空闲监听方法
				//writerIdleTime服务端长时间没有发送写请求，则为空闲，触发写空闲监听,空闲期间，周期性按writerIdleTime的超时间触发空闲监听方法
				//allIdleTime 服务端在allIdleTime时间内未接收到客户端消息，或者，也未去向客户端发送消息，则触发周期性操作
				//pipeline.addLast("ping", new IdleStateHandler(0, 0, 40, TimeUnit.SECONDS));

				// 以("\n")为结尾分割的 解码器
				//                pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
				// 字符串解码 和 编码
				//                pipeline.addLast("decoder", new StringDecoder());
				//                pipeline.addLast("encoder", new StringEncoder());
				pipeline.addLast("decoder", new MessageDecoder());
				pipeline.addLast("encoder", new MessageEncoder());
				pipeline.addLast("handler", new SimpleChannelInboundHandler<byte[]>() {
					@Override
					protected void channelRead0(ChannelHandlerContext ctx, byte[] dataByteArr) throws Exception {
					    try {
						    Message msg = EchoCoreUtils.byteToMessage(dataByteArr);
						    String[]  receiverArr = msg.getReceiver();
							if(InterceptorController.interceptor(ctx, msg)) {
								//LOG_RECEIVE.info(JSON.toJSONString(msg));
								switch (msg.getMsgCode()) {
								case MsgCode.LOGIN_ACTION:
									LoginController.login(ctx, msg);
									break;
								case MsgCode.MESSAGE_TRANS_ACTION:
									
									for (int i = 0; i < receiverArr.length; i++) {
										ChannelHandlerContext receiver_channel = LoginController.user_channel_map.get(receiverArr[i]);
										if(receiver_channel!=null && receiver_channel.channel().isOpen() && receiver_channel.channel().isActive()) {
											receiver_channel.channel().writeAndFlush(dataByteArr);
										}
									}
									break;
								case 4:
								case 5:
								case 6:
								case 7:
								case 8:
								case 9:
									for (int i = 0; i < receiverArr.length; i++) {
										ChannelHandlerContext receiver_channel = LoginController.user_channel_map.get(receiverArr[i]);
										if(receiver_channel!=null && receiver_channel.channel().isOpen() && receiver_channel.channel().isActive()) {
											receiver_channel.channel().writeAndFlush(dataByteArr);
										}
									}
								case MsgCode.HEARTBEAT_ACTION:
									ctx.channel().writeAndFlush(EchoCoreUtils.getMessByCode(MsgCode.SUCCESS));
								default:
									break;
								}
								return;
							}
						}catch (Exception e) {
							LOG.error("server error",e);
							ctx.channel().writeAndFlush(EchoCoreUtils.getErrorMess());
						}
					}

					@Override
					public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
						System.err.println("server channelRegistered");
					}

					@Override
					public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
						System.err.println("server channelUnregistered");
						for (Entry<String, ChannelHandlerContext> entry : LoginController.user_channel_map.entrySet()) {
							if(entry.getValue()==ctx) {
								LOG.info("["+entry.getKey()+"]-用户已退出");
								LoginController.user_channel_map.remove(entry.getKey());
								ctx.close();
							}
						}
					}

					@Override
					public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
						if (evt instanceof IdleStateEvent) {
							IdleStateEvent event = (IdleStateEvent) evt;
							if (event.state().equals(IdleState.READER_IDLE)) {
								System.out.println("READER_IDLE");
							} else if (event.state().equals(IdleState.WRITER_IDLE)) {
								System.out.println("WRITER_IDLE");
							} else if (event.state().equals(IdleState.ALL_IDLE)) {
								System.out.println("ALL_IDLE");
								// 发送心跳
								// ctx.channel().writeAndFlush("ping\n");
							}
						}
						ctx.close();
					}

					@Override
					public void channelActive(ChannelHandlerContext ctx) throws Exception {
						System.err.println("server channelActive=" +ctx);
					}

					@Override
					public void channelInactive(ChannelHandlerContext ctx) throws Exception {
						System.err.println("server channelInactive");
					}
					@Override
					public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
						LOG.info("",cause);
						ctx.close();
					}
				});
			}
		});
		this.serverBootstrap.bind().sync();
	}
	public void shutdown() throws Throwable{
		try {
			if(boss!=null) {
				boss.shutdownGracefully().sync();
			}
		} catch (Throwable e) {
			LOG.error("",e);
		}
		try {
			if(work!=null) {
				work.shutdownGracefully().sync();
			}
		} catch (Throwable e) {
			LOG.error("",e);
		}
		if(serverBootstrap!=null) {
			serverBootstrap = null;
		}
	}
	public ServerBootstrap getServerBootstrap() {
		return serverBootstrap;
	}
	public void setServerBootstrap(ServerBootstrap serverBootstrap) {
		this.serverBootstrap = serverBootstrap;
	}
	public NioEventLoopGroup getBoss() {
		return boss;
	}
	public void setBoss(NioEventLoopGroup boss) {
		this.boss = boss;
	}
	public NioEventLoopGroup getWork() {
		return work;
	}
	public void setWork(NioEventLoopGroup work) {
		this.work = work;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}


}
