package com.logicaldoc.webservice;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.util.config.WebConfigurator;
import com.logicaldoc.util.plugin.LogicalDOCPlugin;

/**
 * This class provides initializations needed by this plug-in
 * 
 * @author Matteo Caruso - Logical Object
 * @since 3.6
 * 
 */
public class WebservicePlugin extends LogicalDOCPlugin {
	protected static Log log = LogFactory.getLog(WebservicePlugin.class);

	@Override
	protected void install() throws Exception {
		PropertiesBean pbean = new PropertiesBean(getClass().getClassLoader().getResource("context.properties"));
		pbean.setProperty("webservice.mtom", "false");
		pbean.setProperty("webservice.enabled", "true");
		pbean.write();

		File dest = new File(getPluginPath());
		dest = dest.getParentFile().getParentFile();
		WebConfigurator config = new WebConfigurator(dest.getPath() + "/web.xml");
		config.addServlet("CXFServlet", "com.logicaldoc.webservice.WebserviceServlet");
		config.writeXMLDoc();
		config.addServletMapping("CXFServlet", "/services/*");
		config.writeXMLDoc();
		
		setRestartRequired();
	}
}