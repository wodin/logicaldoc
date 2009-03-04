package com.logicaldoc.webservice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.util.config.WebConfigurator;
import com.logicaldoc.util.plugin.LogicalDOCPlugin;

/**
 * This class provides initialisations needed by this plug-in
 * 
 * @author Matteo Caruso - Logical Object
 * @since 3.6
 * 
 */
public class WebservicePlugin extends LogicalDOCPlugin {
	protected static Log log = LogFactory.getLog(WebservicePlugin.class);

	@Override
	protected void install() throws Exception {
		WebConfigurator config=new WebConfigurator();
		config.addServlet("CXFServlet", "org.apache.cxf.transport.servlet.CXFServlet");
		config.writeXMLDoc();
		config.addServletMapping("CXFServlet", "/services/*");
		config.writeXMLDoc();
	}
}
