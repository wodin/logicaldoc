package com.logicaldoc.core.text.parser;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.StringReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hwpf.extractor.WordExtractor;

import com.logicaldoc.util.StringUtil;

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
 * @since 3.5
 */
public class DOCParser extends RTFParser {

	protected static Log log = LogFactory.getLog(DOCParser.class);

	@Override
	public void parse(InputStream input) {
		try {

			BufferedInputStream bis = new BufferedInputStream(input);
			bis.mark(Integer.MAX_VALUE);

			String tmp = "";
			try {
				tmp = new WordExtractor(bis).getText();
			} catch (Throwable e) {
				// Maybe the document to be parsed is not a Word file.
				// Try to evaluate it as a RTF file.
			}

			try {
				bis.reset();
			} catch (Exception e) {
			}

			if (tmp.length() == 0) {
				// Try to evaluate it as a RTF file.
				tmp = super.extractText(bis);
			}

			// Replace Control characters
			if (tmp != null)
				tmp = tmp.replaceAll("[\\p{Cntrl}&&[^\\n]]", " ");
			content.append(StringUtil.writeToString(new StringReader(tmp)));
		} catch (Throwable e) {
			log.warn("Failed to extract Word text content", e);
		}
	}
}