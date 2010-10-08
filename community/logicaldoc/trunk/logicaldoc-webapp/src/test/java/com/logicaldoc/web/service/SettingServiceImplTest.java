package com.logicaldoc.web.service;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIEmailSettings;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.beans.GUISession;
import com.logicaldoc.web.AbstractWebappTCase;

public class SettingServiceImplTest extends AbstractWebappTCase {

	// Instance under test
	private SettingServiceImpl service = new SettingServiceImpl();

	private GUISession session;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		SecurityServiceImpl securityService = new SecurityServiceImpl();
		session = securityService.login("admin", "admin", null);
		Assert.assertNotNull(session);
		Assert.assertNotNull(SessionManager.getInstance().get(session.getSid()));
	}

	@Test
	public void testSaveEmailSettings() throws InvalidSessionException {
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
	public void testSaveSettings() throws InvalidSessionException {
		GUIParameter[] params = new GUIParameter[50];
		for (int i = 0; i < params.length; i++) {
			GUIParameter p = new GUIParameter("param" + i + "_name", "Value " + i);
			params[i] = p;
		}

		service.saveSettings(session.getSid(), params);
	}

	@Test
	public void testSaveWSSettings() throws InvalidSessionException {
		GUIParameter[] settings = new GUIParameter[2];

		GUIParameter wsSettings = new GUIParameter();
		wsSettings.setName("webservice.enabled");
		wsSettings.setValue("true");

		GUIParameter wdSettings = new GUIParameter();
		wsSettings.setName("webdav.enabled");
		wsSettings.setValue("true");

		settings[0] = wsSettings;
		settings[1] = wdSettings;

		service.saveClientSettings(session.getSid(), settings);
	}
}