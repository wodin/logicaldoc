package com.logicaldoc.core.document;

import com.logicaldoc.core.ExtensibleObject;

/**
 * A template simply collects a set of attribute names
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class DocumentTemplate extends ExtensibleObject {

	private String name;

	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}