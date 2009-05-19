package com.logicaldoc.web.util;

/**
 * Simple bean that stores a name-value pair
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class Attribute {
	private String name;

	private String value;

	public Attribute(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return getName() + " - " + getValue();
	}
}
