package com.logicaldoc.web.setup;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;

import com.logicaldoc.util.config.DBMSConfigurator;

/**
 * 
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.0
 */
public class ConnectionData {

	private String dbms;

	private String clazz;

	private String dburl;

	private String user;

	private String pswd;

	private String validationQuery;

	private UIComponent classInput;

	private UIComponent dburlInput;

	private UIComponent userInput;

	private UIComponent passwordInput;

	/** Creates a new instance of ConnectionForm */
	public ConnectionData() {
		dbms = "";
		clazz = "";
		dburl = "";
		user = "";
		pswd = "";
	}

	void clear() {
		((UIInput) classInput).setValue(getClazz());
		((UIInput) classInput).setSubmittedValue(null);
		((UIInput) dburlInput).setValue(getDburl());
		((UIInput) dburlInput).setSubmittedValue(null);
		((UIInput) userInput).setValue(getUser());
		((UIInput) userInput).setSubmittedValue(null);
		((UIInput) passwordInput).setValue(getPswd());
		((UIInput) passwordInput).setSubmittedValue(null);
	}

	public UIComponent getClassInput() {
		return classInput;
	}

	public void setClassInput(UIComponent classInput) {
		this.classInput = classInput;
	}

	public UIComponent getPasswordInput() {
		return passwordInput;
	}

	public void setPasswordInput(UIComponent passwordInput) {
		this.passwordInput = passwordInput;
	}

	public UIComponent getUserInput() {
		return userInput;
	}

	public void setUserInput(UIComponent userInput) {
		this.userInput = userInput;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getDbms() {
		return dbms;
	}

	public void setDbms(String dbms) {
		this.dbms = dbms;
	}

	public String getDialect() {
		DBMSConfigurator conf = new DBMSConfigurator();
		return conf.getAttribute(dbms, "dialect");
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getDburl() {
		return dburl;
	}

	public void setDburl(String url) {
		this.dburl = url;
	}

	public UIComponent getDburlInput() {
		return dburlInput;
	}

	public void setDburlInput(UIComponent urlInput) {
		this.dburlInput = urlInput;
	}

	public String getValidationQuery() {
		return validationQuery;
	}

	public void setValidationQuery(String validationQuery) {
		this.validationQuery = validationQuery;
	}

	public String getPswd() {
		return pswd;
	}

	public void setPswd(String pswd) {
		this.pswd = pswd;
	}
}