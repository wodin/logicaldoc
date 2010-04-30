package com.logicaldoc.gui.frontend.client.panels;

import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.beans.GUIEvent;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * Shows the events list
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class EventsWindow extends Window {

	private static EventsWindow instance = new EventsWindow();

	private ListGrid grid;

	public EventsWindow() {
		super();

		HeaderControl trash = new HeaderControl(HeaderControl.TRASH, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				grid.setData(new ListGridRecord[0]);
			}
		});

		setHeaderControls(HeaderControls.HEADER_LABEL, trash, HeaderControls.CLOSE_BUTTON);
		setWidth(400);
		setHeight(200);
		setTitle(I18N.getMessage("lastevents"));
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		grid = new ListGrid() {

			@Override
			protected String getCellCSSText(ListGridRecord record, int rowNum, int colNum) {
				if (GUIEvent.ERROR.equals(record.getAttribute("severity")))
					return "color: #EF4A4A";
				if (GUIEvent.WARNING.equals(record.getAttribute("severity")))
					return "color: #FFBE0F";
				else
					return "color: #577ED0";
			}
		};
		grid.setWidth100();
		grid.setHeight100();
		grid.setCanReorderFields(false);
		grid.setCanFreezeFields(false);
		grid.setCanGroupBy(false);

		ListGridField date = new ListGridField("date", I18N.getMessage("date"), 110);
		date.setAlign(Alignment.CENTER);
		date.setType(ListGridFieldType.DATE);
		date.setCellFormatter(new DateCellFormatter());

		ListGridField detail = new ListGridField("detail", I18N.getMessage("detail"), 300);
		detail.setCanSort(false);

		ListGridField severityLabel = new ListGridField("severityLabel", I18N.getMessage("severity"), 60);

		grid.setFields(date, severityLabel, detail);
		grid.setCanResizeFields(true);
		addItem(grid);
	}

	public void addEvent(GUIEvent event) {
		ListGridRecord record = new ListGridRecord();
		record.setAttribute("date", event.getDate());
		record.setAttribute("detail", event.getDetail());
		record.setAttribute("severity", event.getSeverity());
		record.setAttribute("severityLabel", I18N.getMessage(event.getSeverity()));
		grid.addData(record);
		grid.sort("date", SortDirection.DESCENDING);
	}

	public static EventsWindow get() {
		return instance;
	}
}