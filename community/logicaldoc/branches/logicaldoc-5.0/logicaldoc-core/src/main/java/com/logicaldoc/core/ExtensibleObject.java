package com.logicaldoc.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * An extensible object is able to store an undeterminate number of attributes.
 * Each attribute has a name and a string value.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public abstract class ExtensibleObject extends PersistentObject {
	private Map<String, ExtendedAttribute> attributes = new HashMap<String, ExtendedAttribute>();

	public Map<String, ExtendedAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, ExtendedAttribute> attributes) {
		this.attributes = attributes;
	}

	public Object getValue(String name) {
		if (attributes.get(name) != null)
			return attributes.get(name).getValue();
		else
			return null;
	}

	public ExtendedAttribute getExtendedAttribute(String name) {
		if (attributes.get(name) != null)
			return attributes.get(name);
		else
			return null;
	}

	public ExtendedAttribute setValue(String name, Object value) {
		ExtendedAttribute ext = new ExtendedAttribute();
		ext.setValue(value);
		return attributes.put(name, ext);
	}

	public Set<String> getAttributeNames() {
		return attributes.keySet();
	}

	public void removeAttribute(String name) {
		if(attributes.containsKey(name))
			attributes.remove(name);
	}
}