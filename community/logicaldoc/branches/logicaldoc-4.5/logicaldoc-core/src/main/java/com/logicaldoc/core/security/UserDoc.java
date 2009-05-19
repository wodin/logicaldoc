package com.logicaldoc.core.security;

import java.io.Serializable;
import java.util.Date;

import com.logicaldoc.core.PersistentObject;

/**
 * This is a mapper class for table userdoc.
 * 
 * @author Michael Scholz
 * @version 1.0
 */
public class UserDoc extends PersistentObject implements Serializable {
	private static final long serialVersionUID = 1L;

	private Date date;

	private long userId;

	private long docId;

	public UserDoc() {
		date = new Date();
	}

	public long getUserId() {
		return userId;
	}

	public long getDocId() {
		return docId;
	}

	public Date getDate() {
		return date;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public void setDocId(long docId) {
		this.docId = docId;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}