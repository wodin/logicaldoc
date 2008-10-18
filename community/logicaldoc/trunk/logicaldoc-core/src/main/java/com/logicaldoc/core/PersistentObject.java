package com.logicaldoc.core;


/**
 * This abstract class defines the minimum requirements of persistent objects.
 * 
 * @author Marco Meschieri - Logical Objects
 * @version $Id:$
 * @since 4.0
 */
public abstract class PersistentObject {
	private long id = 0;

	/**
	 * Unique identifier(primary key) in the data store
	 */
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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