package com.logicaldoc.webapp.settings;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.gui.common.client.beans.GUIEmailSettings;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.beans.GUIWebServiceSettings;
import com.logicaldoc.gui.frontend.client.services.SettingService;
import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.webapp.AbstractService;

/**
 * Implementation of the SettingService
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class SettingServiceImpl extends AbstractService implements SettingService {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(SettingServiceImpl.class);

	@Override
	public GUIEmailSettings loadEmailSettings(String sid) {
		validateSession(sid);

		GUIEmailSettings emailSettings = new GUIEmailSettings();
		try {
			PropertiesBean pbean = new PropertiesBean();

			emailSettings.setSmtpServer(pbean.getProperty("smtp.host"));
			emailSettings.setPort(Integer.parseInt(pbean.getProperty("smtp.port")));

			emailSettings.setUsername(pbean.getProperty("smtp.username"));
			emailSettings.setPwd(pbean.getProperty("smtp.password"));

			emailSettings.setConnSecurity(Integer.parseInt(pbean.getProperty("smtp.connectionSecurity")));
			emailSettings.setSecureAuth("true".equals(pbean.getProperty("smtp.authEncripted")) ? true : false);

			emailSettings.setSenderEmail(pbean.getProperty("smtp.sender"));

		} catch (IOException e) {
		}

		return emailSettings;
	}

	@Override
	public GUIParameter[] loadSettings(String sid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GUIWebServiceSettings[] loadWSSettings(String sid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveEmailSettings(String sid, GUIEmailSettings settings) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveSettings(String sid, GUIParameter[] settings) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveWSSettings(String sid, GUIWebServiceSettings wsSettings, GUIWebServiceSettings webDavSettings) {
		// TODO Auto-generated method stub

	}
}