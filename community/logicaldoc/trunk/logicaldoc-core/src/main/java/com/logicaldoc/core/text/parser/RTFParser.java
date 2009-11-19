package com.logicaldoc.core.text.parser;

import java.io.InputStream;
import java.io.StringReader;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.util.StringUtil;

/**
 * @author Michael Scholz
 * @author Alessandro Gasparini
 * @since 3.5
 */
public class RTFParser extends AbstractParser {

	protected static Log log = LogFactory.getLog(RTFParser.class);

	@Override
	public void parse(InputStream input) {
		try {
			RTFEditorKit rek = new RTFEditorKit();
			DefaultStyledDocument doc = new DefaultStyledDocument();
			rek.read(input, doc, 0);
			String text = doc.getText(0, doc.getLength());
			content = StringUtil.writeToString(new StringReader(text));
		} catch (Throwable t) {
			log.warn("Failed to extract RTF text content", t);
		}
	}
}