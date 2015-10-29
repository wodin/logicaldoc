package com.logicaldoc.gui.frontend.client.dashboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIMessage;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.EventPanel;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.MessageService;
import com.logicaldoc.gui.frontend.client.services.MessageServiceAsync;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.validator.IntegerRangeValidator;

/**
 * This is the form used to send messages to other users
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class MessageDialog extends Window {
	private MessageServiceAsync service = (MessageServiceAsync) GWT.create(MessageService.class);

	private DynamicForm form = new DynamicForm();

	private SelectItem recipient = null;

	public MessageDialog() {
		super();

		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				destroy();
			}
		});

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("sendmessage"));
		setWidth(290);
		setHeight(280);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setPadding(5);
		setAutoSize(true);

		form.setWidth(280);
		form.setMargin(5);
		form.setTitleOrientation(TitleOrientation.TOP);
		form.setNumCols(1);

		StaticTextItem recipientItem = ItemFactory.newStaticTextItem("recipientItem", "",
				"<b>" + I18N.message("recipient") + "</b>");
		recipientItem.setShouldSaveValue(false);
		recipientItem.setWrapTitle(false);
		recipient = ItemFactory.newUserSelector("recipient", " ", null, true);
		recipient.setShowTitle(false);
		recipient.setDisplayField("username");

		TextItem subject = ItemFactory.newTextItem("subject", "subject", "");
		subject.setRequired(true);
		subject.setWidth(250);

		final TextAreaItem message = new TextAreaItem();
		message.setName("message");
		message.setTitle(I18N.message("message"));
		message.setValue("");
		message.setWidth(250);

		final CheckboxItem confirmation = new CheckboxItem();
		confirmation.setName("confirmation");
		confirmation.setTitle(I18N.message("confirmation"));

		IntegerItem validity = ItemFactory.newIntegerItem("validity", I18N.message("validity"), 1);
		IntegerRangeValidator integerRangeValidator = new IntegerRangeValidator();
		integerRangeValidator.setMin(1);
		validity.setValidators(integerRangeValidator);
		validity.setHint(I18N.message("days"));

		SelectItem priority = ItemFactory.newPrioritySelector("priority", I18N.message("priority"));

		ButtonItem sendItem = new ButtonItem();
		sendItem.setAutoFit(true);
		sendItem.setTitle(I18N.message("send"));
		sendItem.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				form.validate();
				if (!form.hasErrors()) {
					GUIMessage message = new GUIMessage();
					message.setRecipient(recipient.getSelectedRecord().getAttribute("username"));
					message.setSubject(form.getValueAsString("subject"));
					message.setMessage(form.getValueAsString("message"));
					message.setConfirmation("true".equals(form.getValueAsString("confirmation")));
					if (form.getValueAsString("validity") != null)
						message.setValidity(Integer.parseInt(form.getValueAsString("validity")));
					message.setPriority(Integer.parseInt(form.getValue("priority").toString()));
					
					service.save(Session.get().getSid(), message, new AsyncCallback<Void>() {

						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught);
							destroy();
						}

						@Override
						public void onSuccess(Void result) {
							EventPanel.get().info(I18N.message("messagesent"), null);
							destroy();
						}
					});
				}
			}
		});

		form.setFields(recipientItem, recipient, subject, confirmation, validity, priority, message, sendItem);
		addItem(form);
	}

	public void setRecipient(String id) {
		recipient.setValue(id);
	}
}
