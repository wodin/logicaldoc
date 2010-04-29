package com.logicaldoc.gui.common.client.data;

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

	private TagsDS() {
		setTitleField("word");
		setRecordXPath("/list/tag");
		DataSourceTextField tagField = new DataSourceTextField("word");
		setFields(tagField);
		setDataURL("data/tags.xml");
		setClientOnly(true);
	}

	public static TagsDS getInstance() {
		if (instance == null)
			instance = new TagsDS();
		return instance;
	}
}