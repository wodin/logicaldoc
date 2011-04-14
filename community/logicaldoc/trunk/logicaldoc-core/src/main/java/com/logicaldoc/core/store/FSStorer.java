package com.logicaldoc.core.store;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.SystemQuota;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.StringUtil;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.io.FileUtil;

/**
 * This class is an implementation of the Storer interface to persist documents
 * in the filesystem. From the root of the documents storage, this
 * implementation saves all document's files into a defined directory using the
 * following logic. The document's id is tokenized by three chars tokens, than
 * the doc/ dir is appended, so if the docId=12345, the document's path will
 * be:123/45/doc.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class FSStorer implements Storer {
	protected static Log log = LogFactory.getLog(FSStorer.class);

	protected ContextProperties config;

	public FSStorer() {
	}

	public ContextProperties getConfig() {
		return config;
	}

	public void setConfig(ContextProperties config) {
		this.config = config;
	}

	@Override
	public void delete(long docId) {
		File docDir = getContainer(docId);
		try {
			FileUtils.forceDelete(docDir);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	@Override
	public File getContainer(long docId) {
		String relativePath = computeRelativePath(docId);
		String path = config.getPropertyWithSubstitutions("store.1.dir") + "/" + relativePath;
		return new File(path);
	}

	@Override
	public boolean store(InputStream stream, long docId, String filename) {
		try {
			SystemQuota.checkOverQuota();

			File dir = getContainer(docId);
			FileUtils.forceMkdir(dir);
			File file = new File(new StringBuilder(dir.getPath()).append("/").append(filename).toString());
			FileUtil.writeFile(stream, file.getPath());

			// Performs increment and check of the system quota, then increments
			// the user quota count
			SystemQuota.increment(file.length());
			SystemQuota.incrementUserQuota(docId, file.length());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	@Override
	public String getResourceName(Document doc, String fileVersion, String suffix) {
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document document = doc;

		/*
		 * All versions of a document are stored in the same directory as the
		 * current version, but the filename is the version number without
		 * extension, e.g. "docId/2.1"
		 */
		String filename;
		if (doc.getDocRef() != null) {
			// The shortcut document doesn't have the 'fileversion' and the
			// 'version'
			document = docDao.findById(doc.getDocRef());
		}

		if (StringUtils.isEmpty(fileVersion))
			filename = document.getFileVersion();
		else
			filename = fileVersion;
		if (StringUtils.isEmpty(filename))
			filename = document.getVersion();

		/*
		 * Document's related resources are stored with a suffix, e.g.
		 * "doc/2.1-thumb.jpg"
		 */
		if (StringUtils.isNotEmpty(suffix))
			filename += "-" + suffix;

		return filename;
	}

	@Override
	public InputStream getStream(Document doc, String fileVersion, String suffix) {
		String filename = getResourceName(doc, fileVersion, suffix);

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document document = doc;

		/*
		 * All versions of a document are stored in the same directory as the
		 * current version, but the filename is the version number without
		 * extension, e.g. "docId/2.1"
		 */
		if (doc.getDocRef() != null) {
			// The shortcut document doesn't have the 'fileversion' and the
			// 'version'
			document = docDao.findById(doc.getDocRef());
		}

		File container = getContainer(document.getId());
		File file = new File(container, filename);

		try {
			return new BufferedInputStream(new FileInputStream(file), 2048);
		} catch (Throwable e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public InputStream getStream(long docId, String fileVersion, String suffix) {
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findById(docId);
		return getStream(doc, fileVersion, suffix);
	}

	@Override
	public File getFile(Document doc, String fileVersion, String suffix) {
		String filename = getResourceName(doc, fileVersion, suffix);

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document document = doc;

		/*
		 * All versions of a document are stored in the same directory as the
		 * current version, but the filename is the version number without
		 * extension, e.g. "docId/2.1"
		 */
		if (doc.getDocRef() != null) {
			// The shortcut document doesn't have the 'fileversion' and the
			// 'version'
			document = docDao.findById(doc.getDocRef());
		}

		return new File(getContainer(document.getId()), filename);
	}

	@Override
	public File getFile(long docId, String fileVersion, String suffix) {
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findById(docId);
		return getFile(doc, fileVersion, suffix);
	}

	@Override
	public long getTotalSize() {
		long size = 0;
		File docDir = new File(config.getPropertyWithSubstitutions("store.1.dir"));
		if (docDir.exists())
			size = FileUtils.sizeOfDirectory(docDir);
		return size;
	}

	/**
	 * Computes the relative path of a document's folder inside the storage
	 * root.
	 */
	protected String computeRelativePath(long docId) {
		return StringUtil.split(Long.toString(docId), '/', 3) + "/doc";
	}

	@Override
	public byte[] getBytes(Document doc, String fileVersion, String suffix) {
		InputStream is = null;
		try {
			is = getStream(doc, fileVersion, suffix);
			byte[] bytes = IOUtils.toByteArray(is);
			return bytes;
		} catch (IOException e) {
			log.error(e.getMessage());
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {

				}
		}
		return null;
	}

	@Override
	public void delete(long docId, String resourceName) {
		File file = new File(getContainer(docId), resourceName);
		if (file.exists())
			try {
				FileUtils.forceDelete(file);
			} catch (IOException e) {
				log.error(e.getMessage());
			}
	}

	@Override
	public List<String> listResources(long docId, final String fileVersion) {
		List<String> resources = new ArrayList<String>();
		File container = getContainer(docId);
		File[] buf = container.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.startsWith("."))
					return false;
				else if (StringUtils.isNotEmpty(fileVersion)) {
					return name.startsWith(fileVersion);
				}
				return true;
			}
		});
		for (File file : buf) {
			resources.add(file.getName());
		}
		return resources;
	}

	@Override
	public long getSize(long docId, String resourceName) {
		File file = getContainer(docId);
		file = new File(file, resourceName);
		return file.length();
	}
}