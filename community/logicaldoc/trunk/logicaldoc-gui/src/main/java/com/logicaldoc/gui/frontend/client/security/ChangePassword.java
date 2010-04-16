package com.logicaldoc.gui.frontend.client.security;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.frontend.client.services.SecurityService;
import com.logicaldoc.gui.frontend.client.services.SecurityServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.validator.LengthRangeValidator;
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

	public ChangePassword(final GUIUser user) {
		super();

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.getMessage("changepassword"));
		setWidth(300);
		setHeight(180);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setMembersMargin(5);
		setAutoSize(true);

		final ValuesManager vm = new ValuesManager();
		final DynamicForm form = new DynamicForm();
		form.setValuesManager(vm);
		form.setWidth(350);
		form.setMargin(5);

		PasswordItem password = new PasswordItem();
		password.setName(PASSWORD);
		password.setTitle(I18N.getMessage(PASSWORD));
		password.setRequired(true);

		MatchesFieldValidator equalsValidator = new MatchesFieldValidator();
		equalsValidator.setOtherField(NEWPASSWORDAGAIN);
		equalsValidator.setErrorMessage(I18N.getMessage("passwordnotmatch"));

		LengthRangeValidator sizeValidator = new LengthRangeValidator();
		sizeValidator.setErrorMessage(I18N.getMessage("errorfieldminlenght", Integer.toString(user
				.getPasswordMinLenght())));
		sizeValidator.setMin(user.getPasswordMinLenght());

		PasswordItem newPass = new PasswordItem();
		newPass.setName(NEWPASSWORD);
		newPass.setTitle(I18N.getMessage(NEWPASSWORD));
		newPass.setRequired(true);
		newPass.setValidators(equalsValidator, sizeValidator);

		PasswordItem newPassAgain = new PasswordItem();
		newPassAgain.setName(NEWPASSWORDAGAIN);
		newPassAgain.setTitle(I18N.getMessage(NEWPASSWORDAGAIN));
		newPassAgain.setWrapTitle(false);
		newPassAgain.setRequired(true);

		ButtonItem sendItem = new ButtonItem();
		sendItem.setTitle(I18N.getMessage("apply"));
		sendItem.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				vm.validate();
				if (!vm.hasErrors()) {
					if (vm.getValueAsString(PASSWORD).equals(vm.getValueAsString(NEWPASSWORD))) {
						Map<String, String> errors = new HashMap<String, String>();
						errors.put(NEWPASSWORD, I18N.getMessage("useanotherpassword"));
						vm.setErrors(errors, true);
						return;
					}

					securityService.changePassword(user.getId(), vm.getValueAsString(PASSWORD), vm
							.getValueAsString(NEWPASSWORD), new AsyncCallback<Integer>() {

						@Override
						public void onFailure(Throwable caught) {
							SC.warn(caught.getMessage());
						}

						@Override
						public void onSuccess(Integer ret) {
							if (ret.intValue() > 0) {
								// Alert the user and maintain the popup opened
								if (ret == 1)
									SC.warn(I18N.getMessage("wrongpassword"));
								else
									SC.warn(I18N.getMessage("genericerror"));
							} else {
								// Close the popup
								ChangePassword.this.destroy();
							}
						}
					});
				}
			}
		});

		form.setFields(password, newPass, newPassAgain, sendItem);

		Label label = new Label();
		label.setHeight(30);
		label.setPadding(10);
		label.setMargin(5);
		label.setAlign(Alignment.CENTER);
		label.setValign(VerticalAlignment.CENTER);
		label.setWrap(false);
		label.setIcon("[SKIN]/Dialog/warn.png");
		label.setContents(I18N.getMessage("needtochangepassword"));

		addItem(label);
		addItem(form);
	}
}
