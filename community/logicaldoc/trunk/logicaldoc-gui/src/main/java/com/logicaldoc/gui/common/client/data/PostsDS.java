package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.Session;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceDateTimeField;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Data source to handle documents grid lists. It is based on Xml parsing
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class PostsDS extends DataSource {
	public PostsDS(Long userId) {
		setRecordXPath("/list/post");
		DataSourceTextField id = new DataSourceTextField("id");
		id.setPrimaryKey(true);
		id.setHidden(true);
		id.setRequired(true);
		DataSourceTextField user = new DataSourceTextField("user");
		DataSourceTextField message = new DataSourceTextField("message");
		DataSourceDateTimeField date = new DataSourceDateTimeField("date");
		DataSourceTextField documentId = new DataSourceTextField("docId");
		DataSourceTextField docTitle = new DataSourceTextField("docTitle");
		
		setFields(id, user, message, date, documentId, docTitle);
		setClientOnly(true);

		setDataURL("data/posts.xml?sid=" + Session.get().getSid() + "&" + "userId=" + userId);
	}
}