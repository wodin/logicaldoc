package com.logicaldoc.gui.frontend.client.personal;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIContact;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.ContactService;
import com.logicaldoc.gui.frontend.client.services.ContactServiceAsync;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;

/**
 * This is the form used to edit contact's details.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.8
 */
public class ContactDetails extends Window {

	private ContactServiceAsync service = (ContactServiceAsync) GWT.create(ContactService.class);

	public ContactDetails(final GUIContact contact, final Contacts parent) {
		super();

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("contact"));
		setWidth(500);
		setAutoHeight();
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setMembersMargin(5);
		setAutoSize(true);

		final ValuesManager vm = new ValuesManager();
		final DynamicForm form = new DynamicForm();
		form.setValuesManager(vm);
		form.setMargin(5);
		form.setNumCols(3);
		form.setTitleOrientation(TitleOrientation.TOP);

		TextItem firstName = ItemFactory.newTextItem("firstname", "firstname", contact.getFirstName());
		TextItem lastName = ItemFactory.newTextItem("lastname", "lastname", contact.getLastName());
		TextItem email = ItemFactory.newEmailItem("email", "email", false);
		email.setRequired(true);
		email.setValue(contact.getEmail());
		TextItem address = ItemFactory.newTextItem("address", "address", contact.getAddress());
		TextItem phone = ItemFactory.newTextItem("phone", "phone", contact.getPhone());
		TextItem cell = ItemFactory.newTextItem("cell", "cell", contact.getMobile());
		TextItem company = ItemFactory.newTextItem("company", "company", contact.getCompany());
		company.setWidth(180);

		ButtonItem save = new ButtonItem();
		save.setTitle(I18N.message("save"));
		save.setAutoFit(true);
		save.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				vm.validate();
				if (!vm.hasErrors()) {

					GUIContact c = new GUIContact();
					c.setId(contact.getId());
					c.setUserId(Session.get().getUser().getId());
					c.setFirstName(vm.getValueAsString("firstname"));
					c.setLastName(vm.getValueAsString("lastname"));
					c.setEmail(vm.getValueAsString("email"));
					c.setAddress(vm.getValueAsString("address"));
					c.setPhone(vm.getValueAsString("phone"));
					c.setMobile(vm.getValueAsString("cell"));
					c.setCompany(vm.getValueAsString("company"));

					service.save(Session.get().getSid(), c, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							SC.warn(caught.getMessage());
						}

						@Override
						public void onSuccess(Void ret) {
							ContactDetails.this.destroy();
							if (parent != null)
								parent.refresh();
						}
					});
				}
			}
		});

		form.setFields(email, firstName, lastName, company, address, phone, cell, save);

		addItem(form);
	}
}
