package com.logicaldoc.core.text.parser;

import java.io.InputStream;
import java.util.Locale;

/**
 * Parser that doesn't parse anything
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class DummyParser extends AbstractParser {

	@Override
	public void parse(InputStream input, Locale locale, String encoding) {
		content = "";
	}

}