package com.logicaldoc.gui.setup.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIEmailSettings;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.frontend.client.services.SettingService;

/**
 * Implementation of the SettingService
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class MockSettingServiceImpl extends RemoteServiceServlet implements SettingService {

	private static final long serialVersionUID = 1L;

	@Override
	public GUIParameter[] loadClientSettings(String sid) {
		GUIParameter[] settings = new GUIParameter[2];

		GUIParameter wsSettings = new GUIParameter();
		wsSettings.setName("webservice.enabled");
		wsSettings.setValue("true");

		GUIParameter wdSettings = new GUIParameter();
		wsSettings.setName("webdav.enabled");
		wsSettings.setValue("true");

		GUIParameter officeSettings = new GUIParameter();
		officeSettings.setName("office.enabled");
		officeSettings.setValue("true");

		settings[0] = wsSettings;
		settings[1] = wdSettings;
		settings[2] = wdSettings;

		return settings;
	}

	@Override
	public void saveClientSettings(String sid, GUIParameter[] settings) {

	}

	@Override
	public GUIParameter[] loadSettings(String sid) {
		GUIParameter[] params = new GUIParameter[50];
		for (int i = 0; i < params.length; i++) {
			GUIParameter p = new GUIParameter("param" + i + "_name", "Value " + i);
			params[i] = p;
		}
		return params;
	}

	@Override
	public void saveSettings(String sid, GUIParameter[] settings) {

	}

	@Override
	public GUIEmailSettings loadEmailSettings(String sid) {
		GUIEmailSettings email = new GUIEmailSettings();
		email.setSmtpServer("smtp.logicalobjects.it");
		email.setPort(8080);
		email.setUsername("admin");
		email.setPwd("pippo");
		email.setConnSecurity(GUIEmailSettings.SECURITY_TLS);
		email.setSecureAuth(true);
		email.setSenderEmail("mario@acme.com");

		return email;
	}

	@Override
	public void saveEmailSettings(String sid, GUIEmailSettings settings) {

	}

	@Override
	public String[] loadValues(String sid, String[] names) throws InvalidSessionException {
		String values[] = new String[names.length];
		for (int i = 0; i < names.length; i++) {
			values[i] = names[i] + 1;
		}
		return values;
	}

	@Override
	public GUIParameter[] loadFolders(String sid) throws InvalidSessionException {
		GUIParameter[] params = new GUIParameter[8];
		params[0] = new GUIParameter("dbdir", "db");
		params[1] = new GUIParameter("docdir", "docs");
		params[2] = new GUIParameter("exportdir", "export");
		params[3] = new GUIParameter("importdir", "import");
		params[4] = new GUIParameter("indexdir", "index");
		params[5] = new GUIParameter("logdir", "log");
		params[6] = new GUIParameter("plugindir", "plugin");
		params[7] = new GUIParameter("userdir", "user");

		return params;
	}

	@Override
	public void saveFolders(String sid, GUIParameter[] folders) throws InvalidSessionException {

	}

	@Override
	public GUIParameter[] loadProxySettings(String sid) throws InvalidSessionException {
		GUIParameter[] params = new GUIParameter[8];
		params[0] = new GUIParameter("host", "host");
		params[1] = new GUIParameter("port", "8080");
		params[2] = new GUIParameter("username", "john");
		params[3] = new GUIParameter("password", "scott");

		return params;
	}

	@Override
	public void saveProxySettings(String sid, GUIParameter[] proxySettings) throws InvalidSessionException {
	}

	@Override
	public GUIParameter[] loadOcrSettings(String sid) throws InvalidSessionException {
		GUIParameter[] params = new GUIParameter[8];
		params[0] = new GUIParameter("ocr.enabled", "true");
		params[1] = new GUIParameter("ocr.resolution.threshold", "600");
		params[2] = new GUIParameter("ocr.text.threshold", "1");
		params[3] = new GUIParameter("ocr.includes", "");
		params[4] = new GUIParameter("ocr.excludes", "");
		return params;
	}
}