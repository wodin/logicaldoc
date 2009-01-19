package com.logicaldoc.core.text.parser;

import java.io.File;
import java.io.FileInputStream;

import javax.swing.JEditorPane;
import javax.swing.text.DefaultEditorKit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class for parsing text (*.txt) files. Created on 5. November 2003, 18:14
 * 
 * @author Michael Scholz
 */
public class TXTParser extends AbstractParser {
	protected static Log logger = LogFactory.getLog(TXTParser.class);

	public void parse(File file) {
		try {
			DefaultEditorKit editorkit = new DefaultEditorKit();
			JEditorPane editor = new JEditorPane();
			editor.setEditorKit(editorkit);

			FileInputStream fis = new FileInputStream(file);
			editorkit.read(fis, editor.getDocument(), 0);

			content = editor.getDocument().getText(0, editor.getDocument().getLength());
			fis.close();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
}