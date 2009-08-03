package com.logicaldoc.core.document;

import java.util.HashSet;
import java.util.Set;

import com.logicaldoc.core.PersistentObject;

/**
 * A template simply collects a set of attribute names
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class DocumentTemplate extends PersistentObject {

	private String name;

	private String description;

	private Set<String> attributes = new HashSet<String>();

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

	public Set<String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Set<String> attributes) {
		this.attributes = attributes;
	}
	
	public void addAttribute(String attribute){
		if(!attributes.contains(attribute))
			attributes.add(attribute);
	}
	
	public void removeAttribute(String attribute){
		if(attributes.contains(attribute))
			attributes.remove(attribute);
	}
}