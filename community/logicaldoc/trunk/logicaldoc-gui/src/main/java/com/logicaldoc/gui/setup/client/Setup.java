package com.logicaldoc.gui.setup.client;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.util.EmailValidator;
import com.logicaldoc.gui.setup.client.services.SetupService;
import com.logicaldoc.gui.setup.client.services.SetupServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.FormItemIfFunction;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.BooleanItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangeHandler;
import com.smartgwt.client.widgets.form.validator.RequiredIfFunction;
import com.smartgwt.client.widgets.form.validator.RequiredIfValidator;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * The Setup entry point used for initial installation
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class Setup implements EntryPoint {

	private static final String ORACLE = "Oracle";

	private static final String DB_MYSQL = "MySQL";

	private static final String REPOSITORY_FOLDER = "repositoryFolder";

	private static final String SMTP_SECURITY_CONNECTION = "smtpSecurityConnection";

	private static final String SMTP_SENDER = "smtpSender";

	private static final String SMTP_PASSWORD = "smtpPassword";

	private static final String SMTP_SECURE_AUTH = "smtpSecureAuth";

	private static final String SMTP_USERNAME = "smtpUsername";

	private static final String SMTP_PORT = "smtpPort";

	private static final String SMTP_HOST = "smtpHost";

	private static final String LANGUAGE = "language";

	private static final String DB_TYPE = "dbType";

	private static final String DB_ENGINE = "dbEngine";

	private static final String DB_PASSWORD = "dbPassword";

	private static final String DB_USERNAME = "dbUsername";

	private static final String DB_URL = "dbUrl";

	private static final String DB_DRIVER = "dbDriver";

	private static final String INTERNAL = "Internal";

	int step = 0;

	private IButton submit;

	private Dictionary context = Dictionary.getDictionary("context");

	private ValuesManager vm = new ValuesManager();

	private TabSet tabs;

	private Map<String, String[]> engines = new HashMap<String, String[]>();

	@Override
	public void onModuleLoad() {
		// Prepare a value manager that will include all forms spanned in each
		// tab
		vm = new ValuesManager();

		// Create all the tabs each one for a specific setup step
		tabs = new TabSet();
		tabs.setWidth(400);
		tabs.setHeight(210);
		Tab repositoryTab = setupRepository(vm);
		Tab databaseTab = setupDatabase(vm);
		Tab languageTab = setupLanguage(vm);
		Tab smtpTab = setupSmtp(vm);
		tabs.setTabs(repositoryTab, databaseTab, languageTab, smtpTab);

		// This is the button used to confirm each step
		submit = new IButton();
		submit.setTitle(I18N.getMessage("next"));
		submit.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				onSubmit();
			}
		});

		// Prepare the heading panel with Logo and Title
		// Prepare the logo image to be shown inside the login form
		Label header = new Label(I18N.getMessage("setup"));
		header.setStyleName("setupHeader");
		header.setIcon("[SKIN]/brand/logo.png");
		header.setIconWidth(205);
		header.setIconHeight(40);
		header.setHeight(45);

		// Prepare a panel to layout setup components
		VLayout layout = new VLayout();
		layout.setDefaultLayoutAlign(Alignment.RIGHT);
		layout.setHeight(300);
		layout.setWidth(400);
		layout.setMembersMargin(5);
		layout.addMember(header);
		layout.addMember(tabs);
		layout.addMember(submit);

		// Panel for horizontal centering
		VLayout vPanel = new VLayout();
		vPanel.setDefaultLayoutAlign(Alignment.CENTER);
		vPanel.setWidth100();
		vPanel.setHeight(300);
		vPanel.addMember(layout);

		RootPanel.get().add(vPanel);

		// Remove the loading frame
		RootPanel.getBodyElement().removeChild(RootPanel.get("loadingWrapper").getElement());
	}

	/**
	 * Prepares the repository form
	 */
	private Tab setupRepository(final ValuesManager vm) {
		// Prepare the tab used to configure the repository where documents and
		// other data will be stored
		Tab repositoryTab = new Tab();
		repositoryTab.setTitle(I18N.getMessage("repository"));

		final DynamicForm repositoryForm = new DynamicForm();
		repositoryForm.setID("repositoryForm");
		repositoryForm.setValuesManager(vm);
		TextItem repositoryItem = new TextItem();
		repositoryItem.setWidth(200);
		repositoryItem.setName(REPOSITORY_FOLDER);
		repositoryItem.setTitle(I18N.getMessage("repofolder"));
		repositoryItem.setRequired(true);
		repositoryItem.setWrapTitle(false);
		repositoryItem.setDefaultValue(getDefaultFolder());
		repositoryForm.setFields(repositoryItem);
		repositoryTab.setPane(repositoryForm);
		return repositoryTab;
	}

	/**
	 * Prepares the SMTP form
	 */
	private Tab setupSmtp(final ValuesManager vm) {
		// Prepare the SMTP connection tab
		Tab smtpTab = new Tab();
		smtpTab.setTitle(I18N.getMessage("smtpserver"));
		final DynamicForm smtpForm = new DynamicForm();
		smtpForm.setDisabled(true);
		smtpForm.setID("smtpForm");
		smtpForm.setTitleOrientation(TitleOrientation.TOP);
		smtpForm.setValuesManager(vm);
		smtpTab.setPane(smtpForm);

		TextItem smtpHost = new TextItem();
		smtpHost.setTitle(I18N.getMessage("host"));
		smtpHost.setName(SMTP_HOST);
		smtpHost.setWrapTitle(false);

		IntegerItem smtpPort = new IntegerItem();
		smtpPort.setTitle(I18N.getMessage("port"));
		smtpPort.setName(SMTP_PORT);
		smtpPort.setWrapTitle(false);

		TextItem smtpUsername = new TextItem();
		smtpUsername.setTitle(I18N.getMessage("username"));
		smtpUsername.setName(SMTP_USERNAME);
		smtpUsername.setWrapTitle(false);

		PasswordItem smtpPassword = new PasswordItem();
		smtpPassword.setTitle(I18N.getMessage("password"));
		smtpPassword.setName(SMTP_PASSWORD);
		smtpPassword.setWrapTitle(false);

		BooleanItem smtpSecureAuth = new BooleanItem();
		smtpSecureAuth.setTitle(I18N.getMessage("secureauth"));
		smtpSecureAuth.setName(SMTP_SECURE_AUTH);
		smtpSecureAuth.setWrapTitle(false);
		smtpSecureAuth.setDefaultValue(false);

		SelectItem smtpConnectionSecurity = new SelectItem();
		smtpConnectionSecurity.setTitle(I18N.getMessage("connectionsecurity"));
		smtpConnectionSecurity.setName("smtpConnectionSecurity");
		smtpConnectionSecurity.setDefaultValue(Constants.SMTP_SECURITY_NONE);
		smtpConnectionSecurity.setWrapTitle(false);
		LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
		valueMap.put(Constants.SMTP_SECURITY_NONE, "None");
		valueMap.put(Constants.SMTP_SECURITY_SSL, "SSL");
		valueMap.put(Constants.SMTP_SECURITY_TLS, "TLS");
		valueMap.put(Constants.SMTP_SECURITY_TLS_IF_AVAILABLE, "TLS if available");
		smtpConnectionSecurity.setValueMap(valueMap);

		TextItem smtpSender = new TextItem();
		smtpSender.setTitle(I18N.getMessage("sender"));
		smtpSender.setName(SMTP_SENDER);
		smtpSender.setWrapTitle(false);
		smtpSender.setValidators(new EmailValidator());

		smtpForm.setFields(smtpHost, smtpPort, smtpUsername, smtpPassword, smtpSender, smtpConnectionSecurity,
				smtpSecureAuth);
		return smtpTab;
	}

	/**
	 * Prepares the language form
	 */
	private Tab setupLanguage(final ValuesManager vm) {
		Tab languageTab = new Tab();
		languageTab.setTitle(I18N.getMessage(LANGUAGE));
		
		SelectItem languageItem = new SelectItem();
		languageItem.setValueMap(I18N.getSupportedLanguages());
		languageItem.setName(LANGUAGE);
		languageItem.setTitle(I18N.getMessage("defaultlang"));
		languageItem.setRequired(true);
		languageItem.setWrapTitle(false);
		languageItem.setDefaultValue("en");
		final DynamicForm languageForm = new DynamicForm();
		languageForm.setID("languageForm");
		languageForm.setValuesManager(vm);
		languageForm.setFields(languageItem);
		languageForm.setDisabled(true);
		languageTab.setPane(languageForm);
		return languageTab;
	}

	/**
	 * Prepares the database tab
	 */
	private Tab setupDatabase(final ValuesManager vm) {
		// Prepare the map with all database engines
		engines.put(DB_MYSQL, new String[] { "MySQL 5.x", "com.mysql.jdbc.Driver",
				"jdbc:mysql://<server>[,<failoverhost>][<:3306>]/<database>",
				"org.hibernate.dialect.PostgreSQLDialect", "SELECT 1" });
		engines.put("PostgreSQL", new String[] { "PostgreSQL 8.x", "org.postgresql.Driver",
				"jdbc:postgresql:[<//server>[<:5432>/]]<database>", "org.hibernate.dialect.MySQLDialect", "SELECT 1" });
		engines.put(ORACLE, new String[] { "Oracle 9i/10g", "oracle.jdbc.driver.OracleDriver",
				"jdbc:oracle:thin:@<server>[<:1521>]:<database>", "org.hibernate.dialect.OracleDialect",
				"SELECT 1 FROM DUAL" });

		Tab databaseTab = new Tab();
		databaseTab.setTitle(I18N.getMessage("database"));

		final DynamicForm databaseForm = new DynamicForm();
		databaseForm.setID("database");
		databaseForm.setValuesManager(vm);
		databaseForm.setDisabled(true);

		RadioGroupItem dbType = new RadioGroupItem();
		dbType.setName(DB_TYPE);
		dbType.setWrapTitle(false);
		dbType.setRequired(true);
		dbType.setVertical(false);
		dbType.setValueMap(INTERNAL, I18N.getMessage("external"));
		dbType.setValue(INTERNAL);
		dbType.setRedrawOnChange(true);
		dbType.setTitle(I18N.getMessage("dbtype"));

		// The database engine, if the External db was chosen
		SelectItem dbEngine = new SelectItem();
		dbEngine.setTitle(I18N.getMessage("dbengine"));
		dbEngine.setWrapTitle(false);
		dbEngine.setVisible(false);
		dbEngine.setName(DB_ENGINE);
		dbEngine.setDefaultValue(DB_MYSQL);
		LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
		for (String engine : engines.keySet()) {
			valueMap.put(engine, engines.get(engine)[0]);
		}
		dbEngine.setValueMap(valueMap);
		dbEngine.setShowIfCondition(new FormItemIfFunction() {
			public boolean execute(FormItem item, Object value, DynamicForm form) {
				return !INTERNAL.equals(databaseForm.getValue(DB_TYPE));
			}
		});
		RequiredIfValidator ifValidator = new RequiredIfValidator();
		ifValidator.setExpression(new RequiredIfFunction() {
			public boolean execute(FormItem formItem, Object value) {
				return !INTERNAL.equals(databaseForm.getValue(DB_TYPE));
			}
		});
		dbEngine.setValidators(ifValidator);
		dbEngine.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				String selectedItem = (String) event.getValue();
				databaseForm.getField(DB_DRIVER).setValue(engines.get(selectedItem)[1]);
				databaseForm.getField(DB_URL).setValue(engines.get(selectedItem)[2]);
			}
		});

		// The driver for the external DB
		TextItem dbDriver = new TextItem();
		dbDriver.setVisible(false);
		dbDriver.setTitle(I18N.getMessage("driverclass"));
		dbDriver.setName(DB_DRIVER);
		dbDriver.setDefaultValue(engines.get(DB_MYSQL)[1]);
		dbDriver.setWrapTitle(false);
		dbDriver.setShowIfCondition(new FormItemIfFunction() {
			public boolean execute(FormItem item, Object value, DynamicForm form) {
				return !INTERNAL.equals(databaseForm.getValue(DB_TYPE));
			}
		});
		dbDriver.setValidators(ifValidator);

		// The connection URL to external DB
		TextItem dbUrl = new TextItem();
		dbUrl.setWidth(200);
		dbUrl.setVisible(false);
		dbUrl.setTitle(I18N.getMessage("connectionurl"));
		dbUrl.setName(DB_URL);
		dbUrl.setDefaultValue(engines.get(DB_MYSQL)[2]);
		dbUrl.setWrapTitle(false);
		dbUrl.setShowIfCondition(new FormItemIfFunction() {
			public boolean execute(FormItem item, Object value, DynamicForm form) {
				return !INTERNAL.equals(databaseForm.getValue(DB_TYPE));
			}
		});
		dbUrl.setValidators(ifValidator);

		// The username to access the external DB
		TextItem dbUsername = new TextItem();
		dbUsername.setVisible(false);
		dbUsername.setTitle(I18N.getMessage("username"));
		dbUsername.setName(DB_USERNAME);
		dbUsername.setWrapTitle(false);
		dbUsername.setShowIfCondition(new FormItemIfFunction() {
			public boolean execute(FormItem item, Object value, DynamicForm form) {
				return !INTERNAL.equals(databaseForm.getValue(DB_TYPE));
			}
		});

		// The password to access the external DB
		PasswordItem dbPassword = new PasswordItem();
		dbPassword.setVisible(false);
		dbPassword.setTitle(I18N.getMessage("password"));
		dbPassword.setName(DB_PASSWORD);
		dbPassword.setWrapTitle(false);
		dbPassword.setShowIfCondition(new FormItemIfFunction() {
			public boolean execute(FormItem item, Object value, DynamicForm form) {
				return !INTERNAL.equals(databaseForm.getValue(DB_TYPE));
			}
		});

		databaseForm.setFields(dbType, dbEngine, dbDriver, dbUrl, dbUsername, dbPassword);
		databaseTab.setPane(databaseForm);
		return databaseTab;
	}

	private void onSubmit() {
		vm.validate();
		Tab tab = tabs.getSelectedTab();
		int tabIndex = tabs.getSelectedTabNumber();
		DynamicForm form = (DynamicForm) tab.getPane();
		if (form.hasErrors()) {

		} else {
			if (step == 3) {
				if (!vm.validate())
					SC.warn("invalidfields");

				SetupInfo data = new SetupInfo();
				data.setDbDriver(vm.getValueAsString(DB_DRIVER));
				data.setDbUrl(vm.getValueAsString(DB_URL));
				data.setDbUsername(vm.getValueAsString(DB_USERNAME));
				data.setDbPassword(vm.getValueAsString(DB_PASSWORD));
				data.setDbEngine(vm.getValueAsString(DB_ENGINE));
				data.setDbType(vm.getValueAsString(DB_TYPE));
				data.setLanguage(vm.getValueAsString(LANGUAGE));
				data.setSmtpHost(vm.getValueAsString(SMTP_HOST));
				data.setSmtpPort((Integer) vm.getValues().get(SMTP_PORT));
				data.setSmtpUsername(vm.getValueAsString(SMTP_USERNAME));
				data.setSmtpPassword(vm.getValueAsString(SMTP_PASSWORD));
				data.setSmtpSender(SMTP_SENDER);
				data.setSmtpSecureAuth((Boolean) vm.getValues().get(SMTP_SECURE_AUTH));
				data.setSmtpSecuryConntection(vm.getValueAsString(SMTP_SECURITY_CONNECTION));
				data.setRepositoryFolder(vm.getValueAsString(REPOSITORY_FOLDER));
				data.setDbDialect(engines.get(data.getDbEngine())[3]);
				data.setDbValidationQuery(engines.get(data.getDbEngine())[4]);
				if (data.getDbType().equals(INTERNAL)) {
					data.setDbEngine("Hsqldb");
					data.setDbDriver("org.hsqldb.jdbcDriver");
					data.setDbUrl(("jdbc:hsqldb:" + data.getRepositoryFolder() + "/db").replaceAll("//", "/"));
					data.setDbUsername("sa");
					data.setDbPassword("");
					data.setDbValidationQuery("SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS");
				}

				SetupServiceAsync setupService = (SetupServiceAsync) GWT.create(SetupService.class);
				setupService.setup(data, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						SC.warn(caught.getMessage());
					}

					@Override
					public void onSuccess(Void arg) {
						SC.say(I18N.getMessage("installationperformed"), I18N.getMessage("installationend", context
								.get("product_name")));
					}
				});
			} else {
				// Go to the next tab and enable the contained panel
				tabs.selectTab(tabIndex + 1);
				tabs.getSelectedTab().getPane().setDisabled(false);
				if (step < tabs.getSelectedTabNumber())
					step++;
				if (step == 3)
					submit.setTitle(I18N.getMessage("setup"));
			}
		}
	}

	public static native String getDefaultFolder() /*-{
		return $wnd.defaultFolder;
	}-*/;
}