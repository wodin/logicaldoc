package com.logicaldoc.core.dbinit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java.plugin.registry.Extension;

import com.logicaldoc.util.PluginRegistry;
import com.logicaldoc.util.dbinit.DBInit;

/**
 * Database initialization manager
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class PluginDbInit extends DBInit {

	protected static Log log = LogFactory.getLog(PluginDbInit.class);

	/**
	 * Intitialises the database using sql92.sql from the core plugin and
	 * appending all other extensions connected to the 'DbInit' extension point.
	 */
	public void init() {
		log.info("Start database initialization");
		try {
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

			// Acquire the ordered list of sql files
			getSqlList().clear();
			getSqlList().add("sql/logicaldoc-core.sql");
			for (Extension ext : sortedExts) {
				getSqlList().add(ext.getParameter("sqlFile").valueAsString());
			}

			execute();
		} catch (Throwable e) {
			log.error(e);
			throw new RuntimeException(e.getMessage());
		}
	}
}