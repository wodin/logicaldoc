package com.logicaldoc.core.text.parser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Michael Scholz
 */
public class WPDParser extends AbstractParser {
	protected static Log logger = LogFactory.getLog(WPDParser.class);

	private final int EOF = -1;

	public void parse(File file) {
		StringBuffer buffer = new StringBuffer();
		try {
			FileInputStream in = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(in);
			int token = 0;

			while ((token = bis.read()) != EOF) {
				// 128 (80h) equals space in wordperfect
				if (token == 128) {
					token = 32;
				}

				if ((token > 31) && (token < 126)) {
					buffer.append((char) token);
				}
			}

			in.close();
			bis.close();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
		content = buffer.toString();
	}
}