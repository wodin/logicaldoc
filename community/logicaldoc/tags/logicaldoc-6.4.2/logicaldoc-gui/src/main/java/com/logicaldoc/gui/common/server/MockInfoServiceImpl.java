package com.logicaldoc.gui.common.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.beans.GUIInfo;
import com.logicaldoc.gui.common.client.beans.GUIMessage;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.beans.GUIValuePair;
import com.logicaldoc.gui.common.client.services.InfoService;

/**
 * 
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class MockInfoServiceImpl extends RemoteServiceServlet implements InfoService {

	private static final long serialVersionUID = 1L;

	@Override
	public GUIInfo getInfo(String locale) {
		/*
		 * The product version must be taken from context.properties.
		 */
		GUIInfo info = new GUIInfo();

		StringTokenizer st = new StringTokenizer("it,en,de,fr", ",", false);
		List<GUIValuePair> locs = new ArrayList<GUIValuePair>();
		while (st.hasMoreElements()) {
			String token = (String) st.nextElement();
			Locale a = new Locale(token);
			GUIValuePair l = new GUIValuePair();
			l.setCode(a.toString());
			l.setValue(a.getDisplayName());
			locs.add(l);
		}
		info.setSupportedGUILanguages(locs.toArray(new GUIValuePair[0]));

		GUIValuePair[] languages = new GUIValuePair[2];
		GUIValuePair l = new GUIValuePair();
		Locale loc = new Locale("en");
		l.setCode(loc.toString());
		l.setValue(loc.getDisplayName());
		languages[0] = l;
		l = new GUIValuePair();
		loc = new Locale("it");
		l.setCode(loc.toString());
		l.setValue(loc.getDisplayName());
		languages[1] = l;
		info.setSupportedLanguages(languages);

		info.setBundle(getBundle(locale));
		info.setInstallationId("13245-u9ixcbviwhg934-13423-124t");

		String[] features = new String[70];
		for (int i = 0; i < features.length; i++) {
			features[i] = "Feature_" + i;
		}
		info.setFeatures(features);

		// info.setFeatures(new String[] { "Feature_58", "Feature_59",
		// "Feature_50", "Feature_52", "Feature_53",
		// "Feature_56", "Feature_13", "Feature_17", "Feature_19", "Feature_4"
		// });

		List<GUIMessage> messages = new ArrayList<GUIMessage>();

		GUIMessage message = new GUIMessage();
		message.setMessage("Test message 1");
		messages.add(message);
		message = new GUIMessage();
		message.setMessage("Test message 2");
		message.setUrl("http://www.google.com");
		messages.add(message);
		info.setMessages(messages.toArray(new GUIMessage[0]));

		return info;
	}

	public GUIValuePair[] getBundle(String locale) {
		System.out.println("** get bundle " + locale);

		// In production, use our LocaleUtil to instantiate the locale
		Locale l = new Locale(locale);
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

	@Override
	public GUIParameter[] getSessionInfo(String sid) {
		return null;
	}
}
