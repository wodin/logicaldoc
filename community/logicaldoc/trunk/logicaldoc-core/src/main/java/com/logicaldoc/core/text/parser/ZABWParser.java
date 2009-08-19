package com.logicaldoc.core.text.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Text extractor for AbiWord compressed documents.
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.5
 */
public class ZABWParser extends AbstractParser {

	protected static Log log = LogFactory.getLog(ZABWParser.class);

	@Override
	public void parse(File file, Locale locale, String encoding) {
		String enc = "UTF-8";
		if (StringUtils.isNotEmpty(encoding))
			enc = encoding;

		FileInputStream stream = null;
		try {
			stream = new FileInputStream(file);
			GZIPInputStream gis = new GZIPInputStream(stream);
			parse(gis, null, enc);
		} catch (Exception ex) {
			log.warn("Failed to extract Compressed AbiWord text content", ex);
		} finally {
			try {
				if (stream != null)
					stream.close();
			} catch (IOException e) {
			}
		}
	}

	@Override
	public void parse(InputStream input, Locale locale, String encoding) {
		try {
			AbiWordParser parser = new AbiWordParser();
			parser.parse(input, locale, encoding);
			content = parser.getContent();
		} catch (Exception e) {
			log.warn("Failed to extract AbiWord Compressed zabw text content", e);
		}
	}
}