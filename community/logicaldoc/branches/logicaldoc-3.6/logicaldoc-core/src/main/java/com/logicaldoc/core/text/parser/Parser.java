package com.logicaldoc.core.text.parser;

import java.io.File;
import java.util.Locale;

/**
 * @author Michael Scholz
 */
public interface Parser {
	
	public String getVersion();

	public String getContent();

	public String getAuthor();

	public String getSourceDate();

	public String getKeywords();

	public String getTitle();
	
	public void parse(File file);
	
	public void parse(File file, Locale locale);
}