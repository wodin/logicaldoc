package com.logicaldoc.web.service;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.gui.common.client.beans.GUIEmailSettings;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.beans.GUISession;
import com.logicaldoc.gui.common.client.beans.GUIWebServiceSettings;
import com.logicaldoc.web.AbstractWebappTestCase;

public class SettingServiceImplTest extends AbstractWebappTestCase {

	// Instance under test
	private SettingServiceImpl service = new SettingServiceImpl();

	private GUISession session;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		SecurityServiceImpl securityService = new SecurityServiceImpl();
		session = securityService.login("admin", "admin");
		Assert.assertNotNull(session);
		Assert.assertNotNull(SessionManager.getInstance().get(session.getSid()));
	}

	@Test
	public void testSaveEmailSettings() {
		GUIEmailSettings emailSettings = new GUIEmailSettings();
		emailSettings.setSmtpServer("smtp.logicalobjects.it");
		emailSettings.setPort(8080);
		emailSettings.setUsername("admin");
		emailSettings.setPwd("pippo");
		emailSettings.setConnSecurity(GUIEmailSettings.SECURITY_TLS);
		emailSettings.setSecureAuth(true);
		emailSettings.setSenderEmail("mario@acme.com");

		service.saveEmailSettings(session.getSid(), emailSettings);
	}

	@Test
	public void testSaveSettings() {
		GUIParameter[] params = new GUIParameter[50];
		for (int i = 0; i < params.length; i++) {
			GUIParameter p = new GUIParameter("param" + i + "_name", "Value " + i);
			params[i] = p;
		}

		service.saveSettings(session.getSid(), params);
	}

	@Test
	public void testSaveWSSettings() {
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

		service.saveWSSettings(session.getSid(), wsSettings, wdSettings);
	}
}