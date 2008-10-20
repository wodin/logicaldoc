package com.logicaldoc.core.document;

import com.logicaldoc.core.PersistentObject;

/**
 * @author Michael Scholz
 * @author Marco Meschieri - Logical Objects
 */
public class Term extends PersistentObject {

	private double value = 0.0;

	private int wordCount = 0;

	private String originWord = "";

	private String stem;

	private long docId;

	public Term() {
	}

	public String getStem() {
		return stem;
	}

	public double getValue() {
		return value;
	}

	public void setStem(String stem) {
		this.stem = stem;
	}

	public void setValue(double d) {
		value = d;
	}

	public long getDocId() {
		return docId;
	}

	public void setDocId(long docId) {
		this.docId = docId;
	}

	public int getWordCount() {
		return wordCount;
	}

	public void setWordCount(int wordcount) {
		this.wordCount = wordcount;
	}

	public String getOriginWord() {
		return originWord;
	}

	public void setOriginWord(String originWord) {
		this.originWord = originWord;
	}
}