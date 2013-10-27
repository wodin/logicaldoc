package com.logicaldoc.gui.frontend.client.document;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.data.CalendarEventsDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.services.CalendarService;
import com.logicaldoc.gui.frontend.client.services.CalendarServiceAsync;
import com.smartgwt.client.types.DateDisplayFormat;
import com.smartgwt.client.types.TimeDisplayFormat;
import com.smartgwt.client.widgets.calendar.Calendar;
import com.smartgwt.client.widgets.calendar.CalendarEvent;

/**
 * This panel shows the calendar events on a document
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.7
 */
public class DocumentCalendarPanel extends DocumentDetailTab {

	protected CalendarServiceAsync service = (CalendarServiceAsync) GWT.create(CalendarService.class);

	protected Calendar calendar = null;

	public DocumentCalendarPanel(final GUIDocument document) {
		super(document, null, null);
		setMembersMargin(1);
	}

	@Override
	protected void onTabSelected() {
		calendar = new Calendar() {
			@Override
			protected String getDayBodyHTML(Date date, CalendarEvent[] events, Calendar calendar, int rowNum, int colNum) {
				String returnStr = date.getDate() + "";
				if (events != null && events.length > 0) {
					returnStr += Util.imageHTML("approved.png", 16, 16, "margin-left:8px; margin-top:6px");
				}
				return returnStr;
			}
		};

		long docId = document.getId();
		if (document.getDocRef() != null)
			docId = document.getDocRef();
		calendar.setDataSource(new CalendarEventsDS(docId));

		calendar.setAutoFetchData(true);
		calendar.setDayViewTitle(I18N.message("day"));
		calendar.setWeekViewTitle(I18N.message("week"));
		calendar.setMonthViewTitle(I18N.message("month"));
		calendar.setPreviousButtonHoverText(I18N.message("previous"));
		calendar.setNextButtonHoverText(I18N.message("next"));
		calendar.setCancelButtonTitle(I18N.message("cancel"));
		calendar.setDatePickerHoverText(I18N.message("choosedate"));
		calendar.setDateFormatter(DateDisplayFormat.TOEUROPEANSHORTDATETIME);
		calendar.setTimeFormatter(TimeDisplayFormat.TOSHORT24HOURTIME);

		calendar.setShowDayView(false);
		calendar.setShowWeekView(false);
		calendar.setShowOtherDays(false);
		calendar.setShowDayHeaders(false);
		calendar.setShowDatePickerButton(false);
		calendar.setShowAddEventButton(false);
		calendar.setDisableWeekends(false);
		calendar.setShowDateChooser(false);
		calendar.setCanCreateEvents(false);

		calendar.setWidth(490);
		calendar.setHeight(180);

		setMembers(calendar);
	}
}