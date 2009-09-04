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
	protected void doStart() throws Exception {
		super.doStart();

		boolean ecopied = false;

		// verify that the resources have actually been copied
		File ldwebapp = new File(System.getProperty("logicaldoc.app.rootdir"));
		File extFolder = new File(ldwebapp, "templates/external-authentication");
		File ldapFile = new File(extFolder, "ldap.jspx");
		if (ldapFile.exists())
			ecopied = true;

		if (ecopied == false)
			install();
	}

	@Override
	protected void install() throws Exception {
		super.install();

		String webappDir = resolvePath("webapp");
		File src = new File(webappDir);
		File dest = new File(System.getProperty("logicaldoc.app.rootdir"));
		log.info("Copy web resources from " + src.getPath() + " to " + dest.getPath());
		FileUtils.copyDirectory(src, dest);

		// Add some scheduling defaults
		PropertiesBean pbean = new PropertiesBean();
		pbean.setProperty("schedule.cron.LDAPSynchroniser", "00 10 01 * * ?");
		pbean.setProperty("schedule.length.LDAPSynchroniser", "3600");
		pbean.setProperty("schedule.enabled.LDAPSynchroniser", "false");
		pbean.setProperty("schedule.mode.LDAPSynchroniser", "simple");
		pbean.setProperty("schedule.interval.LDAPSynchroniser", "1800000");
		pbean.setProperty("schedule.delay.LDAPSynchroniser", "1800000");
		pbean.setProperty("schedule.cpuidle.LDAPSynchroniser", "-1");
		pbean.setProperty("schedule.cron.ADSynchroniser", "00 10 01 * * ?");
		pbean.setProperty("schedule.length.ADSynchroniser", "3600");
		pbean.setProperty("schedule.enabled.ADSynchroniser", "false");
		pbean.setProperty("schedule.mode.ADSynchroniser", "simple");
		pbean.setProperty("schedule.interval.ADSynchroniser", "1800000");
		pbean.setProperty("schedule.delay.ADSynchroniser", "1800000");
		pbean.setProperty("schedule.cpuidle.ADSynchroniser", "-1");

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
		logging.addTextAppender("ADSynchroniser");
		logging.write();
		logging.addHtmlAppender("ADSynchroniser_WEB");
		logging.write();
		logging.addCategory("com.logicaldoc.authentication.ldap.ADSynchroniser", new String[] { "ADSynchroniser",
				"ADSynchroniser_WEB" });
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
		pbean.setProperty("ldap.base", "");
		pbean.setProperty("ldap.authentication", "basic");
		pbean.setProperty("ldap.enabled", "false");

		pbean.setProperty("ad.url", "ldap://acme.net:389");
		pbean.setProperty("ad.realm", "acme.net");
		pbean.setProperty("ad.currentDN", "DC=acme,DC=NET");
		pbean.setProperty("ad.authenticationPattern", "{userName}@acme.net");
		pbean.setProperty("ad.username", "Administrator");
		pbean.setProperty("ad.password", "1");
		pbean.setProperty("ad.userIdentiferAttribute", "sAMAccountName");
		pbean.setProperty("ad.logonAttribute", "sAMAccountName");
		pbean.setProperty("ad.userClass", "person");
		pbean.setProperty("ad.groupClass", "group");
		pbean.setProperty("ad.groupIdentiferAttribute", "sAMAccountName");
		pbean.setProperty("ad.userBase", "CN=Users,DC=acme,DC=net");
		pbean.setProperty("ad.groupBase", "CN=Builtin,DC=acme,DC=net");
		pbean.setProperty("ad.base", "");
		pbean.setProperty("ad.authentication", "basic");
		pbean.setProperty("ad.enabled", "false");

		pbean.write();
	}
}