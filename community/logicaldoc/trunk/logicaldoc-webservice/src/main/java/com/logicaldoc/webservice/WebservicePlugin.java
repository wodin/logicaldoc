package com.logicaldoc.webservice;

import java.io.File;

import org.apache.commons.io.FileUtils;
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
		String webappDir = resolvePath("webapp");
		File src = new File(webappDir);
		File dest = new File(webappDir + "/../../../../");

		log.info("Copy web resources from " + src.getPath() + " to "
				+ dest.getPath());
		FileUtils.copyDirectory(src, dest);
		
		WebConfigurator config = new WebConfigurator(dest.getPath()+"/WEB-INF/web.xml");
		config.addServlet("CXFServlet", "com.logicaldoc.webservice.WebserviceServlet");
		config.writeXMLDoc();
		config.addServletMapping("CXFServlet", "/services/*");
		config.writeXMLDoc();
	}
}
