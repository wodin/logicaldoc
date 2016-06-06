package com.logicaldoc.gui.frontend.client.calendar;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUICalendarEvent;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.data.CalendarEventsDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.frontend.client.services.CalendarService;
import com.logicaldoc.gui.frontend.client.services.CalendarServiceAsync;
import com.smartgwt.client.types.DateDisplayFormat;
import com.smartgwt.client.types.TimeDisplayFormat;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.widgets.calendar.Calendar;
import com.smartgwt.client.widgets.calendar.events.CalendarEventClick;
import com.smartgwt.client.widgets.calendar.events.EventClickHandler;

/**
 * Represents a calendar containing events related to documents.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.1.3
 */
public class DocumentCalendar extends Calendar {

	protected CalendarServiceAsync service = (CalendarServiceAsync) GWT.create(CalendarService.class);

	public DocumentCalendar(Long docId, Date date, final AsyncCallback<Void> onChangeCallback) {
		setDataSource(new CalendarEventsDS(docId));
		setAutoFetchData(true);
		setScrollToWorkday(true);
		setShowDayView(false);
		setDayViewTitle(I18N.message("day"));
		setWeekViewTitle(I18N.message("week"));
		setMonthViewTitle(I18N.message("month"));
		setPreviousButtonHoverText(I18N.message("previous"));
		setNextButtonHoverText(I18N.message("next"));
		setCancelButtonTitle(I18N.message("cancel"));
		setDatePickerHoverText(I18N.message("choosedate"));
		if (I18N.message("format_dateshort").startsWith("MM/dd"))
			setDateFormatter(DateDisplayFormat.TOUSSHORTDATE);
		else
			setDateFormatter(DateDisplayFormat.TOEUROPEANSHORTDATE);
		setTimeFormatter(TimeDisplayFormat.TOSHORT24HOURTIME);
		setCanCreateEvents(false);
		setCanResizeTimelineEvents(false);
		setCanDragEvents(false);
		setCanDragReposition(false);
		setCanDragResize(false);
		setCanDrop(false);
		setCanDrag(false);
		setCanAcceptDrop(false);
		setCanDragScroll(false);
		setCanEditLane(false);
		setCanEditEvents(false);
		setCanRemoveEvents(false);
		if (date != null)
			setChosenDate(date);
		else
			setChosenDate(new Date());

		// this setting setting corrupts tha Calendar if there is high event
		// frequency(daily events)
		// setCurrentViewName(choosenView);

		addEventClickHandler(new EventClickHandler() {

			@Override
			public void onEventClick(final CalendarEventClick event) {
				service.getEvent(Long.parseLong(event.getEvent().getAttribute("eventId")),
						new AsyncCallback<GUICalendarEvent>() {
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(final GUICalendarEvent ev) {
								long creatorId = Long.parseLong(event.getEvent().getAttribute("creatorId"));
								GUIUser currentUser = Session.get().getUser();

								if (ev.getParentId() != null
										&& (currentUser.getId() == creatorId || currentUser.isMemberOf("admin"))) {
									LD.ask(I18N.message("editevent"), I18N.message("douwantmodifyalloccurrences"),
											new BooleanCallback() {
												@Override
												public void execute(final Boolean editAllOccurrences) {
													if (!editAllOccurrences) {
														CalendarEventDialog eventDialog = new CalendarEventDialog(ev,
																onChangeCallback);
														eventDialog.show();
													} else {
														service.getEvent(Long.parseLong(event.getEvent().getAttribute(
																"parentId")), new AsyncCallback<GUICalendarEvent>() {
															public void onFailure(Throwable caught) {
																Log.serverError(caught);
															}

															@Override
															public void onSuccess(GUICalendarEvent calEv) {
																CalendarEventDialog eventDialog = new CalendarEventDialog(
																		calEv, onChangeCallback);
																eventDialog.show();
															}
														});
													}
												}
											});
								} else {
									CalendarEventDialog eventDialog = new CalendarEventDialog(ev, onChangeCallback);
									eventDialog.show();
								}
							}
						});
				event.cancel();
			}
		});
	}
}
