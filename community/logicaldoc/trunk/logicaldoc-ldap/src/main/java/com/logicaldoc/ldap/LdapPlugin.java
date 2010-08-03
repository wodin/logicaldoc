package com.logicaldoc.ldap;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.ldap.model.LDAPSynchroniser;
import com.logicaldoc.util.config.LoggingConfigurator;
import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.util.config.WebConfigurator;
import com.logicaldoc.util.plugin.LogicalDOCPlugin;

/**
 * Plugin class for LDAP integration
 * 
 * @author Wenzky Sebastian
 * @since 4.5
 */
public class LdapPlugin extends LogicalDOCPlugin {

	protected static Log log = LogFactory.getLog(LdapPlugin.class);

	private static final String SERVICE_NAME = "LdapService";

	@Override
	protected void install() throws Exception {
		super.install();

		File dest = new File(getPluginPath());
		dest = dest.getParentFile().getParentFile();

		WebConfigurator config = new WebConfigurator(dest.getPath() + "/web.xml");

		config.addServlet(SERVICE_NAME, LdapServiceImpl.class.getName(), 4);
		config.writeXMLDoc();
		config.addServletMapping(SERVICE_NAME, "/frontend/ldap");
		config.writeXMLDoc();

		setRestartRequired();

		// Add some scheduling defaults
		PropertiesBean pbean = new PropertiesBean();
		pbean.setProperty("schedule.cron.LDAPSynchroniser", "00 10 01 * * ?");
		pbean.setProperty("schedule.length.LDAPSynchroniser", "3600");
		pbean.setProperty("schedule.enabled.LDAPSynchroniser", "false");
		pbean.setProperty("schedule.mode.LDAPSynchroniser", "simple");
		pbean.setProperty("schedule.interval.LDAPSynchroniser", "1800000");
		pbean.setProperty("schedule.delay.LDAPSynchroniser", "1800000");
		pbean.setProperty("schedule.cpuidle.LDAPSynchroniser", "-1");

		pbean.write();

		// Add crawler log issues
		LoggingConfigurator logging = new LoggingConfigurator();
		logging.addTextAppender("LDAPSynchroniser");
		logging.write();
		logging.addHtmlAppender("LDAPSynchroniser_WEB");
		logging.write();
		logging.addCategory(LDAPSynchroniser.class.getName(), new String[] { "LDAPSynchroniser",
				"LDAPSynchroniser_WEB" });
		logging.write();

		// Add some default settings
		pbean.setProperty("ldap.url", "ldap://localhost:10389");
		pbean.setProperty("ldap.realm", "localhost");
		pbean.setProperty("ldap.currentDN", "ou=system");
		pbean.setProperty("ldap.authenticationPattern", "{logonAttribute}={userName},{userBaseEntry}");
		pbean.setProperty("ldap.username", "admin");
		pbean.setProperty("ldap.password", "admin");
		pbean.setProperty("ldap.userIdentiferAttribute", "cn");
		pbean.setProperty("ldap.logonAttribute", "uid");
		pbean.setProperty("ldap.userClass", "person");
		pbean.setProperty("ldap.groupClass", "groupOfNames");
		pbean.setProperty("ldap.groupIdentiferAttribute", "cn");
		pbean.setProperty("ldap.userBase", "ou=users,ou=system");
		pbean.setProperty("ldap.groupBase", "ou=groups,ou=system");
		pbean.setProperty("ldap.base", " ");
		pbean.setProperty("ldap.authentication", "basic");
		pbean.setProperty("ldap.enabled", "false");
		pbean.setProperty("ldap.defaultLanguage", "en");
		pbean.write();
	}
}