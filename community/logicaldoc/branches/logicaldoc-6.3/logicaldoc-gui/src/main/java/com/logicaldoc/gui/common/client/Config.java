package com.logicaldoc.gui.common.client;

import java.util.HashMap;

import com.logicaldoc.gui.common.client.beans.GUIInfo;
import com.logicaldoc.gui.common.client.beans.GUIValuePair;

/**
 * Configuration paramenters (ContextProperties)
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class Config {
	private static HashMap<String, String> config = new HashMap<String, String>();

	public static void init(GUIInfo info) {
		config.clear();
		for (GUIValuePair val : info.getConfig()) {
			config.put(val.getCode(), val.getValue());
		}
	}

	public static String getProperty(String name) {
		return config.get(name);
	}
}