package com.logicaldoc.webservice.search;

import com.logicaldoc.core.searchengine.HitImpl;
import com.logicaldoc.util.SnippetStripper;
import com.logicaldoc.util.StringUtil;

/**
 * Represents a web service search result
 * 
 * @author Matteo Caruso - Logical Object
 * @since 5.2
 */
public class WSSearchResult {
	private long time = 0;

	private long estimatedHitsNumber = 0;

	private HitImpl[] hits = new HitImpl[0];

	private int moreHits = 0;

	private int totalHits = 0;

	public int getTotalHits() {
		return totalHits;
	}

	public void setTotalHits(int totalHits) {
		this.totalHits = totalHits;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getEstimatedHitsNumber() {
		return estimatedHitsNumber;
	}

	public void setEstimatedHitsNumber(long estimatedHitsNumber) {
		this.estimatedHitsNumber = estimatedHitsNumber;
	}

	public int getMoreHits() {
		return moreHits;
	}

	public void setMoreHits(int moreHits) {
		this.moreHits = moreHits;
	}

	public HitImpl[] getHits() {
		return hits;
	}

	public void setHits(HitImpl[] hits) {
		this.hits = hits;
		for (HitImpl hitImpl : hits) {
			hitImpl.setSummary("--");
		}
	}
}