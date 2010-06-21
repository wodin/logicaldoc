package com.logicaldoc.web;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.java.plugin.PluginClassLoader;
import org.java.plugin.registry.Identity;
import org.java.plugin.registry.PluginDescriptor;

/**
 * Particular implementation of the plugin registry for the Tomcat servlet
 * container
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class PluginRegistry extends com.logicaldoc.util.PluginRegistry {

	protected void initPlugins(Map<String, Identity> plugins) {
		System.out.println("Intialising plugins");

		Set<String> keys = plugins.keySet();
		Iterator<String> iterator = keys.iterator();

		while (iterator.hasNext()) {
			String manifest = iterator.next();
			System.out.println("manifestURL: " + manifest);

			PluginDescriptor pluginDescriptor = (PluginDescriptor) plugins.get(manifest);
			System.out.println("plugin located: " + pluginDescriptor.getId() + " @ " + pluginDescriptor.getLocation());

			// Now prepare the container class loader with all needed
			// repositories
			ClassLoader cl = this.getClass().getClassLoader();
			PluginClassLoader pcl = (PluginClassLoader) manager.getPluginClassLoader(pluginDescriptor);
			URL[] urls = pcl.getURLs();

			Method addRepository;

			try {
				addRepository = cl.getClass().getMethod("addRepository", new Class[] { String.class });

				for (int i = 0; i < urls.length; i++) {
					String url = prepareRepositoryUrl(urls[i].toString());
					addRepository.invoke(cl, new Object[] { url });
					System.out.println("Added repository " + url + " to " + cl.getClass().getName());
				}

				manager.activatePlugin(pluginDescriptor.getId());
				System.out.println("Activated plugin " + pluginDescriptor.getId());
			} catch (Throwable e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Convert the passed repository url to an equivalent version suitable for
	 * the container classloader
	 * 
	 * @param url The original url
	 * @return The converted url
	 */
	private String prepareRepositoryUrl(String url) {
		String preparedUrl = url;

		if (url.startsWith("jar:")) {
			preparedUrl = url.substring(4);

			if (preparedUrl.contains(".jar!")) {
				int index = preparedUrl.lastIndexOf("!");
				preparedUrl = preparedUrl.substring(0, index);
			}
		}

		return preparedUrl;
	}
}
