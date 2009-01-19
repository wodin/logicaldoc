package com.logicaldoc.core.text.analyzer;

import java.util.Hashtable;
import java.util.Map;

/**
 * This class creates a hashtable filled with stop words.
 * 
 * @author Michael Scholz
 * @version 1.0
 */
public class StopTable {
	/**
	 * This method transforms the array of stop words into a Pam of stop
	 * words.
	 * 
	 * @param stopwords - Map of stop words.
	 */
	public final static Map<String, String> setStopWords(String[] stopwords) {
		Hashtable<String, String> stoptable = new Hashtable<String, String>(stopwords.length);

		for (int i = 0; i < stopwords.length; i++) {
			stoptable.put(stopwords[i], stopwords[i]);
		}

		return stoptable;
	}

}
