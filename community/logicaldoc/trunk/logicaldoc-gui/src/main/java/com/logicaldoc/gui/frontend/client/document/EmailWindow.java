package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIEmail;
import com.logicaldoc.gui.common.client.util.EmailsValidator;
import com.logicaldoc.gui.frontend.client.Log;
import com.logicaldoc.gui.frontend.client.panels.FooterStatus;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;

/**
 * This is the form used to send emails and download tickets
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class EmailWindow extends Window {
	private long docId;

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	public EmailWindow(long docId, String docTitle) {
		super();
		this.docId = docId;

		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClientEvent event) {
				destroy();
			}
		});

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.getMessage("sendmail"));
		setWidth(400);
		setHeight(280);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setPadding(5);
		setAutoSize(true);

		final ValuesManager vm = new ValuesManager();
		final DynamicForm emailForm = new DynamicForm();
		emailForm.setID("emailform");
		emailForm.setValuesManager(vm);
		emailForm.setWidth(350);
		emailForm.setMargin(5);

		TextItem recipients = new TextItem();
		recipients.setName("recipients");
		recipients.setTitle(I18N.getMessage("recipients"));
		recipients.setRequired(true);
		recipients.setValidators(new EmailsValidator());
		recipients.setWidth(300);

		TextItem cc = new TextItem();
		cc.setName("cc");
		cc.setTitle(I18N.getMessage("cc"));
		cc.setValidators(new EmailsValidator());
		cc.setWidth(300);

		TextItem object = new TextItem();
		object.setName("object");
		object.setTitle(I18N.getMessage("object"));
		object.setRequired(true);
		object.setValue(docTitle);
		object.setWidth(300);

		final TextAreaItem message = new TextAreaItem();
		message.setName("message");
		message.setTitle(I18N.getMessage("message"));
		message.setValue("");
		message.setWidth(300);

		final CheckboxItem ticket = new CheckboxItem();
		ticket.setName("sendticket");
		ticket.setTitle(I18N.getMessage("sendticket"));
		ticket.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				try {
					if ("true".equals(event.getValue().toString()))
						message.setValue(message.getValue() + "\n{ticket}");
				} catch (Throwable t) {
					SC.warn(t.getMessage());
				}
			}
		});

		ButtonItem sendItem = new ButtonItem();
		sendItem.setTitle(I18N.getMessage("send"));
		sendItem.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				vm.validate();
				if (!vm.hasErrors()) {
					GUIEmail mail = new GUIEmail();
					mail.setRecipients(vm.getValueAsString("recipients"));
					mail.setCc(vm.getValueAsString("cc"));
					mail.setObject(vm.getValueAsString("object"));
					mail.setMessage(vm.getValueAsString("message"));
					mail.setSendAdTicket("true".equals(vm.getValueAsString("sendticket")));
					mail.setDocId(EmailWindow.this.docId);
					mail.setUser(Session.get().getUser());
					documentService.sendAsEmail(Session.get().getSid(), mail, new AsyncCallback<String>() {

						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught);
							destroy();
						}

						@Override
						public void onSuccess(String result) {
							if ("ok".equals(result)) {
								FooterStatus.getInstance().info("Message sent", null);
							} else {
								FooterStatus.getInstance().error("Message not sent", null);
							}
							destroy();
						}
					});
				}
			}
		});

		emailForm.setFields(recipients, cc, object, ticket, message, sendItem);
		addItem(emailForm);
	}
}
