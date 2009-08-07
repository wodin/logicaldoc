package com.logicaldoc.core.text.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * Parser that doesn't parse anything
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class DummyParser extends AbstractParser {
	
	public void parse(File file) {
	}

	public Reader extractText(InputStream stream, String type, String encoding) throws IOException {
		return null;
	}
}