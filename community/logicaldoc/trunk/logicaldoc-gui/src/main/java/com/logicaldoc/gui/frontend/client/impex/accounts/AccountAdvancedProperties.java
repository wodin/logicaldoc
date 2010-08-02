package com.logicaldoc.gui.frontend.client.impex.accounts;

import java.util.LinkedHashMap;
import java.util.Map;

import com.logicaldoc.gui.common.client.beans.GUIEmailAccount;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * Shows account's advanced properties and read-only data
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class AccountAdvancedProperties extends AccountDetailsTab {
	private DynamicForm form = new DynamicForm();

	private HLayout formsContainer = new HLayout();

	public AccountAdvancedProperties(GUIEmailAccount account, ChangedHandler changedHandler) {
		super(account, changedHandler);
		setWidth100();
		setHeight100();
		setMembers(formsContainer);
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

		TextItem include = ItemFactory.newTextItem("include", "include", account.getIncludes());
		include.addChangedHandler(changedHandler);

		TextItem exclude = ItemFactory.newTextItem("exclude", "exclude", account.getExcludes());
		include.addChangedHandler(changedHandler);

		TextItem folder = ItemFactory.newTextItem("mailfolder", "mailfolder", account.getMailFolder());
		include.addChangedHandler(changedHandler);

		SelectItem format = ItemFactory.newSelectItem("format", "format");
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("0", I18N.message("multiplefiles"));
		map.put("1", I18N.message("singleeml"));
		format.setValueMap(map);
		format.addChangedHandler(changedHandler);
		format.setValue(Integer.toString(account.getFormat()));

		CheckboxItem deleteFomMailbox = new CheckboxItem();
		deleteFomMailbox.setName("delete");
		deleteFomMailbox.setTitle(I18N.message("deletefrommailbox"));
		deleteFomMailbox.setRedrawOnChange(true);
		deleteFomMailbox.setWidth(50);
		deleteFomMailbox.setValue(account.isDeleteFromMailbox());
		deleteFomMailbox.addChangedHandler(changedHandler);
		deleteFomMailbox.setValue(account.isDeleteFromMailbox() ? "yes" : "no");

		form.setItems(folder, format, include, exclude, deleteFomMailbox);

		formsContainer.addMember(form);
	}

	@SuppressWarnings("unchecked")
	boolean validate() {
		Map<String, Object> values = (Map<String, Object>) form.getValues();
		form.validate();
		if (!form.hasErrors()) {
			account.setIncludes((String) values.get("include"));
			account.setExcludes((String) values.get("exclude"));
			account.setDeleteFromMailbox("yes".equals((String) values.get("delete")));
			account.setMailFolder((String) values.get("mailfolder"));
			account.setFormat(Integer.parseInt((String) values.get("format")));
		}
		return !form.hasErrors();
	}
}