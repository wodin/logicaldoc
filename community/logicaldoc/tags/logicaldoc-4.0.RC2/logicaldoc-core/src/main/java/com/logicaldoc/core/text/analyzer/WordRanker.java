package com.logicaldoc.core.text.analyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

/**
 * Provides functionality like getting the top most words in a document.
 * 
 * @author Michael Scholz
 */
public abstract class WordRanker {
	protected Map<String, String> stoptable = new Hashtable<String, String>();

	protected Map<String, WordEntry> wordtable = new Hashtable<String, WordEntry>();

	protected long wordcount = 0;

	protected int minlen = 2;

	protected Entry getTopWord(Hashtable<String, WordEntry> table) {
		Entry entry = new Entry();
		Enumeration<String> enum1 = table.keys();
		int topvalue = -1;
		String topword = "";
		String topOriginWord = "";

		while (enum1.hasMoreElements()) {
			String key = enum1.nextElement();
			WordEntry termEntry = table.get(key);
			int val = termEntry.getValue();

			if (val > topvalue) {
				topvalue = val;
				topword = key;
				topOriginWord = termEntry.getOriginWord();
			}
		}

		entry.setWord(topword);
		entry.setNumber(topvalue);
		entry.setOriginWord(topOriginWord);
		return entry;
	}

	/**
	 * Returns the top words of an analyzed document.
	 * 
	 * @param hits - Number of top words to be returned.
	 * @return
	 */
	public Collection<Entry> getTopWords(int hits) {
		Hashtable<String, WordEntry> table = new Hashtable<String, WordEntry>(wordtable);
		Collection<Entry> coll = new ArrayList<Entry>(hits);

		if (hits > table.size()) {
			hits = table.size();
		}

		for (int i = 0; i < hits; i++) {
			Entry e = getTopWord(table);

			if (!e.getWord().equals("")) {
				coll.add(e);
				table.remove(e.getWord());
			}
		}

		return coll;
	}

	/**
	 * @return Number of entries in the hitlist containing the topwords.
	 */
	public int relevantWords() {
		return wordtable.size();
	}

	/**
	 * @return Number of words in the analyzed document.
	 */
	public long getWordCount() {
		return wordcount;
	}

	public int getMinlen() {
		return minlen;
	}

	/**
	 * Sets the minimum length of words which should be analyzed.
	 * 
	 * @param minlen
	 */
	public void setMinlen(int minlen) {
		this.minlen = minlen;
	}
}
