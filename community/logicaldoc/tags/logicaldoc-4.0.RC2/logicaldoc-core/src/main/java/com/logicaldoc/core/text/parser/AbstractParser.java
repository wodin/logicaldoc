package com.logicaldoc.core.text.parser;

import java.io.File;
import java.util.Locale;

/**
 * Abstract implementation of a Parser
 * 
 * @author Marco Meschieri
 * @version $Id:$
 * @since 3.5
 */
public abstract class AbstractParser implements Parser {
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
	public String getKeywords() {
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
	public void parse(File file, Locale locale) {
		parse(file);
	}
}