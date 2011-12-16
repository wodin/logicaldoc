package com.logicaldoc.webservice;

/**
 * Useful class that contains a correspondence between a name and a value.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
public class WSParameter {

	private String name;

	private String value;

	public WSParameter() {
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
