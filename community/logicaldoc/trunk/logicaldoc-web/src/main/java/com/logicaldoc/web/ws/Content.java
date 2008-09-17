package com.logicaldoc.web.ws;

/**
 * Value object containing metadata about an element contained in the folder
 * 
 * @author Marco Meschieri
 * @version $Id:$
 * @since 3.0
 */
public class Content {
	private int id;

	private String name = "";

	private int writeable = 0;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getWriteable() {
		return writeable;
	}

	public void setWriteable(int writeable) {
		this.writeable = writeable;
	}
}