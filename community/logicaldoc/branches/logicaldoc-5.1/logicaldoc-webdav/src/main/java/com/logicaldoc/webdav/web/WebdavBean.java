package com.logicaldoc.webdav.web;

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
public class WebdavBean {
	protected static Log log = LogFactory.getLog(WebdavBean.class);

	private PropertiesBean properties;

	public String save() {
		try {
			properties.write();
			WebdavMessages.addLocalizedInfo("msg.action.savesettings");
		} catch (IOException e) {
			WebdavMessages.addLocalizedError("errors.action.savesettings");
			log.error("Error saving webdav paramaters", e);
		}
		return null;
	}

	public boolean isEnabled() {
		return "true".equals(properties.get("webdav.enabled"));
	}

	public void setEnabled(boolean enabled) {
		properties.setProperty("webdav.enabled", Boolean.toString(enabled));
	}

	public void setProperties(PropertiesBean properties) {
		this.properties = properties;
	}
}
