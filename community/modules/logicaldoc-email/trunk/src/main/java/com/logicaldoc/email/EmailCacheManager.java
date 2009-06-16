package com.logicaldoc.email;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java.plugin.Plugin;

import com.logicaldoc.util.PluginRegistry;
import com.logicaldoc.util.plugin.LogicalDOCPlugin;

public class EmailCacheManager {
	protected static Log log = LogFactory.getLog(EmailCache.class);

	/**
	 * Retrieves the cache for the specified email
	 * 
	 * @param account
	 * @return
	 * @throws IOException
	 */
	public EmailCache getCache(EmailAccount account) throws IOException {
		File cacheFile = new File(getCacheDirectory(), "cache-" + account.getId() + ".txt");
		return new EmailCache(cacheFile);
	}

	public void deleteCache(EmailAccount account) throws IOException {
		File cacheFile = new File(getCacheDirectory(), "cache-" + account.getId() + ".txt");
		if (cacheFile.exists()) {
			FileUtils.forceDelete(cacheFile);
		}
	}

	/**
	 * Computes the directory in which all cache files must be maintained, that
	 * is the plugin dir
	 */
	private File getCacheDirectory() {
		File file = new File("");
		try {
			LogicalDOCPlugin emailPlugin = (LogicalDOCPlugin) PluginRegistry.getInstance().getManager().getPlugin(
					"logicaldoc-email");
			file = emailPlugin.resolveDataPath("cache");
			if (!file.exists())
				file.mkdir();
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
		}
		return file;
	}
}
