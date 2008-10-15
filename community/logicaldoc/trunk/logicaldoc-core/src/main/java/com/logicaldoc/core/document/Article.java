package com.logicaldoc.core.document;

import java.util.Date;

/**
 * 
 * @author Michael Scholz
 * @author Marco Meschieri
 */
public class Article {
	private static final long serialVersionUID = 1L;

	private int articleId = 0;

	private long docId = 0;

	private String subject = "";

	private String message = "";

	private Date date = new Date();

	private String username = "";

	public Article() {
	}

	public int getArticleId() {
		return articleId;
	}

	public void setArticleId(int articleId) {
		this.articleId = articleId;
	}

	public long getDocId() {
		return docId;
	}

	public void setDocId(long docId) {
		this.docId = docId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void reset() {
		articleId = 0;
		docId = 0;
		subject = "";
		message = "";
		username = "";
		date = new Date();
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Article))
			return false;

		Article other = (Article) obj;

		return other.getArticleId() == this.getArticleId();
	}

	@Override
	public int hashCode() {
		return new Integer(articleId).hashCode();
	}
}