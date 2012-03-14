package com.logicaldoc.gui.frontend.client.dashboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.data.DocumentHistoryDS;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.document.DocumentsPanel;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DragAppearance;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.HeaderControl.HeaderIcon;
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
 * Portlet specialized in listing history records
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class HistoryPortlet extends Portlet {

	private DocumentHistoryDS dataSource;

	private ListGrid list;

	private DocumentServiceAsync service = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private String event = "";

	public HistoryPortlet(final String eventCode) {
		this.event = eventCode;
		refresh();
	}

	private void refresh() {
		if (list != null) {
			removeItem(list);
		}

		long userId = Session.get().getUser().getId();
		int max = 1000;
		if (Constants.EVENT_DOWNLOADED.equals(event) || Constants.EVENT_CHECKEDIN.equals(event)
				|| Constants.EVENT_CHANGED.equals(event))
			max = 10;

		ListGridField version = new ListGridField("version", I18N.message("version"), 70);
		ListGridField date = new ListGridField("date", I18N.message("date"), 110);
		date.setAlign(Alignment.CENTER);
		date.setType(ListGridFieldType.DATE);
		date.setCellFormatter(new DateCellFormatter(false));
		date.setCanFilter(false);
		ListGridField title = new ListGridField("title", I18N.message("title"));
		ListGridField icon = new ListGridField("icon", " ", 24);
		icon.setType(ListGridFieldType.IMAGE);
		icon.setCanSort(false);
		icon.setAlign(Alignment.CENTER);
		icon.setShowDefaultContextMenu(false);
		icon.setImageURLPrefix(Util.imagePrefix());
		icon.setImageURLSuffix(".png");
		icon.setCanFilter(false);

		list = new ListGrid() {
			@Override
			protected String getCellCSSText(ListGridRecord record, int rowNum, int colNum) {
				if (record.getAttributeAsBoolean("new")) {
					return "font-weight: bold;";
				} else {
					return super.getCellCSSText(record, rowNum, colNum);
				}
			}
		};
		list.setEmptyMessage(I18N.message("notitemstoshow"));
		list.setCanFreezeFields(true);
		list.setAutoFetchData(true);
		list.setShowHeader(false);
		list.setCanSelectAll(false);
		list.setSelectionType(SelectionStyle.NONE);
		list.setHeight100();
		list.setBorder("0px");
		dataSource = new DocumentHistoryDS(userId, event, max);
		list.setDataSource(dataSource);
		list.setFields(icon, title, version, date);

		list.addCellDoubleClickHandler(new CellDoubleClickHandler() {
			@Override
			public void onCellDoubleClick(CellDoubleClickEvent event) {
				Record record = event.getRecord();
				DocumentsPanel.get().openInFolder(Long.parseLong(record.getAttributeAsString("folderId")),
						Long.parseLong(record.getAttributeAsString("docId")));
			}
		});

		setDragAppearance(DragAppearance.OUTLINE);
		setDragOpacity(30);
		setCanDrag(false);
		setCanDrop(false);

		HeaderControl markAsRead = new HeaderControl(HeaderControl.TRASH, new ClickHandler() {
			@Override
			public void onClick(ClickEvent e) {
				service.markHistoryAsRead(Session.get().getSid(), event, new AsyncCallback<Void>() {

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
						setTitle(I18N.message(event + "docs", Integer.toString(list.getTotalRows())));
					}
				});
			}
		});

		HeaderControl refresh = new HeaderControl(HeaderControl.REFRESH, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				refresh();
			}
		});

		String icn = "blank.gif";
		if (event.equals(Constants.EVENT_CHECKEDOUT))
			icn = "page_edit.png";
		else if (event.equals(Constants.EVENT_LOCKED))
			icn = "page_white_lock.png";
		else if (event.equals(Constants.EVENT_DOWNLOADED))
			icn = "download.png";
		else if (event.equals(Constants.EVENT_CHECKEDIN))
			icn = "page_white_get.png";
		else if (event.equals(Constants.EVENT_CHANGED))
			icn = "edit.png";

		HeaderIcon portletIcon = ItemFactory.newHeaderIcon(icn);
		HeaderControl hcicon = new HeaderControl(portletIcon);
		hcicon.setSize(16);

		setHeaderControls(hcicon, HeaderControls.HEADER_LABEL, markAsRead, refresh);

		// Count the total of events and the total of unchecked events
		list.addDataArrivedHandler(new DataArrivedHandler() {
			@Override
			public void onDataArrived(DataArrivedEvent e) {
				Record[] records = list.getRecordList().toArray();
				int unread = 0;
				for (Record record : records) {
					if (record.getAttributeAsBoolean("new"))
						unread++;
				}

				int total = list.getTotalRows();
				String title = I18N.message(event + "docs", Integer.toString(total));
				if (unread > 0)
					title = "<b>" + title + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + I18N.message("newitems")
							+ ": " + unread + "</b>";
				setTitle(title);

				if (Constants.EVENT_LOCKED.equals(event))
					Session.get().getUser().setLockedDocs(total);
				else if (Constants.EVENT_CHECKEDOUT.equals(event))
					Session.get().getUser().setCheckedOutDocs(total);
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