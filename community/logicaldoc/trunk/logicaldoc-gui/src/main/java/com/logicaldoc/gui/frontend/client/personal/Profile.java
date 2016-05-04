package com.logicaldoc.gui.frontend.client.personal;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.services.SecurityService;
import com.logicaldoc.gui.common.client.services.SecurityServiceAsync;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.RichTextItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * This is the form used to change file data of the current user.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class Profile extends Window {

	private SecurityServiceAsync securityService = (SecurityServiceAsync) GWT.create(SecurityService.class);

	private ValuesManager vm = new ValuesManager();

	public Profile(final GUIUser user) {
		super();

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("profile"));
		setWidth(550);
		setHeight(300);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();
		setMembersMargin(5);
		setCanDragResize(true);

		final DynamicForm detailsForm = new DynamicForm();
		detailsForm.setHeight100();
		detailsForm.setValuesManager(vm);
		detailsForm.setMargin(5);
		detailsForm.setNumCols(3);
		detailsForm.setTitleOrientation(TitleOrientation.TOP);

		TextItem firstName = ItemFactory.newTextItem("firstname", "firstname", user.getFirstName());
		firstName.setRequired(true);
		TextItem lastName = ItemFactory.newTextItem("lastname", "lastname", user.getName());
		lastName.setRequired(true);

		SelectItem language = ItemFactory.newLanguageSelector("language", false, true);
		language.setValue(user.getLanguage());
		language.setDisabled(Session.get().isDemo() && Session.get().getUser().getId() == 1);
		TextItem address = ItemFactory.newTextItem("address", "address", user.getAddress());
		TextItem postalCode = ItemFactory.newTextItem("postalcode", "postalcode", user.getPostalCode());
		TextItem city = ItemFactory.newTextItem("city", "city", user.getCity());
		TextItem country = ItemFactory.newTextItem("country", "country", user.getCountry());
		TextItem state = ItemFactory.newTextItem("state", "state", user.getState());
		TextItem phone = ItemFactory.newTextItem("phone", "phone", user.getPhone());
		TextItem cell = ItemFactory.newTextItem("cell", "cell", user.getCell());
		SelectItem welcomeScreen = ItemFactory.newWelcomeScreenSelector("welcomescreen", user.getWelcomeScreen());

		StaticTextItem quota = ItemFactory.newStaticTextItem("quota", "maxquota", Util.formatSizeW7(user.getQuota()));
		quota.setWrap(false);

		StaticTextItem quotaCount = ItemFactory.newStaticTextItem("quotaCount", "quota",
				Util.formatSizeW7(user.getQuotaCount()));
		quotaCount.setWrap(false);

		detailsForm.setFields(firstName, lastName, language, address, postalCode, city, country, state, phone, cell,
				welcomeScreen, quotaCount, quota);

		final DynamicForm emailForm = new DynamicForm();
		emailForm.setHeight100();
		emailForm.setValuesManager(vm);
		emailForm.setMargin(5);
		emailForm.setTitleOrientation(TitleOrientation.TOP);

		TextItem email = ItemFactory.newEmailItem("email", "email", false);
		email.setRequired(true);
		email.setWidth(300);
		email.setValue(user.getEmail());

		RichTextItem signature = new RichTextItem();
		signature.setName("signature");
		signature.setTitle(I18N.message("signature"));
		signature.setShowTitle(true);
		signature.setValue(user.getEmailSignature());
		signature.setWidth("*");
		signature.setHeight("*");

		emailForm.setFields(email, signature);

		final TabSet tabs = new TabSet();
		tabs.setHeight100();
		tabs.setWidth100();
		Tab detailsTab = new Tab(I18N.message("details"));
		detailsTab.setPane(detailsForm);
		Tab emailTab = new Tab(I18N.message("email"));
		emailTab.setPane(emailForm);
		tabs.setTabs(detailsTab, emailTab);

		IButton apply = new IButton();
		apply.setTitle(I18N.message("apply"));
		apply.setAutoFit(true);
		apply.setMargin(3);
		apply.setHeight(30);
		apply.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				vm.validate();

				if (!detailsForm.validate())
					tabs.selectTab(0);
				else
					tabs.selectTab(1);

				if (!vm.hasErrors()) {
					GUIUser u = new GUIUser();
					u.setId(user.getId());
					u.setFirstName(vm.getValueAsString("firstname"));
					u.setName(vm.getValueAsString("lastname"));
					u.setEmail(vm.getValueAsString("email"));
					u.setLanguage(vm.getValueAsString("language"));
					u.setAddress(vm.getValueAsString("address"));
					u.setPostalCode(vm.getValueAsString("postalcode"));
					u.setCity(vm.getValueAsString("city"));
					u.setCountry(vm.getValueAsString("country"));
					u.setState(vm.getValueAsString("state"));
					u.setPhone(vm.getValueAsString("phone"));
					u.setCell(vm.getValueAsString("cell"));
					u.setWelcomeScreen(new Integer(vm.getValueAsString("welcomescreen")));
					u.setEmailSignature(vm.getValueAsString("signature"));

					securityService.saveProfile(u, new AsyncCallback<GUIUser>() {
						@Override
						public void onFailure(Throwable caught) {
							SC.warn(caught.getMessage());
						}

						@Override
						public void onSuccess(GUIUser ret) {
							// Update the currently logged user bean
							user.setFirstName(ret.getFirstName());
							user.setName(ret.getName());
							user.setEmail(ret.getEmail());
							user.setEmailSignature(ret.getEmailSignature());
							user.setLanguage(ret.getLanguage());
							user.setAddress(ret.getAddress());
							user.setPostalCode(ret.getPostalCode());
							user.setCity(ret.getCity());
							user.setCountry(ret.getCountry());
							user.setState(ret.getState());
							user.setPhone(ret.getPhone());
							user.setCell(ret.getCell());
							user.setWelcomeScreen(ret.getWelcomeScreen());

							Session.get().setUser(user);
							
							Profile.this.destroy();
						}
					});
				}
			}
		});

		HLayout buttons = new HLayout();
		buttons.setMembers(apply);

		addItem(tabs);
		addItem(buttons);
	}
}
