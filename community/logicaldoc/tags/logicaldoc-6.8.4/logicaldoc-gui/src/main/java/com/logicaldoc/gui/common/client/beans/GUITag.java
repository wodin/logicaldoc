package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;

/**
 * A simple tag representation
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class GUITag implements Serializable {

	private static final long serialVersionUID = 1L;

	private String tag;

	private int count;

	private int scale;
	
	private String link;

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

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

}