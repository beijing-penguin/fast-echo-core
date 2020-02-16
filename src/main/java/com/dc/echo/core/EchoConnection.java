package com.dc.echo.core;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dc.echo.config.LoggerName;
import com.dc.echo.config.NettyConfig;
import com.dc.echo.utils.EchoCoreUtils;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class EchoConnection{
    private static Logger LOG = LoggerFactory.getLogger(LoggerName.CONSOLE);

    public static CountDownLatch  resultWait;

    private static byte[] dataByteArr = null;

    private int readTimeOut = 10;//秒
    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private String host;
    private int port;
    private Channel channel;
    private MessageListener listener;

    // 是否同步获取响应结果
    private boolean sync = false;

    private int keepaliveTimeout = 5;//秒

    public Thread keepaliveThread;

    public EchoConnection(NettyConfig config){
        bootstrap = config.getBootstrap();
        group = config.getBoss();
        host = config.getHost();
        port = config.getPort();
    }
    public EchoConnection(String host,int port){
        this.host = host;
        this.port = port;
    }
    public void startHeartbeat() {
        //开启心跳程序
        keepaliveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(keepaliveThread!=null) {
                    try {
                        Thread.sleep(keepaliveTimeout*1000);
                        sendByteMessage(EchoCoreUtils.getKeepaliveMess());
                    } catch (Throwable e) {
                        LOG.error("",e);
                        return;
                    }
                }
            }
        });
        keepaliveThread.start();
    }
    public EchoConnection connect() throws Throwable{
        try {
            if(group==null) {
                group = new NioEventLoopGroup(1);
            }
            if(bootstrap==null) {
                bootstrap = new Bootstrap()
                        .group(group)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .option(ChannelOption.SO_REUSEADDR, true)
                        //禁用Nagle，Nagle算法就是为了尽可能发送大块数据，避免网络中充斥着许多小数据块。
                        .option(ChannelOption.TCP_NODELAY, true)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ch.config().setAllowHalfClosure(true);
                                ChannelPipeline pipeline = ch.pipeline();
                                //                pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                                //                pipeline.addLast("decoder", new StringDecoder());
                                //                pipeline.addLast("encoder", new StringEncoder());
                                pipeline.addLast("decoder", new MessageDecoder());
                                pipeline.addLast("encoder", new MessageEncoder());
                                pipeline.addLast("handler", new SimpleChannelInboundHandler<byte[]>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, byte[] dataByteArr) throws Exception {
                                        System.err.println("客户端收到"+new String(dataByteArr));
                                        if(sync) {
                                            EchoConnection.dataByteArr = dataByteArr;
                                            resultWait.countDown();
                                        }else {
                                            if(listener!=null) {
                                                listener.callback(ctx,dataByteArr);
                                            }
                                        }
                                    }

                                    @Override
                                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                        System.out.println("Client active ");
                                    }

                                    @Override
                                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                        //System.out.println("Client close ");
                                        //ctx.channel().close();
                                    }
                                    @Override
                                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                        System.out.println("链接异常中断");
                                        LOG.error("",cause);
                                    }
                                    @Override
                                    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                                        //System.err.println("channelRegistered");
                                    }

                                    @Override
                                    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
                                        //System.err.println("channelUnregistered");
                                        //close();
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
                                    }
                                });
                            }
                        });
            }
            channel = bootstrap.connect(host, port).sync().channel();
        }catch (Throwable e) {
            this.close();
            throw e;
        }
        return this;
    }
    public byte[] sendByteMessage(byte[] dataByteArr,MessageListener listener) throws Throwable{
        if(sync) {
            EchoConnection.dataByteArr=null;
            EchoConnection.resultWait = new CountDownLatch(1);
        }
        if(listener!=null ) {
            this.listener = listener;
        }
        if(channel==null || !channel.isOpen() || !channel.isActive()) {
            throw new Exception("channel break,Unable to connect to server");
        }
        channel.writeAndFlush(dataByteArr);
        if(sync) {
            EchoConnection.resultWait.await(readTimeOut,TimeUnit.SECONDS);
            if(EchoConnection.resultWait.getCount()!=0) {
                EchoConnection.dataByteArr = null;
                throw new Exception("send fail");
            }
            return EchoConnection.dataByteArr;
        }else {
            return null;
        }
    }
    public byte[] sendByteMessage(byte[] dataByteArr) throws Throwable{
        return sendByteMessage(dataByteArr, null);
    }
    public void close() {
        if(channel!=null){
            try {
                channel.close().sync();
                channel=null;
            }catch (Exception e) {
                LOG.error("close nettyconnection fail",e);
            }
        }

        if(group!=null) {
            group.shutdownGracefully();
            group = null;
        }
        if(keepaliveThread!=null) {
            keepaliveThread.interrupt();
            keepaliveThread = null;
        }
        bootstrap= null;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    public int getReadTimeOut() {
        return readTimeOut;
    }
    public void setReadTimeOut(int readTimeOut) {
        this.readTimeOut = readTimeOut;
    }
    
    public static byte[] getDataByteArr() {
        return dataByteArr;
    }
    public static void setDataByteArr(byte[] dataByteArr) {
        EchoConnection.dataByteArr = dataByteArr;
    }
    public boolean isSync() {
        return sync;
    }
    public EchoConnection setSync(boolean sync) {
        this.sync = sync;
        return this;
    }
    public MessageListener getListener() {
        return listener;
    }
    public EchoConnection setListener(MessageListener listener) {
        this.listener = listener;
        return this;
    }
}
