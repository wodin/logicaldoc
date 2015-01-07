package com.logicaldoc.core.document.pdf;

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
import com.logicaldoc.util.io.FileUtil;
import com.logicaldoc.util.plugin.PluginRegistry;

/**
 * Manager class used to handle document pdf conversions
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.1.3
 */
public class PdfConverterManager {
	public static final String SUFFIX = "conversion.pdf";

	protected static Logger log = LoggerFactory.getLogger(PdfConverterManager.class);

	private Storer storer;

	private TenantDAO tenantDao;

	// Key is the extension, value is the associated converter
	private Map<String, PdfConverter> builders = new HashMap<String, PdfConverter>();

	/**
	 * Retrieves the content of the Pdf conversion. If the Pdf conversion is not
	 * available in the store, it is created.
	 * 
	 * @param document The document to be processed
	 * @param fileVersion The file version(optional)
	 * @return The content of the PDF as bytes
	 * @throws IOException
	 */
	public byte[] getPdfContents(Document document, String fileVersion) throws IOException {
		String resource = storer.getResourceName(document.getId(), getSuitableFileVersion(document, fileVersion),
				SUFFIX);
		if ("pdf".equals(FilenameUtils.getExtension(document.getFileName()).toLowerCase()))
			resource = storer.getResourceName(document.getId(), getSuitableFileVersion(document, fileVersion), null);
		if (!storer.exists(document.getId(), resource))
			createPdf(document, fileVersion);
		return storer.getBytes(document.getId(), resource);
	}

	/**
	 * Write the content of the Pdf conversion into a file. If the Pdf
	 * conversion is not available in the store, it is created.
	 * 
	 * @param document The document to be processed
	 * @param fileVersion The file version(optional)
	 * @return The content of the PDF as bytes
	 * @throws IOException
	 */
	public void writePdfToFile(Document document, String fileVersion, File out) throws IOException {
		String resource = storer.getResourceName(document.getId(), getSuitableFileVersion(document, fileVersion),
				SUFFIX);
		if ("pdf".equals(FilenameUtils.getExtension(document.getFileName()).toLowerCase()))
			resource = storer.getResourceName(document.getId(), getSuitableFileVersion(document, fileVersion), null);
		if (!storer.exists(document.getId(), resource))
			createPdf(document, fileVersion);
		storer.writeToFile(document.getId(), resource, out);
	}

	/**
	 * Creates the pdf for the specified document and file version. If the Pdf
	 * conversion already exists it, nothing happens.
	 * 
	 * @param document The document to be processed
	 * @param fileVersion The file version(optional)
	 * @throws IOException
	 */
	public void createPdf(Document document, String fileVersion) throws IOException {
		if ("pdf".equals(FilenameUtils.getExtension(document.getFileName()).toLowerCase())) {
			log.debug("Document " + document.getId() + " itself is a Pdf");
			return;
		}

		String resource = storer.getResourceName(document.getId(), getSuitableFileVersion(document, fileVersion),
				SUFFIX);

		if (storer.exists(document.getId(), resource)) {
			log.debug("Pdf conversion already available for document " + document.getId());
			return;
		}

		PdfConverter converter = getConverter(document.getFileName());
		if (converter == null) {
			log.warn("No pdf converter for document " + document.getId());
			return;
		}

		// Prepare I/O files
		File src = null;
		File dest = File.createTempFile("conversion", ".pdf");

		try {
			src = writeToFile(document, fileVersion);
			if (src == null || src.length() == 0)
				log.warn("Unexisting source file,  document: " + document.getId() + " - " + document.getTitle());
			else {
				converter.createPdf(getTenantName(document), src, document.getFileName(), dest);

				if (dest != null && dest.length() > 0)
					storer.store(dest, document.getId(), resource);
				else
					log.warn("The pdf converter was unable to convert document: " + document.getId() + " - "
							+ document.getTitle());
			}
		} catch (Throwable e) {
			log.warn("Error rendering pdf for document: " + document.getId() + " - " + document.getTitle(), e);
		} finally {
			// Delete temporary resources
			FileUtil.strongDelete(src);
			FileUtil.strongDelete(dest);
		}
	}

	/**
	 * Shortcut for createPdf(document, null)
	 */
	public void createPdf(Document document) throws IOException {
		createPdf(document, null);
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
	 * Loads the proper converter for the passed file name
	 */
	public PdfConverter getConverter(String fileName) {
		PdfConverter builder = getConverters().get(FilenameUtils.getExtension(fileName.toLowerCase()));
		if (builder == null)
			builder = getConverters().get("*");
		return builder;
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
	 * Initializes the builders map
	 */
	private void initBuilders() {
		builders.clear();
		// Acquire the 'ThumbnailBuilder' extensions of the core plugin
		PluginRegistry registry = PluginRegistry.getInstance();
		Collection<Extension> exts = registry.getExtensions("logicaldoc-core", "PdfConverter");

		for (Extension ext : exts) {
			String className = ext.getParameter("class").valueAsString();
			String extension = ext.getParameter("extension").valueAsString().toLowerCase();
			try {
				Class clazz = Class.forName(className);
				// Try to instantiate the builder
				Object builder = clazz.newInstance();
				if (!(builder instanceof PdfConverter))
					throw new Exception("The specified pdf converter " + className
							+ " doesn't implement PdfConverter interface");
				builders.put(extension, (PdfConverter) builder);
				log.info("Added new pdf converter " + className + " for extension " + extension);
			} catch (Throwable e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	public Map<String, PdfConverter> getConverters() {
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