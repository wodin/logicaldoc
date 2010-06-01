package com.logicaldoc.web;

import java.text.MessageFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import org.java.plugin.registry.Extension;

import com.logicaldoc.util.PluginRegistry;

/**
 * A class for retrieval of localized messages. All bundles declared in
 * ResourceBundle extension point. The first key match wins.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class Messages extends AbstractMap<String, String> {
	/**
	 * The list of bundles in which keys will be searched
	 */
	private static List<String> bundles = new ArrayList<String>();

	public Messages() {
	}

	public static String getMessage(String key, Locale locale) {
		if (bundles.isEmpty()) {
			// Acquire the 'ResourceBundle' extensions of the core plugin
			PluginRegistry registry = PluginRegistry.getInstance();
			Collection<Extension> exts = registry.getSortedExtensions("logicaldoc-core", "ResourceBundle", null);

			for (Extension ext : exts) {
				bundles.add(ext.getParameter("bundle").valueAsString());
			}
		}

		// Iterate over bundles in reverse order
		for (int i = bundles.size() - 1; i >= 0; i--) {
			String path = bundles.get(i);

			try {
				ResourceBundle bundle = ResourceBundle.getBundle(path, locale);
				return bundle.getString(key);
			} catch (Throwable e) {

			}
		}

		return key;
	}

	public static String getMessage(String key) {
		Locale locale = Locale.getDefault();

		return getMessage(key, locale);
	}

	public static String getMessage(String key, String val0) {
		String msg = getMessage(key);
		return MessageFormat.format(msg, new Object[] { val0 });
	}

	public static String getMessage(String key, Object[] values) {
		String msg = getMessage(key);
		return MessageFormat.format(msg, values);
	}

	public static String getMessage(String key, Locale locale, Object[] values) {
		String msg = getMessage(key, locale);
		return MessageFormat.format(msg, values);
	}

	@Override
	public Set<java.util.Map.Entry<String, String>> entrySet() {
		return null;
	}

	@Override
	public String get(Object key) {
		return Messages.getMessage(key.toString());
	}
}
