package com.logicaldoc.gui.frontend.client.dashboard;

import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.data.DocumentHistoryDS;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DragAppearance;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.LayoutPolicy;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.HeaderControl;
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

	public HistoryPortlet(final String eventCode) {
		long userId = Session.get().getUser().getId();

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
				if (!record.getAttributeAsBoolean("checked")) {
					return "font-style: bold;";
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
		dataSource = new DocumentHistoryDS(userId, eventCode, null);
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
		// setCanDrop(true);
		setDragOpacity(30);
		setVPolicy(LayoutPolicy.NONE);
		setOverflow(Overflow.VISIBLE);
		setHeaderControls(HeaderControls.MINIMIZE_BUTTON, HeaderControls.HEADER_LABEL, new HeaderControl(
				HeaderControl.SETTINGS));
		addItem(list);

		// Count the total of events and the total of unchecked events
		list.addDataArrivedHandler(new DataArrivedHandler() {
			@Override
			public void onDataArrived(DataArrivedEvent event) {
				ListGridRecord[] records = list.getRecords();
				int count = 0;
				int unchecked = 0;
				for (ListGridRecord record : records) {
					if (!record.getAttributeAsBoolean("checked"))
						unchecked++;
					count++;
				}
				String title = I18N.getMessage(eventCode + "docs", Integer.toString(count));
				if (unchecked > 0)
					title = "<b>" + title + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + I18N.getMessage("news")
							+ ": " + unchecked + "</b>";
				setTitle(title);
			}

		});
	}

	@Override
	public void destroy() {
		super.destroy();
		if (dataSource != null)
			dataSource.destroy();
	}
}