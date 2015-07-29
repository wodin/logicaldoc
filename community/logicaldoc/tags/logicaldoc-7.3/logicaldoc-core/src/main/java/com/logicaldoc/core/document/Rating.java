package com.logicaldoc.core.document;

import com.logicaldoc.core.PersistentObject;

/**
 * A rating over a document
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
public class Rating extends PersistentObject {

	private long docId;

	private long userId;

	private int vote = 0;

	private Integer count;

	private Float average;

	public Rating() {
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getDocId() {
		return docId;
	}

	public void setDocId(long docId) {
		this.docId = docId;
	}

	public int getVote() {
		return vote;
	}

	public void setVote(int vote) {
		this.vote = vote;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Float getAverage() {
		return average;
	}

	public void setAverage(Float average) {
		this.average = average;
	}
}