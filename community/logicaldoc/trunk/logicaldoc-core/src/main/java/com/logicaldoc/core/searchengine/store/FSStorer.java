package com.logicaldoc.core.searchengine.store;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.FileBean;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.BackupConfig;
import com.logicaldoc.util.config.SettingsConfig;

/**
 * This class is an implementation of the Storer interface to persist documents
 * in the filesystem.
 * 
 * @author Michael Scholz
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

	/**
	 * @see com.logicaldoc.core.searchengine.store.Storer#store(java.io.InputStream,
	 *      java.lang.String, java.lang.String)
	 */
	public boolean store(InputStream stream, String docPath, String filename,
			String version) {
		try {
			String path = new StringBuilder(settingsConfig.getValue("docdir"))
					.append("/").append(docPath).append("/").toString();
			FileBean.createDir(path);
			FileBean.writeFile(stream, new StringBuilder(path).append(filename)
					.toString());

			// File f = new File(path + filename);
			BackupConfig conf = (BackupConfig) Context.getInstance().getBean(
					BackupConfig.class);

			if (conf.isEnabled()) {
				String backupPath = conf.getLocation();

				// store a backup of the document
				FileBean.copyDir(path, new StringBuilder(backupPath).append(
						docPath).toString());
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}

		return true;
	}

	/*
	 * 
	 * @see com.logicaldoc.core.doxter.Storer#delete(java.lang.String,
	 *      java.lang.String)
	 */
	public void delete(String docPath) {
		SettingsConfig settings = (SettingsConfig) Context.getInstance()
				.getBean(SettingsConfig.class);
		String path = settings.getValue("docdir") + "/";
		BackupConfig backup = (BackupConfig) Context.getInstance().getBean(
				BackupConfig.class);
		String backupPath = backup.getLocation();
		try {
			System.out.println("*** Deleting file " +new File(new StringBuilder(path).append(
					docPath).toString()).getPath());
			
			FileUtils.deleteDirectory(new File(new StringBuilder(path).append(
					docPath).toString()));
			if (backup.isEnabled()) {
				FileUtils.deleteDirectory(new File(
						new StringBuilder(backupPath).append(docPath)
								.toString()));
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	public boolean restoreAll() {
		SettingsConfig settings = (SettingsConfig) Context.getInstance()
				.getBean(SettingsConfig.class);
		String path = settings.getValue("docdir") + "/";
		BackupConfig backup = (BackupConfig) Context.getInstance().getBean(
				BackupConfig.class);
		String backupPath = backup.getLocation();
		boolean varBack = false;
		boolean deleted = FileBean.deleteDir(path);

		if (deleted) {
			boolean copied = FileBean.copyDir(backupPath, path);

			if (copied) {
				varBack = true;
			} else {
				varBack = false;
			}
		} else {
			varBack = false;
		}

		return varBack;
	}
}