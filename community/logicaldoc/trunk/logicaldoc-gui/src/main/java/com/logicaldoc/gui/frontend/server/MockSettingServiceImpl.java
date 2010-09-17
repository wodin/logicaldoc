package com.logicaldoc.gui.frontend.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIEmailSettings;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.beans.GUIWebServiceSettings;
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
	public GUIWebServiceSettings[] loadWSSettings(String sid) {
		GUIWebServiceSettings[] settings = new GUIWebServiceSettings[2];

		GUIWebServiceSettings wsSettings = new GUIWebServiceSettings();
		wsSettings.setEnabled(true);
		wsSettings.setUrl("http://demo.logicaldoc.com:80/logicaldoc/services/Dms");
		wsSettings.setDescriptor("http://demo.logicaldoc.com:80/logicaldoc/services/Dms?wsdl");

		GUIWebServiceSettings wdSettings = new GUIWebServiceSettings();
		wdSettings.setEnabled(false);
		wdSettings.setUrl("http://demo.logicaldoc.com:80/logicaldoc/webdav/store");

		settings[0] = wsSettings;
		settings[1] = wdSettings;

		return settings;
	}

	@Override
	public void saveWSSettings(String sid, GUIWebServiceSettings wsSettings, GUIWebServiceSettings webDavSettings) {

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
}
