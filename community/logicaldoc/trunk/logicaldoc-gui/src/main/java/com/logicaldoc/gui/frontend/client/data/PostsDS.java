package com.logicaldoc.gui.frontend.client.data;

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
	public PostsDS(long discussionId) {
		setID("PostsDS");
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
		replyPath.setHidden(true);

		setFields(id, title, user, indent, message, date, replyPath);
		setClientOnly(true);
		setDataURL("data/posts.xml?sid=" + Session.getInstance().getSid() + "&discussionId=" + discussionId);
	}
}