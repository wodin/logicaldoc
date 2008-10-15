package com.logicaldoc.core.document;

import java.io.Serializable;

/**
 * Composite identifier of <code>Term</code>
 * 
 * @author Marco Meschieri
 * @version $Id: TermID.java,v 1.1 2007/06/29 06:28:28 marco Exp $
 * @since 3.0
 */
public class TermID implements Serializable {

	private static final long serialVersionUID = 1L;

	private long docId;

	private String stem = "";

	public TermID(long docId, String stem) {
		this.docId = docId;
		this.stem = stem;
	}

	public TermID() {
	}

	public long getDocId() {
		return docId;
	}

	public void setDocId(long docId) {
		this.docId = docId;
	}

	public String getStem() {
		return stem;
	}

	public void setStem(String stem) {
		this.stem = stem;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TermID))
			return false;

		TermID other = (TermID) obj;
		if (other.getDocId() != this.getDocId())
			return false;
		return other.getStem().equals(this.getStem());
	}

	@Override
	public int hashCode() {
		return (docId + stem).hashCode();
	}

	@Override
	public String toString() {
		return docId + "," + stem;
	}
}
