package com.logicaldoc.core.document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.logicaldoc.core.PersistentObject;

/**
 * A discussion Thread over a document
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class DiscussionThread extends PersistentObject {

	private long docId;

	private Date creation = new Date();

	private String subject;

	private long creatorId;

	private String creatorName;

	private Date lastPost;

	private int replies;

	private int views;

	private List<DiscussionComment> comments = new ArrayList<DiscussionComment>();

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(long creatorId) {
		this.creatorId = creatorId;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public Date getLastPost() {
		return lastPost;
	}

	public void setLastPost(Date lastPost) {
		this.lastPost = lastPost;
	}

	public int getReplies() {
		return replies;
	}

	public void setReplies(int replies) {
		this.replies = replies;
	}

	public int getViews() {
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}

	public List<DiscussionComment> getComments() {
		return comments;
	}

	public void setComments(List<DiscussionComment> comments) {
		this.comments = comments;
	}

	/**
	 * Appends a new comment, taking care of creating a proper id, incrementing
	 * the replies field and setting the lastPost date.
	 */
	public void appendComment(DiscussionComment comment) {
		if (comment.getReplyTo() != null) {
			// Fix the replyPath
			DiscussionComment parent = comments.get(comment.getReplyTo());
			comment.setReplyPath(parent.getReplyPath() + comment.getReplyTo() + "/");
		}
		comments.add(comment);
		replies = comments.size();
		lastPost = comment.getDate();
	}

	public Date getCreation() {
		return creation;
	}

	public void setCreation(Date creation) {
		this.creation = creation;
	}

	public long getDocId() {
		return docId;
	}

	public void setDocId(long docId) {
		this.docId = docId;
	}
}