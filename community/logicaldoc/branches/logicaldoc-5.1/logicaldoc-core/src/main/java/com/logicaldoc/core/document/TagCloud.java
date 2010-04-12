package com.logicaldoc.core.document;

/**
 * This class is a TagCloud
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 4.0
 */
public class TagCloud {

	private String tag;

	private int occurence;

	private int scale;

	public TagCloud(String tag) {
		this.tag = tag;
	}

	public TagCloud(String tag, int occurence) {
		this.tag = tag;
		this.occurence = occurence;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
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