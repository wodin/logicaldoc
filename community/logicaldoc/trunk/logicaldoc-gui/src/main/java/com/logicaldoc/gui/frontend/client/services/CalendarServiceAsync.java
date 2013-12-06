package com.logicaldoc.gui.frontend.client.services;

import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUICalendarEvent;

public interface CalendarServiceAsync {
	void saveEvent(String sid, GUICalendarEvent event, AsyncCallback<Void> callback);

	void getEvent(String sid, long eventId, AsyncCallback<GUICalendarEvent> callback);

	void deleteEvent(String sid, long eventId, AsyncCallback<Void> callback);

	void countUserEvents(String sid, Date end, AsyncCallback<Integer> callback);

	void find(String sid, Date startDate, Date endDate, Date expireFrom, Date expireTo, Integer frequency,
			String title, String type, String subtype, Integer status, Integer maxRecords, AsyncCallback<GUICalendarEvent[]> callback);
}
