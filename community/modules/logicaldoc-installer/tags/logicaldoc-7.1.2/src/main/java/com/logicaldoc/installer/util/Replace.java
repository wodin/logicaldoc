package com.logicaldoc.installer.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Replaces a token in a file
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 1.0
 */
public class Replace {

	public static void replace(String source, String token, String newValue) {
		boolean windows = System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;

		try {
			File tmp = new File(source + ".tmp");
			File file = new File(source);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "", oldtext = "";
			while ((line = reader.readLine()) != null) {
				oldtext += line.replaceAll(token, newValue.replaceAll("\\\\", "\\\\\\\\"));
				if (windows && !source.endsWith(".sh"))
					oldtext += "\r";
				oldtext += "\n";
			}
			reader.close();

			// To replace a line in a file
			String newtext = oldtext.replaceAll(token, newValue.replaceAll("\\\\", "\\\\\\\\"));

			FileWriter writer = new FileWriter(tmp);
			writer.write(newtext);
			writer.close();

			file.delete();
			tmp.renameTo(file);
		} catch (Throwable ioe) {
			Log.error(ioe.getMessage(), ioe);
		}
	}
}