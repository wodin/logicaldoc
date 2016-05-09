package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;

public class GUITemplate implements Serializable {

	private static final long serialVersionUID = 1L;

	public static int TYPE_DEFAULT = 0;

	public static int TYPE_AOS = 1;

	public static int CATEGORY_GENERIC = 0;

	public static int CATEGORY_ACTIVE_INVOICE = 1;

	public static int CATEGORY_PASSIVE_INVOICE = 2;

	public static int CATEGORY_DDT = 3;

	public static int CATEGORY_CONTRACT = 4;

	public static int SIGNED_NO = 0;

	public static int SIGNED_REQUIRED = 1;

	private long id = 0;

	private String name;

	private String description;

	private boolean readonly = false;

	private int type = TYPE_DEFAULT;

	private int category = CATEGORY_GENERIC;

	private GUIExtendedAttribute[] attributes;

	private int signRequired = SIGNED_NO;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

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

	public GUIExtendedAttribute[] getAttributes() {
		return attributes;
	}

	public void setAttributes(GUIExtendedAttribute[] attributes) {
		this.attributes = attributes;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public int getSignRequired() {
		return signRequired;
	}

	public void setSignRequired(int signRequired) {
		this.signRequired = signRequired;
	}
}
