package com.logicaldoc.plugin.language.zh;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

public class Charrs {

	/** A table of hex digits */
	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
			'E', 'F' };

	public static void main(String[] args) throws IOException {

		Properties propin = new Properties();
		Properties propout = new Properties();
		
		// read a property file in UTF-8 format
		File infile = new File("C:/Users/alle/Desktop/LogicalDOC-Chinese/application_en_GB18030_UTF-8.properties");
		File outfile = new File("C:/Users/alle/Desktop/LogicalDOC-Chinese/application_zh.properties");
		
		Reader reader = new InputStreamReader(new FileInputStream(infile), "UTF-8");
		Writer writer = new OutputStreamWriter(new FileOutputStream(outfile), "Cp1252");

		propin.load(reader);

		// write to a property file with java escaped characters (Cp1252)
		Set<String> propNames = propin.stringPropertyNames();
		
		for (Iterator iter = propNames.iterator(); iter.hasNext();) {
			String propName = (String) iter.next();
			String propValue = propin.getProperty(propName);
			
			String jEscapedValue = convertUnicodeToEncoded(propValue);
			// Add the property to the output properties
			propout.put(propName, jEscapedValue);
		}
		
		propout.store(writer, "fhfghfgh");
	}

	public static void oldMain(String[] args) throws IOException {

		File infile = new File("C:/Users/alle/Desktop/LogicalDOC-Chinese/application_en_GB18030_UTF-8.properties");
		File outfile = new File("C:/Users/alle/Desktop/LogicalDOC-Chinese/x-Cp1252.txt");

		Reader in = new InputStreamReader(new FileInputStream(infile), "UTF-8");
		Writer out = new OutputStreamWriter(new FileOutputStream(outfile), "Cp1252");

		int c;
		while ((c = in.read()) != -1) {
			out.write(c);
		}
		out.flush();

		out.close();
		in.close();
	}

	/**
	 * Converts unicodes to encoded &#92;uxxxx.
	 * 
	 * @param str
	 *            string to convert
	 * @return converted string
	 * @see java.util.Properties
	 */
	public static String convertUnicodeToEncoded(String str) {
		int len = str.length();
		StringBuffer outBuffer = new StringBuffer(len * 2);

		for (int x = 0; x < len; x++) {
			char aChar = str.charAt(x);
			if ((aChar < 0x0020) || (aChar > 0x007e)) {
				outBuffer.append('\\');
				outBuffer.append('u');
				outBuffer.append(toHex((aChar >> 12) & 0xF));
				outBuffer.append(toHex((aChar >> 8) & 0xF));
				outBuffer.append(toHex((aChar >> 4) & 0xF));
				outBuffer.append(toHex(aChar & 0xF));
			} else {
				outBuffer.append(aChar);
			}
		}
		return outBuffer.toString();
	}

	/**
	 * Converts a nibble to a hex character
	 * 
	 * @param nibble
	 *            the nibble to convert.
	 * @return a converted character
	 */
	private static char toHex(int nibble) {
		char hexChar = HEX_DIGITS[(nibble & 0xF)];
		return hexChar;
	}

}