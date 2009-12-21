package com.logicaldoc.core.searchengine.store;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.util.StringUtil;
import com.logicaldoc.util.config.SettingsConfig;
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

	private SettingsConfig settingsConfig;

	public FSStorer() {
	}

	public SettingsConfig getSettingsConfig() {
		return settingsConfig;
	}

	public void setSettingsConfig(SettingsConfig settingsConfig) {
		this.settingsConfig = settingsConfig;
	}

	@Override
	public void delete(long docId) {
		File docDir = getDirectory(docId);
		try {
			FileUtils.deleteDirectory(docDir);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	@Override
	public File getDirectory(long docId) {
		String path = StringUtil.split(Long.toString(docId), '/', 3);
		path = settingsConfig.getValue("docdir") + "/" + path + "/doc";
		return new File(path);
	}

	@Override
	public boolean store(InputStream stream, long docId, String filename) {
		try {
			File dir = getDirectory(docId);
			FileUtils.forceMkdir(dir);
			FileUtil.writeFile(stream, new StringBuilder(dir.getPath()).append("/").append(filename).toString());
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
		/*
		 * All versions of a document are stored in the same directory as the
		 * current version, but the filename is the version number without
		 * extension, e.g. "docId/2.1"
		 */
		String filename;
		if (StringUtils.isEmpty(fileVersion))
			filename = doc.getFileVersion();
		else
			filename = fileVersion;
		if (StringUtils.isEmpty(filename))
			filename = doc.getVersion();

		/*
		 * Document's related resources are stored with a suffix, e.g.
		 * "docId/2.1-thumb.jpg"
		 */
		if (StringUtils.isNotEmpty(suffix))
			filename += "-" + suffix;
		return getFile(doc.getId(), filename);
	}
}