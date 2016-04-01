package com.logicaldoc.core.searchengine;

import java.util.HashSet;
import java.util.Set;

/**
 * Models the field name that can be stored int the index
 * 
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.5
 */
public enum Fields {
	ID("id"), TITLE("title"), FOLDER_ID("folderId"), CONTENT("content"), TAGS("tags"), TEMPLATE_ID("templateId"), FOLDER_NAME(
			"folderName"), CREATION("creation"), DATE("date"), SOURCE_DATE("sourceDate"), COVERAGE("coverage"), SOURCE_AUTHOR(
			"sourceAuthor"), SOURCE_TYPE("sourceType"), SOURCE("source"), RECIPIENT("recipient"), SOURCE_ID("sourceId"), SIZE(
			"size"), CUSTOM_ID("customId"), DOC_REF("docRef"), COMMENT("comment"), LANGUAGE("language"), TENANT_ID(
			"tenantId");

	private final String name;

	private Fields(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static Set<Fields> all() {
		Set<Fields> fields = new HashSet<Fields>();
		fields.add(CONTENT);
		fields.add(ID);
		fields.add(TITLE);
		fields.add(FOLDER_ID);
		fields.add(TAGS);
		fields.add(TEMPLATE_ID);
		fields.add(FOLDER_NAME);
		fields.add(CREATION);
		fields.add(DATE);
		fields.add(SOURCE_DATE);
		fields.add(COVERAGE);
		fields.add(SOURCE_AUTHOR);
		fields.add(SOURCE);
		fields.add(SOURCE_ID);
		fields.add(RECIPIENT);
		fields.add(CUSTOM_ID);
		fields.add(DOC_REF);
		fields.add(COMMENT);
		fields.add(LANGUAGE);
		fields.add(TENANT_ID);
		return fields;
	}

	/**
	 * Fields list suitable for searches
	 */
	public static String searchList() {
		Set<Fields> fields = all();
		String buf = fields.toString();
		buf = buf.substring(buf.indexOf(',') + 1, buf.length() - 1) + ",ext_*";
		return buf;
	}

	@Override
	public String toString() {
		return name;
	}
}