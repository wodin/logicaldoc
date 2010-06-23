package com.logicaldoc.web.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.beans.GUIInfo;
import com.logicaldoc.gui.common.client.beans.GUIValuePair;
import com.logicaldoc.gui.common.client.services.InfoService;
import com.logicaldoc.util.LocaleUtil;

/**
 * 
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
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}

		return info;
	}

	
	static GUIValuePair[] getBundle(String locale) {
		// In production, use our LocaleUtil to instantiate the locale
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
}
