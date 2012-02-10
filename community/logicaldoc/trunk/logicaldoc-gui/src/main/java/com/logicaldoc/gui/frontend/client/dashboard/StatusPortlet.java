package com.logicaldoc.gui.frontend.client.dashboard;

import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.data.DocumentsDS;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.document.DocumentsPanel;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.HeaderControl.HeaderIcon;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.grid.events.DataArrivedEvent;
import com.smartgwt.client.widgets.grid.events.DataArrivedHandler;
import com.smartgwt.client.widgets.layout.Portlet;

public class StatusPortlet extends Portlet {

	private DocumentsDS dataSource;

	private ListGrid list;

	public StatusPortlet(final String eventCode) {
		setCanDrag(false);
		setCanDrop(false);

		int max = 10;
		int status = 0;
		String icn = "blank.gif";
		if (eventCode.equals(Constants.EVENT_CHECKEDOUT)) {
			icn = "page_edit.png";
			status = Constants.DOC_CHECKED_OUT;
		} else if (eventCode.equals(Constants.EVENT_LOCKED)) {
			icn = "page_white_lock.png";
			status = Constants.DOC_LOCKED;
		}
		HeaderIcon portletIcon = ItemFactory.newHeaderIcon(icn);
		HeaderControl hcicon = new HeaderControl(portletIcon);
		hcicon.setSize(16);
		setHeaderControls(hcicon, HeaderControls.HEADER_LABEL);

		ListGridField version = new ListGridField("version", I18N.message("version"), 70);
		ListGridField lastModified = new ListGridField("lastModified", I18N.message("date"), 110);
		lastModified.setAlign(Alignment.CENTER);
		lastModified.setType(ListGridFieldType.DATE);
		lastModified.setCellFormatter(new DateCellFormatter(false));
		lastModified.setCanFilter(false);
		ListGridField title = new ListGridField("title", I18N.message("title"));
		ListGridField icon = new ListGridField("icon", " ", 24);
		icon.setType(ListGridFieldType.IMAGE);
		icon.setCanSort(false);
		icon.setAlign(Alignment.CENTER);
		icon.setShowDefaultContextMenu(false);
		icon.setImageURLPrefix(Util.imagePrefix());
		icon.setImageURLSuffix(".png");
		icon.setCanFilter(false);

		list = new ListGrid();
		list.setEmptyMessage(I18N.message("notitemstoshow"));
		list.setCanFreezeFields(true);
		list.setAutoFetchData(true);
		list.setShowHeader(false);
		list.setCanSelectAll(false);
		list.setSelectionType(SelectionStyle.NONE);
		list.setHeight100();
		list.setBorder("0px");
		dataSource = new DocumentsDS(status, max);
		list.setDataSource(dataSource);
		list.setFields(icon, title, version, lastModified);

		list.addCellDoubleClickHandler(new CellDoubleClickHandler() {
			@Override
			public void onCellDoubleClick(CellDoubleClickEvent event) {
				Record record = event.getRecord();
				DocumentsPanel.get().openInFolder(Long.parseLong(record.getAttributeAsString("folderId")),
						Long.parseLong(record.getAttributeAsString("id")));
			}
		});

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

				int total = list.getTotalRows();
				String title = I18N.message(eventCode + "docs", Integer.toString(total));
				if (unread > 0)
					title = "<b>" + title + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + I18N.message("newitems")
							+ ": " + unread + "</b>";
				setTitle(title);

				if (Constants.EVENT_LOCKED.equals(eventCode))
					Session.get().getUser().setLockedDocs(total);
				else if (Constants.EVENT_CHECKEDOUT.equals(eventCode))
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
