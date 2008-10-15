package com.logicaldoc.core;

import com.logicaldoc.core.document.Document;

/**
 * This abstract class defines the minimum requirements of persistent objects.
 * 
 * @author Marco Meschieri
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
		if (!(obj instanceof Document))
			return false;
		Document other = (Document) obj;
		return other.getId() == this.getId();
	}

	public int hashCode() {
		return new Long(id).hashCode();
	}

	public String toString() {
		return Long.toString(id);
	}
}