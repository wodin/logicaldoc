package com.logicaldoc.gui.frontend.client.security;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.frontend.client.services.SecurityService;
import com.logicaldoc.gui.frontend.client.services.SecurityServiceAsync;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.validator.MatchesFieldValidator;

/**
 * This is the form used to change the password
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class ChangePassword extends Window {
	private static final String PASSWORD = "password";

	private static final String NEWPASSWORDAGAIN = "newpasswordagain";

	private static final String NEWPASSWORD = "newpassword";

	private SecurityServiceAsync securityService = (SecurityServiceAsync) GWT.create(SecurityService.class);

	public ChangePassword(final long userId) {
		super();
		super.setTitle(I18N.getMessage("changepassword"));

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.getMessage("sendmail"));
		setWidth(400);
		setHeight(280);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setPadding(5);
		setAutoSize(true);

		final ValuesManager vm = new ValuesManager();
		final DynamicForm emailForm = new DynamicForm();
		emailForm.setID("changepassword");
		emailForm.setValuesManager(vm);
		emailForm.setWidth(350);
		emailForm.setMargin(5);

		PasswordItem password = new PasswordItem();
		password.setName(PASSWORD);
		password.setTitle(I18N.getMessage(PASSWORD));
		password.setRequired(true);
		password.setWidth(300);

		MatchesFieldValidator validator = new MatchesFieldValidator();
		validator.setOtherField(NEWPASSWORDAGAIN);
		validator.setErrorMessage(I18N.getMessage("passwordnotmatch"));

		PasswordItem newPass = new PasswordItem();
		newPass.setName(NEWPASSWORD);
		newPass.setTitle(I18N.getMessage(NEWPASSWORD));
		newPass.setRequired(true);
		newPass.setValidators(validator);
		newPass.setWidth(300);

		PasswordItem newPassAgain = new PasswordItem();
		newPassAgain.setName(NEWPASSWORDAGAIN);
		newPassAgain.setTitle(I18N.getMessage(NEWPASSWORDAGAIN));
		newPassAgain.setRequired(true);
		newPassAgain.setWidth(300);

		ButtonItem sendItem = new ButtonItem();
		sendItem.setTitle(I18N.getMessage("apply"));
		sendItem.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				vm.validate();
				if (!vm.hasErrors()) {
					securityService.changePassword(userId, vm.getValueAsString(PASSWORD), vm
							.getValueAsString(NEWPASSWORD), new AsyncCallback<Integer>() {

						@Override
						public void onFailure(Throwable caught) {
							SC.warn(caught.getMessage());
						}

						@Override
						public void onSuccess(Integer ret) {
							if (ret.intValue() > 0) {
								// Alert the user and maintain the popup opened
								SC.warn(I18N.getMessage("genericerror"));
							} else {
								// Close the popup
								ChangePassword.this.destroy();
							}
						}
					});

					// GUIEmail mail = new GUIEmail();
					// mail.setRecipients(vm.getValueAsString("recipients"));
					// mail.setCc(vm.getValueAsString("cc"));
					// mail.setObject(vm.getValueAsString("object"));
					// mail.setMessage(vm.getValueAsString("message"));
					// mail.setSendAdTicket("true".equals(vm.getValueAsString("sendticket")));
					// mail.setUser(Session.getInstance().getUser());
					// documentService.sendAsEmail(Session.getInstance().getSid(),
					// mail, new AsyncCallback<String>() {
					//
					// @Override
					// public void onFailure(Throwable caught) {
					// Log.serverError(caught);
					// destroy();
					// }
					//
					// @Override
					// public void onSuccess(String result) {
					// if ("ok".equals(result)) {
					// FooterStatus.getInstance().info("Message sent", null);
					// } else {
					// FooterStatus.getInstance().error("Message not sent",
					// null);
					// }
					// destroy();
					// }
					// });
				}
			}
		});

		emailForm.setFields(password, newPass, newPassAgain, sendItem);
		addItem(emailForm);
	}
}
