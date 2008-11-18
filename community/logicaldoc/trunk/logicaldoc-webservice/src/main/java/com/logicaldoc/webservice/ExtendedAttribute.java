package com.logicaldoc.webservice;

/**
 * Extended attribute of a document
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 4.0
 */
public class ExtendedAttribute {
	
	private String name;

	private String value;

	public ExtendedAttribute() {
		
	}
	
	public ExtendedAttribute(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String field) {
		this.name = field;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}