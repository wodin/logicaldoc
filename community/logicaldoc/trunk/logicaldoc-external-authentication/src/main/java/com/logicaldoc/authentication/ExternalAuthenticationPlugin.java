package com.logicaldoc.authentication;

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
	protected void install() throws Exception {
		super.install();

		// Now add the message bundle
		log.info("Add logicaldoc-external-authentication resource bundle");
		FacesConfigurator facesConfig = new FacesConfigurator();
		facesConfig.addBundle("i18n.application-external-authentication");
		
		// Add some scheduling defaults
		PropertiesBean pbean = new PropertiesBean();
		pbean.setProperty("schedule.cron.LDAPSynchroniser", "00 00 0/6 * * ?");
		pbean.setProperty("schedule.length.LDAPSynchroniser", "3600");
		pbean.setProperty("schedule.enabled.LDAPSynchroniser", "false");
		pbean.write();

		
		
		// Add crawler log issues
		LoggingConfigurator logging = new LoggingConfigurator();
		logging.addTextAppender("LDAPSynchroniser");
		logging.write();
		logging.addHtmlAppender("LDAPSynchroniser_WEB");
		logging.write();
		logging.addCategory("com.logicaldoc.authentication.ldap.LDAPSynchroniser", new String[] {
				"LDAPSynchroniser", "LDAPSynchroniser_WEB" });
		logging.write();
	}
}