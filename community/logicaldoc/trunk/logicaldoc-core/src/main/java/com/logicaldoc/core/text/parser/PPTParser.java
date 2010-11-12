package com.logicaldoc.core.text.parser;

import java.io.InputStream;
import java.io.StringReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hslf.extractor.PowerPointExtractor;

import com.logicaldoc.util.StringUtil;

/**
 * Parser for Office 2003 presentations
 * 
 * @author Michael Scholz
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.5
 */
public class PPTParser extends AbstractParser {

	protected static Log log = LogFactory.getLog(PPTParser.class);

	@Override
	public void parse(InputStream input) {
		try {
			PowerPointExtractor extractor = new PowerPointExtractor(input);
			String tmp = extractor.getText(true, true);

			// Replace Control characters
			if (tmp != null)
				tmp = tmp.replaceAll("[\\p{Cntrl}&&[^\\n]]", " ");

			content = StringUtil.writeToString(new StringReader(tmp));
		} catch (Exception e) {
			log.warn("Failed to extract PowerPoint text content", e);
		}
	}
}