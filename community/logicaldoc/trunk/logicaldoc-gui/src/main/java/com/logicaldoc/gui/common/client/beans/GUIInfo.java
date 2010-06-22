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

	private String productName = "LogicalDOC Community";

	private String release = "5.2";

	private String year = "2010";

	private String help = "http://help.logicaldoc.com";

	private String bugs = "http://bugs.logicaldoc.com";

	private String url = "http://www.logicaldoc.com";

	private String vendor = "Logical Objects Srl";

	private String vendorAddress = "via Carlo Marx 131/2";

	private String vendorCap = "41012";

	private String vendorCountry = "Italy";

	private String vendorCity = "Carpi";

	private String vendorSupport = "support@logicaldoc.com";

	// Optional list of messages to be shown to the user
	private GUIMessage[] messages;
	
	private GUILanguage[] supportedLanguages;

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

	public String getVendorSupport() {
		return vendorSupport;
	}

	public void setVendorSupport(String vendorSupport) {
		this.vendorSupport = vendorSupport;
	}

	public GUILanguage[] getSupportedLanguages() {
		return supportedLanguages;
	}

	public void setSupportedLanguages(GUILanguage[] supportedLanguages) {
		this.supportedLanguages = supportedLanguages;
	}

}