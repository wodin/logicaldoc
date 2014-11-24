package com.logicaldoc.installer.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class Wget {
	public static String wget(String url) {
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
			StringBuffer sb = new StringBuffer();
			String s;
			while ((s = r.readLine()) != null) {
				sb.append(s);
			}
			return sb.toString();
		} catch (Throwable t) {
			return "";
		}
	}
}
