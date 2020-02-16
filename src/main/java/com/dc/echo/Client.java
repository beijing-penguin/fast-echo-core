package com.dc.echo;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.dc.echo.config.LoggerName;
import com.dc.echo.config.MsgCode;
import com.dc.echo.core.EchoConnection;
import com.dc.echo.core.MessageListener;
import com.dc.echo.pojo.Message;
import com.dc.echo.utils.EchoCoreUtils;

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
                Message message = EchoCoreUtils.byteToMessage(login(conn, username));
                if(message.getMsgCode()==MsgCode.LOGIN_ACTION) {
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
            String content = sc.nextLine();
            if(content!=null && content.length()>0) {
                try {
                    for (int i = 0; i < Integer.parseInt(content); i++) {
                        Message message = new Message();
                        message.setVersion("1.0");
                        message.setMsgCode(MsgCode.MESSAGE_TRANS_ACTION);
                        message.setReceiver(new String[] {receiver});
                        message.setSender(username);
                        message.setEncoding(encoding);
                        message.setSendTime(System.currentTimeMillis());
                        message.setContent(content);
                        conn.sendByteMessage(EchoCoreUtils.messageToByteArr(message));//发送消息
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

    public static byte[] login(EchoConnection conn, String username) throws Throwable {
        System.out.println("正在登录...........................");
        Message message = new Message();
        message.setVersion("1.0");
        message.setMsgCode(MsgCode.LOGIN_ACTION);
        message.setSender(username);
        return conn.sendByteMessage(EchoCoreUtils.messageToByteArr(message));// 开始登录
    }

    public static EchoConnection connectServer() throws Throwable {
        EchoConnection conn = new EchoConnection("localhost", 6666).connect().setListener(new MessageListener() {
            @Override
            public void callback(ChannelHandlerContext ctx, byte[] dataByteArr) {
                Message msg = EchoCoreUtils.byteToMessage(dataByteArr);
                if (msg.getSender() != null) {
                    try {
                        if (msg.getMsgCode() == MsgCode.MESSAGE_TRANS_ACTION) {
                            LOG_RECEIVE.info(new String(msg.getContent()));
                            System.out.println();
                            System.out.println("         " + msg.getSender() + ":" +new String(msg.getContent()));
                            System.out.print(msg.getReceiver()[0] + ":");
                        }
                        if (msg.getMsgCode() == MsgCode.USER_EXIST) {// 用户已被注册，请重启程序，并使用新的用户名登录
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
