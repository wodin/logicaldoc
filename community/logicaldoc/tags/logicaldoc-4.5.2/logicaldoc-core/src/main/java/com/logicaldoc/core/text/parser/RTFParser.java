package com.logicaldoc.core.text.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Michael Scholz
 * @author Alessandro Gasparini
 * @since 3.6
 */
public class RTFParser extends AbstractParser {

	protected static Log logger = LogFactory.getLog(RTFParser.class);

	public void parse(File file) {
		try {
			FileInputStream fis = new FileInputStream(file);
			Reader reader = extractText(fis, null, null);
			content = readText(reader, "UTF-8");
		} catch (Exception ex) {
			logger.warn("Failed to extract RTF text content", ex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Reader extractText(InputStream stream, String type, String encoding) throws IOException {

		try {
			RTFEditorKit rek = new RTFEditorKit();
			DefaultStyledDocument doc = new DefaultStyledDocument();
			rek.read(stream, doc, 0);
			String text = doc.getText(0, doc.getLength());
			return new StringReader(text);
		} catch (Throwable t) {
			logger.warn("Failed to extract RTF text content", t);
			return new StringReader("");
		} finally {
			stream.close();
		}
	}
}