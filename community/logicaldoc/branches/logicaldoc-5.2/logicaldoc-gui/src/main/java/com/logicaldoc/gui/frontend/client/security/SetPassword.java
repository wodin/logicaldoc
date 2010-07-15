package com.logicaldoc.gui.frontend.client.security;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
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
import com.smartgwt.client.widgets.form.validator.LengthRangeValidator;
import com.smartgwt.client.widgets.form.validator.MatchesFieldValidator;

/**
 * This is the form used to change the password of a user from the
 * administration interface.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class SetPassword extends Window {
	private static final String NEWPASSWORDAGAIN = "newpasswordagain";

	private static final String NEWPASSWORD = "newpassword";

	private SecurityServiceAsync securityService = (SecurityServiceAsync) GWT.create(SecurityService.class);

	public SetPassword(final long userId) {
		super();

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("changepassword"));
		setWidth(300);
		setHeight(140);
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

		MatchesFieldValidator equalsValidator = new MatchesFieldValidator();
		equalsValidator.setOtherField(NEWPASSWORDAGAIN);
		equalsValidator.setErrorMessage(I18N.message("passwordnotmatch"));

		LengthRangeValidator sizeValidator = new LengthRangeValidator();
		sizeValidator.setErrorMessage(I18N.message("errorfieldminlenght", Integer.toString(Session.get().getUser()
				.getPasswordMinLenght())));
		sizeValidator.setMin(Session.get().getUser().getPasswordMinLenght());

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
		apply.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				vm.validate();
				if (!vm.hasErrors()) {

					securityService.changePassword(userId, null, vm.getValueAsString(NEWPASSWORD),
							new AsyncCallback<Integer>() {

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
											Log.warn(I18N.message("wrongpassword"), null);
										else if (ret == 2)
											Log.warn(I18N.message("passwdnotnotified"), null);
										else
											Log.warn(I18N.message("genericerror"), null);
									}
									SetPassword.this.destroy();
								}
							});
				}
			}
		});

		form.setFields(newPass, newPassAgain, apply);

		addItem(form);
	}
}
