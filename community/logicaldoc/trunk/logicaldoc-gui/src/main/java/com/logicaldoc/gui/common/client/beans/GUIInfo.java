package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;

/**
 * General product informations
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class GUIInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String productName = "LogicalDOC Community (free)";

	private String product = "LogicalDOC";

	private String release = "6.7";

	private String year = "2013";

	private String help = "http://help.logicaldoc.com";

	private String bugs = "http://bugs.logicaldoc.com";

	private String url = "http://www.logicaldoc.com";

	private String forum = "http://forums.logicaldoc.com";

	private String vendor = "Logical Objects Srl";

	private String vendorAddress = "via Aldo Moro interna, 3";

	private String vendorCap = "41012";

	private String vendorCountry = "Italy";

	private String vendorCity = "Carpi";

	private String support = "support@logicaldoc.com";

	private String installationId;

	private String licensee;

	private int sessionHeartbeat = -1;

	private String runLevel;

	// Optional list of messages to be shown to the user
	private GUIMessage[] messages = new GUIMessage[0];

	private GUIValuePair[] supportedLanguages = new GUIValuePair[0];

	private GUIValuePair[] supportedGUILanguages = new GUIValuePair[0];

	private GUIValuePair[] bundle = new GUIValuePair[0];

	private GUIValuePair[] config = new GUIValuePair[0];

	private String[] features = new String[0];

	private boolean databaseConnected = true;

	private String logoSrc = "data:image/png;base64,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";

	private String logoHeadSrc = "data:image/png;base64,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";

	
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getRelease() {
		return release;
	}

	public void setRelease(String release) {
		this.release = release;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getHelp() {
		return help;
	}

	public void setHelp(String help) {
		this.help = help;
	}

	public String getBugs() {
		return bugs;
	}

	public void setBugs(String bugs) {
		this.bugs = bugs;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public GUIMessage[] getMessages() {
		return messages;
	}

	public void setMessages(GUIMessage[] messages) {
		this.messages = messages;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getVendorAddress() {
		return vendorAddress;
	}

	public void setVendorAddress(String vendorAddress) {
		this.vendorAddress = vendorAddress;
	}

	public String getVendorCap() {
		return vendorCap;
	}

	public void setVendorCap(String vendorCap) {
		this.vendorCap = vendorCap;
	}

	public String getVendorCountry() {
		return vendorCountry;
	}

	public void setVendorCountry(String vendorCountry) {
		this.vendorCountry = vendorCountry;
	}

	public String getVendorCity() {
		return vendorCity;
	}

	public void setVendorCity(String vendorCity) {
		this.vendorCity = vendorCity;
	}

	public GUIValuePair[] getSupportedLanguages() {
		return supportedLanguages;
	}

	public void setSupportedLanguages(GUIValuePair[] supportedLanguages) {
		this.supportedLanguages = supportedLanguages;
	}

	public GUIValuePair[] getBundle() {
		return bundle;
	}

	public void setBundle(GUIValuePair[] bundle) {
		this.bundle = bundle;
	}

	public String[] getFeatures() {
		return features;
	}

	public boolean isEnabled(String feature) {
		if (features == null || features.length == 0)
			return false;
		else {
			for (String f : features) {
				if (f.equals(feature))
					return true;
			}
		}
		return false;
	}

	public void setFeatures(String[] features) {
		this.features = features;
	}

	public String getInstallationId() {
		return installationId;
	}

	public void setInstallationId(String installationId) {
		this.installationId = installationId;
	}

	public GUIValuePair[] getSupportedGUILanguages() {
		return supportedGUILanguages;
	}

	public void setSupportedGUILanguages(GUIValuePair[] supportedGUILanguages) {
		this.supportedGUILanguages = supportedGUILanguages;
	}

	public String getSupport() {
		return support;
	}

	public void setSupport(String support) {
		this.support = support;
	}

	public String getLicensee() {
		return licensee;
	}

	public void setLicensee(String licensee) {
		this.licensee = licensee;
	}

	public int getSessionHeartbeat() {
		return sessionHeartbeat;
	}

	public void setSessionHeartbeat(int sessionHeartbeat) {
		this.sessionHeartbeat = sessionHeartbeat;
	}

	public GUIValuePair[] getConfig() {
		return config;
	}

	public String getConfig(String name) {
		for (GUIValuePair val : getConfig()) {
			if (name.equals(val.getCode()))
				return val.getValue();
		}
		return null;
	}

	public void setConfig(String name, String value) {
		for (GUIValuePair val : getConfig()) {
			if (name.equals(val.getCode())) {
				val.setValue(value);
				return;
			}
		}
	}

	public void setConfig(GUIValuePair[] config) {
		this.config = config;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getForum() {
		return forum;
	}

	public void setForum(String forum) {
		this.forum = forum;
	}

	public String getRunLevel() {
		return runLevel;
	}

	public void setRunLevel(String runLevel) {
		this.runLevel = runLevel;
	}

	public boolean isDatabaseConnected() {
		return databaseConnected;
	}

	public void setDatabaseConnected(boolean databaseConnected) {
		this.databaseConnected = databaseConnected;
	}

	public String getLogoSrc() {
		return logoSrc;
	}

	public void setLogoSrc(String logoSrc) {
		this.logoSrc = logoSrc;
	}

	public String getLogoHeadSrc() {
		return logoHeadSrc;
	}

	public void setLogoHeadSrc(String logoHeadSrc) {
		this.logoHeadSrc = logoHeadSrc;
	}
}