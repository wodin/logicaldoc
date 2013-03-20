package com.logicaldoc.gui.frontend.client.gdocs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.widgets.ContactingServer;
import com.logicaldoc.gui.frontend.client.services.GDocsService;
import com.logicaldoc.gui.frontend.client.services.GDocsServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.SubmitItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;

/**
 * This popup window is used to handle Google Docs settings.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.7
 */
public class GDocsSettings extends Window {
	private SubmitItem save;

	private ValuesManager vm;

	protected GDocsServiceAsync gdocsService = (GDocsServiceAsync) GWT.create(GDocsService.class);

	public GDocsSettings(String[] settings) {
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("googledocs"));
		setWidth(320);
		setHeight(120);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setPadding(5);
		setMembersMargin(3);

		DynamicForm form = new DynamicForm();
		vm = new ValuesManager();
		form.setValuesManager(vm);

		TextItem username = ItemFactory.newTextItem("use" + "rname", "username", settings[0]);
		username.setRequired(true);
		username.setWidth(200);

		TextItem password = ItemFactory.newPasswordItem("password", "password", null);
		password.setRequired(true);
		password.setWidth(200);

		save = new SubmitItem();
		save.setTitle(I18N.message("save"));
		save.setAlign(Alignment.RIGHT);
		save.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSave();
			}
		});

		form.setItems(username, password, save);

		addItem(form);
	}

	public void onSave() {
		if (!vm.validate())
			return;

		ContactingServer.get().show();
		gdocsService.saveSettings(Session.get().getSid(), vm.getValueAsString("username"),
				vm.getValueAsString("password"), new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						ContactingServer.get().hide();
						SC.warn(I18N.message("connectionfailed"));
						destroy();
					}

					@Override
					public void onSuccess(Void result) {
						ContactingServer.get().hide();
						Log.info(I18N.message("connectionestablished"), null);
						destroy();
					}
				});
	}
}