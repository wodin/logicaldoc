package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIEmail;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.EventPanel;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.RichTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;

/**
 * This is the form used to send emails and download tickets
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class EmailDialog extends Window {
	private long[] docIds;

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private ValuesManager vm = new ValuesManager();

	public EmailDialog(long[] docIds, String docTitle) {
		super();
		this.docIds = docIds;

		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				destroy();
			}
		});

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("sendmail"));
		setWidth(500);
		setHeight(400);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setPadding(5);
		setAutoSize(true);

		final DynamicForm form = new DynamicForm();
		form.setID("emailform");
		form.setValuesManager(vm);
		form.setWidth(380);
		form.setMargin(5);
		form.setTitleOrientation(TitleOrientation.TOP);
		form.setNumCols(1);

		TextItem recipients = ItemFactory.newEmailItem("recipients", "recipients", true);
		recipients.setWidth(250);
		recipients.setRequired(true);

		TextItem cc = ItemFactory.newEmailItem("cc", "cc", true);
		cc.setWidth(250);

		TextItem subject = ItemFactory.newTextItem("subject", "subject", docTitle);
		subject.setRequired(true);
		subject.setWidth(250);

		final RichTextItem message = new RichTextItem();
		message.setName("message");
		message.setTitle(I18N.message("message"));
		message.setValue("");
		message.setWidth(490);
		message.setHeight(200);

		final CheckboxItem ticket = new CheckboxItem();
		ticket.setName("sendticket");
		ticket.setTitle(I18N.message("sendticket"));

		final CheckboxItem zip = new CheckboxItem();
		zip.setName("zip");
		zip.setTitle(I18N.message("zipattachments"));

		ButtonItem sendItem = new ButtonItem();
		sendItem.setTitle(I18N.message("send"));
		sendItem.setAutoFit(true);
		sendItem.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				vm.validate();
				if (!vm.hasErrors()) {
					GUIEmail mail = new GUIEmail();
					mail.setRecipients(vm.getValueAsString("recipients"));
					mail.setCc(vm.getValueAsString("cc"));
					mail.setSubject(vm.getValueAsString("subject"));
					mail.setMessage(vm.getValueAsString("message"));
					mail.setSendAsTicket("true".equals(vm.getValueAsString("sendticket")));
					mail.setZipCompression("true".equals(vm.getValueAsString("zip")));
					mail.setDocIds(EmailDialog.this.docIds);

					documentService.sendAsEmail(Session.get().getSid(), mail, new AsyncCallback<String>() {

						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught);
							destroy();
						}

						@Override
						public void onSuccess(String result) {
							if ("ok".equals(result)) {
								EventPanel.get().info(
										I18N.message("messagesent") + ". " + I18N.message("documentcopysent"), null);
							} else {
								EventPanel.get().error(I18N.message("messagenotsent"), null);
							}
							destroy();
						}
					});
				}
			}
		});

		// The download ticket is available on single selection only
		if (docIds.length == 1)
			form.setFields(recipients, cc, subject, ticket, message, sendItem);
		else
			form.setFields(recipients, cc, subject, zip, message, sendItem);

		addItem(form);
	}
}
