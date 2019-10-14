package com.dc.echo;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.dc.echo.config.LoggerName;
import com.dc.echo.config.EchoCode;
import com.dc.echo.core.EchoConnection;
import com.dc.echo.core.MessageListener;
import com.dc.echo.pojo.Header;
import com.dc.echo.pojo.Message;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.SystemPropertyUtil;

/**
 * java -jar fast-im-core-0.0.1-SNAPSHOT.jar
 * 
 * @author dc
 */
public class Client {
    private static Logger LOG = LoggerFactory.getLogger(LoggerName.CONSOLE);
    private static Logger LOG_RECEIVE = LoggerFactory.getLogger(LoggerName.RECEIVE);
    //private static Logger LOG_SEND = LoggerFactory.getLogger(LoggerName.SEND);
    public static EchoConnection conn;
    public static long start;
    public static void main(String[] args) throws Throwable {
        System.out.println("当前机器线程核心线程数="+Math.max(1, SystemPropertyUtil.getInt(
                "io.netty.eventLoopThreads", Runtime.getRuntime().availableProcessors() * 2)));
        String encoding = System.getProperty("file.encoding");
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
                if(JSON.parseObject(message.getHeader(), Header.class).getMsgType()==EchoCode.LOGIN_ACTION) {
                    conn.setSync(false);
                    break;
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
                    for (int i = 0; i < Integer.parseInt(context); i++) {
                        Message message = new Message();
                        Map<String, Object> headerMap = new HashMap<String, Object>();
                        headerMap.put("version", "1.0");
                        headerMap.put("msgType", EchoCode.MESSAGE_TRANS_ACTION);//发送消息
                        headerMap.put("receiver", new String[] {receiver});
                        headerMap.put("sender", username);
                        headerMap.put("encoding", encoding);
                        headerMap.put("sendTime", System.currentTimeMillis());
                        message.setHeader(JSON.toJSONString(headerMap));
                    
                        message.setBody(context.getBytes());
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

    public static Message login(EchoConnection conn, String username) throws Throwable {
        System.out.println("正在登录...........................");
        Message message = new Message();
        Map<String, Object> headerMap = new HashMap<String, Object>();
        headerMap.put("version", "1.0");
        headerMap.put("msgType", EchoCode.LOGIN_ACTION);// 登录
        headerMap.put("sender", username);
        message.setHeader(JSON.toJSONString(headerMap));
        return conn.sendMessage(message);// 开始登录
    }

    public static EchoConnection connectServer() throws Throwable {
        EchoConnection conn = new EchoConnection("localhost", 8000).connect().setListener(new MessageListener() {
            @Override
            public void callback(ChannelHandlerContext ctx, Message message) {
                Header header = JSON.parseObject(message.getHeader(), Header.class);
                if (header.getSender() != null) {
                    try {
                        if (header.getMsgType() == EchoCode.MESSAGE_TRANS_ACTION) {
                            LOG_RECEIVE.info(new String(message.getBody()));
                            System.out.println();
                            System.out.println("         " + header.getSender() + ":" +new String(message.getBody()));
                            System.out.print(header.getReceiver()[0] + ":");
                        }
                        if (header.getMsgType() == EchoCode.USER_EXIST) {// 用户已被注册，请重启程序，并使用新的用户名登录
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
