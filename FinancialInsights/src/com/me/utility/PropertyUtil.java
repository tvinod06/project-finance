package com.me.utility;

import java.io.InputStream;
import java.util.Properties;

public class PropertyUtil {

	private static Properties prop = null;
	private static InputStream input = null;

	private void initialise() {
		try {
			String propFile = "Info.properties";
			prop = new Properties();
			input = getClass().getClassLoader().getResourceAsStream(propFile);
			if (input == null) {
				System.out.println("Sorry, unable to find " + propFile);
				return;
			}
			prop.load(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getProperty(String key){
		if(prop == null){
			new PropertyUtil().initialise();
		}
		return prop.getProperty(key);
	}
}
