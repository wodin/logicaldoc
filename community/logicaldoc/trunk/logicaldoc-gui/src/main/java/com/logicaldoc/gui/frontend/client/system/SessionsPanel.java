package com.logicaldoc.gui.frontend.client.system;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.data.SessionsDS;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.frontend.client.services.SecurityService;
import com.logicaldoc.gui.frontend.client.services.SecurityServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

/**
 * Displays a list list of user sessions, allowing the kill operation.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class SessionsPanel extends VLayout {

	private SecurityServiceAsync service = (SecurityServiceAsync) GWT.create(SecurityService.class);

	private ListGrid list;

	public SessionsPanel() {
		refresh();

		HLayout buttons = new HLayout();
		Button refresh = new Button(I18N.getMessage("refresh"));
		buttons.addMember(refresh);
		buttons.setMembersMargin(4);
		buttons.setWidth100();
		buttons.setHeight(15);
		refresh.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				removeMember(list);
				refresh();
			}
		});

		setMembersMargin(5);
		addMember(buttons);
	}

	private void refresh() {
		ListGridField sid = new ListGridField("sid", I18N.getMessage("sid"), 250);

		ListGridField username = new ListGridField("username", I18N.getMessage("username"), 80);
		username.setCanFilter(true);

		ListGridField created = new ListGridField("created", I18N.getMessage("createdon"), 110);
		created.setAlign(Alignment.CENTER);
		created.setType(ListGridFieldType.DATE);
		created.setCellFormatter(new DateCellFormatter());
		created.setCanFilter(false);

		ListGridField renew = new ListGridField("renew", I18N.getMessage("lastrenew"), 110);
		renew.setAlign(Alignment.CENTER);
		renew.setType(ListGridFieldType.DATE);
		renew.setCellFormatter(new DateCellFormatter());
		renew.setCanFilter(false);

		ListGridField statusLabel = new ListGridField("statusLabel", I18N.getMessage("status"), 80);
		statusLabel.setCanFilter(false);

		list = new ListGrid() {
			@Override
			protected String getCellCSSText(ListGridRecord record, int rowNum, int colNum) {
				if (getFieldName(colNum).equals("sid")) {
					if (Session.get().getSid().equals(record.getAttribute("sid"))) {
						return "font-weight: bold;";
					} else {
						return super.getCellCSSText(record, rowNum, colNum);
					}
				} else if (getFieldName(colNum).equals("statusLabel")) {
					if (!"0".equals(record.getAttribute("status"))) {
						return "color: red;";
					} else {
						return super.getCellCSSText(record, rowNum, colNum);
					}
				} else {
					return super.getCellCSSText(record, rowNum, colNum);
				}
			}
		};

		list.setShowRecordComponents(true);
		list.setShowRecordComponentsByCell(true);
		list.setCanFreezeFields(true);
		list.setAutoFetchData(true);
		list.setSelectionType(SelectionStyle.SINGLE);
		list.setDataSource(new SessionsDS());
		list.invalidateCache();
		list.setFields(sid, statusLabel, username, created, renew);

		list.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				showContextMenu();
				event.cancel();
			}
		});

		addMember(list, 0);
	}

	private void showContextMenu() {
		Menu contextMenu = new Menu();

		MenuItem killSession = new MenuItem();
		killSession.setTitle(I18N.getMessage("kill"));
		killSession.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				SC.ask(I18N.getMessage("question"), I18N.getMessage("confirmkill"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							ListGridRecord record = list.getSelectedRecord();
							service.kill(record.getAttributeAsString("sid"), new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(Void result) {
									list.getSelectedRecord().setAttribute("statusLabel", "Closed");
									list.getSelectedRecord().setAttribute("status", "1");
									list.updateData(list.getSelectedRecord());
								}
							});
						}
					}
				});
			}
		});

		if (!"0".equals(list.getSelectedRecord().getAttributeAsString("status"))
				|| Session.get().getSid().equals(list.getSelectedRecord().getAttributeAsString("sid")))
			killSession.setEnabled(false);

		contextMenu.setItems(killSession);
		contextMenu.showContextMenu();
	}
}
