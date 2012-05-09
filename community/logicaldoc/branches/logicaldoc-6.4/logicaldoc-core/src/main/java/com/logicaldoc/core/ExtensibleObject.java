package com.logicaldoc.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
		if (attributes!=null && attributes.get(name) != null)
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
		ExtendedAttribute ext = getExtendedAttribute(name);
		if (ext == null)
			ext = new ExtendedAttribute();
		ext.setValue(value);
		return attributes.put(name, ext);
	}

	public Set<String> getAttributeNames() {
		if(attributes==null)
			return new HashSet<String>();
		return attributes.keySet();
	}

	public void removeAttribute(String name) {
		if (attributes!=null && attributes.containsKey(name))
			attributes.remove(name);
	}

	public ExtendedAttribute getAttributeAtPosition(int position) {
		if (position < 0)
			return null;
		List<ExtendedAttribute> extendedAttributes = new ArrayList<ExtendedAttribute>(attributes.values());
		if (position >= extendedAttributes.size())
			return null;
		ExtendedAttribute attribute = null;
		for (ExtendedAttribute extendedAttribute : extendedAttributes) {
			if (extendedAttribute.getPosition() == position) {
				attribute = extendedAttribute;
				break;
			}
		}
		return attribute;
	}
}