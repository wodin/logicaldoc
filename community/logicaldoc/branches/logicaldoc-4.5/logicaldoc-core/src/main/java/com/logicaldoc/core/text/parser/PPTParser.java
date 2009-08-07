package com.logicaldoc.core.text.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hslf.extractor.PowerPointExtractor;

/**
 * Parser for Office 2003 presentations
 * 
 * @author Michael Scholz
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.6
 */
public class PPTParser extends AbstractParser {

	protected static Log logger = LogFactory.getLog(PPTParser.class);

	/**
	 * {@inheritDoc}
	 */
	public Reader extractText(InputStream stream, String type, String encoding) throws IOException {
		try {
			PowerPointExtractor extractor = new PowerPointExtractor(stream);
			String tmp = extractor.getText(true, true);
			
			// Replace Control characters
			if (tmp != null)
				tmp = tmp.replaceAll("\\p{Cntrl}", " ");
			
			return new StringReader(tmp);
		} catch (RuntimeException e) {
			logger.warn("Failed to extract PowerPoint text content", e);
			return new StringReader("");
		} finally {
			try {
				stream.close();
			} catch (IOException ignored) {
			}
		}
	}

	public void parse(File file) {
		try {
			FileInputStream stream = new FileInputStream(file);
			Reader reader = extractText(stream, null, null);
			content = readText(reader, "UTF-8");
		} catch (Throwable ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

}