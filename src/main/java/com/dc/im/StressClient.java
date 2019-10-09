package com.dc.im;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.dc.im.config.LoggerName;
import com.dc.im.core.MessageListener;
import com.dc.im.core.NettyConnection;
import com.dc.im.pojo.Header;
import com.dc.im.pojo.Message;
import com.dc.im.pojo.MsgType;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.SystemPropertyUtil;

/**
 * 压力测试客户端
 * java -jar fast-im-core-0.0.1-SNAPSHOT.jar
 * @author dc
 */
public class StressClient {
    private static Logger LOG = LoggerFactory.getLogger(LoggerName.CONSOLE);
    private static Logger LOG_RECEIVE = LoggerFactory.getLogger(LoggerName.RECEIVE);
    //private static Logger LOG_SEND = LoggerFactory.getLogger(LoggerName.SEND);
    public static NettyConnection conn;
    public static long start;
    public static void main(String[] args) throws Throwable {
        String encoding = System.getProperty("file.encoding");
        System.out.println("核心线程数"+Math.max(1, SystemPropertyUtil.getInt(
                "io.netty.eventLoopThreads", Runtime.getRuntime().availableProcessors() * 2)));
        Scanner sc = new Scanner(System.in,encoding);
        System.out.println(encoding);
        String username;
        while(true) {
            System.out.print(new String("输入自己的用户名："));
            username = sc.nextLine();
            conn = connectServer();
            try {
                conn.setSync(true);
                Message message = login(conn, username);
                if(JSON.parseObject(message.getHeader(), Header.class).getMsgType()==MsgType.USER_LOGIN) {
                    conn.setSync(false);
                    break;
                }else {
                    System.err.println("登录失败，返回数据="+JSON.toJSONString(message));
                }
            }catch (Throwable e) {
                System.out.print("登录失败");
                LOG.error("",e);
            }
            conn.close();
        }
        System.out.print("输入对方的用户名：");
        String receiver = sc.nextLine();
        System.out.print(username+":");
        while(sc.hasNextLine()) {
            String context = sc.nextLine();
            if(context!=null && context.length()>0) {
                try {
                	long sendTime = System.currentTimeMillis();
                    for (int i = 0; i < Integer.parseInt(context); i++) {
                        Message message = new Message();
                        Map<String, Object> headerMap = new HashMap<String, Object>();
                        headerMap.put("version", "1.0");
                        headerMap.put("msgType", MsgType.MESSAGE_IM);//发送消息
                        headerMap.put("receiver", new String[] {receiver});
                        headerMap.put("sender", username);
                        headerMap.put("encoding", encoding);
                        headerMap.put("sendTime", sendTime);
                        message.setHeader(JSON.toJSONString(headerMap));
                    
                        message.setBody(String.valueOf(i+"压力测试压力测试压力测试压力测试压力测试压力测试压力测试压力测试").getBytes());
                        conn.sendMessage(message);//发送消息
                    }
                    
                }catch (Exception e) {
                    LOG.error("",e);
                    System.out.println("消息发送失败");
                    try {
                        conn.close();
                        System.out.println("正在重新连接服务器");
                        Thread.sleep(2000);
                        conn = connectServer();
                        login(conn, username);
                        System.out.println("连接成功--");
                    }catch (Exception ee) {
                        LOG.error("",ee);
                    }
                }
                System.out.print(username+":");
            }
        }
        sc.close();
    }

    public static Message login(NettyConnection conn, String username) throws Throwable {
        System.out.println("正在登录...........................");
        Message message = new Message();
        Map<String, Object> headerMap = new HashMap<String, Object>();
        headerMap.put("version", "1.0");
        headerMap.put("msgType", MsgType.USER_LOGIN);// 登录
        headerMap.put("sender", username);
        message.setHeader(JSON.toJSONString(headerMap));
        return conn.sendMessage(message);// 开始登录
    }

    public static NettyConnection connectServer() throws Throwable {
        NettyConnection conn = new NettyConnection("localhost", 8000).connect().setListener(new MessageListener() {
            @Override
            public void callback(ChannelHandlerContext ctx, Message message) {
                Header header = JSON.parseObject(message.getHeader(), Header.class);
                if (header.getSender() != null) {
                    try {
                        if (header.getMsgType() == MsgType.MESSAGE_IM) {
                            LOG_RECEIVE.info(new String(message.getBody()));
                            System.out.println();
                            System.out.println("         " + header.getSender() + ":" +(System.currentTimeMillis()-header.getSendTime()) +"-"+new String(message.getBody()));
                            System.out.print(header.getReceiver()[0] + ":");
                        }
                        if (header.getMsgType() == MsgType.USER_EXIST) {// 用户已被注册，请重启程序，并使用新的用户名登录
                            System.out.print("用户已被注册，请重启程序，并使用新的用户名登录");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return conn;
    }
}
