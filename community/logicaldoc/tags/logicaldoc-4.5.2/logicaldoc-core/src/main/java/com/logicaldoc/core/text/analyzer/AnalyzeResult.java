package com.logicaldoc.core.text.analyzer;

import java.util.Hashtable;

/**
 * @author Michael Scholz
 */
public class AnalyzeResult {
	private long wordCount = 0;

	private Hashtable<String, WordEntry> wordTable = new Hashtable<String, WordEntry>();

	public AnalyzeResult() {
	}

	public long getWordCount() {
		return wordCount;
	}

	public Hashtable<String, WordEntry> getWordTable() {
		return wordTable;
	}

	public void setWordCount(long l) {
		wordCount = l;
	}

	public void setWordTable(Hashtable<String, WordEntry> hashtable) {
		wordTable = hashtable;
	}

}