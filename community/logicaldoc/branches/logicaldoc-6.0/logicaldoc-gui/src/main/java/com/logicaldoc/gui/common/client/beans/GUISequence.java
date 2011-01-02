package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;

public class GUISequence implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;

	private String frequency = "";

	private String template = "";

	private int value;

	// If the current sequence year is = 0, it is not a year sequence.
	private int year = 0;

	// If the current sequence month is = 0, it is not a month sequence.
	private int month = 0;

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}