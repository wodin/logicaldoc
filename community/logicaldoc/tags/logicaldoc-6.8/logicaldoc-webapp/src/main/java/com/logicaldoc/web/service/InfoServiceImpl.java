package com.logicaldoc.web.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.SystemInfo;
import com.logicaldoc.core.communication.SystemMessage;
import com.logicaldoc.core.communication.SystemMessageDAO;
import com.logicaldoc.core.i18n.Language;
import com.logicaldoc.core.i18n.LanguageManager;
import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.SystemQuota;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.gui.common.client.beans.GUIInfo;
import com.logicaldoc.gui.common.client.beans.GUIMessage;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.beans.GUIValuePair;
import com.logicaldoc.gui.common.client.services.InfoService;
import com.logicaldoc.i18n.I18N;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.LocaleUtil;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.web.ApplicationInitializer;

/**
 * Implementation of the InfoService
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class InfoServiceImpl extends RemoteServiceServlet implements InfoService {

	private static Logger log = LoggerFactory.getLogger(InfoServiceImpl.class);

	private static final long serialVersionUID = 1L;

	@Override
	public GUIInfo getInfo(String locale) {
		ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);

		GUIInfo info = null;
		try {
			info = getInfo();
			info.setBundle(getBundle(locale));

			Locale withLocale = LocaleUtil.toLocale(locale);
			ArrayList<GUIValuePair> supportedLanguages = new ArrayList<GUIValuePair>();

			List<String> installedLocales = I18N.getLocales();
			for (String loc : installedLocales) {
				if ("enabled".equals(config.getProperty("lang." + loc + ".gui"))) {
					Locale lc = LocaleUtil.toLocale(loc);
					GUIValuePair l = new GUIValuePair();
					l.setCode(loc);
					l.setValue(lc.getDisplayName(withLocale));
					supportedLanguages.add(l);
				}
			}

			info.setSupportedGUILanguages(supportedLanguages.toArray(new GUIValuePair[0]));

			LanguageManager manager = LanguageManager.getInstance();
			Collection<Language> languages = manager.getActiveLanguages();
			supportedLanguages.clear();
			for (Language language : languages) {
				Locale lc = language.getLocale();
				GUIValuePair l = new GUIValuePair();
				l.setCode(lc.toString());
				l.setValue(lc.getDisplayName(withLocale));
				supportedLanguages.add(l);
			}
			info.setSupportedLanguages(supportedLanguages.toArray(new GUIValuePair[0]));

			List<GUIMessage> messages = new ArrayList<GUIMessage>();

			// Check if the application needs to be restarted
			if (ApplicationInitializer.needRestart) {
				GUIMessage restartReminder = new GUIMessage();
				restartReminder.setMessage(getValue(info, "needrestart"));
				messages.add(restartReminder);
			}

			if (!ApplicationInitializer.needRestart) {
				// Checks if LogicalDOC has been initialized
				String jdbcUrl = config.getProperty("jdbc.url");
				if (jdbcUrl.startsWith("jdbc:hsqldb:mem:")) {
					GUIMessage setupReminder = new GUIMessage();
					setupReminder.setMessage(getValue(info, "setup.reminder"));
					HttpServletRequest request = this.getThreadLocalRequest();
					if (request != null) {
						String urlPrefix = request.getScheme() + "://" + request.getServerName() + ":"
								+ request.getServerPort() + request.getContextPath();
						setupReminder.setUrl(urlPrefix + "/setup");
					}
					messages.add(setupReminder);
				} else {
					// Check if the database is connected
					UserDAO dao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
					int test = -1;
					try {
						test = dao.queryForInt("select count(*) from ld_user");
					} catch (Throwable t) {
						test = -1;
					}
					if (test < 1) {
						info.setDatabaseConnected(false);
						GUIMessage m = new GUIMessage();
						m.setMessage(I18N.message("databasenotconnected", locale));
						messages.add(m);
					}
				}
			}

			if (info.isDatabaseConnected()) {
				// Checks if the system quota or the quota threshold is
				// exceeded.
				boolean quotaExcedeed = false;
				try {
					SystemQuota.checkOverQuota();
				} catch (Throwable e) {
					quotaExcedeed = true;
					GUIMessage m = new GUIMessage();
					m.setMessage(I18N.message("quotaexceeded", locale));
					messages.add(m);
				}
				if (SystemQuota.checkOverThreshold() && !quotaExcedeed) {
					GUIMessage m = new GUIMessage();
					m.setMessage(I18N.message("quotathresholdexceeded", locale));
					messages.add(m);
				}
			}

			info.setMessages(messages.toArray(new GUIMessage[0]));

			return info;
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	/**
	 * Retrieves the informations but not localization issues like messages and
	 * installed languages.
	 */
	public static GUIInfo getInfo() {
		ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);

		/*
		 * Populate the infos from the SystemInfo
		 */
		GUIInfo info = new GUIInfo();
		SystemInfo inf = SystemInfo.get();
		info.setDate(inf.getDate());
		info.setBugs(inf.getBugs());
		info.setFeatures(inf.getFeatures());
		info.setForum(inf.getForum());
		info.setHelp(inf.getHelp());
		info.setInstallationId(inf.getInstallationId());
		info.setLicensee(inf.getLicensee());
		info.setProduct(inf.getProduct());
		info.setProductName(inf.getProductName());
		info.setRelease(inf.getRelease());
		info.setRunLevel(inf.getRunLevel());
		info.setSupport(inf.getSupport());
		info.setUrl(inf.getUrl());
		info.setVendor(inf.getVendor());
		info.setVendorAddress(inf.getVendorAddress());
		info.setVendorCap(inf.getVendorCap());
		info.setVendorCity(inf.getVendorCity());
		info.setVendorCountry(inf.getVendorCountry());
		info.setYear(inf.getYear());
		info.setSessionHeartbeat(Integer.parseInt(config.getProperty("session.heartbeat")));
		info.setRunLevel(config.getProperty("runlevel"));

		try {
			ArrayList<GUIValuePair> values = new ArrayList<GUIValuePair>();
			for (Object key : config.keySet()) {
				GUIValuePair pair = new GUIValuePair();
				pair.setCode((String) key);
				pair.setValue(config.getProperty((String) key));
				values.add(pair);
			}
			info.setConfig(values.toArray(new GUIValuePair[0]));
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}

		return info;
	}

	static protected GUIValuePair[] getBundle(String locale) {
		Locale l = LocaleUtil.toLocale(locale);
		ResourceBundle rb = ResourceBundle.getBundle("i18n.messages", l);
		GUIValuePair[] buf = new GUIValuePair[rb.keySet().size()];
		int i = 0;
		for (String key : rb.keySet()) {
			GUIValuePair entry = new GUIValuePair();
			entry.setCode(key);
			entry.setValue(rb.getString(key));
			buf[i++] = entry;
		}
		return buf;
	}

	protected String getValue(GUIInfo info, String message) {
		for (GUIValuePair valuePair : info.getBundle()) {
			if (valuePair.getCode().equals(message)) {
				return valuePair.getValue();
			}
		}
		return "";
	}

	@Override
	public GUIParameter[] getSessionInfo(String sid) {
		log.debug("Requested info for session " + sid);

		try {
			SystemMessageDAO messageDao = (SystemMessageDAO) Context.getInstance().getBean(SystemMessageDAO.class);
			GUIParameter[] parameters = new GUIParameter[1];

			UserSession session = SessionManager.getInstance().get(sid);
			if (session != null) {
				GUIParameter messages = new GUIParameter("messages", ""
						+ messageDao.getCount(session.getUserName(), SystemMessage.TYPE_SYSTEM, 0));
				parameters[0] = messages;
			}
			return parameters;
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}
}