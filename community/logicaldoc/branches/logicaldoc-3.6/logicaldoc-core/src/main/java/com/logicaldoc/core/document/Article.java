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

	private int docId = 0;

	private String subject = "";

	private String message = "";

	private String articleDate = "";

	private Date date = new Date();

	private String username = "";

	public Article() {
	}

	public String getArticleDate() {
		return articleDate;
	}

	public void setArticleDate(String articleDate) {
		this.articleDate = articleDate;
	}

	public int getArticleId() {
		return articleId;
	}

	public void setArticleId(int articleId) {
		this.articleId = articleId;
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
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
		articleDate = "";
		username = "";
		date = new Date();
	}

	public Date getDate() {
		if (articleDate != "") {
			try {
				date.setTime(Long.parseLong(articleDate));
			} catch (Exception e) {
				date = new Date();
			}
		}

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