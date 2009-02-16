package com.logicaldoc.authentication;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.ExtensionPoint;

import com.logicaldoc.util.config.ContextConfigurator;
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
	public static String SERVLET_NAME="LDAPEXTAUTH";
	
	@Override
	protected void doStart() throws Exception {
		ContextConfigurator cfg = new ContextConfigurator();
		WebConfigurator config = new WebConfigurator();
		config.addServlet(SERVLET_NAME,
				"com.logicaldoc.authentication.ldap.AuthServlet", 5);
		config.writeXMLDoc();
		config.addServletMapping(SERVLET_NAME, "/ldapImport/*");
		config.writeXMLDoc();
		
		List<String> authenticationComponents = new LinkedList<String>();
		
		ExtensionPoint toolExtPoint = getManager().getRegistry()
				.getExtensionPoint(getDescriptor().getId(),
						"AuthenticationComponents");
		for (Iterator<Extension> it = toolExtPoint.getConnectedExtensions()
				.iterator(); it.hasNext();) {
			Extension ext = it.next();
			authenticationComponents.add(ext.getParameter("beanId").valueAsString());
		}
	
		cfg.addPropertyBeanRefList("authenticationChain", "authenticationComponents", authenticationComponents);
		
		cfg.write();
	}
}