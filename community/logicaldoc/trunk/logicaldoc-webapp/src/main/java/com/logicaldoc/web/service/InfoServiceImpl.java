package com.logicaldoc.web.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java.plugin.registry.Extension;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.beans.GUIInfo;
import com.logicaldoc.gui.common.client.beans.GUIMessage;
import com.logicaldoc.gui.common.client.beans.GUIValuePair;
import com.logicaldoc.gui.common.client.services.InfoService;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.LocaleUtil;
import com.logicaldoc.util.PluginRegistry;
import com.logicaldoc.util.config.PropertiesBean;
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
			Properties i18n = new Properties();
			try {
				i18n.load(this.getClass().getResourceAsStream("/i18n/i18n.properties"));
			} catch (IOException e) {
				log.error(e.getMessage());
			}

			Locale withLocale = LocaleUtil.toLocale(locale);
			ArrayList<GUIValuePair> supportedLanguages = new ArrayList<GUIValuePair>();
			GUIValuePair l = new GUIValuePair();
			l.setCode("en");
			l.setValue(Locale.ENGLISH.getDisplayName(withLocale));
			supportedLanguages.add(l);

			StringTokenizer st = new StringTokenizer(i18n.getProperty("locales"), ",", false);
			while (st.hasMoreElements()) {
				String code = (String) st.nextElement();
				if (code.equals("en"))
					continue;
				Locale lc = LocaleUtil.toLocale(code);
				l = new GUIValuePair();
				l.setCode(code);
				l.setValue(lc.getDisplayName(withLocale));
				supportedLanguages.add(l);
			}

			info.setSupportedLanguages(supportedLanguages.toArray(new GUIValuePair[0]));
			info.setBundle(getBundle(locale));

			// Checks if LogicalDOC has been initialised
			PropertiesBean pbean = (PropertiesBean) Context.getInstance().getBean("ContextProperties");
			List<GUIMessage> messages = new ArrayList<GUIMessage>();
			String jdbcUrl = pbean.getProperty("jdbc.url");
			if (StringUtils.isNotEmpty(jdbcUrl)) {
				if (jdbcUrl.equals("jdbc:hsqldb:mem:logicaldoc")) {
					GUIMessage setupReminder = new GUIMessage();
					setupReminder.setMessage(getValue(info, "setup.reminder"));
					HttpServletRequest request = this.getThreadLocalRequest();
					String urlPrefix = request.getScheme() + "://" + request.getServerName() + ":"
							+ request.getServerPort() + request.getContextPath();
					setupReminder.setUrl(urlPrefix + "/setup");
					messages.add(setupReminder);
				}
			}

			// Check if the application needs to be restarted
			if (ApplicationInitializer.needRestart) {
				GUIMessage restartReminder = new GUIMessage();
				restartReminder.setMessage(getValue(info, "needrestart"));
				messages.add(restartReminder);
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
			info.setFeatures(new String[] { "Feature_52", "Feature_53", "Feature_56", "Feature_13" });
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}

		return info;
	}

	static GUIValuePair[] getBundle(String locale) {
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

	private String getValue(GUIInfo info, String message) {
		for (GUIValuePair valuePair : info.getBundle()) {
			if (valuePair.getCode().equals(message)) {
				return valuePair.getValue();
			}
		}
		return "";
	}
}
