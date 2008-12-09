package com.logicaldoc.core.searchengine.store;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.util.CharsetDetector;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.SettingsConfig;
import com.logicaldoc.util.io.FileUtil;

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
	public boolean store(InputStream stream, String docPath, String filename, String version) {
		try {
			String fn = CharsetDetector.convert(filename);
			String path = new StringBuilder(settingsConfig.getValue("docdir")).append("/").append(docPath).append("/")
					.toString();
			FileUtils.forceMkdir(new File(path));
			FileUtil.writeFile(stream, new StringBuilder(path).append(fn).toString());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}

		return true;
	}

	/**
	 * @see com.logicaldoc.core.doxter.Storer#delete(java.lang.String,
	 *      java.lang.String)
	 */
	public void delete(String docPath) {
		SettingsConfig settings = (SettingsConfig) Context.getInstance().getBean(SettingsConfig.class);
		String path = settings.getValue("docdir") + "/";
		try {
			FileUtils.deleteDirectory(new File(new StringBuilder(path).append(docPath).toString()));
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
}