package com.logicaldoc.core.text.parser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Michael Scholz
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.6
 */
public class WPDParser extends AbstractParser {
	
	protected static Log logger = LogFactory.getLog(WPDParser.class);

	private final int EOF = -1;

	public void parse(File file) {
		
		
		try {
			FileInputStream fis = new FileInputStream(file);
			Reader reader = extractText(fis, null, null);
			content = readText(reader, "UTF-8");
			
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
	
    /**
     * {@inheritDoc}
     */
	public Reader extractText(InputStream stream, String type, String encoding) throws IOException {
		
		try {
			StringBuilder sb = new StringBuilder();
			BufferedInputStream bis = new BufferedInputStream(stream);
			
			int token = 0;
			while ((token = bis.read()) != EOF) {
				// 128 (80h) equals space in wordperfect
				if (token == 128) {
					token = 32;
				}

				if ((token > 31) && (token < 126)) {
					sb.append((char) token);
				}
			}
			return new StringReader(sb.toString());
		} finally {
			stream.close();
		}
	}
}