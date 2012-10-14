package com.logicaldoc.gui.frontend.client.dashboard;

import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.data.PostsDS;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.document.DocumentsPanel;
import com.smartgwt.client.data.Record;
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
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.layout.Portlet;

/**
 * Portlet specialized in listing the most recent comments of the current user.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class PostsPortlet extends Portlet {

	private PostsDS dataSource;

	private ListGrid list;

	public PostsPortlet() {
		refresh();
	}

	private void refresh() {
		setTitle(I18N.message("lastposts"));

		if (list != null) {
			removeItem(list);
		}

		long userId = Session.get().getUser().getId();

		ListGridField date = new ListGridField("date", I18N.message("date"), 90);
		date.setAlign(Alignment.CENTER);
		date.setType(ListGridFieldType.DATE);
		date.setCellFormatter(new DateCellFormatter(true));
		date.setCanFilter(false);
		ListGridField title = new ListGridField("title", I18N.message("title"));

		list = new ListGrid();
		list.setEmptyMessage(I18N.message("notitemstoshow"));
		list.setCanFreezeFields(true);
		list.setAutoFetchData(true);
		list.setShowHeader(false);
		list.setCanSelectAll(false);
		list.setSelectionType(SelectionStyle.NONE);
		list.setHeight100();
		list.setBorder("0px");
		dataSource = new PostsDS(userId);
		list.setDataSource(dataSource);
		list.setFields(date, title);

		list.addCellDoubleClickHandler(new CellDoubleClickHandler() {
			@Override
			public void onCellDoubleClick(CellDoubleClickEvent event) {
				Record record = event.getRecord();
				DocumentsPanel.get().openInFolder(Long.parseLong(record.getAttributeAsString("docId")));
			}
		});

		setDragAppearance(DragAppearance.OUTLINE);
		setDragOpacity(30);
		setCanDrag(false);
		setCanDrop(false);
		HeaderControl refresh = new HeaderControl(HeaderControl.REFRESH, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				refresh();
			}
		});

		HeaderIcon portletIcon = ItemFactory.newHeaderIcon("tag_blue.png");
		HeaderControl hcicon = new HeaderControl(portletIcon);
		hcicon.setSize(16);

		setHeaderControls(hcicon, HeaderControls.HEADER_LABEL, refresh);

		addItem(list);
	}

	@Override
	public void destroy() {
		super.destroy();
		if (dataSource != null)
			dataSource.destroy();
	}
}