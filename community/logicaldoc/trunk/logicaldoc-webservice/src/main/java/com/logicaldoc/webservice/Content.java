package com.logicaldoc.webservice;

/**
 * Value object containing metadata about an element contained in the folder
 * 
 * @author Marco Meschieri
 * @version $Id:$
 * @since 3.0
 */
public class Content {
	private int id;

	private String title = "";

	private int writeable = 0;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getWriteable() {
		return writeable;
	}

	public void setWriteable(int writeable) {
		this.writeable = writeable;
	}
}