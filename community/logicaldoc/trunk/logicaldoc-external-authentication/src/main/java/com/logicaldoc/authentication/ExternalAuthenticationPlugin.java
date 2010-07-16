package com.logicaldoc.authentication;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.util.config.LoggingConfigurator;
import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.util.plugin.LogicalDOCPlugin;

/**
 * Plugin class for LDAP integration
 * 
 * @author Wenzky Sebastian
 * @since 4.5
 */
public class ExternalAuthenticationPlugin extends LogicalDOCPlugin {

	protected static Log log = LogFactory.getLog(ExternalAuthenticationPlugin.class);

	@Override
	protected void install() throws Exception {
		super.install();

//		String webappDir = resolvePath("webapp");
//		File src = new File(webappDir);
//		File dest = new File(webappDir + "/../../../../");
//		log.info("Copy web resources from " + src.getPath() + " to " + dest.getPath());
//		FileUtils.copyDirectory(src, dest);

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
		logging.addCategory("com.logicaldoc.authentication.ldap.LDAPSynchroniser", new String[] { "LDAPSynchroniser",
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