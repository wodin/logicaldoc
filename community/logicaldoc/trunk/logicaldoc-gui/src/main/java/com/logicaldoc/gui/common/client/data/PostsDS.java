package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.Session;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceDateTimeField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Data source to handle documents grid lists. It is based on Xml parsing
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class PostsDS extends DataSource {
	public PostsDS(Long discussionId, Long userId) {
		setRecordXPath("/list/post");
		DataSourceTextField title = new DataSourceTextField("title");
		DataSourceTextField id = new DataSourceTextField("id");
		id.setPrimaryKey(true);
		id.setHidden(true);
		id.setRequired(true);
		DataSourceTextField user = new DataSourceTextField("user");
		DataSourceIntegerField indent = new DataSourceIntegerField("indent");
		DataSourceTextField message = new DataSourceTextField("message");
		DataSourceDateTimeField date = new DataSourceDateTimeField("date");
		DataSourceTextField replyPath = new DataSourceTextField("replyPath");
		DataSourceTextField documentId = new DataSourceTextField("docId");
		replyPath.setHidden(true);

		setFields(id, title, user, indent, message, date, replyPath, documentId);
		setClientOnly(true);

		setDataURL("data/posts.xml?sid=" + Session.get().getSid() + "&"
				+ (discussionId != null ? ("discussionId=" + discussionId) : ("userId=" + userId)));
	}
}