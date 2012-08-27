package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.Session;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceDateTimeField;
import com.smartgwt.client.data.fields.DataSourceImageField;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Datasource to retrieve the subscriptions of the current user.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class SubscriptionsDS extends DataSource {

	public SubscriptionsDS() {
		setTitleField("name");
		setRecordXPath("/list/subscription");
		DataSourceTextField id = new DataSourceTextField("id");
		id.setPrimaryKey(true);
		id.setRequired(true);

		DataSourceImageField icon = new DataSourceImageField("icon");
		DataSourceTextField name = new DataSourceTextField("name");
		DataSourceDateTimeField created = new DataSourceDateTimeField("created");
		DataSourceTextField type = new DataSourceTextField("type");
		DataSourceTextField objectId = new DataSourceTextField("objectid");

		setFields(id, icon, name, created, type, objectId);
		setClientOnly(true);
		setDataURL("data/subscriptions.xml?sid=" + Session.get().getSid());
	}
}