package com.logicaldoc.core.dbinit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.java.plugin.registry.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.util.dbinit.DBInit;
import com.logicaldoc.util.plugin.PluginRegistry;

/**
 * Database initialization manager
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class PluginDbInit extends DBInit {

	protected static Logger log = LoggerFactory.getLogger(PluginDbInit.class);

	/**
	 * Intitialises the database using 'DbInit' extension point.
	 */
	public void init() {
		init(null);
	}

	public void init(String[] ids) {
		log.info("Start database initialization");
		log.info("Database engine is " + getDbms());

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

			getSqlList().clear();

			List<String> idSet = new ArrayList<String>();
			if (ids != null)
				idSet = (List<String>) Arrays.asList(ids);

			// Acquire the ordered list of sql files
			for (Extension ext : sortedExts) {
				String id = ext.getDeclaringPluginDescriptor().getId();
				if (!idSet.isEmpty() && !idSet.contains(id))
					continue;

				String sqlFile = ext.getParameter("sqlFile").valueAsString();
				if (exists(sqlFile + "." + getDbms().toLowerCase()))
					getSqlList().add(sqlFile + "." + getDbms().toLowerCase());
				else
					getSqlList().add(sqlFile);
			}
			execute();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * Checks resource existence into the classpath
	 */
	private boolean exists(String name) {
		ClassLoader cl = this.getClass().getClassLoader();
		try {
			return cl.getResourceAsStream(name) != null;
		} catch (Throwable t) {
			return false;
		}
	}
}