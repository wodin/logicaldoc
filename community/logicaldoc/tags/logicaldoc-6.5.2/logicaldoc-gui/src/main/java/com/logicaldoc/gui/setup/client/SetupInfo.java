package com.logicaldoc.gui.setup.client;

import java.io.Serializable;

/**
 * A simple bean collecting all needed setup informations
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class SetupInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String repositoryFolder;

	private String dbType;

	private String smtpHost;

	private Integer smtpPort = 25;

	private String smtpUsername;

	private String smtpPassword;

	private String smtpSender = "logicaldoc@acme.com";

	private boolean smtpSecureAuth = false;

	private String smtpSecuryConntection;

	private String language;

	private String dbEngine;

	private String dbDriver;

	private String dbUrl;

	private String dbUsername;

	private String dbPassword;

	private String dbValidationQuery;

	private String dbDialect;

	private String regName;

	private String regOrganization;

	private String regEmail;

	private String regWebsite;

	public String getRepositoryFolder() {
		return repositoryFolder;
	}

	public void setRepositoryFolder(String repositoryFolder) {
		this.repositoryFolder = repositoryFolder;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getSmtpHost() {
		return smtpHost;
	}

	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}

	public String getSmtpUsername() {
		return smtpUsername;
	}

	public void setSmtpUsername(String smtpUsername) {
		this.smtpUsername = smtpUsername;
	}

	public String getSmtpPassword() {
		return smtpPassword;
	}

	public void setSmtpPassword(String smtpPassword) {
		this.smtpPassword = smtpPassword;
	}

	public String getSmtpSender() {
		return smtpSender;
	}

	public void setSmtpSender(String smtpSender) {
		this.smtpSender = smtpSender;
	}

	public String getSmtpConnectionSecurity() {
		return smtpSecuryConntection;
	}

	public void setSmtpSecuryConntection(String smtpSecuryConntection) {
		this.smtpSecuryConntection = smtpSecuryConntection;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getDbEngine() {
		return dbEngine;
	}

	public void setDbEngine(String dbEngine) {
		this.dbEngine = dbEngine;
	}

	public String getDbDriver() {
		return dbDriver;
	}

	public void setDbDriver(String dbDriver) {
		this.dbDriver = dbDriver;
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public String getDbUsername() {
		return dbUsername;
	}

	public void setDbUsername(String dbUsername) {
		this.dbUsername = dbUsername;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	public String getDbValidationQuery() {
		return dbValidationQuery;
	}

	public void setDbValidationQuery(String dbValidationQuery) {
		this.dbValidationQuery = dbValidationQuery;
	}

	public String getDbDialect() {
		return dbDialect;
	}

	public void setDbDialect(String dbDialect) {
		this.dbDialect = dbDialect;
	}

	public boolean isSmtpSecureAuth() {
		return smtpSecureAuth;
	}

	public void setSmtpSecureAuth(boolean smtpSecureAuth) {
		this.smtpSecureAuth = smtpSecureAuth;
	}

	public Integer getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(Integer smtpPort) {
		this.smtpPort = smtpPort;
	}

	public String getSmtpSecuryConntection() {
		return smtpSecuryConntection;
	}

	public String getRegName() {
		return regName;
	}

	public void setRegName(String regName) {
		this.regName = regName;
	}

	public String getRegOrganization() {
		return regOrganization;
	}

	public void setRegOrganization(String regOrganization) {
		this.regOrganization = regOrganization;
	}

	public String getRegEmail() {
		return regEmail;
	}

	public void setRegEmail(String regEmail) {
		this.regEmail = regEmail;
	}

	public String getRegWebsite() {
		return regWebsite;
	}

	public void setRegWebsite(String regWebsite) {
		this.regWebsite = regWebsite;
	}
}