package com.logicaldoc.core.document;

/**
 * This class is a TagCloud
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 4.0
 */
public class TagCloud {

	private String keyword;

	private int occurence;

	private int scale;

	public TagCloud(String keyword) {
		this.keyword = keyword;
	}

	public TagCloud(String keyword, int occurence) {
		this.keyword = keyword;
		this.occurence = occurence;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public int getOccurence() {
		return occurence;
	}

	public void setOccurence(int occurence) {
		this.occurence = occurence;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}
}