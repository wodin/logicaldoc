package com.logicaldoc.core.document.thumbnail;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java.plugin.registry.Extension;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.util.PluginRegistry;
import com.logicaldoc.util.config.PropertiesBean;

/**
 * Manager class used to handle document thumbnails
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class ThumbnailManager {
	protected static Log log = LogFactory.getLog(ThumbnailManager.class);

	private DocumentManager documentManager;

	// Key is the extension, value is the associated builder
	private Map<String, ThumbnailBuilder> builders = new HashMap<String, ThumbnailBuilder>();

	/**
	 * Creates the thumbnail for the specified document and file version
	 * 
	 * @param document The document to be treated
	 * @param fileVersion The file version(optional)
	 * @throws IOException
	 */
	public void createTumbnail(Document document, String fileVersion) throws IOException {
		if (builders.isEmpty())
			initBuilders();
		System.out.println("extensions: "+builders.keySet());
		ThumbnailBuilder builder = builders.get(document.getFileExtension());
		
		if (builder == null)
			return;

		int size = 150;
		try {
			PropertiesBean conf = new PropertiesBean();
			size = Integer.parseInt(conf.getProperty("thumbnail.size"));
		} catch (Throwable t) {
			t.printStackTrace();
		}
		File src = documentManager.getDocumentFile(document, fileVersion);
		File dest = new File(src.getParentFile(), src.getName() + "-thumb.jpg");
		builder.build(src, document.getFileName(), size, dest);
	}

	/**
	 * Creates the thumbnail for the specified document
	 * 
	 * @param document The document to be treated
	 * @throws IOException
	 */
	public void createTumbnail(Document document) throws IOException {
		createTumbnail(document, null);
	}

	/**
	 * Initializes the builders map
	 */
	private void initBuilders() {
		builders.clear();
		// Acquire the 'ThumbnailBuilder' extensions of the core plugin
		PluginRegistry registry = PluginRegistry.getInstance();
		Collection<Extension> exts = registry.getExtensions("logicaldoc-core", "ThumbnailBuilder");

		for (Extension ext : exts) {
			String className = ext.getParameter("class").valueAsString();
			String extension = ext.getParameter("extension").valueAsString().toLowerCase();
			try {
				Class clazz = Class.forName(className);
				// Try to instantiate the builder
				Object builder = clazz.newInstance();
				if (!(builder instanceof ThumbnailBuilder))
					throw new Exception("The specified builder " + className
							+ " doesn't implement ThumbnailBuilder interface");
				builders.put(extension, (ThumbnailBuilder) builder);
				log.info("Added new thumbnail builder " + className + " for extension " + extension);
			} catch (Throwable e) {
				log.error(e.getMessage());
			}
		}
	}

	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}
}