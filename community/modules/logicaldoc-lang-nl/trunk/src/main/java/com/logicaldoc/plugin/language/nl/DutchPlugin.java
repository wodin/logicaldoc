package com.logicaldoc.plugin.language.nl;

import java.io.File;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.i18n.Language;
import com.logicaldoc.core.searchengine.Indexer;
import com.logicaldoc.util.config.FacesConfigurator;
import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.util.plugin.LogicalDOCPlugin;

public class DutchPlugin extends LogicalDOCPlugin {

	protected static Log log = LogFactory.getLog(DutchPlugin.class);

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
		
		// Create Lucene Index
		createLuceneIndex();

		// Now add the message bundle
		log.info("Add Dutch (nl) language to faces-config.xml");
		FacesConfigurator facesConfig = new FacesConfigurator();
		facesConfig.addLanguageToFacesConfig("nl");
	}

	private void createLuceneIndex() throws Exception {
		try {
			Class.forName("com.logicaldoc.core.i18n.Language");
		} catch (ClassNotFoundException ex) {
			// The core plugin was not ready, so avoid initializations
			return;
		}

		log.info("Create Lucene Index");
		PropertiesBean pbean = new PropertiesBean(getClass().getClassLoader().getResource("context.properties"));
		String indexdir = pbean.getProperty("conf.indexdir");
		log.info("indexdir = '" + indexdir + "'");
		if (indexdir == null || indexdir.equals("")) {
			throw new Exception("System un-setted up, impossible to create Lucene Index");
		}

		try {
			Language ldLanguage = new Language(new Locale("nl"));
			File indexPath = new File(indexdir, ldLanguage.getIndex());

			// Prevent overwrite of an already present index
			if (indexPath.exists())
				return;

			Indexer.createIndex(indexPath, "nl");
		} catch (Exception e) {
			log.error("Unable to: createLuceneIndex(): " + e.getMessage(), e);
			throw e;
		}
	}

}
