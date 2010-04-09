package com.logicaldoc.webservice;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.util.config.PropertiesBean;


/**
 * Configuration bean
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.6
 */
public class WebserviceBean {
	protected static Log log = LogFactory.getLog(WebserviceBean.class);

	private PropertiesBean properties;

	public String save() {
		try {
			properties.write();
			WebServiceMessages.addLocalizedInfo("msg.action.savesettings");
		} catch (IOException e) {
			WebServiceMessages.addLocalizedError("errors.action.savesettings");
			log.error("Error saving webservice paramaters", e);
		}
		return null;
	}

	public boolean isEnabled() {
		return "true".equals(properties.get("webservice.enabled"));
	}

	public void setEnabled(boolean enabled) {
		properties.setProperty("webservice.enabled", Boolean.toString(enabled));
	}

	public void setProperties(PropertiesBean properties) {
		this.properties = properties;
	}
}
