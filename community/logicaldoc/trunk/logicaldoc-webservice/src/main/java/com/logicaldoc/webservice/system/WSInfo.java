package com.logicaldoc.webservice.system;

import java.io.Serializable;

/**
 * General product informations
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.1
 */
public class WSInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String productName = "LogicalDOC Community";

	private String product = "LogicalDOC";

	private String release = "6.1";

	private String year = "2011";

	private String help = "http://help.logicaldoc.com";

	private String bugs = "http://bugs.logicaldoc.com";

	private String url = "http://www.logicaldoc.com";

	private String forum = "http://forums.logicaldoc.com";

	private String vendor = "Logical Objects Srl";

	private String vendorAddress = "via Carlo Marx 131/2";

	private String vendorCap = "41012";

	private String vendorCountry = "Italy";

	private String vendorCity = "Carpi";

	private String support = "support@logicaldoc.com";

	private String installationId;

	private String licensee;

	private String[] features = new String[0];

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

	public String[] getFeatures() {
		return features;
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
}