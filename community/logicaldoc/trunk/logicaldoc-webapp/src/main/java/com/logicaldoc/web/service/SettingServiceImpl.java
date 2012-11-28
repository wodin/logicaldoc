package com.logicaldoc.web.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.communication.EMailSender;
import com.logicaldoc.core.generic.Generic;
import com.logicaldoc.core.generic.dao.GenericDAO;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIDashlet;
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

	private static Logger log = LoggerFactory.getLogger(SettingServiceImpl.class);

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
					|| name.startsWith("password") || name.startsWith("ad") || name.startsWith("webservice")
					|| name.startsWith("webdav") || name.startsWith("cmis") || name.startsWith("runlevel")
					|| name.startsWith("stat") || name.startsWith("index") || name.equals("id")
					|| name.startsWith("lang") || name.startsWith("reg.") || name.startsWith("ocr.")
					|| name.startsWith("barcode.") || name.startsWith("task.") || name.startsWith("quota")
					|| name.startsWith("store") || name.startsWith("flexpaperviewer") || name.startsWith("omnipage.")
					|| name.startsWith("command.") || name.startsWith("gui.") || name.startsWith("upload.")
					|| name.equals("userno") || name.startsWith("search.") || name.startsWith("swftools.")
					|| name.contains("password") || name.startsWith("audit.user") || name.startsWith("openoffice.path")
					|| name.startsWith("tag.") || name.startsWith("jdbc.") || name.startsWith("cluster")
					|| name.startsWith("ip."))
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
					|| key.toString().startsWith("cmis") || key.toString().startsWith("command.")
					|| key.toString().startsWith("openoffice") || key.toString().startsWith("swftools.")) {
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
				conf.setProperty(settings[i].getName(), settings[i].getValue() != null ? settings[i].getValue() : "");
				System.out.println("** "+settings[i].getName()+"="+settings[i].getValue());
			}
			
			conf.write();

			log.info("Parameters settings data written successfully.");
		} catch (Exception e) {
			log.error("Exception writing Parameters settings data: " + e.getMessage(), e);
			e.printStackTrace();
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
	public GUIParameter[] loadSettingsByNames(String sid, String[] names) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

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
	public GUIParameter[][] loadRepositories(String sid) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

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
	public void saveRepositories(String sid, GUIParameter[][] repos) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

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
				conf.setProperty(f.getName(), f.getValue());
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

		GUIParameter[] params = new GUIParameter[13];
		params[0] = new GUIParameter("ocr.enabled", conf.getProperty("ocr.enabled"));
		params[1] = new GUIParameter("ocr.resolution.threshold", conf.getProperty("ocr.resolution.threshold"));
		params[2] = new GUIParameter("ocr.text.threshold", conf.getProperty("ocr.text.threshold"));
		params[3] = new GUIParameter("ocr.includes", conf.getProperty("ocr.includes"));
		params[4] = new GUIParameter("ocr.excludes", conf.getProperty("ocr.excludes"));
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
	public GUIParameter[] loadQuotaSettings(String sid) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);

		GUIParameter[] params = new GUIParameter[2];
		params[0] = new GUIParameter("quota.docs",
				StringUtils.isNotEmpty(conf.getProperty("quota.docs")) ? conf.getProperty("quota.docs") : "");
		params[1] = new GUIParameter("quota.threshold",
				StringUtils.isNotEmpty(conf.getProperty("quota.threshold")) ? conf.getProperty("quota.threshold") : "");

		return params;
	}

	@Override
	public void saveQuotaSettings(String sid, GUIParameter[] quotaSettings) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		try {
			for (GUIParameter f : quotaSettings) {
				conf.setProperty(f.getName(), f.getValue());
			}
			conf.write();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public GUIParameter[] computeStoragesSize(String sid) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		List<GUIParameter> storagesList = new ArrayList<GUIParameter>();
		GUIParameter param = null;
		for (Object key : conf.keySet()) {
			String name = key.toString();
			if (name.startsWith("store.") && !name.endsWith("write")) {
				File docDir = new File(conf.getProperty(name));
				if (docDir.exists()) {
					param = new GUIParameter(name, "" + (FileUtils.sizeOfDirectory(docDir) / (1024 * 1024)));
					storagesList.add(param);
				}
			}
		}

		return storagesList.toArray(new GUIParameter[0]);
	}

	@Override
	public GUIParameter[] loadGUISettings(String sid) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);

		List<GUIParameter> params = new ArrayList<GUIParameter>();
		for (Object name : conf.keySet()) {
			if (name.toString().startsWith("gui."))
				params.add(new GUIParameter(name.toString(), conf.getProperty(name.toString())));
		}
		params.add(new GUIParameter("upload.maxsize", conf.getProperty("upload.maxsize")));
		params.add(new GUIParameter("search.hits", conf.getProperty("search.hits")));
		params.add(new GUIParameter("search.extattr", conf.getProperty("search.extattr")));

		return params.toArray(new GUIParameter[0]);
	}

	@Override
	public void saveDashlets(String sid, GUIDashlet[] dashlets) throws InvalidSessionException {
		UserSession session = SessionUtil.validateSession(sid);
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
}