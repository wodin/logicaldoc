package com.logicaldoc.gui.frontend.client.settings;

import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.frontend.client.Log;
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
		setMembersMargin(10);
		setMargin(30);
		setHeight(400);

		TabSet tabs = new TabSet();
		tabs.setWidth(508);
		tabs.setHeight(395);
		Tab tab = new Tab();
		tab.setTitle(I18N.getMessage("parameters"));
		tabs.setTabs(tab);

		DynamicForm form = new DynamicForm();
		form.setValuesManager(vm);
		form.setTitleOrientation(TitleOrientation.TOP);
		form.setNumCols(3);
		form.setColWidths(100, 100, 100);  
		form.setWidth(480);

		TextItem[] items = new TextItem[settings.length];

		int i = 0;
		for (GUIParameter parameter : settings) {
			TextItem item = new TextItem();
			item.setName(parameter.getName());
			item.setTitle(parameter.getName());
			item.setValue(parameter.getValue());
			items[i] = item;
			i++;
		}

		form.setItems(items);

		IButton save = new IButton();
		save.setTitle(I18N.getMessage("save"));
		save.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				final Map<String, Object> values = vm.getValues();

				if (vm.validate()) {
					for (GUIParameter param : ParametersSettingsPanel.this.settings) {
						param.setValue((String) values.get(param.getName()));
					}

					service.saveSettings(Session.get().getSid(), ParametersSettingsPanel.this.settings,
							new AsyncCallback<Void>() {

								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(Void ret) {
									Log.info(I18N.getMessage("settingssaved"), null);
								}
							});
				}
			}
		});

		tab.setPane(form);
		setMembers(tabs, save);
	}
}
