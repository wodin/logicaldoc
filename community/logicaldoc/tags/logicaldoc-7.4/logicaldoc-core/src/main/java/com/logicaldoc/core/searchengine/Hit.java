package com.logicaldoc.core.searchengine;

import com.logicaldoc.core.document.Document;

/**
 * Search result
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 5.2
 */
public class Hit extends Document implements Comparable<Hit> {

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

	@Override
	public int compareTo(Hit other) {
		if (other == null)
			return -1;
		if (other.score == this.score) {
			if (this.getTitle() != null)
				return this.getTitle().compareToIgnoreCase(other.getTitle());
			else
				return 0;
		} else
			return -1 * (new Integer(this.score).compareTo(new Integer(other.score)));
	}
}