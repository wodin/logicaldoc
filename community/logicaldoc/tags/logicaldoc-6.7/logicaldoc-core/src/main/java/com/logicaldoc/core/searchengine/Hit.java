package com.logicaldoc.core.searchengine;

import com.logicaldoc.core.document.Document;

/**
 * Search result
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 5.2
 */
public class Hit extends Document {

	private int score;

	private String content;

	private String summary;

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}
}