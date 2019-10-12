package com.dc.echo.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppKeyConfig {
	private static Logger LOG = LoggerFactory.getLogger(LoggerName.CONSOLE);

	public static Set<String> appKeySet = new HashSet<String>();

	static {
		try {
			String propertiesName = "appKey.properties";
			InputStream in = ServerConfig.class.getClassLoader().getResourceAsStream(propertiesName);

			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = br.readLine()) != null){
				appKeySet.add(line);
			}
			br.close();
			in.close();
		}catch (Exception e) {
			LOG.error("",e);
		}
	}
}
