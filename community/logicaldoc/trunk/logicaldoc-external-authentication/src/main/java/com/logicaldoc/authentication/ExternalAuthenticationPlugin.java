package com.logicaldoc.authentication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
	}
}