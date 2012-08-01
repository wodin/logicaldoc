package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;
import java.util.Date;

/**
 * This class defines the value of an attribute associated to an extensible
 * object. For each value, is possible to define the type and if it is mandatory
 * or not.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 4.5.1
 */
public class GUIExtendedAttribute implements Comparable<GUIExtendedAttribute>, Serializable {

	private static final long serialVersionUID = 1L;

	public static final int TYPE_STRING = 0;

	public static final int TYPE_INT = 1;

	public static final int TYPE_DOUBLE = 2;

	public static final int TYPE_DATE = 3;

	public static final int EDITOR_DEFAULT = 0;

	public static final int EDITOR_LISTBOX = 1;

	private int editor = EDITOR_DEFAULT;

	private String stringValue;

	private Long intValue;

	private Double doubleValue;

	private Date dateValue;

	private int type = TYPE_STRING;

	private boolean mandatory = false;

	private int position = 0;

	private String name;

	private String label;

	// Optional array of possible values
	private String[] options;

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public Long getIntValue() {
		return intValue;
	}

	public void setIntValue(Long intValue) {
		this.intValue = intValue;
	}

	public Double getDoubleValue() {
		return doubleValue;
	}

	public void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
	}

	public Date getDateValue() {
		return dateValue;
	}

	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	/**
	 * Gets the attribute value. It can be as String, Long, Double or Date.
	 * 
	 * @return The attribute value as Object.
	 */
	public Object getValue() {
		switch (type) {
		case TYPE_STRING:
			return getStringValue();
		case TYPE_INT:
			return getIntValue();
		case TYPE_DOUBLE:
			return getDoubleValue();
		case TYPE_DATE:
			return getDateValue();
		}
		return null;
	}

	/**
	 * Sets the attribute value. It can be as String, Long, Double or Date.
	 * 
	 * @param value The attribute value.
	 */
	public void setValue(Object value) {
		if (value instanceof java.lang.String) {
			this.type = TYPE_STRING;
			setStringValue((String) value);
		} else if (value instanceof Long) {
			this.type = TYPE_INT;
			setIntValue((Long) value);
		} else if (value instanceof Integer) {
			this.type = TYPE_INT;
			setIntValue(new Long(((Integer) value).intValue()));
		} else if (value instanceof Double) {
			this.type = TYPE_DOUBLE;
			setDoubleValue((Double) value);
		} else if (value instanceof Date) {
			this.type = TYPE_DATE;
			setDateValue((Date) value);
		} else if (value == null) {
			setStringValue(null);
			setDoubleValue(null);
			setIntValue(null);
			setDateValue(null);
		} else {
			throw new IllegalArgumentException("Not a String, Long, Double or Date value: "
					+ value.getClass().getName());
		}
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	/**
	 * This is the position of the attribute into the attributes list.
	 */
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	public int compareTo(GUIExtendedAttribute o) {
		return new Integer(getPosition()).compareTo(o.getPosition());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getEditor() {
		return editor;
	}

	public void setEditor(int editor) {
		this.editor = editor;
	}

	public String[] getOptions() {
		return options;
	}

	public void setOptions(String[] options) {
		this.options = options;
	}
}