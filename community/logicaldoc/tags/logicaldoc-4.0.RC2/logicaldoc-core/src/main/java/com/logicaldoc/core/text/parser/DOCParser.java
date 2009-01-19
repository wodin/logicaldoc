package com.logicaldoc.core.text.parser;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hwpf.HWPFDocument;

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
 */
public class DOCParser extends AbstractParser {
	protected static Log logger = LogFactory.getLog(DOCParser.class);

	/**
	 * This function actually parses the doc file using the HWPF library. The
	 * text content is stored in the class member variable content.
	 * 
	 * @param file The MS Word (*.doc, *.dot) file to be parsed.
	 */
	public void parse(File file) {
		try {
			// for reading the MS Word file we use the deprecated HWPF library
			// provided by Jakarta POI
			FileInputStream in = new FileInputStream(file);
			HWPFDocument doc = new HWPFDocument(in);

			// this call returns the complete document text without any
			// formatting
			content = doc.getRange().text();
			in.close();
		} catch (Throwable ex) {
			logger.error(ex.getMessage());
		}
	}
}