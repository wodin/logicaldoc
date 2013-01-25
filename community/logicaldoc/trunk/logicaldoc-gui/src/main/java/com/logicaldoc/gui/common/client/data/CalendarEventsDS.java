package com.logicaldoc.gui.common.client.data;

import com.logicaldoc.gui.common.client.Session;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceDateTimeField;
import com.smartgwt.client.data.fields.DataSourceSequenceField;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Data source to retrieve calendar events.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.7
 */
public class CalendarEventsDS extends DataSource {
	public CalendarEventsDS() {
		setRecordXPath("/list/event");
		DataSourceSequenceField id = new DataSourceSequenceField("eventId");
		id.setPrimaryKey(true);

		DataSourceTextField title = new DataSourceTextField("name");
		DataSourceTextField description = new DataSourceTextField("description");
		DataSourceDateTimeField start = new DataSourceDateTimeField("startDate");
		DataSourceDateTimeField end = new DataSourceDateTimeField("endDate");
		DataSourceTextField parentId = new DataSourceTextField("parentId");
		
		setFields(id, title, description, start, end, parentId);
		setClientOnly(true);
		setDataURL("data/calendarevents.xml?sid=" + Session.get().getSid());
	}
}