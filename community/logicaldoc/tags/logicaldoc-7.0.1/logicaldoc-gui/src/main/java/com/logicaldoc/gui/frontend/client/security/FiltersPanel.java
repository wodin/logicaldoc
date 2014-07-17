package com.logicaldoc.gui.frontend.client.security;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.SettingService;
import com.logicaldoc.gui.frontend.client.services.SettingServiceAsync;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This panel shows the the black list and white list of IPs and host names.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.5
 */
public class FiltersPanel extends VLayout {

	private SettingServiceAsync settingsService = (SettingServiceAsync) GWT.create(SettingService.class);

	private ValuesManager vm = new ValuesManager();

	private GUIUser user;

	private ChangedHandler changedHandler;

	public FiltersPanel(GUIUser user, ChangedHandler changedHandler) {
		super();
		this.user = user;
		this.changedHandler = changedHandler;

		setWidth100();
		setHeight100();
		setMembersMargin(20);

		init();
	}

	public FiltersPanel() {
		setWidth100();

		init();
	}

	private void init() {
		DynamicForm form = new DynamicForm();
		form.setValuesManager(vm);
		form.setTitleOrientation(TitleOrientation.TOP);
		if (user != null)
			form.setNumCols(2);
		else
			form.setNumCols(1);
		final TextAreaItem whitelist = ItemFactory.newTextAreaItem("whitelist", "whitelist", null);
		whitelist.setHeight(120);
		whitelist.setWidth(350);
		whitelist.setHint(I18N.message("blacklisthint"));
		if (changedHandler != null)
			whitelist.addChangedHandler(changedHandler);
		if (Session.get().isDemo())
			whitelist.setDisabled(true);

		final TextAreaItem blacklist = ItemFactory.newTextAreaItem("blacklist", "blacklist", null);
		blacklist.setHeight(120);
		blacklist.setWidth(350);
		blacklist.setHint(I18N.message("blacklisthint"));
		if (changedHandler != null)
			blacklist.addChangedHandler(changedHandler);
		if (Session.get().isDemo())
			blacklist.setDisabled(true);

		form.setItems(whitelist, blacklist);

		if (user == null) {
			/*
			 * We are operating on general filters
			 */
			settingsService.loadSettingsByNames(Session.get().getSid(),
					new String[] { "ip.whitelist", "ip.blacklist" }, new AsyncCallback<GUIParameter[]>() {
						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught);
						}

						@Override
						public void onSuccess(GUIParameter[] params) {
							whitelist.setValue(params[0].getValue().replace(',', '\n'));
							blacklist.setValue(params[1].getValue().replace(',', '\n'));
						}
					});
		} else {
			/*
			 * We are operating on user's specific filters
			 */
			whitelist.setValue(user.getIpWhitelist() != null ? user.getIpWhitelist().replace(',', '\n') : "");
			blacklist.setValue(user.getIpBlacklist() != null ? user.getIpBlacklist().replace(',', '\n') : "");
		}

		setMembers(form);
	}

	public void save() {
		String whitelist = vm.getValueAsString("whitelist");
		String blacklist = vm.getValueAsString("blacklist");

		GUIParameter[] params = new GUIParameter[2];
		params[0] = new GUIParameter("ip.whitelist", whitelist != null ? whitelist.replace('\n', ',').replaceAll(" ",
				"") : null);
		params[1] = new GUIParameter("ip.blacklist", blacklist != null ? blacklist.replace('\n', ',').replaceAll(" ",
				"") : null);

		settingsService.saveSettings(Session.get().getSid(), params, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(Void params) {

			}
		});
	}

	boolean validate() {
		String whitelist = vm.getValueAsString("whitelist");
		String blacklist = vm.getValueAsString("blacklist");
		user.setIpWhitelist(whitelist != null ? whitelist.replace('\n', ',').replaceAll(" ", "") : null);
		user.setIpBlacklist(blacklist != null ? blacklist.replace('\n', ',').replaceAll(" ", "") : null);
		return true;
	}
}