package com.logicaldoc.core.text;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This class puts all words of a string into a collection.
 * 
 * @author Michael Scholz
 * @version 1.0
 */
public class StringParser {
	private Collection<String> wordTable = new ArrayList<String>();

	/** Creates a new instance of StringParser */
	public StringParser(String text) {
		init(text);
	}

	/**
	 * Returns the wordtable.
	 */
	public Collection<String> getWordTable() {
		return wordTable;
	}

	/**
	 * This method extracts the words of a string.
	 */
	protected void init(String text) {
		BreakIterator boundary = BreakIterator.getWordInstance();
		boundary.setText(text);

		int start = boundary.first();

		for (int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary.next()) {
			String word = text.substring(start, end).trim();

			if (word.length() > 1) {
				wordTable.add(word);
			}
		}
	}
}