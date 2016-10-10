package com.logicaldoc.cmis;

import java.io.File;

import org.apache.chemistry.opencmis.server.impl.CmisRepositoryContextListener;
import org.apache.chemistry.opencmis.server.shared.BasicAuthCallContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.config.WebConfigurator;
import com.logicaldoc.util.plugin.LogicalDOCPlugin;

/**
 * This class provides initialisations needed by Cmis-Plugin
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.5.1
 */
public class CmisPlugin extends LogicalDOCPlugin {
	protected static Logger log = LoggerFactory.getLogger(CmisPlugin.class);

	private static final String SERVLET_NAME = "Cmis";

	@Override
	protected void install() throws Exception {
		super.install();

		File dest = new File(getPluginPath());
		dest = dest.getParentFile().getParentFile();

		WebConfigurator config = new WebConfigurator(dest.getPath() + "/web.xml");

		config.addServlet(SERVLET_NAME, CmisServlet.class.getName(), 4);
		config.writeXMLDoc();

		config.addInitParam(SERVLET_NAME, "callContextHandler", BasicAuthCallContextHandler.class.getName(), null);

		config.addServletMapping(SERVLET_NAME, "/service/cmis/*");
		config.writeXMLDoc();

		config.addListener(CmisRepositoryContextListener.class.getName());
		config.writeXMLDoc();

		config.addContextParam("org.apache.chemistry.opencmis.REPOSITORY_CONFIG_FILE", "/cmis-repository.properties",
				null, WebConfigurator.INIT_PARAM.PARAM_OVERWRITE);
		config.writeXMLDoc();

		ContextProperties pbean = new ContextProperties();
		pbean.setProperty("cmis.enabled", "true");
		pbean.setProperty("cmis.changelog", "true");
		pbean.write();

		setRestartRequired();
	}
}