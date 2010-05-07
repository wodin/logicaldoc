package com.logicaldoc.gui.frontend.client.security;

import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.data.UserHistoryDS;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This panel shows the history of a user
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class UserHistoryPanel extends VLayout {

	private UserHistoryDS dataSource;

	private ListGrid listGrid;

	public UserHistoryPanel(long userId) {
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
		dataSource = new UserHistoryDS(userId);
		listGrid.setDataSource(dataSource);
		listGrid.setFields(event, date, comment, version, title, path, sid);
		addMember(listGrid);
	}

	@Override
	public void destroy() {
		super.destroy();
		if (dataSource != null)
			dataSource.destroy();
	}
}