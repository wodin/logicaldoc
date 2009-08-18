package com.logicaldoc.core.text.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hwpf.extractor.WordExtractor;

/**
 * Parses a MS Word (*.doc, *.dot) file to extract the text contained in the
 * file. This class uses the external library HWPF provided by the Apache
 * Jakarta POI project. Even though this library provides features to extract
 * the document author and version, we do not use those features, because the
 * library is known to be buggy. The important part is to get the text content,
 * not extracting the author, date, etc. is not essential.
 * 
 * @author Michael Scholz
 * @author Sebastian Stein
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.6
 */
public class DOCParser extends AbstractParser {

	protected static Log logger = LogFactory.getLog(DOCParser.class);

	/**
	 * This function actually parses the doc file using the HWPF library. The
	 * text content is stored in the class member variable content.
	 * 
	 * @param file
	 *            The MS Word (*.doc, *.dot) file to be parsed.
	 */
	public void parse(File file) {
		try {
			FileInputStream stream = new FileInputStream(file);
			Reader reader = extractText(stream, null, null);
			content = readText(reader, "UTF-8");
		} catch (Exception ex) {
			logger.warn("Failed to extract Word text content", ex);
		}
	}

	/**
	 * {@inheritDoc} Returns an empty reader if an error occured extracting text
	 * from the word document.
	 */
	public Reader extractText(InputStream stream, String type, String encoding) throws IOException {
		try {
			String tmp = new WordExtractor(stream).getText();
			
			if (tmp != null)
				tmp = tmp.replaceAll("[\\p{Cntrl}&&[^\\n]]", " ");

			return new StringReader(tmp);
		} catch (Exception e) {
			logger.warn("Failed to extract Word text content", e);
			return new StringReader("");
		} finally {
			stream.close();
		}
	}
}