package com.logicaldoc.web.util;

import com.logicaldoc.core.ExtendedAttribute;

/**
 * Simple bean that stores a name-Extended Attribute pair
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class Attribute {
	
	private String name;

	private ExtendedAttribute attribute;

	public Attribute(String name, ExtendedAttribute attribute) {
		super();
		this.name = name;
		this.attribute = attribute;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ExtendedAttribute getAttribute() {
		return attribute;
	}

	public void setAttribute(ExtendedAttribute attribute) {
		this.attribute = attribute;
	}

	@Override
	public String toString() {
		return getName() + " - " + getAttribute().getValue().toString();
	}
}
