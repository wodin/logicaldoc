package com.logicaldoc.core.text.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * Parser for Office 2003 worksheets
 * 
 * @author Michael Scholz
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.6
 */
public class XLSParser extends AbstractParser {

	protected static Log logger = LogFactory.getLog(XLSParser.class);

	public void parse(File file) {
		try {
			FileInputStream fis = new FileInputStream(file);
			Reader reader = extractText(fis, null, null);
			content = readText(reader, "UTF-8");
		} catch (Exception e) {
			logger.warn("Failed to extract Excel text content", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Reader extractText(InputStream stream, String type, String encoding) throws IOException {
		try {
			POIFSFileSystem fs = new POIFSFileSystem(stream);
			String tmp = new ExcelExtractor(fs).getText();
			
			// Replace Control characters
			if (tmp != null)
				tmp = tmp.replaceAll("\\p{Cntrl} && ^\\n", " ");

			return new StringReader(tmp);
		} catch (RuntimeException e) {
			logger.warn("Failed to extract Excel text content", e);
			return new StringReader("");
		} finally {
			stream.close();
		}
	}
}