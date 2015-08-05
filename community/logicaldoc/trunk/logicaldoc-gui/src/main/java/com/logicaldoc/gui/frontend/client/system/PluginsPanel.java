package com.logicaldoc.gui.frontend.client.system;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIValue;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.frontend.client.services.SystemService;
import com.logicaldoc.gui.frontend.client.services.SystemServiceAsync;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Displays a list of plugins available for the application.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.2
 */
public class PluginsPanel extends VLayout {
	private SystemServiceAsync service = (SystemServiceAsync) GWT.create(SystemService.class);

	private ListGrid list;

	public PluginsPanel() {
		setMembersMargin(3);

		ListGridField name = new ListGridField("name", I18N.message("name"), 250);
		name.setCanEdit(false);

		ListGridField version = new ListGridField("version", I18N.message("version"));
		version.setCanEdit(false);

		list = new ListGrid();
		list.setEmptyMessage(I18N.message("notitemstoshow"));
		list.setCanEdit(false);
		list.setWidth100();
		list.setHeight100();
		list.setAutoFetchData(true);
		list.setShowFilterEditor(true);
		list.setFilterOnKeypress(true);
		list.setSelectionType(SelectionStyle.SINGLE);
		list.setFields(name, version);

		addMember(list);

		service.getPlugins(Session.get().getSid(), new AsyncCallback<GUIValue[]>() {
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(GUIValue[] plugins) {
				ListGridRecord[] records = new ListGridRecord[plugins.length];
				for (int i = 0; i < plugins.length; i++) {
					records[i] = new ListGridRecord();
					records[i].setAttribute("name", plugins[i].getCode());
					records[i].setAttribute("version", plugins[i].getValue());
				}
				list.setRecords(records);
			}
		});
	}
}
