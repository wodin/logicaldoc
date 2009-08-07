package com.logicaldoc.core.text.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.zip.GZIPInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Text extractor for AbiWord compressed documents.
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.6
 */
public class ZABWParser extends AbstractParser {

	protected static Log logger = LogFactory.getLog(ZABWParser.class);

	public void parse(File file) {

		System.err.println(file);
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(file);
			GZIPInputStream gis = new GZIPInputStream(stream);

			String encoding = "UTF-8";
			Reader reader = extractText(gis, null, encoding);

			content = readText(reader, encoding);

		} catch (Exception ex) {
			logger.warn("Failed to extract Compressed AbiWord text content", ex);
		} finally {
			try {
				if (stream != null)
					stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Reader extractText(InputStream stream, String type, String encoding) throws IOException {
		try {
			AbiWordParser parser = new AbiWordParser();
			Reader reader = parser.extractText(stream, type, encoding);
			return reader;
		} catch (Exception e) {
			logger.warn("Failed to extract AbiWord Compressed zabw text content", e);
			return new StringReader("");
		} finally {
			stream.close();
		}
	}

}