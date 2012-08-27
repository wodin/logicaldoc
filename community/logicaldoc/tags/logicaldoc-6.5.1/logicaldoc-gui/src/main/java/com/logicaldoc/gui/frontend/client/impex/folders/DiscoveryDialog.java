package com.logicaldoc.gui.frontend.client.impex.folders;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.SettingService;
import com.logicaldoc.gui.frontend.client.services.SettingServiceAsync;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;

/**
 * This is the form used for the workflow task setting.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class DiscoveryDialog extends Window {
	private SettingServiceAsync service = (SettingServiceAsync) GWT.create(SettingService.class);

	public DiscoveryDialog(String username, String password, String language) {
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("automaticdiscovery"));
		setWidth(200);
		setHeight(200);
		setMembersMargin(5);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setPadding(5);
		setAutoSize(true);

		final DynamicForm form = new DynamicForm();
		form.setTitleOrientation(TitleOrientation.TOP);
		form.setNumCols(1);
		TextItem usr = ItemFactory.newTextItem("username", "username", username);
		PasswordItem psw = ItemFactory.newPasswordItem("password", "password", password);
		SelectItem lang = ItemFactory.newLanguageSelector("language", false, false);
		
		ButtonItem save = new ButtonItem();
		save.setTitle(I18N.message("save"));
		save.setAutoFit(true);
		save.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				GUIParameter u=new GUIParameter();
				u.setName("smb.username");
				u.setValue(form.getValueAsString("username"));
				GUIParameter p=new GUIParameter();
				p.setName("smb.password");
				p.setValue(form.getValueAsString("password"));
				GUIParameter l=new GUIParameter();
				l.setName("smb.lang");
				l.setValue(form.getValueAsString("language"));
				
				service.saveSettings(Session.get().getSid(), new GUIParameter[]{u,p,l}, new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(Void ret) {
							}
						});
				destroy();
			}
		});

		form.setFields(usr,psw,lang,save);
		addItem(form);
	}
}
