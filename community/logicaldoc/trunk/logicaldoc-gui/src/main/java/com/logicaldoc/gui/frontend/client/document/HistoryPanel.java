package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.user.client.Window;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.data.DocumentHistoryDS;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;

/**
 * This panel shows the history of a document
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class HistoryPanel extends DocumentDetailTab {

	private DocumentHistoryDS dataSource;

	private ListGrid listGrid;

	public HistoryPanel(final GUIDocument document) {
		super(document, null);

		ListGridField user = new ListGridField("user", I18N.getMessage("user"), 100);
		ListGridField event = new ListGridField("event", I18N.getMessage("event"), 200);
		ListGridField version = new ListGridField("version", I18N.getMessage("version"), 70);
		ListGridField date = new ListGridField("date", I18N.getMessage("date"), 110);
		date.setAlign(Alignment.CENTER);
		date.setType(ListGridFieldType.DATE);
		date.setCellFormatter(new DateCellFormatter());
		ListGridField comment = new ListGridField("comment", I18N.getMessage("comment"));
		ListGridField title = new ListGridField("title", I18N.getMessage("title"));
		ListGridField path = new ListGridField("path", I18N.getMessage("path"));
		ListGridField sid = new ListGridField("sid", I18N.getMessage("sid"));

		listGrid = new ListGrid();
		listGrid.setCanFreezeFields(true);
		listGrid.setAutoFetchData(true);
		dataSource = new DocumentHistoryDS(document.getId(), null);
		listGrid.setDataSource(dataSource);
		listGrid.setFields(user, event, date, comment, version, title, path, sid);
		addMember(listGrid);

		listGrid.addCellDoubleClickHandler(new CellDoubleClickHandler() {
			@Override
			public void onCellDoubleClick(CellDoubleClickEvent event) {
				ListGridRecord record = event.getRecord();
				Window.open("download?sid=" + Session.get().getSid() + "&docId=" + document.getId() + "&versionId="
						+ record.getAttribute("version") + "&open=true", "_blank", "");
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