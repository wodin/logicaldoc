package com.logicaldoc.webservice.search;

import com.logicaldoc.util.SnippetStripper;
import com.logicaldoc.webservice.document.WSDocument;

/**
 * Represents a web service search result
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class WSSearchResult {
	private long time = 0;

	private long estimatedHitsNumber = 0;

	private WSDocument[] hits = new WSDocument[0];

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

	public WSDocument[] getHits() {
		return hits;
	}

	public void setHits(WSDocument[] hits) {
		this.hits = hits;
		for (WSDocument hit : hits) {
			if (hit.getSummary() != null)
				hit.setSummary(SnippetStripper.strip(hit.getSummary()));
		}
	}
}