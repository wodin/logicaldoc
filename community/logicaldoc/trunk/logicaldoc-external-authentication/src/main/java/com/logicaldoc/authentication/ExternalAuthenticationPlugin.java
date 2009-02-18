package com.logicaldoc.authentication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.util.config.WebConfigurator;
import com.logicaldoc.util.plugin.LogicalDOCPlugin;

/**
 * Plugin class for LDAP integration
 * 
 * @author Wenzky Sebastian
 * @since 4.5
 */
public class ExternalAuthenticationPlugin extends LogicalDOCPlugin {
	protected static Log log = LogFactory.getLog(ExternalAuthenticationPlugin.class);

	public static String SERVLET_NAME = "LDAPEXTAUTH";

	@Override
	protected void install() throws Exception {
		super.install();
		WebConfigurator config = new WebConfigurator();
		config.addServlet(SERVLET_NAME, "com.logicaldoc.authentication.ldap.AuthServlet", 5);
		config.writeXMLDoc();
		config.addServletMapping(SERVLET_NAME, "/ldapImport/*");
		config.writeXMLDoc();
	}
}