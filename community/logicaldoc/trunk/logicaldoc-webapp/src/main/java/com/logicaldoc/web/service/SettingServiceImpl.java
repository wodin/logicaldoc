package com.logicaldoc.web.service;

import java.io.IOException;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIEmailSettings;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.beans.GUIWebServiceSettings;
import com.logicaldoc.gui.frontend.client.services.SettingService;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.web.util.SessionUtil;

/**
 * Implementation of the SettingService
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class SettingServiceImpl extends RemoteServiceServlet implements SettingService {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(SettingServiceImpl.class);

	@Override
	public GUIEmailSettings loadEmailSettings(String sid) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		GUIEmailSettings emailSettings = new GUIEmailSettings();
		try {
			ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);

			emailSettings.setSmtpServer(conf.getProperty("smtp.host"));
			emailSettings.setPort(Integer.parseInt(conf.getProperty("smtp.port")));
			emailSettings.setUsername(!conf.getProperty("smtp.username").trim().isEmpty() ? conf
					.getProperty("smtp.username") : "");
			emailSettings.setPwd(!conf.getProperty("smtp.password").trim().isEmpty() ? conf
					.getProperty("smtp.password") : "");
			emailSettings.setConnSecurity(conf.getProperty("smtp.connectionSecurity"));
			emailSettings.setSecureAuth("true".equals(conf.getProperty("smtp.authEncripted")) ? true : false);
			emailSettings.setSenderEmail(conf.getProperty("smtp.sender"));

			log.info("Email settings data loaded successfully.");
		} catch (Exception e) {
			log.error("Exception loading Email settings data: " + e.getMessage(), e);
		}

		return emailSettings;
	}

	@Override
	public void saveEmailSettings(String sid, GUIEmailSettings settings) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		try {
			ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);

			conf.setProperty("smtp.host", settings.getSmtpServer());
			conf.setProperty("smtp.port", Integer.toString(settings.getPort()));
			conf.setProperty("smtp.username", !settings.getUsername().trim().isEmpty() ? settings.getUsername() : "");
			conf.setProperty("smtp.password", !settings.getPwd().trim().isEmpty() ? settings.getPwd() : "");
			conf.setProperty("smtp.connectionSecurity", settings.getConnSecurity());
			conf.setProperty("smtp.authEncripted", settings.isSecureAuth() ? "true" : "false");
			conf.setProperty("smtp.sender", settings.getSenderEmail());

			conf.write();

			log.info("Email settings data written successfully.");
		} catch (Exception e) {
			log.error("Exception writing Email settings data: " + e.getMessage(), e);
		}
	}

	@Override
	public GUIParameter[] loadSettings(String sid) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		TreeSet<String> sortedSet = new TreeSet<String>();
		ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		for (Object key : conf.keySet()) {
			String name = key.toString();
			if (name.endsWith(".hidden") || name.endsWith("readonly"))
				continue;
			if (conf.containsKey(name + ".hidden")) {
				if ("true".equals(conf.getProperty(name + ".hidden")))
					continue;
			} else if (name.startsWith("product") || name.startsWith("skin") || name.startsWith("conf")
					|| name.startsWith("ldap") || name.startsWith("schedule") || name.startsWith("smtp")
					|| name.startsWith("gui") || name.startsWith("password") || name.startsWith("ad")
					|| name.startsWith("webservice") || name.startsWith("webdav") || name.startsWith("runlevel")
					|| name.startsWith("stat") || name.startsWith("index"))
				continue;

			sortedSet.add(key.toString());
		}

		GUIParameter[] params = new GUIParameter[sortedSet.size()];
		int i = 0;
		for (String key : sortedSet) {
			GUIParameter p = new GUIParameter(key, conf.getProperty(key));
			params[i] = p;
			i++;
		}

		return params;
	}

	@Override
	public GUIWebServiceSettings[] loadWSSettings(String sid) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		HttpServletRequest request = this.getThreadLocalRequest();
		String urlPrefix = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
				+ request.getContextPath();

		ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		GUIWebServiceSettings[] settings = new GUIWebServiceSettings[2];

		GUIWebServiceSettings wsSettings = new GUIWebServiceSettings();
		wsSettings.setEnabled("true".equals(conf.get("webservice.enabled")));
		wsSettings.setUrl(urlPrefix + "/services/AuthService");
		wsSettings.setDescriptor(urlPrefix + "/services/AuthService?wsdl");

		GUIWebServiceSettings wdSettings = new GUIWebServiceSettings();
		wdSettings.setEnabled("true".equals(conf.get("webdav.enabled")));
		wdSettings.setUrl(urlPrefix + "/webdav/store");

		settings[0] = wsSettings;
		settings[1] = wdSettings;

		return settings;
	}

	@Override
	public void saveSettings(String sid, GUIParameter[] settings) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		try {
			ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
			for (int i = 0; i < settings.length; i++) {
				conf.setProperty(settings[i].getName(), settings[i].getValue());
			}

			conf.write();

			log.info("Parameters settings data written successfully.");
		} catch (Exception e) {
			log.error("Exception writing Parameters settings data: " + e.getMessage(), e);
		}
	}

	@Override
	public void saveWSSettings(String sid, GUIWebServiceSettings wsSettings, GUIWebServiceSettings webDavSettings)
			throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		try {
			ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);

			conf.setProperty("webservice.enabled", wsSettings.isEnabled() ? "true" : "false");
			conf.setProperty("webdav.enabled", webDavSettings.isEnabled() ? "true" : "false");

			conf.write();

			log.info("Web Service and WebDAV settings data written successfully.");
		} catch (Exception e) {
			log.error("Exception writing Web Service and WebDAV settings data: " + e.getMessage(), e);
		}
	}

	@Override
	public String[] loadValues(String sid, String[] names) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		String[] values = new String[names.length];
		try {
			ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);

			for (int i = 0; i < names.length; i++) {
				values[i] = conf.getProperty(names[i]);
			}
		} catch (Exception e) {
			log.error("Exception reading settings: " + e.getMessage(), e);
		}
		return values;
	}

	@Override
	public GUIParameter[] loadFolders(String sid) throws InvalidSessionException {
		ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);

		GUIParameter[] params = new GUIParameter[8];
	    params[0] = new GUIParameter("dbdir", conf.getProperty("conf.dbdir"));
	    params[1] = new GUIParameter("docdir", conf.getProperty("conf.docdir"));
	    params[2] = new GUIParameter("exportdir", conf.getProperty("conf.exportdir"));
	    params[3] = new GUIParameter("importdir", conf.getProperty("conf.importdir"));
	    params[4] = new GUIParameter("indexdir", conf.getProperty("conf.indexdir"));
	    params[5] = new GUIParameter("logdir", conf.getProperty("conf.logdir"));
	    params[6] = new GUIParameter("plugindir", conf.getProperty("conf.plugindir"));
	    params[7] = new GUIParameter("userdir", conf.getProperty("conf.userdir"));
		
	    return params;
	}
	
	@Override
	public void saveFolders(String sid, GUIParameter[] folders) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		try {
			for (GUIParameter f : folders) {
				conf.setProperty("conf."+f.getName(), f.getValue());
			}
			conf.write();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
}