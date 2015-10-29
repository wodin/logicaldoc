package com.logicaldoc.core.document.thumbnail;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.java.plugin.registry.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.security.Tenant;
import com.logicaldoc.core.security.dao.TenantDAO;
import com.logicaldoc.core.store.Storer;
import com.logicaldoc.util.MimeType;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.io.FileUtil;
import com.logicaldoc.util.plugin.PluginRegistry;

/**
 * Manager class used to handle document thumbnails
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class ThumbnailManager {
	public static final String SUFFIX_PREVIEW = "preview.swf";

	public static final String SUFFIX_TILE = "tile.jpg";

	public static final String SUFFIX_THUMB = "thumb.jpg";

	protected static Logger log = LoggerFactory.getLogger(ThumbnailManager.class);

	private Storer storer;

	private TenantDAO tenantDao;

	// Key is the extension, value is the associated builder
	private Map<String, ThumbnailBuilder> builders = new HashMap<String, ThumbnailBuilder>();

	/**
	 * Creates the thumbnail for the specified document and file version. The
	 * thumbnail is an image rendering of the first page only.
	 * 
	 * @param document The document to be treated
	 * @param fileVersion The file version(optional)
	 * @throws IOException
	 */
	public void createTumbnail(Document document, String fileVersion) throws IOException {
		createImage(document, fileVersion, "thumbnail", SUFFIX_THUMB);
	}

	/**
	 * Creates the tile for the specified document and file version. The
	 * thumbnail is an image rendering of the first page only.
	 * 
	 * @param document The document to be treated
	 * @param fileVersion The file version(optional)
	 * @throws IOException
	 */
	public void createTile(Document document, String fileVersion) throws IOException {
		createImage(document, fileVersion, "tile", SUFFIX_TILE);
	}

	protected void createImage(Document document, String fileVersion, String type, String suffix) throws IOException {
		ThumbnailBuilder builder = getBuilder(document);
		if (builder == null) {
			log.warn("No builder found for document " + document.getId());
			return;
		}

		String tenantName = getTenantName(document);

		int size = 150;
		try {
			ContextProperties conf = new ContextProperties();
			size = Integer.parseInt(conf.getProperty(tenantName + ".gui." + type + ".size"));
		} catch (Throwable t) {
			log.error(t.getMessage());
		}

		int quality = 100;
		try {
			ContextProperties conf = new ContextProperties();
			int buf = Integer.parseInt(conf.getProperty(tenantName + ".gui." + type + ".quality"));
			if (buf < 1)
				buf = 1;
			if (buf > 100)
				buf = 100;
			quality = buf;
		} catch (Throwable t) {
			log.error(t.getMessage());
		}

		// Prepare I/O files
		File src = null;
		File dest = File.createTempFile("dest", suffix);

		try {
			src = writeToFile(document, fileVersion);

			builder.buildThumbnail(tenantName, src, document.getFileName(), dest, size, quality);

			// Put the resource
			String resource = storer.getResourceName(document.getId(), getSuitableFileVersion(document, fileVersion),
					suffix);
			storer.store(dest, document.getId(), resource);
		} catch (Throwable e) {
			log.warn("Error rendering image for document: " + document.getId() + " - " + document.getTitle(), e);
		} finally {
			// Delete temporary resources
			FileUtil.strongDelete(src);
			FileUtil.strongDelete(dest);
		}
	}

	protected String getTenantName(Document document) {
		String tenantName = "default";
		try {
			Tenant tenant = tenantDao.findById(document.getTenantId());
			tenantName = tenant.getName();
		} catch (Throwable t) {
			log.error(t.getMessage());
		}
		return tenantName;
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
	 * Loads the proper builder for the passed document
	 */
	private ThumbnailBuilder getBuilder(Document document) {
		ThumbnailBuilder builder = getBuilders().get(document.getFileExtension().toLowerCase());

		if (builder == null) {
			log.warn("No registered thumbnail builder for extension " + document.getFileExtension().toLowerCase());
			try {
				String mime = MimeType.getByFilename(document.getFileName());
				if ("text/plain".equals(mime)) {
					log.warn("Try to convert as plain text");
					builder = getBuilders().get("txt");
				}
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}

		return builder;
	}

	/**
	 * Creates the preview for all the pages of the document.
	 * 
	 * @param document The document to be treated
	 * @param fileVersion The file version(optional)
	 * @throws IOException
	 */
	public void createPreview(Document document, String fileVersion) throws IOException {
		ThumbnailBuilder builder = getBuilder(document);
		if (builder == null) {
			log.warn("No builder found for document " + document.getId());
			return;
		}

		String tenantName = getTenantName(document);

		/*
		 * We need to produce the SWF conversion
		 */

		// Prepare I/O resources
		File src = null;
		File pagesRoot = null;
		File previewFile = null;

		try {
			pagesRoot = File.createTempFile("preview", "");
			pagesRoot.delete();
			pagesRoot.mkdir();

			src = writeToFile(document, fileVersion);

			previewFile = builder.buildPreview(tenantName, src, document.getFileName(), pagesRoot);

			String fileVer = getSuitableFileVersion(document, fileVersion);
			String resource = storer.getResourceName(document.getId(), fileVer, SUFFIX_PREVIEW);
			storer.store(previewFile, document.getId(), resource);
			log.debug("Stored preview for " + document.getFileName());
		} catch (Throwable e) {
			log.warn("Error creating preview for document: " + document.getId() + " " + document.getTitle(), e);
		} finally {
			if (pagesRoot != null)
				FileUtil.strongDelete(pagesRoot);
			if (src != null)
				FileUtil.strongDelete(src);
		}
	}

	/**
	 * Write a document into a temporary file.
	 * 
	 * @throws IOException
	 */
	private File writeToFile(Document document, String fileVersion) throws IOException {
		File target = File.createTempFile("scr", "." + FilenameUtils.getExtension(document.getFileName()));
		String fver = getSuitableFileVersion(document, fileVersion);
		String resource = storer.getResourceName(document.getId(), fver, null);
		storer.writeToFile(document.getId(), resource, target);
		return target;
	}

	/**
	 * Returns the fileVersion in case this is not null or
	 * document.getFileVersion() otherwise
	 */
	private String getSuitableFileVersion(Document document, String fileVersion) {
		String fver = fileVersion;
		if (fver == null)
			fver = document.getFileVersion();
		return fver;
	}

	/**
	 * Creates the preview for all the pages of the document
	 * 
	 * @param document The document to be treated
	 * @throws IOException
	 */
	public void createPreview(Document document) throws IOException {
		createPreview(document, null);
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

	public void setTenantDao(TenantDAO tenantDao) {
		this.tenantDao = tenantDao;
	}
}