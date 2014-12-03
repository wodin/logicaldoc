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
import com.smartgwt.client.types.ViewName;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.widgets.calendar.Calendar;
import com.smartgwt.client.widgets.calendar.events.CalendarEventClick;
import com.smartgwt.client.widgets.calendar.events.EventClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Calendar dashboard that displays the events in which the user is involved
 * into.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.7
 */
public class CalendarDashboard extends VLayout {
	protected CalendarServiceAsync service = (CalendarServiceAsync) GWT.create(CalendarService.class);

	protected Calendar calendar = null;

	private static CalendarDashboard instance;

	private Date choosenDate = null;

	private ViewName choosenView = null;

	public static CalendarDashboard get() {
		if (instance == null)
			instance = new CalendarDashboard();
		return instance;
	}

	public CalendarDashboard() {
		setWidth100();
		setHeight100();
		refresh();
	}

	private void initGUI() {
		calendar = new Calendar();
		calendar.setDataSource(new CalendarEventsDS(null));
		calendar.setAutoFetchData(true);
		calendar.setScrollToWorkday(true);
		calendar.setShowDayView(false);
		calendar.setDayViewTitle(I18N.message("day"));
		calendar.setWeekViewTitle(I18N.message("week"));
		calendar.setMonthViewTitle(I18N.message("month"));
		calendar.setPreviousButtonHoverText(I18N.message("previous"));
		calendar.setNextButtonHoverText(I18N.message("next"));
		calendar.setCancelButtonTitle(I18N.message("cancel"));
		calendar.setDatePickerHoverText(I18N.message("choosedate"));
		if (I18N.message("format_dateshort").startsWith("MM/dd"))
			calendar.setDateFormatter(DateDisplayFormat.TOUSSHORTDATE);
		else
			calendar.setDateFormatter(DateDisplayFormat.TOEUROPEANSHORTDATE);
		calendar.setTimeFormatter(TimeDisplayFormat.TOSHORT24HOURTIME);
		calendar.setCanCreateEvents(false);
		calendar.setCanResizeTimelineEvents(false);
		calendar.setCanDragEvents(false);
		calendar.setCanDragReposition(false);
		calendar.setCanDragResize(false);
		calendar.setCanDrop(false);
		calendar.setCanDrag(false);
		calendar.setCanAcceptDrop(false);
		calendar.setCanDragScroll(false);
		calendar.setCanEditLane(false);
		calendar.setCanEditEvents(false);
		calendar.setCanRemoveEvents(false);
		if (choosenDate != null)
			calendar.setChosenDate(choosenDate);
		else
			calendar.setChosenDate(new Date());
		
		//this setting setting corrupts tha Calendar if there is high event frequency(daily events)
		//calendar.setCurrentViewName(choosenView);
		
		
		calendar.addEventClickHandler(new EventClickHandler() {

			@Override
			public void onEventClick(final CalendarEventClick event) {
				service.getEvent(Session.get().getSid(), Long.parseLong(event.getEvent().getAttribute("eventId")),
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
														CalendarEventDialog eventDialog = new CalendarEventDialog(ev);
														eventDialog.show();
													} else {
														service.getEvent(Session.get().getSid(), Long.parseLong(event
																.getEvent().getAttribute("parentId")),
																new AsyncCallback<GUICalendarEvent>() {
																	public void onFailure(Throwable caught) {
																		Log.serverError(caught);
																	}

																	@Override
																	public void onSuccess(GUICalendarEvent calEv) {
																		CalendarEventDialog eventDialog = new CalendarEventDialog(
																				calEv);
																		eventDialog.show();
																	}
																});
													}
												}
											});
								} else {
									CalendarEventDialog eventDialog = new CalendarEventDialog(ev);
									eventDialog.show();
								}
							}
						});
				event.cancel();
			}
		});

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