package com.logicaldoc.webservice.document;

import java.io.Serializable;

/**
 * Web Service Document Link.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.1.1
 */
public class WSLink implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;

	private String type;

	private long doc1;

	private long doc2;

	public WSLink() {

	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getDoc1() {
		return doc1;
	}

	public void setDoc1(long doc1) {
		this.doc1 = doc1;
	}

	public long getDoc2() {
		return doc2;
	}

	public void setDoc2(long doc2) {
		this.doc2 = doc2;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
