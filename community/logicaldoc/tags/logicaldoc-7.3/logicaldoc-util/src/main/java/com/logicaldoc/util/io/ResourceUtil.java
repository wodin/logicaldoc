package com.logicaldoc.util.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Utiliry class for classpath resources IO issues
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 4.5
 */
public class ResourceUtil {

	public static String readAsString(String resourceName) throws IOException {
		StringBuffer resourceData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				ResourceUtil.class.getResourceAsStream(resourceName)));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			resourceData.append(buf, 0, numRead);
		}
		reader.close();
		return resourceData.toString();
	}

	/**
	 * Copy a resource from the classpath into a file
	 * 
	 * @param classpath The classpath specification
	 * @param out The target file
	 * @throws IOException
	 */
	public static void copyResource(String classpath, File out) throws IOException {
		InputStream is = new BufferedInputStream(ResourceUtil.class.getResource(classpath).openStream());
		OutputStream os = new BufferedOutputStream(new FileOutputStream(out));
		try {
			for (;;) {
				int b = is.read();
				if (b == -1)
					break;
				os.write(b);
			}
		} finally {
			is.close();
			os.close();
		}
	}
}
