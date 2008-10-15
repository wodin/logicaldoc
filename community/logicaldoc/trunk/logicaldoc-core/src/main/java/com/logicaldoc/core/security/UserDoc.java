package com.logicaldoc.core.security;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 * This is a mapper class for table userdoc.
 * 
 * @author Michael Scholz
 * @version 1.0
 */
public class UserDoc implements Serializable {
	private static final long serialVersionUID = 1L;

	private Date date;

	private UserDocID id = new UserDocID(0, "");

	public UserDoc() {
		date = new Date();
	}

	public String getUserName() {
		return id.getUserName();
	}

	public UserDocID getId() {
		return id;
	}

	public void setId(UserDocID id) {
		this.id = id;
	}

	public long getDocId() {
		return id.getDocId();
	}

	public Date getDate() {
		return date;
	}

	public void setUserName(String uname) {
		id.setUserName(uname);
	}

	public void setDocId(long id) {
		this.id.setDocId(id);
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String toString() {
		// return ReflectionToStringBuilder.toString(this);
		return (new ReflectionToStringBuilder(this) {
			protected boolean accept(java.lang.reflect.Field f) {
				return super.accept(f);
			} // end method accept
		}).toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof UserDoc))
			return false;
		UserDoc other = (UserDoc) obj;
		return other.getId().equals(other.getId());
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}