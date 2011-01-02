package com.logicaldoc.util.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Utiliry class for classpath resources IO issues
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 4.5
 */
public class ResourceUtil {

	public static String readAsString(String resourceName) throws IOException {
		StringBuffer resourceData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new InputStreamReader(ResourceUtil.class.getResourceAsStream(resourceName)));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			resourceData.append(buf, 0, numRead);
		}
		reader.close();
		return resourceData.toString();
	}
}
