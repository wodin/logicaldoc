package com.logicaldoc.gui.frontend.client.personal;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.services.SecurityService;
import com.logicaldoc.gui.common.client.services.SecurityServiceAsync;
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
 * This is the form used to change the password of the current user, for example
 * when the password expired.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class ChangePassword extends Window {
	private static final String PASSWORD = "password";

	private static final String NEWPASSWORDAGAIN = "newpasswordagain";

	private static final String NEWPASSWORD = "newpassword";

	private SecurityServiceAsync securityService = (SecurityServiceAsync) GWT.create(SecurityService.class);

	public ChangePassword(final GUIUser user, String alertKey) {
		super();

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("changepassword"));
		setWidth(300);
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
		password.setTitle(I18N.message(PASSWORD));
		password.setRequired(true);

		MatchesFieldValidator equalsValidator = new MatchesFieldValidator();
		equalsValidator.setOtherField(NEWPASSWORDAGAIN);
		equalsValidator.setErrorMessage(I18N.message("passwordnotmatch"));

		LengthRangeValidator sizeValidator = new LengthRangeValidator();
		sizeValidator
				.setErrorMessage(I18N.message("errorfieldminlenght", Integer.toString(user.getPasswordMinLenght())));
		sizeValidator.setMin(user.getPasswordMinLenght());

		PasswordItem newPass = new PasswordItem();
		newPass.setName(NEWPASSWORD);
		newPass.setTitle(I18N.message(NEWPASSWORD));
		newPass.setRequired(true);
		newPass.setValidators(equalsValidator, sizeValidator);

		PasswordItem newPassAgain = new PasswordItem();
		newPassAgain.setName(NEWPASSWORDAGAIN);
		newPassAgain.setTitle(I18N.message(NEWPASSWORDAGAIN));
		newPassAgain.setWrapTitle(false);
		newPassAgain.setRequired(true);

		ButtonItem apply = new ButtonItem();
		apply.setTitle(I18N.message("apply"));
		apply.setAutoFit(true);
		apply.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				vm.validate();
				if (!vm.hasErrors()) {
					if (vm.getValueAsString(PASSWORD).equals(vm.getValueAsString(NEWPASSWORD))) {
						Map<String, String> errors = new HashMap<String, String>();
						errors.put(NEWPASSWORD, I18N.message("useanotherpassword"));
						vm.setErrors(errors, true);
						return;
					}

					securityService.changePassword(user.getId(), vm.getValueAsString(PASSWORD),
							vm.getValueAsString(NEWPASSWORD), true, new AsyncCallback<Integer>() {

								@Override
								public void onFailure(Throwable caught) {
									SC.warn(caught.getMessage());
								}

								@Override
								public void onSuccess(Integer ret) {
									if (ret.intValue() > 0) {
										// Alert the user and maintain the popup
										// opened
										if (ret == 1)
											SC.warn(I18N.message("wrongpassword"));
										else if (ret == 2)
											SC.warn(I18N.message("passwdnotnotified"));
										else
											SC.warn(I18N.message("genericerror"));
									} else {
										// Close the popup
										ChangePassword.this.destroy();
										Log.info(I18N.message("event.user.passwordchanged"), null);
									}
								}
							});
				}
			}
		});

		form.setFields(password, newPass, newPassAgain, apply);

		if (alertKey != null) {
			Label label = new Label();
			label.setHeight(30);
			label.setPadding(10);
			label.setMargin(5);
			label.setAlign(Alignment.CENTER);
			label.setValign(VerticalAlignment.CENTER);
			label.setWrap(false);
			label.setIcon("[SKIN]/Dialog/warn.png");
			label.setContents(I18N.message(alertKey));
			addItem(label);
		}

		addItem(form);
	}
}
