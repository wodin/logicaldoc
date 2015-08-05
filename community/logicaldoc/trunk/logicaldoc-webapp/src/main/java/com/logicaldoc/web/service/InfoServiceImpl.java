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
import com.logicaldoc.core.rss.FeedMessage;
import com.logicaldoc.core.rss.dao.FeedMessageDAO;
import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.Tenant;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.gui.common.client.beans.GUIInfo;
import com.logicaldoc.gui.common.client.beans.GUIMessage;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.beans.GUITenant;
import com.logicaldoc.gui.common.client.beans.GUIValue;
import com.logicaldoc.gui.common.client.services.InfoService;
import com.logicaldoc.i18n.I18N;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.LocaleUtil;
import com.logicaldoc.util.SoftwareVersion;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.web.ApplicationListener;

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
	public GUIInfo getInfo(String locale, String tenantName) {
		ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);

		GUIInfo info = null;
		try {
			info = getInfo(tenantName);
			info.setBundle(getBundle(locale));

			Locale withLocale = LocaleUtil.toLocale(locale);
			ArrayList<GUIValue> supportedLanguages = new ArrayList<GUIValue>();

			List<String> installedLocales = I18N.getLocales();
			for (String loc : installedLocales) {
				if ("enabled".equals(config.getProperty(tenantName + ".lang." + loc + ".gui"))) {
					Locale lc = LocaleUtil.toLocale(loc);
					GUIValue l = new GUIValue();
					l.setCode(loc);
					l.setValue(lc.getDisplayName(withLocale));
					supportedLanguages.add(l);
				}
			}

			info.setSupportedGUILanguages(supportedLanguages.toArray(new GUIValue[0]));

			LanguageManager manager = LanguageManager.getInstance();
			Collection<Language> languages = manager.getActiveLanguages(tenantName);
			supportedLanguages.clear();
			for (Language language : languages) {
				Locale lc = language.getLocale();
				GUIValue l = new GUIValue();
				l.setCode(lc.toString());
				l.setValue(lc.getDisplayName(withLocale));
				supportedLanguages.add(l);
			}
			info.setSupportedLanguages(supportedLanguages.toArray(new GUIValue[0]));

			List<GUIMessage> messages = new ArrayList<GUIMessage>();

			// Check if the application needs to be restarted
			if (ApplicationListener.needRestart) {
				GUIMessage restartReminder = new GUIMessage();
				restartReminder.setMessage(getValue(info, "needrestart"));
				messages.add(restartReminder);
			}

			if (!ApplicationListener.needRestart) {
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

			String newVersion = detectNewVersion();
			if (newVersion != null) {
				GUIMessage m = new GUIMessage();
				m.setMessage(I18N.message("newversionaval", withLocale, newVersion));
				messages.add(m);
			}

			info.setMessages(messages.toArray(new GUIMessage[0]));

			return info;
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	public String detectNewVersion() {
		try {
			ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
			SoftwareVersion actualVersion = new SoftwareVersion(config.getProperty("product.release"));

			FeedMessageDAO feedMessageDao = (FeedMessageDAO) Context.getInstance().getBean(FeedMessageDAO.class);
			List<FeedMessage> messages = feedMessageDao.findByTitle("LogicalDOC v%");
			for (FeedMessage message : messages) {
				if (message.getDeleted() == 0 && message.getRead() == 0) {
					SoftwareVersion otherVersion = new SoftwareVersion(message.getTitle().substring(
							"LogicalDOC v".length()));
					if (otherVersion.compareTo(actualVersion) > 0)
						return otherVersion.get();
				}
			}
		} catch (Throwable t) {
			log.warn(t.getMessage());
		}
		return null;
	}

	/**
	 * Retrieves the informations but not localization issues like messages and
	 * installed languages.
	 */
	public static GUIInfo getInfo(String tenantName) {
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
		info.setRunLevel(config.getProperty("runlevel"));

		String tname = tenantName;
		if (tname == null)
			tname = Tenant.DEFAULT_NAME;

		GUITenant tenant = null;
		try {
			tenant = SecurityServiceImpl.getTenant(tname);
		} catch (Throwable t) {
			// Before setup we may have exception here
			log.warn(t.getMessage());
		}

		// If no tenant was found, go with the default one
		if (tenant == null) {
			tenant = new GUITenant();
			tenant.setName(Tenant.DEFAULT_NAME);
			tenant.setId(Tenant.DEFAULT_ID);
		}

		info.setTenant(tenant);
		info.setSessionHeartbeat(Integer.parseInt(config.getProperty(tname + ".session.heartbeat")));

		try {
			ArrayList<GUIValue> values = new ArrayList<GUIValue>();
			for (Object key : config.keySet()) {
				GUIValue pair = new GUIValue();
				pair.setCode((String) key);
				pair.setValue(config.getProperty((String) key));
				values.add(pair);
			}
			info.setConfig(values.toArray(new GUIValue[0]));
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}

		return info;
	}

	static protected GUIValue[] getBundle(String locale) {
		Locale l = LocaleUtil.toLocale(locale);
		ResourceBundle rb = ResourceBundle.getBundle("i18n.messages", l);
		GUIValue[] buf = new GUIValue[rb.keySet().size()];
		int i = 0;
		for (String key : rb.keySet()) {
			GUIValue entry = new GUIValue();
			entry.setCode(key);
			entry.setValue(rb.getString(key));
			buf[i++] = entry;
		}
		return buf;
	}

	protected String getValue(GUIInfo info, String message) {
		for (GUIValue valuePair : info.getBundle()) {
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