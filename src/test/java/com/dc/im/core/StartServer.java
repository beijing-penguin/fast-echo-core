package com.dc.im.core;

public class StartServer {
    public static void main(String[] args) {
        EchoRemotingServer server = new EchoRemotingServer(8000);
        try {
            server.start();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
