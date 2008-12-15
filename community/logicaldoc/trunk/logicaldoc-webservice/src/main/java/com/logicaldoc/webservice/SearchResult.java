package com.logicaldoc.webservice;

/**
 * Represents a search result
 * 
 * @author Marco Meschieri - Logical Object
 * @since 3.0
 */
public class SearchResult {
	private long time = 0;

	private long estimatedHitsNumber = 0;

	private Result[] result = new Result[0];

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

	public Result[] getResult() {
		return result;
	}

	public void setResult(Result[] result) {
		this.result = result;
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
}
