package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.Session;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Datasource to retrieve all documents tags. It is based on Xml parsing.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class TagsDS extends DataSource {
	private static TagsDS instance;

	public TagsDS(String firstLetter) {
		setTitleField("word");
		setRecordXPath("/list/tag");
		DataSourceTextField word = new DataSourceTextField("word");
		word.setPrimaryKey(true);
		DataSourceTextField count = new DataSourceTextField("count");
		setFields(word, count);
		setDataURL("data/tags.xml?sid=" + Session.get().getSid()
				+ (firstLetter != null ? "&firstLetter=" + firstLetter.charAt(0) : ""));
		setClientOnly(true);
	}

	public static TagsDS getInstance() {
		if (instance == null)
			instance = new TagsDS(null);
		return instance;
	}
}