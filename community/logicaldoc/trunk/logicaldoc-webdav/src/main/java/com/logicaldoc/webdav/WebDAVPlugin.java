package com.logicaldoc.webdav;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.config.WebConfigurator;
import com.logicaldoc.util.plugin.LogicalDOCPlugin;
import com.logicaldoc.webdav.web.WebdavServlet;

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

		File dest = new File(getPluginPath());
		dest = dest.getParentFile().getParentFile();

		WebConfigurator config = new WebConfigurator(dest.getPath() + "/web.xml");

		config.addServlet(SERVLET_NAME, WebdavServlet.class.getName(), 4);
		config.writeXMLDoc();
		config.addInitParam(SERVLET_NAME, "resource-path-prefix", "/webdav", null);
		config.addInitParam(SERVLET_NAME, "resource-config", "/WEB-INF/config.xml", null);
		config.addServletMapping(SERVLET_NAME, "/webdav/*");
		config.writeXMLDoc();

		ContextProperties pbean = new ContextProperties(getClass().getClassLoader().getResource("context.properties"));
		pbean.setProperty("webdav.enabled", "true");
		pbean.write();

		setRestartRequired();
	}
}