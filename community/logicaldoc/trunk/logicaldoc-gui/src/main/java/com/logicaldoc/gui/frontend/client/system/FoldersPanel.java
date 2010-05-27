package com.logicaldoc.gui.frontend.client.system;

import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.services.SystemService;
import com.logicaldoc.gui.frontend.client.services.SystemServiceAsync;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * This panel shows the Folders settings.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class FoldersPanel extends VLayout {

	private SystemServiceAsync service = (SystemServiceAsync) GWT.create(SystemService.class);

	private ValuesManager vm = new ValuesManager();

	public FoldersPanel() {
		setWidth100();
		setMembersMargin(10);
		setMargin(30);
		setHeight(400);

		TabSet tabs = new TabSet();
		tabs.setWidth(380);
		tabs.setHeight(270);
		Tab tab = new Tab();
		tab.setTitle(I18N.getMessage("folders"));
		tabs.setTabs(tab);

		DynamicForm foldersForm = new DynamicForm();
		foldersForm.setWidth(300);
		foldersForm.setColWidths(1, "*");
		foldersForm.setValuesManager(vm);
		foldersForm.setTitleOrientation(TitleOrientation.LEFT);

		TextItem docdir = ItemFactory.newTextItem("docdir", "docdir", Util.getContext().get("conf_docdir"));
		docdir.setRequired(true);
		docdir.setWidth(250);

		TextItem indexdir = new TextItem();
		indexdir.setWidth(250);
		indexdir.setName("indexdir");
		indexdir.setTitle("indexdir");
		indexdir.setValue(Util.getContext().get("conf_indexdir"));
		indexdir.setRequired(true);

		TextItem userdir = new TextItem();
		userdir.setWidth(250);
		userdir.setName("userdir");
		userdir.setTitle("userdir");
		userdir.setValue(Util.getContext().get("conf_userdir"));
		userdir.setRequired(true);

		TextItem plugindir = new TextItem();
		plugindir.setWidth(250);
		plugindir.setName("plugindir");
		plugindir.setTitle("plugindir");
		plugindir.setValue(Util.getContext().get("conf_plugindir"));
		plugindir.setRequired(true);

		TextItem importdir = new TextItem();
		importdir.setWidth(250);
		importdir.setName("importdir");
		importdir.setTitle("importdir");
		importdir.setValue(Util.getContext().get("conf_importdir"));
		importdir.setRequired(true);

		TextItem exportdir = new TextItem();
		exportdir.setWidth(250);
		exportdir.setName("exportdir");
		exportdir.setTitle("exportdir");
		exportdir.setValue(Util.getContext().get("conf_exportdir"));
		exportdir.setRequired(true);

		foldersForm.setItems(docdir, indexdir, userdir, plugindir, importdir, exportdir);

		IButton save = new IButton();
		save.setTitle(I18N.getMessage("save"));
		save.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				final Map<String, Object> values = vm.getValues();

				if (vm.validate()) {
					GUIParameter[] folders = new GUIParameter[6];
					GUIParameter docdir = new GUIParameter("docdir", (String) values.get("docdir"));
					folders[0] = docdir;
					GUIParameter indexdir = new GUIParameter("indexdir", (String) values.get("indexdir"));
					folders[1] = indexdir;
					GUIParameter userdir = new GUIParameter("userdir", (String) values.get("userdir"));
					folders[2] = userdir;
					GUIParameter plugindir = new GUIParameter("plugindir", (String) values.get("plugindir"));
					folders[3] = plugindir;
					GUIParameter importdir = new GUIParameter("importdir", (String) values.get("importdir"));
					folders[4] = importdir;
					GUIParameter exportdir = new GUIParameter("exportdir", (String) values.get("exportdir"));
					folders[5] = exportdir;

					service.saveFolders(Session.get().getSid(), folders, new AsyncCallback<Void>() {

						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught);
						}

						@Override
						public void onSuccess(Void result) {
							Log.info(I18N.getMessage("settingssaved"), null);
						}
					});
				}
			}
		});

		tab.setPane(foldersForm);
		setMembers(tabs, save);
	}
}