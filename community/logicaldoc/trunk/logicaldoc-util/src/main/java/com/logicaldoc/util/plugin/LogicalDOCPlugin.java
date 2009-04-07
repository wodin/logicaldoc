package com.logicaldoc.util.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java.plugin.Plugin;

import com.logicaldoc.util.config.PropertiesBean;

/**
 * This is the base class for LogicalDOC plugins.
 * 
 * The methods of this class are automatically invoked by the manager during
 * plug-in life cycle management (activation and deactivation).
 * 
 * In addition, this class makes available methods to manage the plug-in's
 * properties stored in file plugin.properties, and allows access to the plug-in
 * framework ({@see org.java.plugin.PluginManager manager},
 * {@see org.java.plugin.registry.PluginRegistry registry}) which it was
 * loaded.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public abstract class LogicalDOCPlugin extends Plugin {

	protected static Log log = LogFactory.getLog(LogicalDOCPlugin.class);

	private static final String PLUGIN_PROPERTIES = "plugin.properties";

	private Properties data = new Properties();

	/**
	 * This method will be called once during plug-in activation before any
	 * access to any code from this plug-in.
	 * 
	 * @throws Exception if an error has occurred during plug-in start-up
	 * @see org.java.plugin.Plugin#doStart()
	 */
	@Override
	protected void doStart() throws Exception {
		loadData();
		try {
			if (StringUtils.isEmpty(getProperty("install"))) {
				install();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
				data.setProperty("install", df.format(new Date()));
				log.info("Plugin " + getDescriptor().getId() + " installed");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
		start();
		saveData();
	}

	protected void loadData() throws IOException {
		data.load(new FileInputStream(resolveDataFile()));
	}

	protected void saveData() throws IOException {
		data.store(new FileOutputStream(resolveDataFile()), "");
	}

	/**
	 * Resolves a relative path inside the plugin shadow folder
	 * 
	 * @param relativePath The relative path
	 * @return The absolute path
	 */
	public String resolvePath(String relativePath) {
		String path = getManager().getPathResolver().resolvePath(getDescriptor(), relativePath).toString();
		if (path.startsWith("file:")) {
			path = path.substring(5);
			URLDecoder decoder = new URLDecoder();
			try {
				path = decoder.decode(path, "UTF-8");
			} catch (UnsupportedEncodingException e) {
			}
		}
		return path;
	}

	/**
	 * Resolves a relative path inside the plugin data folder
	 * 
	 * @param relativePath The relative path
	 * @return The file
	 */
	public File resolveDataPath(String relativePath) {
		return new File(getDataDirectory(), relativePath);
	}

	private File resolveDataFile() {
		File file = new File(getDataDirectory(), PLUGIN_PROPERTIES);
		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		return file;
	}

	/**
	 * Returns the data directory for this plugin, that is
	 * {conf.plugindir}/{pluginId}. It will be created in not existing.
	 */
	private File getDataDirectory() {
		PropertiesBean conf;
		try {
			conf = new PropertiesBean();
			File dir = new File(conf.getPropertyWithSubstitutions("conf.plugindir"));
			String pluginName = getDescriptor().getUniqueId().substring(0,
					getDescriptor().getUniqueId().lastIndexOf('@'));
			dir = new File(dir, pluginName);
			if (!dir.exists()) {
				dir.mkdirs();
				dir.mkdir();
			}
			return dir;
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return null;
	}

	/**
	 * Concrete implementations of this method must insert first installation
	 * logic such as database initialisation.
	 */
	protected void install() throws Exception {
	}

	/**
	 * Concrete implementations of this method must insert startup
	 * initialisations.
	 */
	protected void start() throws Exception {
	}

	/**
	 * This method will be called once during plug-in deactivation. After this
	 * method call, no other code from this plug-in can be accessed, unless
	 * {@link #doStart()} method will be called again (but for another instance
	 * of this class).
	 * 
	 * @throws Exception if an error has occurred during plug-in shutdown
	 * @see org.java.plugin.Plugin#doStop()
	 */
	@Override
	protected void doStop() throws Exception {
	}

	public Properties getData() {
		return data;
	}

	public String getProperty(String name) {
		return data.getProperty(name);
	}
}