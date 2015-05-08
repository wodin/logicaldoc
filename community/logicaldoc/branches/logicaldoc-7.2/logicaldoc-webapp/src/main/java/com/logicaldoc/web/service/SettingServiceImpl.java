package com.logicaldoc.web.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.communication.EMail;
import com.logicaldoc.core.communication.EMailSender;
import com.logicaldoc.core.generic.Generic;
import com.logicaldoc.core.generic.GenericDAO;
import com.logicaldoc.core.security.Tenant;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.gui.common.client.ServerException;
import com.logicaldoc.gui.common.client.beans.GUIDashlet;
import com.logicaldoc.gui.common.client.beans.GUIEmailSettings;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.frontend.client.services.SettingService;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.web.util.ServiceUtil;

/**
 * Implementation of the SettingService
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class SettingServiceImpl extends RemoteServiceServlet implements SettingService {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(SettingServiceImpl.class);

	@Override
	public GUIEmailSettings loadEmailSettings(String sid) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		GUIEmailSettings emailSettings = new GUIEmailSettings();
		try {
			ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);

			emailSettings.setSmtpServer(conf.getProperty(session.getTenantName() + ".smtp.host"));
			emailSettings.setPort(Integer.parseInt(conf.getProperty(session.getTenantName() + ".smtp.port")));
			emailSettings
					.setUsername(!conf.getProperty(session.getTenantName() + ".smtp.username").trim().isEmpty() ? conf
							.getProperty(session.getTenantName() + ".smtp.username") : "");
			emailSettings.setPwd(!conf.getProperty(session.getTenantName() + ".smtp.password").trim().isEmpty() ? conf
					.getProperty(session.getTenantName() + ".smtp.password") : "");
			emailSettings.setConnSecurity(conf.getProperty(session.getTenantName() + ".smtp.connectionSecurity"));
			emailSettings
					.setSecureAuth("true".equals(conf.getProperty(session.getTenantName() + ".smtp.authEncripted")) ? true
							: false);
			emailSettings.setSenderEmail(conf.getProperty(session.getTenantName() + ".smtp.sender"));

			log.info("Email settings data loaded successfully.");
		} catch (Exception e) {
			log.error("Exception loading Email settings data: " + e.getMessage(), e);
		}

		return emailSettings;
	}

	@Override
	public void saveEmailSettings(String sid, GUIEmailSettings settings) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		try {
			ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);

			conf.setProperty(session.getTenantName() + ".smtp.host", settings.getSmtpServer());
			conf.setProperty(session.getTenantName() + ".smtp.port", Integer.toString(settings.getPort()));
			conf.setProperty(session.getTenantName() + ".smtp.username",
					!settings.getUsername().trim().isEmpty() ? settings.getUsername() : "");
			conf.setProperty(session.getTenantName() + ".smtp.password",
					!settings.getPwd().trim().isEmpty() ? settings.getPwd() : "");
			conf.setProperty(session.getTenantName() + ".smtp.connectionSecurity", settings.getConnSecurity());
			conf.setProperty(session.getTenantName() + ".smtp.authEncripted", settings.isSecureAuth() ? "true"
					: "false");
			conf.setProperty(session.getTenantName() + ".smtp.sender", settings.getSenderEmail());

			conf.write();

			EMailSender sender = (EMailSender) Context.getInstance().getBean(EMailSender.class);
			sender.setHost(conf.getProperty(Tenant.DEFAULT_NAME + ".smtp.host"));
			sender.setPort(Integer.parseInt(conf.getProperty(Tenant.DEFAULT_NAME + ".smtp.port")));
			sender.setUsername(conf.getProperty(Tenant.DEFAULT_NAME + ".smtp.username"));
			sender.setPassword(conf.getProperty(Tenant.DEFAULT_NAME + ".smtp.password"));
			sender.setSender(conf.getProperty(Tenant.DEFAULT_NAME + ".smtp.sender"));
			sender.setAuthEncripted("true".equals(conf.getProperty(Tenant.DEFAULT_NAME + ".smtp.authEncripted")) ? true
					: false);
			sender.setConnectionSecurity(Integer.parseInt(conf.getProperty(Tenant.DEFAULT_NAME
					+ ".smtp.connectionSecurity")));

			log.info("Email settings data written successfully.");
		} catch (Exception e) {
			log.error("Exception writing Email settings data: " + e.getMessage(), e);
		}
	}

	@Override
	public GUIParameter[] loadSettings(String sid) throws ServerException {
		ServiceUtil.validateSession(sid);

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
					|| name.startsWith("ldap") || name.startsWith("schedule") || name.contains(".smtp.")
					|| name.contains("password") || name.startsWith("ad") || name.startsWith("webservice")
					|| name.startsWith("webdav") || name.startsWith("cmis") || name.startsWith("runlevel")
					|| name.startsWith("stat") || name.contains("index") || name.equals("id")
					|| name.contains(".lang.") || name.startsWith("reg.") || name.startsWith("ocr.")
					|| name.contains(".ocr.") || name.contains("barcode") || name.startsWith("task.")
					|| name.startsWith("quota") || name.startsWith("store") || name.startsWith("flexpaperviewer")
					|| name.startsWith("omnipage.") || name.startsWith("command.") || name.contains(".gui.")
					|| name.contains(".upload.") || name.equals("userno") || name.contains(".search.")
					|| name.startsWith("swftools.") || name.contains("password") || name.startsWith("openoffice.path")
					|| name.contains("tag.") || name.startsWith("jdbc.") || name.startsWith("cluster")
					|| name.startsWith("ip.") || name.contains(".extcall.") || name.contains("anonymous")
					|| name.startsWith("hibernate.") || name.contains(".session.") || name.contains("acmecad.")
					|| name.contains("antivirus.") || name.startsWith("login.") || name.equals("upload.maxsize")
					|| name.startsWith("news.") || name.equals("registry") || name.equals("searchengine")
					|| name.equals("load"))
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
	public GUIParameter[] loadClientSettings(String sid) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		List<GUIParameter> params = new ArrayList<GUIParameter>();
		for (Object key : conf.keySet()) {
			if (key.toString().equals("webservice.enabled") || key.toString().startsWith("webdav")
					|| key.toString().startsWith("cmis") || key.toString().startsWith("command.")
					|| key.toString().startsWith("acmecad.command") || key.toString().startsWith("openoffice")
					|| key.toString().startsWith("swftools.")
					|| key.toString().startsWith(session.getTenantName() + ".extcall.")) {
				GUIParameter p = new GUIParameter(key.toString(), conf.getProperty(key.toString()));
				params.add(p);
			}
		}

		return params.toArray(new GUIParameter[0]);
	}

	@Override
	public void saveSettings(String sid, GUIParameter[] settings) throws ServerException {
		ServiceUtil.validateSession(sid);

		try {
			ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
			for (int i = 0; i < settings.length; i++) {
				if (settings[i] == null || StringUtils.isEmpty(settings[i].getName()))
					continue;
				conf.setProperty(settings[i].getName(), settings[i].getValue() != null ? settings[i].getValue() : "");
			}

			conf.write();

			log.info("Parameters settings data written successfully.");
		} catch (Exception e) {
			log.error("Exception writing Parameters settings data: " + e.getMessage(), e);
		}
	}

	@Override
	public GUIParameter[] loadSettingsByNames(String sid, String[] names) throws ServerException {
		ServiceUtil.validateSession(sid);

		GUIParameter[] values = new GUIParameter[names.length];
		try {
			ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);

			for (int i = 0; i < names.length; i++) {
				values[i] = new GUIParameter(names[i], conf.getProperty(names[i]));
			}
		} catch (Exception e) {
			log.error("Exception reading settings: " + e.getMessage(), e);
		}
		return values;
	}

	@Override
	public GUIParameter[][] loadRepositories(String sid) throws ServerException {
		ServiceUtil.validateSession(sid);

		ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);

		// Collect the directories
		GUIParameter[] dirs = new GUIParameter[6];
		dirs[0] = new GUIParameter("dbdir", conf.getProperty("conf.dbdir"));
		dirs[1] = new GUIParameter("exportdir", conf.getProperty("conf.exportdir"));
		dirs[2] = new GUIParameter("importdir", conf.getProperty("conf.importdir"));
		dirs[3] = new GUIParameter("logdir", conf.getProperty("conf.logdir"));
		dirs[4] = new GUIParameter("plugindir", conf.getProperty("conf.plugindir"));
		dirs[5] = new GUIParameter("userdir", conf.getProperty("conf.userdir"));

		// Prepare the stores
		List<GUIParameter> tmp = new ArrayList<GUIParameter>();
		for (Object key : conf.keySet()) {
			String name = key.toString();
			if (name.startsWith("store."))
				tmp.add(new GUIParameter(name, conf.getProperty(name)));
		}

		GUIParameter[][] repos = new GUIParameter[2][Math.max(dirs.length, tmp.size())];
		// In the first array insert the folders
		repos[0] = dirs;
		// In the second array insert the stores configuration
		repos[1] = tmp.toArray(new GUIParameter[0]);

		return repos;
	}

	@Override
	public void saveRepositories(String sid, GUIParameter[][] repos) throws ServerException {
		ServiceUtil.validateSession(sid);

		ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		try {
			// First of all the folders
			for (GUIParameter f : repos[0]) {
				conf.setProperty("conf." + f.getName(), f.getValue());
			}
			// Now the storages
			for (GUIParameter f : repos[1]) {
				if (f == null || f.getValue().trim().isEmpty())
					continue;
				conf.setProperty(f.getName().replaceAll("_", "."), f.getValue());
			}
			conf.write();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public GUIParameter[] loadOcrSettings(String sid) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);

		GUIParameter[] params = new GUIParameter[13];
		params[0] = new GUIParameter("ocr.enabled", conf.getProperty("ocr.enabled"));
		params[1] = new GUIParameter(session.getTenantName() + ".ocr.resolution.threshold", conf.getProperty(session
				.getTenantName() + ".ocr.resolution.threshold"));
		params[2] = new GUIParameter(session.getTenantName() + ".ocr.text.threshold", conf.getProperty(session
				.getTenantName() + ".ocr.text.threshold"));
		params[3] = new GUIParameter(session.getTenantName() + ".ocr.includes", conf.getProperty(session
				.getTenantName() + ".ocr.includes"));
		params[4] = new GUIParameter(session.getTenantName() + ".ocr.excludes", conf.getProperty(session
				.getTenantName() + ".ocr.excludes"));
		params[5] = new GUIParameter("ocr.timeout", conf.getProperty("ocr.timeout"));
		params[6] = new GUIParameter("ocr.engine", conf.getProperty("ocr.engine"));
		params[7] = new GUIParameter("command.tesseract", conf.getProperty("command.tesseract"));
		params[8] = new GUIParameter("omnipage.path", conf.getProperty("omnipage.path"));
		params[9] = new GUIParameter("ocr.count", conf.getProperty("ocr.count"));
		params[10] = new GUIParameter("ocr.rendres", conf.getProperty("ocr.rendres"));
		params[11] = new GUIParameter("ocr.rendres.barcode", conf.getProperty("ocr.rendres.barcode"));
		params[12] = new GUIParameter("ocr.batch", conf.getProperty("ocr.batch"));

		return params;
	}

	@Override
	public GUIParameter[] computeStoragesSize(String sid) throws ServerException {
		ServiceUtil.validateSession(sid);

		ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		List<GUIParameter> storagesList = new ArrayList<GUIParameter>();
		GUIParameter param = null;
		for (Object key : conf.keySet()) {
			String name = key.toString();
			if (name.startsWith("store.") && name.endsWith(".dir")) {
				File docDir = new File(conf.getProperty(name));
				if (docDir.exists()) {
					param = new GUIParameter(name, "" + FileUtils.sizeOfDirectory(docDir));
					storagesList.add(param);
				}
			}
		}

		return storagesList.toArray(new GUIParameter[0]);
	}

	@Override
	public GUIParameter[] loadGUISettings(String sid) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);

		List<GUIParameter> params = new ArrayList<GUIParameter>();
		for (Object name : conf.keySet()) {
			if (name.toString().startsWith(session.getTenantName() + ".gui")
					&& !name.toString().contains("dropspot.mode"))
				params.add(new GUIParameter(name.toString(), conf.getProperty(name.toString())));
		}
		if (session.getTenantName().equals(Tenant.DEFAULT_NAME))
			params.add(new GUIParameter("upload.maxsize", conf.getProperty("upload.maxsize")));
		params.add(new GUIParameter(session.getTenantName() + ".upload.disallow", conf.getProperty(session
				.getTenantName() + ".upload.disallow")));
		params.add(new GUIParameter(session.getTenantName() + ".search.hits", conf.getProperty(session.getTenantName()
				+ ".search.hits")));
		params.add(new GUIParameter(session.getTenantName() + ".search.extattr", conf.getProperty(session
				.getTenantName() + ".search.extattr")));
		params.add(new GUIParameter(session.getTenantName() + ".session.timeout", conf.getProperty(session
				.getTenantName() + ".session.timeout")));
		params.add(new GUIParameter(session.getTenantName() + ".session.heartbeat", conf.getProperty(session
				.getTenantName() + ".session.heartbeat")));

		return params.toArray(new GUIParameter[0]);
	}

	@Override
	public void saveDashlets(String sid, GUIDashlet[] dashlets) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);
		GenericDAO gDao = (GenericDAO) Context.getInstance().getBean(GenericDAO.class);
		UserDAO uDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);

		try {
			/*
			 * Delete the actual dashlets for this user
			 */
			Map<String, Generic> settings = uDao.findUserSettings(session.getUserId(), "dashlet");
			for (Generic setting : settings.values()) {
				gDao.delete(setting.getId());
			}

			/*
			 * Now save the new dashlets
			 */
			for (GUIDashlet dashlet : dashlets) {
				Generic generic = new Generic("usersetting", "dashlet-" + dashlet.getId(), session.getUserId());
				generic.setInteger1((long) dashlet.getId());
				generic.setInteger2((long) dashlet.getColumn());
				generic.setInteger3((long) dashlet.getRow());
				generic.setString1(Long.toString(dashlet.getIndex()));
				gDao.store(generic);
			}
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public boolean testEmail(String sid, String email) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		EMailSender sender = new EMailSender(session.getTenantName());

		try {
			EMail mail;
			mail = new EMail();
			mail.setAccountId(-1);
			mail.setAuthor(config.getProperty(session.getTenantName() + ".smtp.sender"));
			mail.setAuthorAddress(config.getProperty(session.getTenantName() + ".smtp.sender"));
			mail.parseRecipients(email);
			mail.setFolder("outbox");
			mail.setSentDate(new Date());
			mail.setSubject("test");
			mail.setMessageText("test");

			log.info("Sending test email to " + email);
			sender.send(mail);
			log.info("Test email sent");
			return true;
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}
}