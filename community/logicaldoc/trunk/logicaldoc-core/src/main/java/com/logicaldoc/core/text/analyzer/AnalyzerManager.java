package com.logicaldoc.core.text.analyzer;

import java.util.Collection;
import java.util.Iterator;

/**
 * Class for analysing texts like extracting keywords from a given text. Created
 * on 24.03.2004
 * 
 * @author Michael Scholz
 */
public class AnalyzerManager {

	public AnalyzerManager() {
	}

	/**
	 * This method extracts a specified number of keywords and appends them to a
	 * String
	 * 
	 * @param count Number of keywords.
	 * @param text Given text of a document.
	 * @param language Identified language of the text.
	 * @return String of keywords like "Information, Retrieval, DMS, CMS"
	 * @throws Exception
	 */
	public String getTermsAsString(int count, String text, String language) throws Exception {
		StringBuffer result = new StringBuffer();
		Analyzer analyzer = AnalyzerFactory.getAnalyzer(language);
		analyzer.analyze(text);

		Collection<Entry> terms = analyzer.getTopWords(count);
		Iterator<Entry> iter = terms.iterator();
		int temp = 0;

		while (iter.hasNext() && (temp < count)) {
			Entry entry = iter.next();

			if (temp > 0) {
				result.append(", ");
			}

			result.append(entry.getOriginWord());
			temp++;
		}

		return result.toString();
	}
}