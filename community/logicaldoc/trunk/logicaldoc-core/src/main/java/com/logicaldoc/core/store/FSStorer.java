package com.logicaldoc.core.store;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
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

	private ContextProperties config;

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
		File docDir = getDirectory(docId);
		try {
			FileUtils.forceDelete(docDir);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	@Override
	public File getDirectory(long docId) {
		String path = StringUtil.split(Long.toString(docId), '/', 3);
		path = config.getPropertyWithSubstitutions("conf.docdir") + "/" + path + "/doc";
		return new File(path);
	}

	@Override
	public boolean store(InputStream stream, long docId, String filename) {
		try {
			File dir = getDirectory(docId);
			FileUtils.forceMkdir(dir);
			File file = new File(new StringBuilder(dir.getPath()).append("/").append(filename).toString());
			FileUtil.writeFile(stream, file.getPath());
			SystemQuota.increment(file.length());
			SystemQuota.checkOverQuota();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	@Override
	public File getFile(long docId, String filename) {
		File docDir = getDirectory(docId);
		return new File(docDir, filename);
	}

	@Override
	public File getFile(Document doc, String fileVersion, String suffix) {
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
		 * "docId/2.1-thumb.jpg"
		 */
		if (StringUtils.isNotEmpty(suffix))
			filename += "-" + suffix;

		return getFile(document.getId(), filename);
	}

	@Override
	public File getFile(long docId, String fileVersion, String suffix) {
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findById(docId);
		return getFile(doc, fileVersion, suffix);
	}

	@Override
	public void clean(long docId) {
		File docDir = getDirectory(docId);
		File[] listFiles = docDir.listFiles();
		List<String> deletedVersions = new ArrayList<String>();
		if (listFiles != null) {
			for (int i = 0; i < listFiles.length; i++) {
				if (listFiles[i].isFile() && listFiles[i].getName().endsWith(".deleted")) {
					String version = listFiles[i].getName().substring(0, listFiles[i].getName().indexOf(".deleted"));
					deletedVersions.add(version);
					SystemQuota.decrement(listFiles[i].length());
					listFiles[i].delete();
				}
			}
			for (String deletedVersion : deletedVersions) {
				for (int i = 0; i < listFiles.length; i++) {
					if (listFiles[i].isFile() && listFiles[i].getName().startsWith(deletedVersion + "-")) {
						SystemQuota.decrement(listFiles[i].length());
						listFiles[i].delete();
					}
				}
			}
		}
	}

	@Override
	public long getTotalSize() {
		long size = 0;
		File docDir = new File(config.getPropertyWithSubstitutions("conf.docdir"));
		if (docDir.exists())
			size = FileUtils.sizeOfDirectory(docDir);

		return size;
	}
}