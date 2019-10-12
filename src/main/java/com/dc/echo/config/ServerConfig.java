package com.dc.echo.config;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Properties;

import com.dc.echo.utils.SystemUtils;


public class ServerConfig {
	public int port;
	
	private static ServerConfig config = new ServerConfig();
    public ServerConfig() {}
    public static ServerConfig getInstance() {
        return config;
    }
    
    
	
	
	static {
        try {
            String propertiesName = "server.properties";
            InputStream in = ServerConfig.class.getClassLoader().getResourceAsStream(propertiesName);
            Properties prop = new Properties();
            prop.load(new InputStreamReader(in,"UTF-8"));
            Iterator<String> it = prop.stringPropertyNames().iterator();
            Field[] fieldArr = ServerConfig.class.getDeclaredFields();
            while (it.hasNext()) {
                String key = it.next();
                for (int i = 0; i < fieldArr.length; i++) {
                    Field field = fieldArr[i];
                    if(field.getName().equals(SystemUtils.separatorToJavaBean(key))) {
                        field.setAccessible(true);
                        field.set(config, SystemUtils.getValueByFieldType(prop.get(key), field.getType()));
                    }
                }
            }
            in.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
	
}
