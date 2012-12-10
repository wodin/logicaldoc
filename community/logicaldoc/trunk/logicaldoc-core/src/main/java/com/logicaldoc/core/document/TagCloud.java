package com.logicaldoc.core.document;

/**
 * This class is a TagCloud
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 4.0
 */
public class TagCloud {

	private String tag;

	private int count;

	private int scale;

	public TagCloud(String tag) {
		this.tag = tag;
	}

	public TagCloud(String tag, int count) {
		this.tag = tag;
		this.count = count;
	}

	public TagCloud() {
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}
}