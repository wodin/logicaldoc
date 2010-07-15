package com.logicaldoc.web.admin;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.web.i18n.Messages;

/**
 * This form allows the editing of all application's paramaters(excluding the
 * skin-related ones)
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class ParametersForm {
	protected static Log log = LogFactory.getLog(ParametersForm.class);

	public PropertiesBean getConfig() {
		return (PropertiesBean) Context.getInstance().getBean("ContextProperties");
	}

	public Collection<String> getKeys() {
		TreeSet<String> sortedSet = new TreeSet<String>();
		PropertiesBean config = getConfig();
		for (Object key : getConfig().keySet()) {
			String name = key.toString();
			if (name.endsWith(".hidden") || name.endsWith("readonly"))
				continue;
			if (config.containsKey(name + ".hidden")) {
				if ("true".equals(config.getProperty(name + ".hidden")))
					continue;
			} else if (name.startsWith("product") || name.startsWith("skin") || name.startsWith("conf")
					|| name.startsWith("ldap") || name.startsWith("schedule") || name.startsWith("smtp")
					|| name.startsWith("gui") || name.startsWith("password") || name.startsWith("ad")
					|| name.startsWith("webservice") || name.startsWith("webdav") || name.startsWith("runlevel")
					|| name.startsWith("stat") || name.startsWith("index"))
				continue;

			sortedSet.add(key.toString());
		}
		return sortedSet;
	}

	public String getParamName() {
		Map<String, Object> map = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
		Object parameter = (Object) map.get("entry");
		return parameter.toString();
	}

	public boolean isReadonly() {
		String name = getParamName();
		PropertiesBean config = getConfig();
		if ("true".equals(config.getProperty(name + ".readonly"))) {
			return true;
		} else
			return false;
	}

	public String save() {
		try {
			getConfig().write();
			Messages.addLocalizedInfo("msg.action.savesettings");
		} catch (IOException e) {
			Messages.addLocalizedError("errors.action.savesettings");
			log.error("Error saving paramaters", e);
		}
		return null;
	}
}