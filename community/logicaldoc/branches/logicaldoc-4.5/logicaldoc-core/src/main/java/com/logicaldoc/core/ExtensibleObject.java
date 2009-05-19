package com.logicaldoc.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * An extensible object is able to store an undeterminate number of attributes.
 * Each attribute har a name and a string value.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public abstract class ExtensibleObject extends PersistentObject {
	private Map<String, String> attributes = new HashMap<String, String>();

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public String getValue(String name) {
		return attributes.get(name);
	}

	public String setValue(String name, String value) {
		return attributes.put(name, value);
	}
	
	public Set<String> getAttributeNames(){
		return attributes.keySet();
	}
}