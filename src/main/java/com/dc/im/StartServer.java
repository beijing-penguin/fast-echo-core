package com.dc.im;

import com.dc.im.core.NettyRemotingServer;

public class StartServer {
    public static void main(String[] args) {
        NettyRemotingServer server = new NettyRemotingServer(8000);
        try {
            server.start();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
