package com.logicaldoc.webservice;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.XMLGregorianCalendar;

import com.logicaldoc.webservice.security.WSUser;

/**
 * Extended attribute of a document
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 4.0
 */
public class WSAttribute implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int TYPE_STRING = 0;

	public static final int TYPE_INT = 1;

	public static final int TYPE_DOUBLE = 2;

	public static final int TYPE_DATE = 3;

	public static final int TYPE_USER = 4;

	public static final int TYPE_BOOLEAN = 5;

	private String name;

	private String stringValue;

	private Long intValue;

	private Double doubleValue;

	private String dateValue;

	private int type = TYPE_STRING;

	private int mandatory = 0;

	private int position = 0;

	private String label;

	private int editor = 0;

	public WSAttribute() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return getName() + (getValue() != null ? (" - " + getValue().toString()) : "");
	}

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

	public String getDateValue() {
		return dateValue;
	}

	public void setDateValue(String dateValue) {
		this.dateValue = dateValue;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getMandatory() {
		return mandatory;
	}

	public void setMandatory(int mandatory) {
		this.mandatory = mandatory;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
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
		case TYPE_BOOLEAN:
			return getIntValue();
		case TYPE_DOUBLE:
			return getDoubleValue();
		case TYPE_DATE:
			return AbstractService.convertStringToDate(getDateValue());
		case TYPE_USER:
			return getIntValue();
		}
		return null;
	}

	/**
	 * Sets the attribute value. It can be as String, Long, Double or Date.
	 * 
	 * @param value The attribute value.
	 */
	public void setValue(Object value) {
		if (getType() == WSAttribute.TYPE_USER && !(value instanceof WSUser)) {
			/*
			 * Needed to fix JAXB logic that will invoke getValue(that returns a
			 * Long) and setValue
			 */
			if (value instanceof Long)
				this.intValue = (Long) value;
			else if (value instanceof String)
				this.stringValue = (String) value;
			return;
		}

		if (value instanceof String) {
			this.type = TYPE_STRING;
			setStringValue((String) value);
		} else if (value instanceof Long) {
			this.type = TYPE_INT;
			setIntValue((Long) value);
		} else if (value instanceof Integer) {
			this.type = TYPE_INT;
			if (value != null)
				setIntValue(((Integer) value).longValue());
		} else if (value instanceof Boolean) {
			setIntValue(((Boolean) value).booleanValue() ? 1L : 0L);
			this.type = TYPE_BOOLEAN;
		} else if (value instanceof Double) {
			this.type = TYPE_DOUBLE;
			setDoubleValue((Double) value);
		} else if (value instanceof Date) {
			this.type = TYPE_DATE;
			setDateValue(AbstractService.convertDateToString((Date) value));
		} else if (value instanceof WSUser) {
			this.stringValue = ((WSUser) value).getFullName();
			this.intValue = ((WSUser) value).getId();
			this.type = TYPE_USER;
		} else {
			this.type = TYPE_DATE;
			if (value != null) {
				XMLGregorianCalendar theXGCal = (XMLGregorianCalendar) value;
				GregorianCalendar theGCal = theXGCal.toGregorianCalendar();
				Date theDate = theGCal.getTime();
				setDateValue(AbstractService.convertDateToString((Date) theDate));
			} else
				setDateValue(null);
		}
	}

	public int getEditor() {
		return editor;
	}

	public void setEditor(int editor) {
		this.editor = editor;
	}
}