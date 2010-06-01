package com.logicaldoc.web.settings;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.gui.common.client.beans.GUIEmailSettings;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.beans.GUISecuritySettings;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.beans.GUIWebServiceSettings;
import com.logicaldoc.gui.frontend.client.services.SettingService;
import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.web.AbstractService;

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

			log.info("Email settings data written successfully.");
		} catch (Exception e) {
			log.error("Exception writing Email settings data: " + e.getMessage(), e);
		}

		return emailSettings;
	}
	
	@Override
	public void saveEmailSettings(String sid, GUIEmailSettings settings) {
		validateSession(sid);

		try {
//			PropertiesBean pbean = new PropertiesBean();
//
//			pbean.setProperty("password.ttl", Integer.toString(securitySettings.getPwdExpiration()));
//			pbean.setProperty("password.size", Integer.toString(securitySettings.getPwdSize()));
//
//			String users = "";
//			for (GUIUser user : securitySettings.getNotifiedUsers()) {
//				users = users + user.getUserName() + ", ";
//			}

//			pbean.setProperty("audit.user", users.trim());
//			pbean.write();
			log.info("Security settings data written successfully.");
		} catch (Exception e) {
			log.error("Exception writing Security settings data: " + e.getMessage(), e);
		}
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
	public void saveSettings(String sid, GUIParameter[] settings) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveWSSettings(String sid, GUIWebServiceSettings wsSettings, GUIWebServiceSettings webDavSettings) {
		// TODO Auto-generated method stub

	}
}