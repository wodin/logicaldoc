package com.logicaldoc.gui.frontend.client.settings;

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
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * This panel shows the Parameters settings.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class ParametersSettingsPanel extends VLayout {
	private SettingServiceAsync service = (SettingServiceAsync) GWT.create(SettingService.class);

	private ValuesManager vm = new ValuesManager();

	private GUIParameter[] settings;

	public ParametersSettingsPanel(GUIParameter[] settings) {
		this.settings = settings;

		setWidth100();
		setHeight100();
		setMembersMargin(5);
		setMargin(5);

		TabSet tabs = new TabSet();
		tabs.setWidth100();
		tabs.setHeight100();
		Tab tab = new Tab();
		tab.setTitle(I18N.message("parameters"));
		tabs.setTabs(tab);

		DynamicForm form = new DynamicForm();
		form.setValuesManager(vm);
		form.setTitleOrientation(TitleOrientation.TOP);
		form.setNumCols(4);
		form.setColWidths(100, 100, 100, 100);

		TextItem[] items = new TextItem[settings.length];

		int i = 0;
		for (GUIParameter parameter : settings) { 
			TextItem item = ItemFactory.newTextItem(parameter.getName(), parameter.getName(), parameter.getValue());
			items[i] = item;
			i++;
		}

		form.setItems(items);

		IButton save = new IButton();
		save.setTitle(I18N.message("save"));
		save.addClickHandler(new ClickHandler() {
			@SuppressWarnings("unchecked")
			public void onClick(ClickEvent event) {
				Map<String, Object> values = (Map<String, Object>) vm.getValues();

				if (vm.validate()) {
					for (GUIParameter param : ParametersSettingsPanel.this.settings) {
						param.setValue((String) values.get(ItemFactory.filterItemName(param.getName())));
						Session.get().getInfo().setConfig(param.getName(), param.getValue());
					}

					service.saveSettings(Session.get().getSid(), ParametersSettingsPanel.this.settings,
							new AsyncCallback<Void>() {

								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(Void ret) {
									Log.info(I18N.message("settingssaved"), null);
								}
							});
				}
			}
		});
		save.setDisabled(Session.get().isDemo() && Session.get().getUser().getId() == 1);

		tab.setPane(form);
		setMembers(tabs, save);
	}
}
