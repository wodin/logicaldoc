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

	private String timeStamp;

	private UserDocID id = new UserDocID(0, "");

	public UserDoc() {
		timeStamp = String.valueOf(new Date().getTime());
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

	public int getMenuId() {
		return id.getMenuId();
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setUserName(String uname) {
		id.setUserName(uname);
	}

	public void setMenuId(int id) {
		this.id.setMenuId(id);
	}

	public void setTimeStamp(String time) {
		timeStamp = time;
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