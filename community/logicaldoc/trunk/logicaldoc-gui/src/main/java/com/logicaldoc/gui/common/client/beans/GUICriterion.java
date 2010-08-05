package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;

public class GUICriterion implements Serializable {

	private static final long serialVersionUID = 1L;

	private String field;

	private String operator;

	private Serializable value;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Serializable getValue() {
		return value;
	}

	public void setValue(Serializable value) {
		this.value = value;
	}
}