package com.logicaldoc.core;

/**
 * Represents an option for a multi/choice extended attribute
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.1
 */
public class ExtendedAttributeOption extends PersistentObject implements Comparable<ExtendedAttributeOption> {

	private long templateId;

	private String attribute;

	private String value;

	private String label;

	private int position = 0;

	public ExtendedAttributeOption() {
	}

	public ExtendedAttributeOption(long templateId, String attribute, String value) {
		this();
		this.templateId = templateId;
		this.attribute = attribute;
		this.value = value;
	}

	public long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	public int compareTo(ExtendedAttributeOption other) {
		return new Integer(position).compareTo(new Integer(other.position));
	}
}
