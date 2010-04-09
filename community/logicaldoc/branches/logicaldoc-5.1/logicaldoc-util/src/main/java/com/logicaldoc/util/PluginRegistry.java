package com.logicaldoc.util;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.java.plugin.JpfException;
import org.java.plugin.ObjectFactory;
import org.java.plugin.PluginManager;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.ExtensionPoint;
import org.java.plugin.registry.Identity;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.registry.Extension.Parameter;
import org.java.plugin.util.ExtendedProperties;

/**
 * Central point where plugins are loaded and handled. The class is abstract and
 * must be personalized as needed. The used implementation can be specified with
 * the 'logicaldoc.app.pluginregistry' system property.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public abstract class PluginRegistry {

	// System property containing the plugin registry implementation to be used
	public static final String LOGICALDOC_PLUGINSREGISTRY = "logicaldoc.pluginsregistry";

	protected PluginManager manager = null;

	private static PluginRegistry instance;

	public static PluginRegistry getInstance() {
		if (instance == null) {
			String pluginregistry = System.getProperty(LOGICALDOC_PLUGINSREGISTRY);
			if (StringUtils.isEmpty(pluginregistry)) {
				pluginregistry = "com.logicaldoc.util.DefaultPluginRegistry";
			}
			try {
				System.out.println("Instantiate concrete PluginRegistry: " + pluginregistry);
				instance = (PluginRegistry) Class.forName(pluginregistry).newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return instance;
	}

	/**
	 * Initializes all found plugins
	 */
	public void init() {
		ExtendedProperties properties = new ExtendedProperties();
		properties.put("org.java.plugin.PathResolver", "com.logicaldoc.util.ShadingPathResolver");
		properties.put("com.logicaldoc.util.ShadingPathResolver.shadowFolder", System
				.getProperty("logicaldoc.app.pluginsdir")
				+ "/../.plugins");
		properties.put("com.logicaldoc.util.ShadingPathResolver.unpackMode", "always");

		ObjectFactory pluginObjectFactory = ObjectFactory.newInstance(properties);
		manager = pluginObjectFactory.createManager();

		List<PluginManager.PluginLocation> pluginLocations = locatePlugins(System
				.getProperty("logicaldoc.app.pluginsdir"));

		if (pluginLocations.size() > 0) {
			Map<String, Identity> plugins = null;
			try {
				PluginManager.PluginLocation[] pLocations = (PluginManager.PluginLocation[]) pluginLocations
						.toArray(new PluginManager.PluginLocation[0]);
				plugins = manager.publishPlugins(pLocations);
			} catch (JpfException e) {
				throw new RuntimeException("Error publishing plugins", e);
			}

			System.out.println("Succesfully registered " + plugins.size() + " plugins");
			initPlugins(plugins);
		}
	}

	/**
	 * Initializes found plugins
	 * 
	 * @param plugins Map of found plugins
	 */
	protected abstract void initPlugins(Map<String, Identity> plugins);

	protected List<PluginManager.PluginLocation> locatePlugins(String pluginsDirectoryPath) {

		List<PluginManager.PluginLocation> pluginLocations = new LinkedList<PluginManager.PluginLocation>();

		// look for all zip files in plugin directory
		File pluginDirectory = new File(pluginsDirectoryPath);

		System.out.println("Searching for plugins in " + pluginDirectory.getAbsolutePath());

		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return (name.endsWith(".zip"));
			}
		};

		if (pluginDirectory.isDirectory()) {

			// find the plugins
			List<String> pluginsList = Arrays.asList(pluginDirectory.list(filter));

			if (pluginsList.size() > 0) {

				Iterator<String> i = pluginsList.iterator();

				while (i.hasNext()) {

					String pluginFilename = (String) i.next();

					File pluginZIPFile = new File(pluginDirectory.getPath() + "/" + pluginFilename);

					if (!pluginZIPFile.exists())
						throw new RuntimeException("file not Found:" + pluginZIPFile.getAbsolutePath());

					try {

						final URL manifestURL = new URL("jar:file:" + pluginZIPFile.getAbsolutePath() + "!/plugin.xml");

						final URL contextURL = pluginZIPFile.toURL();

						System.out.println("Found plugin file: " + pluginZIPFile.getName());

						pluginLocations.add(new PluginManager.PluginLocation() {
							public URL getManifestLocation() {
								return manifestURL;
							}

							public URL getContextLocation() {
								return contextURL;
							}
						});
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
			} else {
				System.out.println("No Plugins Found");
			}

		} else {
			throw new RuntimeException("Unable to access Plugins directory: " + pluginDirectory.getAbsolutePath());
		}
		return pluginLocations;
	}

	public PluginManager getManager() {
		return manager;
	}

	/**
	 * Returns the extensions connected to the specified extension point
	 * 
	 * @param pluginId The plugin identifier
	 * @param extensionPoint The extension point id
	 * @return List of connected extensions
	 */
	public Collection<Extension> getExtensions(String pluginId, String extensionPoint) {
		Collection<Extension> exts = new ArrayList<Extension>();
		try {
			PluginRegistry registry = PluginRegistry.getInstance();
			PluginDescriptor descriptor = registry.getManager().getRegistry().getPluginDescriptor(pluginId);
			ExtensionPoint dbinitExtPoint = registry.getManager().getRegistry().getExtensionPoint(descriptor.getId(),
					extensionPoint);
			exts = dbinitExtPoint.getConnectedExtensions();
		} catch (Exception e) {

		}
		return exts;
	}
	
	/**
	 * Returns the extensions connected to the specified extension point
	 * 
	 * @param pluginId The plugin identifier
	 * @param extensionPoint The extension point id
	 * @param sortingParameter Extensions will be sorted by this parameter (if null 'position' parameter is used)
	 * @return List of connected extensions
	 */
	public List<Extension> getSortedExtensions(String pluginId, String extensionPoint, final String sortingParameter) {
		Collection<Extension> exts = getExtensions(pluginId,extensionPoint);
		
		// Sort the extensions according to ascending position
		List<Extension> sortedExts = new ArrayList<Extension>();
		for (Extension extension : exts) {
			sortedExts.add(extension);
		}
		
		Collections.sort(sortedExts, new Comparator<Extension>() {
			public int compare(Extension e1, Extension e2) {
				String sortParam="position";
				if(StringUtils.isNotEmpty(sortingParameter))
					sortParam=sortingParameter;
				int position1 = Integer.parseInt(e1.getParameter(sortParam).valueAsString());
				int position2 = Integer.parseInt(e2.getParameter(sortParam).valueAsString());
				if (position1 < position2)
					return -1;
				else if (position1 > position2)
					return 1;
				else
					return 0;
			}
		});
		return sortedExts;
	}
	

	/**
	 * Retrieves the list of registered plugins
	 * 
	 * @return The list of registered plugins descriptors
	 */
	public Collection<PluginDescriptor> getPlugins() {
		PluginRegistry registry = PluginRegistry.getInstance();
		return registry.getManager().getRegistry().getPluginDescriptors();
	}

	/**
	 * Retrieve the plugin descriptor
	 * 
	 * @return The plugin descriptor
	 */
	public PluginDescriptor getPlugin(String pluginId) {
		PluginRegistry registry = PluginRegistry.getInstance();
		return registry.getManager().getRegistry().getPluginDescriptor(pluginId);
	}
	
	/**
	 * Iterates over all Plugins and checks for database mappings 
	 * that must be included into the Configuration
	 * @return
	 */
	public String[] getMappings(){
		PluginRegistry registry = PluginRegistry.getInstance();
		Collection<Extension> exts = registry.getExtensions("logicaldoc-core", "DatabaseMapping");
		List<String> mappings = new ArrayList<String>();
		
		for (Extension extension : exts) {
			Collection<Parameter> parameters = extension.getParameters("mapping");
			for (Parameter parameter : parameters) {
				mappings.add(parameter.valueAsString());
			}
		}
		
		return mappings.toArray(new String[]{});
	}
}