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

		settings[0] = wsSettings;
		settings[1] = wdSettings;

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
	public GUIParameter[][] loadRepositories(String sid) throws InvalidSessionException {
		GUIParameter[][] repos = new GUIParameter[2][7];
		repos[0][0] = new GUIParameter("dbdir", "db");
		repos[0][1] = new GUIParameter("exportdir", "export");
		repos[0][2] = new GUIParameter("importdir", "import");
		repos[0][3] = new GUIParameter("indexdir", "index");
		repos[0][4] = new GUIParameter("logdir", "log");
		repos[0][5] = new GUIParameter("plugindir", "plugin");
		repos[0][6] = new GUIParameter("userdir", "user");

		repos[1][0] = new GUIParameter("store1", "store1");
		repos[1][1] = new GUIParameter("store2", "store2");

		return repos;
	}

	@Override
	public void saveRepositories(String sid, GUIParameter[][] repos) throws InvalidSessionException {

	}

	@Override
	public GUIParameter[] loadOcrSettings(String sid) throws InvalidSessionException {
		GUIParameter[] params = new GUIParameter[6];
		params[0] = new GUIParameter("ocr.enabled", "true");
		params[1] = new GUIParameter("ocr.resolution.threshold", "600");
		params[2] = new GUIParameter("ocr.text.threshold", "1");
		params[3] = new GUIParameter("ocr.includes", "");
		params[4] = new GUIParameter("ocr.excludes", "");
		params[5] = new GUIParameter("ocr.timeout", "60");
		return params;
	}

	@Override
	public GUIParameter[] loadQuotaSettings(String sid) throws InvalidSessionException {
		GUIParameter[] params = new GUIParameter[2];
		params[0] = new GUIParameter("quota", "20");
		params[1] = new GUIParameter("quota.threshold", "10");

		return params;
	}

	@Override
	public void saveQuotaSettings(String sid, GUIParameter[] quotaSettings) throws InvalidSessionException {
	}

	@Override
	public GUIParameter[] computeStoragesSize(String sid) throws InvalidSessionException {
		GUIParameter[] params = new GUIParameter[2];

		for (int i = 0; i < 2; i++) {
			GUIParameter param = new GUIParameter();
			param.setName("store." + i);
			param.setValue("23456789" + i);
			params[i] = param;
		}

		return params;
	}

	@Override
	public GUIParameter[] loadGUISettings(String sid) throws InvalidSessionException {
		return null;
	}

	@Override
	public GUIParameter[] loadSettingsByNames(String sid, String[] names) throws InvalidSessionException {
		return loadSettings(sid);
	}
}