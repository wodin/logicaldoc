package com.logicaldoc.core.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java.plugin.registry.Extension;

import com.logicaldoc.util.plugin.PluginRegistry;

/**
 * A manager for document listeners. It's internals are initialized from the extension
 * point 'DocumentListener' of the core plugin.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class DocumentListenerManager {
	protected static Log log = LogFactory.getLog(DocumentListenerManager.class);

	private List<DocumentListener> listeners = null;

	public void init() {
		listeners = new ArrayList<DocumentListener>();

		// Acquire the 'DocumentListener' extensions of the core plugin
		PluginRegistry registry = PluginRegistry.getInstance();
		Collection<Extension> exts = registry.getExtensions("logicaldoc-core", "DocumentListener");

		// Sort the extensions according to ascending position
		List<Extension> sortedExts = new ArrayList<Extension>();
		for (Extension extension : exts) {
			sortedExts.add(extension);
		}
		Collections.sort(sortedExts, new Comparator<Extension>() {
			public int compare(Extension e1, Extension e2) {
				int position1 = Integer.parseInt(e1.getParameter("position").valueAsString());
				int position2 = Integer.parseInt(e2.getParameter("position").valueAsString());
				if (position1 < position2)
					return -1;
				else if (position1 > position2)
					return 1;
				else
					return 0;
			}
		});

		for (Extension ext : sortedExts) {
			String className = ext.getParameter("class").valueAsString();
			try {
				Class clazz = Class.forName(className);
				// Try to instantiate the listener
				Object listener = clazz.newInstance();
				if (!(listener instanceof DocumentListener))
					throw new Exception("The specified listener " + className
							+ " doesn't implement DocumentListener interface");
				listeners.add((DocumentListener) listener);
				log.info("Added new document listener " + className + " position "
						+ ext.getParameter("position").valueAsString());
			} catch (Throwable e) {
				log.error(e.getMessage());
			}
		}
	}

	/**
	 * The ordered list of listeners
	 */
	public List<DocumentListener> getListeners() {
		if (listeners == null)
			init();
		return listeners;
	}
}
