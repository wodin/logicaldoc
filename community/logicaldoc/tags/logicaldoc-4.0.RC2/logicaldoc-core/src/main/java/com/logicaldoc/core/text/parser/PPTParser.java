package com.logicaldoc.core.text.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.poifs.eventfilesystem.POIFSReader;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderEvent;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderListener;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

/**
 * Parser for Office 2003 presentations
 * 
 * @author Michael Scholz
 */
public class PPTParser extends AbstractParser implements POIFSReaderListener {
	private InputStream input;

	protected static Log logger = LogFactory.getLog(PPTParser.class);

	/**
	 * @see com.logicaldoc.core.text.parser.Parser#getContent()
	 */
	public String getContent() {
		return content;
	}

	public void parse(File file) {
		try {
			content = "";
			input = new FileInputStream(file);
			POIFSReader reader = new POIFSReader();
			reader.registerListener(this);
			reader.read(input);
		} catch (Throwable ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (Exception ex) {
					logger.error(ex.getMessage(), ex);
				}
			}
		}
	}

	public void processPOIFSReaderEvent(POIFSReaderEvent event) {
		StringBuffer buffer = new StringBuffer();
		try {
			if (!event.getName().equalsIgnoreCase("PowerPoint Document")) {
				return;
			}

			DocumentInputStream input = event.getStream();
			int letter = 0;
			StringBuffer word = new StringBuffer();
			boolean separator = true;

			while ((letter = input.read()) != -1) {
				if (((letter > 64) && (letter < 91)) || ((letter > 96) && (letter < 123))) {
					word.append((char) letter);
					separator = true;
				} else {
					if ((letter == 196) || (letter == 214) || (letter == 220) || (letter == 223) || (letter == 228)
							|| (letter == 246) || (letter == 252)) {
						word.append((char) letter);
						separator = true;
					} else {
						if (letter == 32) {
							buffer.append((char) 32);
						}

						if (separator && (letter != 0)) {
							if (word.length() > 2) {
								buffer.append(word);
								buffer.append((char) 32);
							}

							word = new StringBuffer();
							separator = false;
						}
					}
				}
			}
		} catch (Throwable ex) {
			logger.error(ex.getMessage(), ex);
		}
		content = buffer.toString();
	}
}