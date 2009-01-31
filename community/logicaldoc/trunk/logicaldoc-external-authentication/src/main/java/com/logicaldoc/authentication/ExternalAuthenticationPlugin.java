package com.logicaldoc.authentication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.util.plugin.LogicalDOCPlugin;

/**
 * This class provides initialisations needed by ExternalAuthentication-Plugin
 * 
 * @author Wenzky Sebastian
 * 
 */
public class ExternalAuthenticationPlugin extends LogicalDOCPlugin {
	protected static Log log = LogFactory.getLog(ExternalAuthenticationPlugin.class);

	@Override
	protected void doStart() throws Exception {
		
	}
}