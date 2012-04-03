package com.logicaldoc.gui.frontend.client.system;

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
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * The Clustering console.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.5
 */
public class ClusteringPanel extends HLayout {
	private SettingServiceAsync service = (SettingServiceAsync) GWT.create(SettingService.class);

	private TabSet tabs = new TabSet();

	private ValuesManager vm = new ValuesManager();

	public ClusteringPanel(GUIParameter[] parameters) {
		setWidth100();
		setHeight100();

		setMembersMargin(10);

		Tab settings = new Tab();
		settings.setTitle(I18N.message("cluster"));

		DynamicForm clusterForm = new DynamicForm();
		clusterForm.setWidth(300);
		clusterForm.setColWidths(1, "*");
		clusterForm.setValuesManager(vm);
		clusterForm.setTitleOrientation(TitleOrientation.LEFT);

		RadioGroupItem enabled = ItemFactory.newBooleanSelector("eenabled", "enabled");
		enabled.setValue("true".equals(parameters[0].getValue()) ? "yes" : "no");

		TextItem name = ItemFactory.newTextItem("name", I18N.message("name"), parameters[1].getValue());
		name.setRequired(true);

		ButtonItem save = new ButtonItem();
		save.setTitle(I18N.message("save"));
		save.addClickHandler(new ClickHandler() {
			@SuppressWarnings("unchecked")
			public void onClick(ClickEvent event) {
				final Map<String, Object> values = vm.getValues();

				if (vm.validate()) {
					final GUIParameter[] settings = new GUIParameter[2];
					settings[0] = new GUIParameter("cluster.enabled", values.get("eenabled").equals("yes") ? "true"
							: "false");
					settings[1] = new GUIParameter("cluster.name", vm.getValueAsString("name"));

					service.saveSettings(Session.get().getSid(), settings, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught);
						}

						@Override
						public void onSuccess(Void result) {
							Log.info(I18N.message("settingssaved") + " " + I18N.message("needrestart"), null);
						}
					});
				}
			}
		});

		clusterForm.setItems(enabled, name, save);
		settings.setPane(clusterForm);

		Tab channels = new Tab();
		channels.setTitle(I18N.message("channels"));
		channels.setPane(new ChannelsPanel());

		tabs.setTabs(settings, channels);
		setMembers(tabs);
	}
}