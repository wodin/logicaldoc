package com.logicaldoc.core.document;

import com.logicaldoc.core.ExtensibleObject;

/**
 * A template simply collects a set of attribute names
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class DocumentTemplate extends ExtensibleObject {

	public static int TYPE_DEFAULT = 0;

	public static int TYPE_AOS = 1;

	public static int CATEGORY_GENERIC = 0;

	public static int CATEGORY_ACTIVE_INVOICE = 1;

	public static int CATEGORY_PASSIVE_INVOICE = 2;
	
	public static int CATEGORY_DDT = 3;

	public static int CATEGORY_CONTRACT = 4;

	private String name;

	private String description;

	private int readonly = 0;

	private int type = TYPE_DEFAULT;

	private int category = CATEGORY_GENERIC;

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

	public int getReadonly() {
		return readonly;
	}

	public void setReadonly(int readonly) {
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
}