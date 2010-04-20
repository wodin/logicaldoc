package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;

/**
 * Collects the results of a search and store some search statistics
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class GUIResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private long time;

	private boolean hasMore = false;

	private GUIResultHit[] hits;

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public boolean isHasMore() {
		return hasMore;
	}

	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}

	public GUIResultHit[] getHits() {
		return hits;
	}

	public void setHits(GUIResultHit[] hits) {
		this.hits = hits;
	}
}