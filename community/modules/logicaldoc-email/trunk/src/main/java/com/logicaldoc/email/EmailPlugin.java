package com.logicaldoc.email;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.util.config.FacesConfigurator;
import com.logicaldoc.util.config.LoggingConfigurator;
import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.util.plugin.LogicalDOCPlugin;

/**
 * Web module plugin class. At the startup copies web resources into the webapp
 * dir
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class EmailPlugin extends LogicalDOCPlugin {
	protected static Log log = LogFactory.getLog(EmailPlugin.class);

	@Override
	protected void install() throws Exception {
		String webappDir = resolvePath("webapp");

		File src = new File(webappDir);
		File dest = new File(System.getProperty("logicaldoc.app.rootdir"));

		log.info("Copy web resources from " + src.getPath() + " to " + dest.getPath());
		FileUtils.copyDirectory(src, dest);

		// Now add the message bundle
		log.info("Add logicaldoc-email resource bundle");
		FacesConfigurator facesConfig = new FacesConfigurator();
		facesConfig.addBundle("i18n.application-email");

		try {
			// Add some scheduling defaults
			PropertiesBean pbean = new PropertiesBean();
			pbean.setProperty("schedule.cron.EmailCrawler", "00 10 * * * ?");
			pbean.setProperty("schedule.length.EmailCrawler", "1800");
			pbean.setProperty("schedule.enabled.EmailCrawler", "true");
			pbean.write();
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		// Add crawler log issues
		LoggingConfigurator logging = new LoggingConfigurator();
		logging.addTextAppender("EmailCrawler");
		logging.write();
		logging.addHtmlAppender("EmailCrawler_WEB");
		logging.write();
		logging.addCategory("com.logicaldoc.email.EmailCrawler", new String[] { "EmailCrawler", "EmailCrawler_WEB" });
		logging.write();
	}
}