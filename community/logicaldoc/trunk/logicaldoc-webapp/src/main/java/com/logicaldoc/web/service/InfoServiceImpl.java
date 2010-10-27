package com.logicaldoc.web.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java.plugin.registry.Extension;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.communication.SystemMessage;
import com.logicaldoc.core.communication.dao.SystemMessageDAO;
import com.logicaldoc.core.i18n.Language;
import com.logicaldoc.core.i18n.LanguageManager;
import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.gui.common.client.beans.GUIInfo;
import com.logicaldoc.gui.common.client.beans.GUIMessage;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.beans.GUIValuePair;
import com.logicaldoc.gui.common.client.services.InfoService;
import com.logicaldoc.i18n.I18N;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.LocaleUtil;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.plugin.PluginRegistry;
import com.logicaldoc.web.ApplicationInitializer;

/**
 * Implementation of the InfoService
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class InfoServiceImpl extends RemoteServiceServlet implements InfoService {

	private static Log log = LogFactory.getLog(InfoServiceImpl.class);

	private static final long serialVersionUID = 1L;

	@Override
	public GUIInfo getInfo(String locale) {
		GUIInfo info = new GUIInfo();
		try {
			ContextProperties pbean = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);

			ArrayList<GUIValuePair> values = new ArrayList<GUIValuePair>();
			ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
			for (Object key : config.keySet()) {
				GUIValuePair pair = new GUIValuePair();
				pair.setCode((String) key);
				pair.setValue(config.getProperty((String) key));
				values.add(pair);
			}
			info.setConfig(values.toArray(new GUIValuePair[0]));

			Locale withLocale = LocaleUtil.toLocale(locale);
			ArrayList<GUIValuePair> supportedLanguages = new ArrayList<GUIValuePair>();

			List<String> installedLocales = I18N.getLocales();
			for (String loc : installedLocales) {
				if ("enabled".equals(pbean.getProperty("lang." + loc + ".gui"))) {
					Locale lc = LocaleUtil.toLocale(loc);
					GUIValuePair l = new GUIValuePair();
					l.setCode(loc);
					l.setValue(lc.getDisplayName(withLocale));
					supportedLanguages.add(l);
				}
			}

			info.setSupportedGUILanguages(supportedLanguages.toArray(new GUIValuePair[0]));
			info.setBundle(getBundle(locale));

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
				// Checks if LogicalDOC has been initialised
				String jdbcUrl = pbean.getProperty("jdbc.url");
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
				}
			}

			info.setMessages(messages.toArray(new GUIMessage[0]));

			// Collect installed features
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

			info.setInstallationId(config.getProperty("id"));
			info.setRelease(config.getProperty("product.release"));
			info.setYear(config.getProperty("product.year"));
			info.setSessionHeartbeat(Integer.parseInt(config.getProperty("session.heartbeat")));
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

		SystemMessageDAO messageDao = (SystemMessageDAO) Context.getInstance().getBean(SystemMessageDAO.class);
		GUIParameter[] parameters = new GUIParameter[1];

		UserSession session = SessionManager.getInstance().get(sid);
		GUIParameter messages = new GUIParameter("messages", ""
				+ messageDao.getCount(session.getUserName(), SystemMessage.TYPE_SYSTEM, 0));
		parameters[0] = messages;

		return parameters;
	}
}