package com.logicaldoc.plugin.language.nl;

import java.io.File;
import java.util.Locale;

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
		// Create Lucene Index
		createLuceneIndex();

		// Now add the message bundle
		log.info("Add NL language to faces-config.xml");
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
		PropertiesBean pbean = new PropertiesBean(getClass().getClassLoader()
				.getResource("context.properties"));
		String indexdir = pbean.getProperty("conf.indexdir");
		log.info("indexdir = '" + indexdir + "'");
		if (indexdir == null || indexdir.equals(""))
			throw new Exception(
					"System un-setted up, impossible to create Lucene Index");

		try {
			Language ptLanguage = new Language(new Locale("nl"));
			File indexPath = new File(indexdir, ptLanguage.getIndex());

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
