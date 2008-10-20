package com.logicaldoc.core.document;

import com.logicaldoc.core.PersistentObject;

/**
 * Represents a document link. It represents a link beetween two documents
 * (docId1 and docId2). Every link can be of a certain type.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 4.0
 */
public class DocumentLink extends PersistentObject {

	private String type;

	private long docId1 = -1;

	private long docId2 = -1;

	public DocumentLink() {

	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getDocId1() {
		return docId1;
	}

	public void setDocId1(long docId1) {
		this.docId1 = docId1;
	}

	public long getDocId2() {
		return docId2;
	}

	public void setDocId2(long docId2) {
		this.docId2 = docId2;
	}

}
