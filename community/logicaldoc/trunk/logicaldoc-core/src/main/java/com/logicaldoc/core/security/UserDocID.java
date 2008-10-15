package com.logicaldoc.core.security;

import java.io.Serializable;

/**
 * Identifier of <code>UserDoc</code>
 * 
 * @author Marco Meschieri
 * @version $Id: UserDocID.java,v 1.1 2007/06/29 06:28:29 marco Exp $
 * @since 3.0
 */
public class UserDocID implements Serializable {

	private static final long serialVersionUID = 1L;

	private String userName;

	private long docId;

	public UserDocID() {
	}

	public UserDocID(long docId, String userName) {
		super();
		this.userName = userName;
		this.docId = docId;
	}

	public long getDocId() {
		return docId;
	}

	public void setDocId(long docId) {
		this.docId = docId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof UserDocID))
			return false;

		UserDocID other = (UserDocID) obj;
		if (other.getDocId() != this.getDocId())
			return false;
		return other.getUserName().equals(this.getUserName());
	}

	@Override
	public int hashCode() {
		return (getUserName() + getDocId()).hashCode();
	}
}