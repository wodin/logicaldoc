package com.logicaldoc.core.document.thumbnail;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.java.plugin.registry.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.store.Storer;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.plugin.PluginRegistry;

/**
 * Manager class used to handle document thumbnails
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class ThumbnailManager {
	protected static Logger log = LoggerFactory.getLogger(ThumbnailManager.class);

	private Storer storer;

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
		ThumbnailBuilder builder = getBuilders().get(document.getFileExtension().toLowerCase());

		if (builder == null) {
			log.warn("No registered thumbnail for extension " + document.getFileExtension().toLowerCase());
			try {
				String resource = storer.getResourceName(document, null, null);
				MagicMatch match = Magic.getMagicMatch(storer.getBytes(document.getId(), resource), true);
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

		int quality = 100;
		try {
			ContextProperties conf = new ContextProperties();
			int buf = Integer.parseInt(conf.getProperty("gui.thumbnail.quality"));
			if (buf < 1)
				buf = 1;
			if (buf > 100)
				buf = 100;
			quality = buf;
		} catch (Throwable t) {
			log.error(t.getMessage());
		}

		// Prepare I/O files
		File src = File.createTempFile("scr", "." + FilenameUtils.getExtension(document.getFileName()));
		File dest = File.createTempFile("dest", "thumb.jpg");

		try {
			String fver = fileVersion;
			if (fver == null)
				fver = document.getFileVersion();
			String resource = storer.getResourceName(document.getId(), fver, null);
			storer.writeTo(document.getId(), resource, src);

			// Perform the elaboration
			builder.build(src, document.getFileName(), size, dest, quality);

			// Put the resource
			resource = storer.getResourceName(document.getId(), fver, "thumb.jpg");
			storer.store(dest, document.getId(), resource);
		} catch (Exception e) {
			log.warn("Error creating thumbnail for document: " + document.getTitle(), e);
		} finally {
			// Delete temporary resources
			FileUtils.deleteQuietly(src);
			FileUtils.deleteQuietly(dest);
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

	public Map<String, ThumbnailBuilder> getBuilders() {
		if (builders.isEmpty())
			initBuilders();
		return builders;
	}

	public void setStorer(Storer storer) {
		this.storer = storer;
	}
}