package com.logicaldoc.gui.frontend.client.dashboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.data.DocumentHistoryDS;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DragAppearance;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.grid.events.DataArrivedEvent;
import com.smartgwt.client.widgets.grid.events.DataArrivedHandler;
import com.smartgwt.client.widgets.layout.Portlet;

/**
 * Portlet specialized in listing the
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class HistoryPortlet extends Portlet {
	private DocumentHistoryDS dataSource;

	private ListGrid list;

	private DocumentServiceAsync service = (DocumentServiceAsync) GWT.create(DocumentService.class);

	public HistoryPortlet(final String eventCode) {
		long userId = Session.get().getUser().getId();
		int maxRows = 1000;
		if (Constants.EVENT_DOWNLOADED.equals(eventCode) || Constants.EVENT_CHECKEDIN.equals(eventCode)
				|| Constants.EVENT_CHANGED.equals(eventCode))
			maxRows = 10;

		ListGridField version = new ListGridField("version", I18N.getMessage("version"), 70);
		ListGridField date = new ListGridField("date", I18N.getMessage("date"), 110);
		date.setAlign(Alignment.CENTER);
		date.setType(ListGridFieldType.DATE);
		date.setCellFormatter(new DateCellFormatter());
		ListGridField title = new ListGridField("title", I18N.getMessage("title"));
		ListGridField icon = new ListGridField("icon", " ", 24);
		icon.setType(ListGridFieldType.IMAGE);
		icon.setCanSort(false);
		icon.setAlign(Alignment.CENTER);
		icon.setShowDefaultContextMenu(false);
		icon.setImageURLPrefix(Util.imagePrefix() + "/application/");
		icon.setImageURLSuffix(".png");
		icon.setCanFilter(false);

		list = new ListGrid() {
			@Override
			protected String getCellCSSText(ListGridRecord record, int rowNum, int colNum) {
				if (!record.getAttributeAsBoolean("new")) {
					return "font-weight: bold;";
				} else {
					return super.getCellCSSText(record, rowNum, colNum);
				}
			}
		};
		list.setCanFreezeFields(true);
		list.setAutoFetchData(true);
		list.setShowHeader(false);
		list.setCanSelectAll(false);
		list.setSelectionType(SelectionStyle.NONE);
		list.setHeight100();
		list.setBorder("0px");
		dataSource = new DocumentHistoryDS(userId, eventCode, maxRows);
		list.setDataSource(dataSource);
		list.setFields(icon, title, version, date);

		list.addCellDoubleClickHandler(new CellDoubleClickHandler() {
			@Override
			public void onCellDoubleClick(CellDoubleClickEvent event) {
				SC.say("dbclick");
				// ListGridRecord record = event.getRecord();
				// Window.open("download?sid=" + Session.get().getSid() +
				// "&docId=" + document.getId() + "&versionId="
				// + record.getAttribute("version") + "&open=true", "_blank",
				// "");
			}
		});

		setShowShadow(true);
		setAnimateMinimize(true);
		setDragAppearance(DragAppearance.OUTLINE);
		setDragOpacity(30);

		HeaderControl markAsRead = new HeaderControl(HeaderControl.TRASH, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				service.markHistoryAsRead(Session.get().getSid(), eventCode, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void ret) {
						RecordList l = list.getRecordList();
						for (int i = 0; i < list.getTotalRows(); i++) {
							l.get(i).setAttribute("new", false);
						}
						list.redraw();
						setTitle(I18N.getMessage(eventCode + "docs", Integer.toString(list.getTotalRows())));
					}
				});
			}
		});

		setHeaderControls(HeaderControls.MINIMIZE_BUTTON, HeaderControls.HEADER_LABEL, markAsRead);

		// Count the total of events and the total of unchecked events
		list.addDataArrivedHandler(new DataArrivedHandler() {
			@Override
			public void onDataArrived(DataArrivedEvent event) {
				Record[] records = list.getRecordList().toArray();
				int unread = 0;
				for (Record record : records) {
					if (record.getAttributeAsBoolean("new"))
						unread++;
				}

				String title = I18N.getMessage(eventCode + "docs", Integer.toString(list.getTotalRows()));
				if (unread > 0)
					title = "<b>" + title + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + I18N.getMessage("news")
							+ ": " + unread + "</b>";
				setTitle(title);
			}
		});

		addItem(list);
	}

	@Override
	public void destroy() {
		super.destroy();
		if (dataSource != null)
			dataSource.destroy();
	}
}