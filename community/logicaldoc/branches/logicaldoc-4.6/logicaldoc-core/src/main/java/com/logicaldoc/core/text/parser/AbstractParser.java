package com.logicaldoc.core.text.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract implementation of a Parser
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.5
 */
public abstract class AbstractParser implements Parser {

	protected static Log log = LogFactory.getLog(AbstractParser.class);

	protected String content = "";

	@Override
	public String getAuthor() {
		return "";
	}

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public String getTags() {
		return "";
	}

	@Override
	public String getSourceDate() {
		return "";
	}

	@Override
	public String getTitle() {
		return "";
	}

	@Override
	public String getVersion() {
		return "";
	}

	@Override
	public void parse(File file, Locale locale, String encoding) {
		String enc = "UTF-8";
		if (StringUtils.isNotEmpty(encoding))
			enc = encoding;
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			parse(is, locale, enc);
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
				}
		}
	}
}