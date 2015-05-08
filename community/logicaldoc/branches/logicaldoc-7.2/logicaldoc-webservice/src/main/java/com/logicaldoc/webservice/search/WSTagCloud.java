package com.logicaldoc.webservice.search;

import com.logicaldoc.core.document.TagCloud;

/**
 * This class is a TagCloud for WebServices API
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.7
 */
public class WSTagCloud {

	private String tag;

	private int count;

	private int scale;

	public WSTagCloud(String tag) {
		this.tag = tag;
	}

	public WSTagCloud(String tag, int count) {
		this.tag = tag;
		this.count = count;
	}

	public WSTagCloud() {
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

	public TagCloud toTagCloud() {
		TagCloud tc = new TagCloud();
		tc.setCount(count);
		tc.setScale(scale);
		tc.setTag(tag);
		return tc;
	}

	public static WSTagCloud fromTagCloud(TagCloud tc) {
		WSTagCloud wtc = new WSTagCloud();
		wtc.setCount(tc.getCount());
		wtc.setScale(tc.getScale());
		wtc.setTag(tc.getTag());
		return wtc;
	}
}