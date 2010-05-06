package com.logicaldoc.core.searchengine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java.plugin.registry.Extension;

import com.logicaldoc.util.PluginRegistry;

/**
 * This class executes a search against the full-text indexes
 * 
 * @author Michael Scholz
 */
public abstract class Search {
	protected static Log log = LogFactory.getLog(Search.class);

	protected boolean moreHitsPresent = false;

	protected SearchOptions options;

	protected List<Hit> hits = new ArrayList<Hit>();

	protected int estimatedHitsNumber = 0;

	protected long execTime = 0;

	public static Search get(SearchOptions opt) {
		// Acquire the 'Search' extensions of the core plugin
		PluginRegistry registry = PluginRegistry.getInstance();
		Collection<Extension> extensions = new ArrayList<Extension>();
		try {
			extensions = registry.getExtensions("logicaldoc-core", "Search");
		} catch (Throwable e) {
			log.error(e.getMessage());
		}

		Search search = null;

		for (Extension ext : extensions) {
			int type = Integer.parseInt(ext.getParameter("type").valueAsString());
			if (type != opt.getType())
				continue;
			
			String className = ext.getParameter("class").valueAsString();
			try {
				search = (Search) Class.forName(className).newInstance();
				search.setOptions(opt);
			} catch (Throwable e) {
				log.error(e.getMessage());
			}
			break;
		}

		return search;
	}

	protected Search() {
	}

	/**
	 * Perform the search
	 * 
	 * @return The list of hits
	 */
	public final List<Hit> search() {
		log.info("Launch search");
		Date start = new Date();
		hits.clear();
		moreHitsPresent = false;

		try {
			internalSearch();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}

		Date finish = new Date();
		execTime = finish.getTime() - start.getTime();
		log.info("Search finished in " + execTime + "ms");

		return hits;
	}

	/**
	 * Concrete implementations must give a particular search algorithm that
	 * populates the hits list.
	 */
	protected abstract void internalSearch() throws Exception;

	public List<Hit> getHits() {
		return hits;
	}

	public boolean isMoreHitsPresent() {
		return moreHitsPresent;
	}

	public void setMoreHitsPresent(boolean moreHitsPresent) {
		this.moreHitsPresent = moreHitsPresent;
	}

	public int getEstimatedHitsNumber() {
		return estimatedHitsNumber;
	}

	/**
	 * Query execution time in milliseconds
	 */
	public long getExecTime() {
		return execTime;
	}

	public SearchOptions getOptions() {
		return options;
	}

	public void setOptions(SearchOptions options) {
		this.options = options;
	}
}