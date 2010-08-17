package com.logicaldoc.core.document.thumbnail;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java.plugin.registry.Extension;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.plugin.PluginRegistry;

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
		ThumbnailBuilder builder = getBuilders().get(document.getFileExtension());

		if (builder == null) {
			log.warn("No registered thumbnail for extension " + document.getFileExtension());
			try {
				MagicMatch match = Magic.getMagicMatch(documentManager.getDocumentFile(document), true);
				if ("text/plain".equals(match.getMimeType())) {
					log.warn("Try to convert as plain text");
					builder = getBuilders().get("txt");
				} else {
					return;
				}
			} catch (Exception e) {
				return;
			}
		}

		int size = 150;
		try {
			ContextProperties conf = new ContextProperties();
			size = Integer.parseInt(conf.getProperty("gui.thumbnail.size"));
		} catch (Throwable t) {
			log.error(t.getMessage());
		}

		float quality = 1;
		try {
			ContextProperties conf = new ContextProperties();
			int buf = Integer.parseInt(conf.getProperty("gui.thumbnail.quality"));
			if (buf < 1)
				buf = 1;
			if (buf > 100)
				buf = 100;
			quality = (float) buf / (float) 100;
		} catch (Throwable t) {
			log.error(t.getMessage());
		}

		int scaleAlgorithm = Image.SCALE_SMOOTH;
		try {
			ContextProperties conf = new ContextProperties();
			scaleAlgorithm = Integer.parseInt(conf.getProperty("gui.thumbnail.scale"));
		} catch (Throwable t) {
			log.error(t.getMessage());
		}

		try {
			File src = documentManager.getDocumentFile(document, fileVersion);
			File dest = new File(src.getParentFile(), src.getName() + "-thumb.jpg");
			builder.build(src, document.getFileName(), size, dest, scaleAlgorithm, quality);
		} catch (Exception e) {
			log.warn("Error creating thumbnail for document: " + document.getTitle());
		}
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
				log.error(e.getMessage(), e);
			}
		}
	}

	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}

	public Map<String, ThumbnailBuilder> getBuilders() {
		if (builders.isEmpty())
			initBuilders();
		return builders;
	}
}