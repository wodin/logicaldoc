package com.logicaldoc.core.text.parser.wordperfect;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.text.parser.AbstractParser;

/**
 * @author Alessandro Gasparini
 * @since 4.5.2
 */
public class WordPerfectParser extends AbstractParser {
	
	protected static Log logger = LogFactory.getLog(WordPerfectParser.class);

	public void parse(File file) {
		try {
			FileInputStream fis = new FileInputStream(file);
			Reader reader = extractText(fis, null, null);
			content = readText(reader, "UTF-8");
		} catch (Exception e) {
			logger.warn("Failed to extract WordPerfect text content", e);
		}
	}
	
	public Reader extractText(InputStream stream, String type, String encoding) throws IOException {
		try {
			WPStringExtractor extractor = new WPStringExtractor();
			String text = extractor.extract(stream).trim();
			return new StringReader(text);

		} catch (Exception e) {
			logger.warn("Failed to extract WordPerfect text content", e);
			return new StringReader("");
		} finally {
			stream.close();
		}
	}	

}