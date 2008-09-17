package com.logicaldoc.core.text.parser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Michael Scholz
 */
public class XMLParser extends AbstractParser {
	protected static Log logger = LogFactory.getLog(XMLParser.class);

	public void parse(File file) {
		StringBuffer buffer = new StringBuffer();
		try {
			InputStream in = new FileInputStream(file);
			BufferedInputStream reader = new BufferedInputStream(in);
			int ichar = 0;
			boolean istag = false;

			while ((ichar = reader.read()) != -1) {
				if (ichar == 60) {
					buffer.append((char) 32);
					istag = true;
				}

				if (!istag) {
					buffer.append((char) ichar);
				}

				if (ichar == 62) {
					istag = false;
				}
			}

			in.close();
			reader.close();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
		content = buffer.toString();
	}
}