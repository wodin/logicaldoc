package com.logicaldoc.gui.frontend.client.impex.folders;

import java.util.Map;

import com.logicaldoc.gui.common.client.beans.GUIShare;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
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

	public ImportFolderStandardProperties(GUIShare share, ChangedHandler changedHandler) {
		super(share, changedHandler);
		setWidth100();
		setHeight100();
		setMembers(formsContainer);
		targetSelector = new FolderSelector("target", false);
		targetSelector.setRequired(true);
		targetSelector.setTitle(I18N.message("targetfolder"));
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
		form.setNumCols(3);
		form.setTitleOrientation(TitleOrientation.TOP);

		TextItem path = ItemFactory.newTextItem("path", "path", share.getPath());
		path.addChangedHandler(changedHandler);
		path.setRequired(true);

		TextItem username = ItemFactory.newTextItem("username", "username", share.getUsername());
		username.addChangedHandler(changedHandler);

		TextItem password = ItemFactory.newPasswordItem("password", "password", share.getPassword());
		password.addChangedHandler(changedHandler);

		targetSelector.setFolder(share.getTarget());

		SelectItem language = ItemFactory.newLanguageSelector("language", false, false);
		language.addChangedHandler(changedHandler);
		language.setValue(share.getLanguage());
		
		if ("smb".equals(share.getProvider()))
			form.setItems(path, targetSelector, language, username, password);
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
			share.setTarget(targetSelector.getFolder());
			share.setLanguage((String) values.get("username"));
		}
		return !form.hasErrors();
	}
}