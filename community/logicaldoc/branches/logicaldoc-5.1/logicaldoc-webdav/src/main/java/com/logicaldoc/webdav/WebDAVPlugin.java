package com.logicaldoc.webdav;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.util.config.WebConfigurator;
import com.logicaldoc.util.plugin.LogicalDOCPlugin;

/**
 * This class provides initialisations needed by WebDAV-Plugin
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class WebDAVPlugin extends LogicalDOCPlugin {
	protected static Log log = LogFactory.getLog(WebDAVPlugin.class);

	private static final String SERVLET_NAME = "Webdav";

	@Override
	protected void install() throws Exception {
		super.install();
		
		String webappDir = resolvePath("webapp");
		File src = new File(webappDir);
		File dest = new File(System.getProperty("logicaldoc.app.rootdir"));

		log.info("Copy web resources from " + src.getPath() + " to "
				+ dest.getPath());
		FileUtils.copyDirectory(src, dest);
		
		WebConfigurator config = new WebConfigurator();
		config.addServlet(SERVLET_NAME, "com.logicaldoc.webdav.web.WebdavServlet", 4);
		config.writeXMLDoc();
		config.addInitParam(SERVLET_NAME, "resource-path-prefix", "/webdav", null);
		config.addInitParam(SERVLET_NAME, "resource-config", "/WEB-INF/config.xml", null);
		config.addServletMapping(SERVLET_NAME, "/webdav/*");
		config.writeXMLDoc();

		PropertiesBean pbean = new PropertiesBean(getClass().getClassLoader().getResource("context.properties"));
		pbean.setProperty("webdav.enabled", "true");
		pbean.write();
	}
}