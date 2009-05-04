package com.logicaldoc.authentication;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.util.config.FacesConfigurator;
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

		// Now add the message bundle
		log.info("Add logicaldoc-external-authentication resource bundle");
		FacesConfigurator facesConfig = new FacesConfigurator();
		facesConfig.addBundle("i18n.application-external-authentication");

		// Add some scheduling defaults
		PropertiesBean pbean = new PropertiesBean();
		pbean.setProperty("schedule.cron.LDAPSynchroniser", "00 10 01 * * ?");
		pbean.setProperty("schedule.length.LDAPSynchroniser", "3600");
		pbean.setProperty("schedule.enabled.LDAPSynchroniser", "false");
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

		//Add some default settings
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
		
	}
}