package com.logicaldoc.core.text.parser;

import java.io.InputStream;
import java.io.StringReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.logicaldoc.util.StringUtil;

/**
 * Parser for Office 2003 worksheets
 * 
 * @author Michael Scholz
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.5
 */
public class XLSParser extends AbstractParser {

	protected static Log log = LogFactory.getLog(XLSParser.class);

	@Override
	public void parse(InputStream input) {
		try {
			POIFSFileSystem fs = new POIFSFileSystem(input);
			String tmp = new ExcelExtractor(fs).getText();

			// Replace Control characters
			if (tmp != null)
				tmp = tmp.replaceAll("[\\p{Cntrl}&&[^\\n]]", " ");

			content = StringUtil.writeToString(new StringReader(tmp));
		} catch (Exception e) {
			log.warn("Failed to extract Excel text content", e);
		}
	}
}