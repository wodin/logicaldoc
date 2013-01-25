package com.logicaldoc.gui.frontend.client.calendar;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUICalendarEvent;
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
import com.smartgwt.client.widgets.calendar.events.CalendarEventRemoveClick;
import com.smartgwt.client.widgets.calendar.events.DateChangedEvent;
import com.smartgwt.client.widgets.calendar.events.DateChangedHandler;
import com.smartgwt.client.widgets.calendar.events.EventClickHandler;
import com.smartgwt.client.widgets.calendar.events.EventRemoveClickHandler;
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

	private Date lastChosenDate = new Date();

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

	@SuppressWarnings("deprecation")
	private void initGUI() {
		calendar = new Calendar();
		calendar.setDataSource(new CalendarEventsDS());
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
		calendar.setScrollToWorkday(true);
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

		calendar.setChosenDate(lastChosenDate);

		calendar.addDateChangedHandler(new DateChangedHandler() {
			@Override
			public void onDateChanged(DateChangedEvent event) {
				lastChosenDate = calendar.getChosenDate();
			}
		});

		calendar.addEventRemoveClickHandler(new EventRemoveClickHandler() {
			@Override
			public void onEventRemoveClick(final CalendarEventRemoveClick event) {
				event.cancel();
				LD.ask(I18N.message("delevent"), I18N.message("deleventconfirm"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							if (event.getEvent().getAttribute("parentId") != null) {
								LD.ask(I18N.message("delevent"), I18N.message("douwantdeletealloccurrences"),
										new BooleanCallback() {
											@Override
											public void execute(Boolean value) {
												Long id = value ? Long.parseLong(event.getEvent().getAttribute(
														"parentId")) : Long.parseLong(event.getEvent().getAttribute(
														"eventId"));
												service.deleteEvent(Session.get().getSid(), id,
														new AsyncCallback<Void>() {
															@Override
															public void onFailure(Throwable caught) {
																Log.serverError(caught);
															}

															@Override
															public void onSuccess(Void arg) {
																refresh();
															}
														});
											}
										});
							} else
								service.deleteEvent(Session.get().getSid(),
										Long.parseLong(event.getEvent().getAttribute("eventId")),
										new AsyncCallback<Void>() {
											@Override
											public void onFailure(Throwable caught) {
												Log.serverError(caught);
											}

											@Override
											public void onSuccess(Void arg) {
												refresh();
											}
										});
						}
					}
				});
			}
		});

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
								if (ev.getParentId() != null)
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
								else {
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
		if (calendar != null)
			removeMember(calendar);
		initGUI();
	}
}