package com.logicaldoc.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java.plugin.registry.Extension;

import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.plugin.PluginRegistry;

public class SystemInfo {
	protected static Log log = LogFactory.getLog(SystemInfo.class);

	private static final long serialVersionUID = 1L;

	private String productName = "LogicalDOC Community";

	private String product = "LogicalDOC";

	private String release = "6.1";

	private String year = "2010";

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

	private String runLevel;

	private String[] features;

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getForum() {
		return forum;
	}

	public void setForum(String forum) {
		this.forum = forum;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
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

	public String getSupport() {
		return support;
	}

	public void setSupport(String support) {
		this.support = support;
	}

	public String getInstallationId() {
		return installationId;
	}

	public void setInstallationId(String installationId) {
		this.installationId = installationId;
	}

	public String getLicensee() {
		return licensee;
	}

	public void setLicensee(String licensee) {
		this.licensee = licensee;
	}

	public String getRunLevel() {
		return runLevel;
	}

	public void setRunLevel(String runLevel) {
		this.runLevel = runLevel;
	}

	public static SystemInfo get() {
		SystemInfo info = new SystemInfo();

		/*
		 * Collect product identification
		 */
		try {
			// Acquire the 'SystemInfo' extensions of the core plugin
			PluginRegistry registry = PluginRegistry.getInstance();
			Collection<Extension> exts = registry.getExtensions("logicaldoc-core", "SystemInfo");

			if (!exts.isEmpty()) {
				String className = exts.iterator().next().getParameter("class").valueAsString();
				try {
					@SuppressWarnings("rawtypes")
					Class clazz = Class.forName(className);
					// Try to instantiate the info
					Object tmp = clazz.newInstance();
					if (!(tmp instanceof SystemInfo))
						throw new Exception("The specified info " + className
								+ " doesn't implement SystemInfo interface");

					info = (SystemInfo) tmp;
				} catch (Throwable e) {
					log.error(e.getMessage());
				}
			}
		} catch (Throwable e) {
			log.error(e.getMessage());
		}

		/*
		 * Read some informations from the context
		 */
		try {
			// Read some informations from the context
			ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
			info.setRelease(config.getProperty("product.release"));
			info.setYear(config.getProperty("product.year"));
			info.setRunLevel(config.getProperty("runlevel"));
		} catch (Throwable e) {
			log.error(e.getMessage());
		}

		/*
		 * Collect installed features
		 */
		try {
			List<String> features = new ArrayList<String>();
			PluginRegistry registry = PluginRegistry.getInstance();
			Collection<Extension> exts = registry.getExtensions("logicaldoc-core", "Feature");
			for (Extension extension : exts) {
				// Retrieve the task name
				String name = extension.getParameter("name").valueAsString();
				if (!features.contains(name))
					features.add(name);
			}
			info.setFeatures(features.toArray(new String[0]));
		} catch (Throwable e) {
			log.error(e.getMessage());
		}

		return info;
	}

	public String[] getFeatures() {
		return features;
	}

	public void setFeatures(String[] features) {
		this.features = features;
	}
}