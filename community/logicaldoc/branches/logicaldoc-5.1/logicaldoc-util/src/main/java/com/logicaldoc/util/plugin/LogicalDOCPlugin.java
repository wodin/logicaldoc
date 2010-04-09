package com.logicaldoc.util.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
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
	 * access to any code from this plug-in. If the plug-in needs to be
	 * installed(@link #isInstallNeeded())the
	 * <code>install()</code> method is called
	 * 
	 * @throws Exception if an error has occurred during plug-in start-up
	 * @see org.java.plugin.Plugin#doStart()
	 */
	@Override
	protected void doStart() throws Exception {
		loadData();
		try {
			if (isInstallNeeded()) {
				install();
				getInstallMark().createNewFile();
				log.info("Plugin " + getDescriptor().getId() + " installed");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
		start();
		saveData();
	}

	/**
	 * Return the install mark file, that is the file called 'install' inside
	 * the plug-in's shadow folder
	 */
	private File getInstallMark() {
		return new File(resolvePath("install").toString());
	}

	/**
	 * Tells is the plug-in need to be installed. This default implementation
	 * check if the file 'install' exists o the plug-in's shadow folder.
	 * Concrete implementations are free to re-implement this logic.
	 * 
	 * @return true if the plug-in need to be reinstalled
	 */
	protected boolean isInstallNeeded() {
		return !getInstallMark().exists();
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
	public File getDataDirectory() {
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
	 * This method copies all i18n resources into WEB-INF/classes/i18n
	 */
	protected void copyI18n() {
		try {
			String classesDir = getManager().getPathResolver().resolvePath(getDescriptor(), "classes").toString();
			log.debug("classesDir: " + classesDir);
			if (classesDir.startsWith("file:")) {
				classesDir = classesDir.substring(5);
			}

			File src = new File(classesDir, "i18n");
			File destRoot = new File(System.getProperty("logicaldoc.app.rootdir"));
			File destClasses = new File(destRoot, "WEB-INF/classes/i18n");

			log.debug("Copy resources from " + src.getPath() + " to " + destClasses.getPath());
			FileUtils.copyDirectory(src, destClasses);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
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