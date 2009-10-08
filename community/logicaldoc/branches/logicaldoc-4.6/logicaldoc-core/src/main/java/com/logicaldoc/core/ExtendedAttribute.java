package com.logicaldoc.core;

import java.util.Date;

/**
 * This class defines the value of an attribute associated to an extensible
 * object. For each value, is possible to define the type and if it is mandatory
 * or not.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 4.5.1
 */
public class ExtendedAttribute {

	public static final int TYPE_STRING = 0;

	public static final int TYPE_INT = 1;

	public static final int TYPE_DOUBLE = 2;

	public static final int TYPE_DATE = 3;

	private String stringValue;

	private Integer intValue;

	private Double doubleValue;

	private Date dateValue;

	private int type = TYPE_STRING;

	private int mandatory = 0;

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public Integer getIntValue() {
		return intValue;
	}

	public void setIntValue(Integer intValue) {
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
	 * Gets the attribute value. It can be as String, Integer, Double or Date.
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
	 * Sets the attribute value. It can be as String, Integer, Double or Date.
	 * 
	 * @param value The attribute value.
	 */
	public void setValue(Object value) {
		if (value instanceof String) {
			this.type = TYPE_STRING;
			setStringValue((String) value);
		} else if (value instanceof Integer) {
			this.type = TYPE_INT;
			setIntValue((Integer) value);
		} else if (value instanceof Double) {
			this.type = TYPE_DOUBLE;
			setDoubleValue((Double) value);
		} else if (value instanceof Date) {
			this.type = TYPE_DATE;
			setDateValue((Date) value);
		} else {
			System.out.println("Value: " + value.getClass());
			throw new IllegalArgumentException("No a String, Integer, Double or Date value");
		}
	}

	/**
	 * Whether an attribute value is mandatory or not.
	 * 
	 * @return If 0, the attribute value is not mandatory; if 1, the attribute
	 *         value is mandatory.
	 */
	public int getMandatory() {
		return mandatory;
	}

	public void setMandatory(int mandatory) {
		this.mandatory = mandatory;
	}
}