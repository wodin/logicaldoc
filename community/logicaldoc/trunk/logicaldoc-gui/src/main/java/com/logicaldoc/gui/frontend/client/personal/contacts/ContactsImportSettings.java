package com.logicaldoc.gui.frontend.client.personal.contacts;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIContact;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.widgets.ContactingServer;
import com.logicaldoc.gui.frontend.client.services.ContactService;
import com.logicaldoc.gui.frontend.client.services.ContactServiceAsync;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpinnerItem;
import com.smartgwt.client.widgets.form.fields.SubmitItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;

/**
 * This popup window is used to upload a new contacts file to the server.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.0.1
 */
public class ContactsImportSettings extends Window {

	private ContactServiceAsync service = (ContactServiceAsync) GWT.create(ContactService.class);

	private DynamicForm form;

	public ContactsImportSettings() {
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("uploadcontacts"));
		setWidth(380);
		setHeight(330);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		form = new DynamicForm();

		SelectItem separated = ItemFactory.newSelectItem("separatedby", "separatedby");
		separated.setWidth(80);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put(",", ",");
		map.put(";", ";");
		separated.setValueMap(map);
		separated.setValue(",");

		SelectItem delimiter = ItemFactory.newSelectItem("textdelimiter", "textdelimiter");
		delimiter.setWidth(80);
		LinkedHashMap<String, String> map2 = new LinkedHashMap<String, String>();
		map2.put("\"", "\"");
		map2.put("'", "'");
		delimiter.setValueMap(map2);
		delimiter.setValue("\"");

		RadioGroupItem skip = ItemFactory.newBooleanSelector("skipfirstrow", "skipfirstrow");
		skip.setValue("yes");

		SpinnerItem firstName = ItemFactory.newSpinnerItem("firstname", "firstname", 1);
		firstName.setRequired(true);
		firstName.setWidth(60);
		firstName.setMin(1);
		firstName.setHint(I18N.message("columnindex"));

		SpinnerItem lastName = ItemFactory.newSpinnerItem("lastname", "lastname", 2);
		lastName.setRequired(true);
		lastName.setWidth(60);
		lastName.setMin(1);
		lastName.setHint(I18N.message("columnindex"));

		SpinnerItem email = ItemFactory.newSpinnerItem("email", "email", 3);
		email.setRequired(true);
		email.setWidth(60);
		email.setMin(1);
		email.setHint(I18N.message("columnindex"));

		SpinnerItem company = ItemFactory.newSpinnerItem("company", "company", 4);
		company.setRequired(true);
		company.setWidth(60);
		company.setMin(1);
		company.setHint(I18N.message("columnindex"));

		SpinnerItem phone = ItemFactory.newSpinnerItem("phone", "phone", 5);
		phone.setRequired(true);
		phone.setWidth(60);
		phone.setMin(1);
		phone.setHint(I18N.message("columnindex"));

		SpinnerItem mobile = ItemFactory.newSpinnerItem("mobile", "cell", 6);
		mobile.setRequired(true);
		mobile.setWidth(60);
		mobile.setMin(1);
		mobile.setHint(I18N.message("columnindex"));

		SpinnerItem address = ItemFactory.newSpinnerItem("address", "address", 7);
		address.setRequired(true);
		address.setWidth(60);
		address.setMin(1);
		address.setHint(I18N.message("columnindex"));

		SubmitItem importButton = new SubmitItem();
		importButton.setTitle(I18N.message("iimport"));
		importButton.setEndRow(true);
		importButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onImport();
			}
		});

		form.setItems(separated, delimiter, skip, firstName, lastName, email, company, phone, mobile, address,
				importButton);

		addItem(form);
	}

	public String getSeparator() {
		@SuppressWarnings("unchecked")
		Map<String, Object> values = (Map<String, Object>) form.getValues();
		return values.get("separatedby").toString();
	}

	public String getTextDelimiter() {
		@SuppressWarnings("unchecked")
		Map<String, Object> values = (Map<String, Object>) form.getValues();
		return values.get("textdelimiter").toString();
	}

	public boolean isSkipFirstRow() {
		@SuppressWarnings("unchecked")
		Map<String, Object> values = (Map<String, Object>) form.getValues();
		return "yes".equals(values.get("skipfirstrow").toString());
	}

	public int getFirstNameIndex() {
		@SuppressWarnings("unchecked")
		Map<String, Object> values = (Map<String, Object>) form.getValues();
		return Integer.parseInt(values.get("firstname").toString());
	}

	public int getLastNameIndex() {
		@SuppressWarnings("unchecked")
		Map<String, Object> values = (Map<String, Object>) form.getValues();
		return Integer.parseInt(values.get("lastname").toString());
	}

	public int getEmailIndex() {
		@SuppressWarnings("unchecked")
		Map<String, Object> values = (Map<String, Object>) form.getValues();
		return Integer.parseInt(values.get("email").toString());
	}

	public int getCompanyIndex() {
		@SuppressWarnings("unchecked")
		Map<String, Object> values = (Map<String, Object>) form.getValues();
		return Integer.parseInt(values.get("company").toString());
	}

	public int getPhoneIndex() {
		@SuppressWarnings("unchecked")
		Map<String, Object> values = (Map<String, Object>) form.getValues();
		return Integer.parseInt(values.get("phone").toString());
	}

	public int getMobileIndex() {
		@SuppressWarnings("unchecked")
		Map<String, Object> values = (Map<String, Object>) form.getValues();
		return Integer.parseInt(values.get("mobile").toString());
	}

	public int getAddressIndex() {
		@SuppressWarnings("unchecked")
		Map<String, Object> values = (Map<String, Object>) form.getValues();
		return Integer.parseInt(values.get("address").toString());
	}

	private void onImport() {
		if (form.validate()) {
			ContactingServer.get().show();
			try {
				service.parseContacts(Session.get().getSid(), true, getSeparator(), getTextDelimiter(),
						isSkipFirstRow(), getFirstNameIndex(), getLastNameIndex(), getEmailIndex(), getCompanyIndex(),
						getPhoneIndex(), getMobileIndex(), getAddressIndex(), new AsyncCallback<GUIContact[]>() {

							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
								ContactingServer.get().hide();
							}

							@Override
							public void onSuccess(GUIContact[] contacts) {
								ContactingServer.get().hide();
								ContactsImportPreview preview = new ContactsImportPreview(ContactsImportSettings.this);
								preview.show();
								preview.setContacts(contacts);
							}
						});
			} catch (Throwable t) {
				ContactingServer.get().hide();
			}
		}
	}
}