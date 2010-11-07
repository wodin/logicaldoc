package com.logicaldoc.gui.frontend.client.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.SettingService;
import com.logicaldoc.gui.frontend.client.services.SettingServiceAsync;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.FormItem;
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

	private SettingServiceAsync service = (SettingServiceAsync) GWT.create(SettingService.class);

	private ValuesManager vm = new ValuesManager();

	public FoldersPanel(GUIParameter[] folders) {
		setWidth100();
		setMembersMargin(10);
		setMargin(30);
		setHeight(400);

		TabSet tabs = new TabSet();
		tabs.setWidth(380);
		tabs.setHeight(270);
		Tab tab = new Tab();
		tab.setTitle(I18N.message("folders"));
		tabs.setTabs(tab);

		DynamicForm foldersForm = new DynamicForm();
		foldersForm.setWidth(300);
		foldersForm.setColWidths(1, "*");
		foldersForm.setValuesManager(vm);
		foldersForm.setTitleOrientation(TitleOrientation.LEFT);

		List<FormItem> items = new ArrayList<FormItem>();

		for (GUIParameter f : folders) {
			TextItem item = ItemFactory.newTextItem(f.getName(), f.getName(), f.getValue());
			item.setRequired(true);
			item.setWidth(250);
			items.add(item);
		}

		foldersForm.setItems(items.toArray(new FormItem[0]));

		IButton save = new IButton();
		save.setTitle(I18N.message("save"));
		save.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				final Map<String, Object> values = vm.getValues();

				if (vm.validate()) {
					List<GUIParameter> folders = new ArrayList<GUIParameter>();
					for (String name : values.keySet()) {
						GUIParameter dir = new GUIParameter(name, (String) values.get(name));
						folders.add(dir);
					}

					service.saveFolders(Session.get().getSid(), folders.toArray(new GUIParameter[0]),
							new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(Void result) {
									Log.info(I18N.message("settingssaved"), null);
								}
							});
				}
			}
		});
		save.setDisabled(Session.get().isDemo() && Session.get().getUser().getId() == 1);

		tab.setPane(foldersForm);
		setMembers(tabs, save);
	}
}