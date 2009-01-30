package com.logicaldoc.core.text.parser;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.record.RecordFormatException;

/**
 * Parser for Office 2003 worksheets
 * 
 * @author Michael Scholz
 */
public class XLSParser extends AbstractParser {
	protected static Log log = LogFactory.getLog(XLSParser.class);

	public void parse(File file) {
		XLSRecordListener listener = new XLSRecordListener();
		try {
			content = listener.parse(file).toString();
		} catch (RecordFormatException re) {
			log.error("Encrypted document, unable to decrypt");
		}
	}
}