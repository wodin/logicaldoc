package com.logicaldoc.plugin.language.pt;

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

/**
 * Web module plugin class. 
 * 
 * @author Alessandro Gasparini
 * @version $Id$
 * @since ###release###
 */
public class PortuguesePlugin extends LogicalDOCPlugin {

	protected static Log log = LogFactory.getLog(PortuguesePlugin.class);

	@Override
	protected void install() throws Exception {
		String webappDir = getManager().getPathResolver().resolvePath(getDescriptor(), "webapp").toString();
		if (webappDir.startsWith("file:")) {
			webappDir = webappDir.substring(5);
		}

		File src = new File(webappDir);
		File dest = new File(System.getProperty("logicaldoc.app.rootdir"));

		log.info("Copy web resources from " + src.getPath() + " to " + dest.getPath());
		FileUtils.copyDirectory(src, dest);

		// Create Lucene Index
		createLuceneIndex();
		
		// Now add the message bundle
		log.info("Add PT language to faces-config.xml");
		FacesConfigurator facesConfig = new FacesConfigurator();
		facesConfig.addLanguageToFacesConfig("pt");

		// TODO: Verificare che nn ci siano eccezzioni se nn trova la classe Snwball
	}

	private void createLuceneIndex() throws Exception {
		
		log.info("Create Lucene Index");
		PropertiesBean pbean = new PropertiesBean(getClass().getClassLoader().getResource("context.properties"));
		String indexdir = pbean.getProperty("conf.indexdir");
		log.info("indexdir = '" + indexdir + "'");
		if (indexdir == null || indexdir.equals(""))
			throw new Exception("System un-setted up, impossible to create Lucene Index");
		
		try {
			Language ptLanguage = new Language(new Locale("pt"));
			File indexPath = new File(indexdir, ptLanguage.getIndex());
			
			// Prevent overwrite of an already present index
			if (indexPath.exists())
				return;
			
			Indexer.createIndex(indexPath, "pt");
		} catch (Exception e) {
			log.error("Unable to: createLuceneIndex(): " + e.getMessage(), e);
			throw e;
		}
	}
	
}
