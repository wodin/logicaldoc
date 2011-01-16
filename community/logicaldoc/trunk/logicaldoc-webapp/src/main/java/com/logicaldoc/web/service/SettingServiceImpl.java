package com.logicaldoc.web.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.communication.EMailSender;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIEmailSettings;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
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

			EMailSender sender = (EMailSender) Context.getInstance().getBean(EMailSender.class);
			sender.setHost(conf.getProperty("smtp.host"));
			sender.setPort(Integer.parseInt(conf.getProperty("smtp.port")));
			sender.setUsername(conf.getProperty("smtp.username"));
			sender.setPassword(conf.getProperty("smtp.password"));
			sender.setSender(conf.getProperty("smtp.sender"));
			sender.setAuthEncripted("true".equals(conf.getProperty("smtp.authEncripted")) ? true : false);
			sender.setConnectionSecurity(Integer.parseInt(conf.getProperty("smtp.connectionSecurity")));

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
					|| name.startsWith("password") || name.startsWith("ad")
					|| name.startsWith("webservice") || name.startsWith("webdav") || name.startsWith("runlevel")
					|| name.startsWith("stat") || name.startsWith("index") || name.startsWith("proxy")
					|| name.equals("id") || name.startsWith("lang") || name.startsWith("reg.")
					|| name.startsWith("ocr.")|| name.startsWith("barcode.")|| name.startsWith("task."))
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
	public GUIParameter[] loadClientSettings(String sid) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		List<GUIParameter> params = new ArrayList<GUIParameter>();
		for (Object key : conf.keySet()) {
			if (key.toString().equals("webservice.enabled") || key.toString().startsWith("webdav")
					|| key.toString().startsWith("office")) {
				GUIParameter p = new GUIParameter(key.toString(), conf.getProperty(key.toString()));
				params.add(p);
			}
		}

		return params.toArray(new GUIParameter[0]);
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
	public void saveClientSettings(String sid, GUIParameter[] settings) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		try {
			ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
			for (GUIParameter setting : settings) {
				conf.setProperty(setting.getName(), setting.getValue());
			}
			conf.write();

			log.info("Client Tools settings data written successfully.");
		} catch (Exception e) {
			log.error("Exception writing Client Tools settings data: " + e.getMessage(), e);
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
		SessionUtil.validateSession(sid);

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
				conf.setProperty("conf." + f.getName(), f.getValue());
			}
			conf.write();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	public GUIParameter[] loadProxySettings(String sid) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);

		GUIParameter[] params = new GUIParameter[4];
		params[0] = new GUIParameter("host", conf.getProperty("proxy.host"));
		params[1] = new GUIParameter("port", conf.getProperty("proxy.port"));
		params[2] = new GUIParameter("username", conf.getProperty("proxy.username"));
		params[3] = new GUIParameter("password", conf.getProperty("proxy.password"));

		return params;
	}

	@Override
	public void saveProxySettings(String sid, GUIParameter[] proxySettings) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		try {
			for (GUIParameter f : proxySettings) {
				conf.setProperty("proxy." + f.getName(), f.getValue());
			}
			conf.write();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public GUIParameter[] loadOcrSettings(String sid) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);

		GUIParameter[] params = new GUIParameter[6];
		params[0] = new GUIParameter("ocr.enabled", conf.getProperty("ocr.enabled"));
		params[1] = new GUIParameter("ocr.resolution.threshold", conf.getProperty("ocr.resolution.threshold"));
		params[2] = new GUIParameter("ocr.text.threshold", conf.getProperty("ocr.text.threshold"));
		params[3] = new GUIParameter("ocr.includes", conf.getProperty("ocr.includes"));
		params[4] = new GUIParameter("ocr.excludes", conf.getProperty("ocr.excludes"));
		params[5] = new GUIParameter("ocr.timeout", conf.getProperty("ocr.timeout"));
		return params;
	}
}