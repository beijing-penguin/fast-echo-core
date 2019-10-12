package com.dc.im;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dc.im.config.LoggerName;
import com.dc.im.config.ServerConfig;
import com.dc.im.core.EchoRemotingServer;

public class StartServer {
	private static Logger LOG = LoggerFactory.getLogger(LoggerName.CONSOLE);
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
