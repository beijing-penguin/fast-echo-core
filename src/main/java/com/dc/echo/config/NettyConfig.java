package com.dc.echo.config;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;

public class NettyConfig {
    
    private Bootstrap bootstrap;
    private EventLoopGroup boss;
    private int port;
    private String host;
    
    
    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }
    public Bootstrap getBootstrap() {
        return bootstrap;
    }
    public void setBootstrap(Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }
    public EventLoopGroup getBoss() {
        return boss;
    }
    public void setBoss(EventLoopGroup boss) {
        this.boss = boss;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    
    
}
