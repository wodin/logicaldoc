package com.logicaldoc.gui.frontend.client.document;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.frontend.client.calendar.DocumentCalendar;
import com.logicaldoc.gui.frontend.client.services.CalendarService;
import com.logicaldoc.gui.frontend.client.services.CalendarServiceAsync;
import com.smartgwt.client.types.ViewName;
import com.smartgwt.client.widgets.calendar.Calendar;

/**
 * This panel shows the calendar events on a document
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.7
 */
public class DocumentCalendarPanel extends DocumentDetailTab {

	protected CalendarServiceAsync service = (CalendarServiceAsync) GWT.create(CalendarService.class);

	protected Calendar calendar = null;

	private Date choosenDate = null;

	private ViewName choosenView = null;

	public DocumentCalendarPanel(final GUIDocument document) {
		super(document, null, null);
		setMembersMargin(1);
	}

	@Override
	public void onTabSelected() {
		long docId = document.getId();
		if (document.getDocRef() != null)
			docId = document.getDocRef();

		calendar = new DocumentCalendar(docId, null, new AsyncCallback<Void>() {

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
		setMembers(calendar);
	}

	public void refresh() {
		if (calendar != null) {
			removeMember(calendar);
			choosenDate = calendar.getChosenDate();
			choosenView = calendar.getCurrentViewName();
		}

		onTabSelected();
	}
}