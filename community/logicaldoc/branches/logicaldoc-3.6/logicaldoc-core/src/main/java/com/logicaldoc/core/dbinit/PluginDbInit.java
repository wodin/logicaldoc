package com.logicaldoc.core.dbinit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.logicaldoc.util.PluginRegistry;
import com.logicaldoc.util.dbinit.DBInit;
import org.java.plugin.registry.Extension;

/**
 * Database initialization manager
 * 
 * @author Marco Meschieri
 * @version $Id:$
 * @since 3.0
 */
public class PluginDbInit extends DBInit {

	/**
	 * Intitialises the database using sql92.sql from the core plugin and
	 * appending all other extensions connected to the 'DbInit' extension point.
	 */
	public void init() {
		// Acquire the 'DbInit' extensions of the core plugin
		PluginRegistry registry = PluginRegistry.getInstance();
		Collection<Extension> exts = registry.getExtensions("logicaldoc-core", "DbInit");
		
		// Sort the extensions according to ascending position
		List<Extension> sortedExts = new ArrayList<Extension>();
		for (Extension extension : exts) {
			sortedExts.add(extension);
		}
		Collections.sort(sortedExts, new Comparator<Extension>() {
			public int compare(Extension e1, Extension e2) {
				int position1 = e1.getParameter("position").valueAsNumber().intValue();
				int position2 = e2.getParameter("position").valueAsNumber().intValue();
				if (position1 < position2)
					return -1;
				else if (position1 > position2)
					return 1;
				else
					return 0;
			}
		});
		
		// Acquire the ordered list of sql files
		getSqlList().clear();
		getSqlList().add("com/logicaldoc/core/logicaldoc-core.sql");
		for (Extension ext : sortedExts) {
			getSqlList().add(ext.getParameter("sqlFile").valueAsString());
		}
		execute();
	}
}