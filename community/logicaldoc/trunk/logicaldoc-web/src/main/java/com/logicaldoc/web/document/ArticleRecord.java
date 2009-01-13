package com.logicaldoc.web.document;

import java.util.Date;

import com.logicaldoc.core.document.Article;

/**
 * Utility class suitable for template display
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class ArticleRecord extends Article {
	private static final long serialVersionUID = 1L;

	private Article wrappedArticle;

	private ArticlesRecordsManager manager;

	public ArticleRecord(Article wrappedArticle, ArticlesRecordsManager manager) {
		super();
		this.wrappedArticle = wrappedArticle;
		this.manager = manager;
	}

	public boolean equals(Object obj) {
		return wrappedArticle.equals(obj);
	}

	public long getId() {
		return wrappedArticle.getId();
	}

	public Date getDate() {
		return wrappedArticle.getDate();
	}

	public long getDocId() {
		return wrappedArticle.getDocId();
	}

	public String getMessage() {
		return wrappedArticle.getMessage();
	}

	public String getSubject() {
		return wrappedArticle.getSubject();
	}

	public String getUsername() {
		return wrappedArticle.getUsername();
	}
	
	public long getUserId() {
		return wrappedArticle.getUserId();
	}	

	public int hashCode() {
		return wrappedArticle.hashCode();
	}

	public void reset() {
		wrappedArticle.reset();
	}

	public void setId(long articleId) {
		wrappedArticle.setId(articleId);
	}

	public void setDate(Date date) {
		wrappedArticle.setDate(date);
	}

	public void setDocId(long docId) {
		wrappedArticle.setDocId(docId);
	}

	public void setMessage(String message) {
		wrappedArticle.setMessage(message);
	}

	public void setSubject(String subject) {
		wrappedArticle.setSubject(subject);
	}

	public void setUsername(String username) {
		wrappedArticle.setUsername(username);
	}
	
	public void setUserId(long userId) {
		wrappedArticle.setUserId(userId);
	}

	public String toString() {
		return wrappedArticle.toString();
	}

	public String select() {
		manager.setSelectedArticle(this);
		return null;
	}
	
	public boolean isEditable() {
		return true;
	}
	
	public boolean isDeletable() {
		return true;
	}
	
	public String edit() {
		manager.editArticle(this);
		return null;
	}
	
	public String delete() {
		return manager.deleteArticle(this);
	}

	public Article getWrappedArticle() {
		return wrappedArticle;
	}
}
