package com.logicaldoc.gui.common.client.data;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Data source to show notes in the posts portlet. It is based on Xml parsing
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class EventsDS extends DataSource {
	public EventsDS(String locale, boolean folder, boolean workflow, boolean user) {
		setRecordXPath("/list/event");
		DataSourceTextField code = new DataSourceTextField("code");
		code.setPrimaryKey(true);
		code.setHidden(true);
		code.setRequired(true);
		DataSourceTextField type = new DataSourceTextField("type");
		DataSourceTextField label = new DataSourceTextField("label");

		setFields(code, type, label);
		setClientOnly(true);

		setDataURL("data/events.xml?locale=" + locale + "&folder=" + folder + "&workflow=" + "&user=" + user);
	}
}