package com.logicaldoc.core.document;

import java.util.Date;

/**
 * A single post of a discussion thread
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class DiscussionComment implements Comparable<DiscussionComment> {
	private Integer replyTo;

	private long userId;

	private String userName;

	private String subject;

	private String body;

	private String replyPath = "/";

	private Date date = new Date();

	private int deleted = 0;

	private Long threadId;
	
	private Long docId;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	/**
	 * Returns a formatted body suitable for HTML output
	 */
	public String getFormattedBody() {
		String buf=body.replaceAll("\\n", "<br/>");
		return buf;
	}
	
	public void setBody(String body) {
		this.body = body;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getIndentLevel() {
		return getReplyPath().split("/").length - 1;
	}

	@Override
	public int compareTo(DiscussionComment other) {
		return date.compareTo(other.getDate());
	}

	public Integer getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(Integer replyTo) {
		this.replyTo = replyTo;
	}

	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public String getReplyPath() {
		return replyPath;
	}

	public void setReplyPath(String replyPath) {
		this.replyPath = replyPath;
	}

	public Long getThreadId() {
		return threadId;
	}

	public void setThreadId(Long threadId) {
		this.threadId = threadId;
	}

	public Long getDocId() {
		return docId;
	}

	public void setDocId(Long docId) {
		this.docId = docId;
	}
}