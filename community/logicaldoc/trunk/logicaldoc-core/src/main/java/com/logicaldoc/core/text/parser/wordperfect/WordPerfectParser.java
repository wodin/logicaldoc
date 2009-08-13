package com.logicaldoc.core.text.parser.wordperfect;

import java.io.InputStream;
import java.io.StringReader;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.text.parser.AbstractParser;
import com.logicaldoc.util.StringUtil;

/**
 * @author Alessandro Gasparini
 * @since 4.5.2
 */
public class WordPerfectParser extends AbstractParser {

	protected static Log log = LogFactory.getLog(WordPerfectParser.class);

	@Override
	public void parse(InputStream input, Locale locale, String encoding) {
		try {
			WPStringExtractor extractor = new WPStringExtractor();
			String text = extractor.extract(input).trim();
			StringUtil.writeToString(new StringReader(text));
		} catch (Exception e) {
			log.warn("Failed to extract WordPerfect text content", e);
		}
	}
}