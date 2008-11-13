package com.logicaldoc.webdav;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.webdav.DavConstants;

import com.logicaldoc.util.config.WebConfigurator;
import com.logicaldoc.util.plugin.LogicalDOCPlugin;

/**
 * This class provides initialisations needed by this plug-in
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class WebDAVPlugin extends LogicalDOCPlugin {
	protected static Log log = LogFactory.getLog(WebDAVPlugin.class);
	private static final String SERVLET_NAME = "Webdav";

	@Override
	protected void doStart() throws Exception {
		WebConfigurator config=new WebConfigurator();
		
		config.addServlet(SERVLET_NAME, "com.logicaldoc.webdav.web.WebdavServlet", 4);
		config.writeXMLDoc();
		config.addInitParam(SERVLET_NAME, "resource-path-prefix", "/webdav", null);
		config.addInitParam(SERVLET_NAME, "resource-config", "/WEB-INF/config.xml", null);
		config.addServletMapping(SERVLET_NAME, "/webdav/*");
		config.writeXMLDoc();
		System.out.println("out: " + DavConstants.XML_DEPTH);
	}
}
