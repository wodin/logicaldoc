package com.logicaldoc.gui.frontend.client.calendar;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.frontend.client.services.CalendarService;
import com.logicaldoc.gui.frontend.client.services.CalendarServiceAsync;
import com.smartgwt.client.types.ViewName;
import com.smartgwt.client.widgets.calendar.Calendar;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Calendar dashboard that displays the events in which the user is involved
 * into.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.7
 */
public class UserCalendarPanel extends VLayout {
	protected CalendarServiceAsync service = (CalendarServiceAsync) GWT.create(CalendarService.class);

	protected Calendar calendar = null;

	private static UserCalendarPanel instance;

	private Date choosenDate = null;

	private ViewName choosenView = null;

	public static UserCalendarPanel get() {
		if (instance == null)
			instance = new UserCalendarPanel();
		return instance;
	}

	public UserCalendarPanel() {
		setWidth100();
		setHeight100();
		refresh();
	}

	private void initGUI() {
		calendar = new DocumentCalendar(null, choosenDate, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(Void arg0) {
				refresh();
			}
		});
		
		calendar.setChosenDate(choosenDate);
		calendar.setCurrentViewName(choosenView);
		
		addMember(calendar);
	}

	public void refresh() {
		if (calendar != null) {
			removeMember(calendar);
			choosenDate = calendar.getChosenDate();
			choosenView = calendar.getCurrentViewName();
		}

		initGUI();
	}
}