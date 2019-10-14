package com.dc.echo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dc.echo.config.ServerConfig;
import com.dc.echo.core.EchoRemotingServer;

public class StartServer {
	private static Logger LOG = LoggerFactory.getLogger(StartServer.class);
	public static void main(String[] args) {
		EchoRemotingServer server = new EchoRemotingServer(ServerConfig.getInstance().port);
		try {
			server.start();
			LOG.info("success,bind port is "+server.getPort());
		} catch (Throwable e) {
			LOG.error("",e);
		}
	}
}