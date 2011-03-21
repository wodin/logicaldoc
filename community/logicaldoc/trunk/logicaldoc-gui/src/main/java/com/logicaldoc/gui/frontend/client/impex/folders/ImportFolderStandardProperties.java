package com.logicaldoc.gui.frontend.client.impex.folders;

import java.util.Map;

import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUIShare;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.widgets.FolderChangeListener;
import com.logicaldoc.gui.common.client.widgets.FolderSelector;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * Shows import folder's standard properties and read-only data
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class ImportFolderStandardProperties extends ImportFolderDetailsTab {
	private DynamicForm form = new DynamicForm();

	private HLayout formsContainer = new HLayout();

	private FolderSelector targetSelector;

	public ImportFolderStandardProperties(GUIShare share, final ChangedHandler changedHandler) {
		super(share, changedHandler);
		setWidth100();
		setHeight100();
		setMembers(formsContainer);
		targetSelector = new FolderSelector("target", false);
		targetSelector.setRequired(true);
		targetSelector.setTitle(I18N.message("target"));
		if (share.getTarget() != null)
			targetSelector.setFolder(share.getTarget());
		targetSelector.addFolderChangeListener(new FolderChangeListener() {
			@Override
			public void onChanged(GUIFolder folder) {
				changedHandler.onChanged(null);
			}
		});
		refresh();
	}

	private void refresh() {
		form.clearValues();
		form.clearErrors(false);

		if (form != null)
			form.destroy();

		if (formsContainer.contains(form))
			formsContainer.removeChild(form);

		form = new DynamicForm();
		form.setNumCols(2);
		form.setTitleOrientation(TitleOrientation.TOP);

		TextItem path = ItemFactory.newTextItem("path", "path", share.getPath());
		path.addChangedHandler(changedHandler);
		path.setRequired(true);

		TextItem domain = ItemFactory.newTextItem("domain", "domain", share.getDomain());
		domain.addChangedHandler(changedHandler);

		TextItem username = ItemFactory.newTextItem("username", "username", share.getUsername());
		username.addChangedHandler(changedHandler);

		TextItem password = ItemFactory.newPasswordItem("password", "password", share.getPassword());
		password.addChangedHandler(changedHandler);

		SelectItem language = ItemFactory.newLanguageSelector("language", false, false);
		language.addChangedHandler(changedHandler);
		language.setRequired(true);
		language.setValue(share.getLanguage());

		if ("smb".equals(share.getProvider()))
			form.setItems(path, targetSelector, language, domain, username, password );
		else
			form.setItems(path, targetSelector, language);

		formsContainer.addMember(form);

	}

	@SuppressWarnings("unchecked")
	boolean validate() {
		Map<String, Object> values = (Map<String, Object>) form.getValues();
		form.validate();
		if (!form.hasErrors()) {
			share.setPath((String) values.get("path"));
			share.setUsername((String) values.get("username"));
			share.setPassword((String) values.get("password"));
			share.setDomain((String) values.get("domain"));
			share.setTarget(targetSelector.getFolder());
			share.setLanguage((String) values.get("language"));
		}
		return !form.hasErrors();
	}
}