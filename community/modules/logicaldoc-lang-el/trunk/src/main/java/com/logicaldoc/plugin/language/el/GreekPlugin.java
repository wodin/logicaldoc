package com.logicaldoc.plugin.language.el;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.util.config.FacesConfigurator;
import com.logicaldoc.util.plugin.LogicalDOCPlugin;

/**
 * Language module plugin class.
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 4.5
 */
public class GreekPlugin extends LogicalDOCPlugin {

	protected static Log log = LogFactory.getLog(GreekPlugin.class);

	@Override
	protected void install() throws Exception {
		
		String classesDir = getManager().getPathResolver().resolvePath(getDescriptor(), "classes").toString();
		log.debug("classesDir: " + classesDir);
		
		if (classesDir.startsWith("file:")) {
			classesDir = classesDir.substring(5);
		}

		File src = new File(classesDir, "i18n");
		File destRoot = new File(System.getProperty("logicaldoc.app.rootdir"));
		File destClasses = new File(destRoot, "WEB-INF/classes/i18n");

		log.debug("Copy resources from " + src.getPath() + " to " + destClasses.getPath());
		FileUtils.copyDirectory(src, destClasses);

		// Now add the message bundle
		log.info("Add Greek (el) language to faces-config.xml");
		FacesConfigurator facesConfig = new FacesConfigurator();
		facesConfig.addLanguageToFacesConfig("el");
	}
}
