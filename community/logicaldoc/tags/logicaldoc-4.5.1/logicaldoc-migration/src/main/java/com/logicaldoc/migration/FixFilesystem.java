package com.logicaldoc.migration;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.util.StringUtil;

/**
 * Fixes the filesystem from 4.0 layout to 4.5 layout
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class FixFilesystem {

	public static void main(String[] args) {
		FixFilesystem instance = new FixFilesystem();
		instance.setDocsFolder(new File(args[0]));
		instance.setTempFolder(new File(args[1]));
		instance.start();
	}

	protected static Log log = LogFactory.getLog(FixFilesystem.class);

	// Original docs folder with the old layout
	private File docsFolder;

	// Temporary docs folder with the new layout
	private File tempFolder;

	private long errors = 0;

	private long converted = 0;

	// Map of documents in the old filesystem, key is the docId, value is the
	// document's folder
	private TreeMap<Long, File> cache = new TreeMap<Long, File>();

	public FixFilesystem() {
	}

	private void cacheDocuments(File dir) {
		File files[] = dir.listFiles();
		if (files == null)
			return;
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory() && files[i].getName().startsWith("doc_")) {
				cache.put(new Long(files[i].getName().substring("doc_".length())), files[i]);
			} else if (files[i].isDirectory()) {
				cacheDocuments(files[i]);
			}
		}
	}

	public File getDirectory(long docId) {
		String path = StringUtil.split(Long.toString(docId), '/', 3);
		path = tempFolder + "/" + path + "/doc";
		return new File(path);
	}

	private int getCompletion() {
		long count = cache.size();
		long step = errors + converted;
		return Math.round((float) step / (float) count * 100);
	}

	/**
	 * Generate the database population looking for folders and files in the
	 * rootFolder directory
	 */
	public void start() {
		log.info("Start File System conversion");
		errors = 0;
		converted = 0;
		cache.clear();
		cacheDocuments(docsFolder);
		log.info("Collected " + cache.size() + " documents to be converted");

		tempFolder.mkdirs();
		tempFolder.mkdir();

		int oldCompletion = getCompletion();

		// Now move each documen's folder to the proper location
		for (Long id : cache.keySet()) {
			try {
				File targetDir = getDirectory(id.longValue());
				FileUtils.moveDirectory(cache.get(id), targetDir);
				converted++;
			} catch (Throwable e) {
				log.error(e.getMessage(), e);
				errors++;
			}
			int completion = getCompletion();
			if (completion != oldCompletion) {
				oldCompletion = completion;
				log.info("Process completed at " + completion + "%");
			}
		}

		if (errors == 0) {
			// Drop the old repository and replace it with the new one
			try {
				FileUtils.forceDelete(docsFolder);
				tempFolder.renameTo(docsFolder);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}

		log.info("End of File System conversion");
		log.info("Converted " + converted + " documents");
		log.info("Found " + errors + " errors");
	}

	public File getDocsFolder() {
		return docsFolder;
	}

	public void setDocsFolder(File docsFolder) {
		this.docsFolder = docsFolder;
	}

	public long getErrors() {
		return errors;
	}

	public long getConverted() {
		return converted;
	}

	public File getTempFolder() {
		return tempFolder;
	}

	public void setTempFolder(File tempFolder) {
		this.tempFolder = tempFolder;
	}
}