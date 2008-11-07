package com.logicaldoc.core;

import java.util.Date;

/**
 * This abstract class defines the minimum requirements of persistent objects.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public abstract class PersistentObject {
	private long id = 0;

	private Date lastModified;

	/**
	 * Unique identifier in the data store
	 */
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/**
	 * The last time this instance was modified
	 */
	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof PersistentObject))
			return false;
		PersistentObject other = (PersistentObject) obj;
		return other.getId() == this.getId();
	}

	public int hashCode() {
		return new Long(getId()).hashCode();
	}

	public String toString() {
		return Long.toString(getId());
	}
}