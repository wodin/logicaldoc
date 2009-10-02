package com.logicaldoc.web.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.DiscussionComment;
import com.logicaldoc.core.document.DiscussionThread;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DiscussionThreadDAO;
import com.logicaldoc.core.security.SecurityManager;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.util.FacesUtil;

/**
 * Control that allows the user to list and select articles
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class DiscussionsManager {
	
	protected static Log log = LogFactory.getLog(DiscussionsManager.class);

	private Collection<DiscussionThread> threads = new ArrayList<DiscussionThread>();

	private List<DiscussionComment> comments = new ArrayList<DiscussionComment>();

	private DiscussionThread selectedThread;

	private DiscussionComment selectedComment;

	private Document selectedDocument;

	private DocumentNavigation documentNavigation;

	private String subject;

	private String body;

	private UIInput subjectInput = null;

	private UIInput bodyInput = null;

	public UIInput getSubjectInput() {
		return subjectInput;
	}

	public void setSubjectInput(UIInput subjectInput) {
		this.subjectInput = subjectInput;
	}

	public UIInput getBodyInput() {
		return bodyInput;
	}

	public void setBodyInput(UIInput bodyInput) {
		this.bodyInput = bodyInput;
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

	public void setBody(String body) {
		this.body = body;
	}

	public void selectComment(DiscussionComment comment) {
		selectedComment = comment;
		setSubject(comment.getSubject());
		setBody("");
		FacesUtil.forceRefresh(subjectInput);
		FacesUtil.forceRefresh(bodyInput);
	}

	/**
	 * Changes the currently selected document and updates the articles list.
	 * 
	 * @param doc
	 */
	public void selectDocument(Document doc) {
		selectedDocument = doc;
		reload();
	}

	private void reload() {
		// initiate the list
		if (threads != null) {
			threads.clear();
		} else {
			threads = new ArrayList<DiscussionThread>(10);
		}

		try {
			long docId = selectedDocument.getId();
			DiscussionThreadDAO dao = (DiscussionThreadDAO) Context.getInstance().getBean(DiscussionThreadDAO.class);
			threads = dao.findByDocId(docId);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			Messages.addMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
		}

		setSubject("");
		setBody("");
		FacesUtil.forceRefresh(subjectInput);
		FacesUtil.forceRefresh(bodyInput);
	}

	/**
	 * Cleans up the resources used by this class. This method could be called
	 * when a session destroyed event is called.
	 */
	public void dispose() {
		threads.clear();
		comments.clear();
	}

	/**
	 * Gets the list of threads which will be used by the ice:dataTable
	 * component.
	 * 
	 * @return array list of threads
	 */
	public Collection<DiscussionThread> getThreads() {
		return threads;
	}
	
	public int getCount() {
		return getThreads().size();
	}

	public String back() {
		if (selectedThread == null) {
			documentNavigation.showDocuments();
		} else {
			// reload the list to avoid incorrect behaviour during
			// edit-changesubject-back actions
			selectDocument(selectedDocument);
		}
		setSelectedThread(null);
		setSelectedComment(null);
		return null;
	}

	/**
	 * Appends the new thread
	 */
	public String addThread() {
		try {
			DiscussionThread thread = new DiscussionThread();
			thread.setDocId(getSelectedDocument().getId());
			thread.setCreatorId(SessionManagement.getUserId());
			thread.setCreatorName(SessionManagement.getUser().getFullName());
			thread.setLastPost(thread.getCreation());

			DiscussionComment firstComment = new DiscussionComment();
			firstComment.setSubject(getSubject().trim());
			firstComment.setBody(getBody().trim());
			firstComment.setUserId(thread.getCreatorId());
			firstComment.setUserName(thread.getCreatorName());
			firstComment.setDate(thread.getLastPost());
			thread.getComments().add(firstComment);
			thread.setSubject(getSubject().trim());

			DiscussionThreadDAO dao = (DiscussionThreadDAO) Context.getInstance().getBean(DiscussionThreadDAO.class);
			dao.store(thread);

			Messages.addLocalizedInfo("discussion.save.ok");
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			Messages.addLocalizedError("discussion.save.error");
		}

		selectedThread = null;
		setSubject("");
		setBody("");
		FacesUtil.forceRefresh(subjectInput);
		FacesUtil.forceRefresh(bodyInput);
		selectDocument(getSelectedDocument());
		return null;
	}

	/**
	 * Checks if the current user can delete threads and comments. Only
	 * administrators can delete threads and comments.
	 */
	public boolean isDeleteAllowed() {
		SecurityManager manager = (SecurityManager) Context.getInstance().getBean(SecurityManager.class);
		return manager.isMemberOf(SessionManagement.getUserId(), "admin");
	}

	public String replyTo() {
		DiscussionComment comment = (DiscussionComment) FacesContext.getCurrentInstance().getExternalContext()
				.getRequestMap().get("comment");
		selectComment(comment);

		subject = selectedThread.getSubject();
		if (!subject.startsWith("RE:"))
			subject = "RE: " + subject;
		setBody("");
		FacesUtil.forceRefresh(subjectInput);
		FacesUtil.forceRefresh(bodyInput);
		return null;
	}

	public String postComment() {
		DiscussionComment comment = new DiscussionComment();
		if (getSelectedComment() != null) {
			comment.setReplyTo(getSelectedThread().getComments().indexOf(getSelectedComment()));
		}
		comment.setSubject(getSubject().trim());
		comment.setBody(getBody().trim());
		comment.setUserId(SessionManagement.getUserId());
		comment.setUserName(SessionManagement.getUser().getFullName());
		getSelectedThread().appendComment(comment);

		try {
			DiscussionThreadDAO dao = (DiscussionThreadDAO) Context.getInstance().getBean(DiscussionThreadDAO.class);
			dao.store(getSelectedThread());
			Messages.addLocalizedInfo("discussion.comment.add.ok");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			Messages.addLocalizedError("discussion.comment.add.error");
		}

		selectedComment = null;
		return showComments();
	}

	public String showComments() {
		DiscussionThread thread = (DiscussionThread) FacesContext.getCurrentInstance().getExternalContext()
				.getRequestMap().get("entry");
		DiscussionThreadDAO dao = (DiscussionThreadDAO) Context.getInstance().getBean(DiscussionThreadDAO.class);
		if (thread != null) {
			setSelectedThread(thread);
			if (thread != null)
				dao.initialize(thread);
		} else {
			thread = selectedThread;
		}

		thread.setViews(thread.getViews() + 1);
		dao.store(thread);

		// Prepare the comments list
		comments.clear();

		// Key is the replyPath, value is a list of comments ordered by date
		Map<String, List<DiscussionComment>> map = new HashMap<String, List<DiscussionComment>>();

		// First of all drop all deleted comments and group comments by path
		for (DiscussionComment comment : thread.getComments()) {
			if (0 == comment.getDeleted()) {
				List<DiscussionComment> list = map.get(comment.getReplyPath());
				if (list == null) {
					list = new ArrayList<DiscussionComment>();
					map.put(comment.getReplyPath(), list);
				}
				list.add(comment);
				Collections.sort(list);
			}
		}

		// Then flatten the comments tree
		flatten(map, "/");

		FacesUtil.forceRefresh(subjectInput);
		FacesUtil.forceRefresh(bodyInput);
		setSubject("RE: " + thread.getSubject());
		setBody("");
		return null;
	}

	/**
	 * Utility method used to flatten the reply tree
	 */
	private void flatten(Map<String, List<DiscussionComment>> map, String path) {
		List<DiscussionComment> list = map.get(path);
		if (list == null)
			return;
		for (DiscussionComment comment : list) {
			comments.add(comment);
			flatten(map, path + selectedThread.getComments().indexOf(comment) + "/");
		}
	}

	public Document getSelectedDocument() {
		return selectedDocument;
	}

	public void setSelectedDocument(Document selectedDocument) {
		this.selectedDocument = selectedDocument;
	}

	public DiscussionThread getSelectedThread() {
		return selectedThread;
	}

	public void setSelectedThread(DiscussionThread thread) {
		this.selectedThread = thread;
	}

	public void setDocumentNavigation(DocumentNavigation documentNavigation) {
		this.documentNavigation = documentNavigation;
	}

	public String deleteThread() {
		try {
			DiscussionThread thread = (DiscussionThread) FacesContext.getCurrentInstance().getExternalContext()
					.getRequestMap().get("entry");
			DiscussionThreadDAO dao = (DiscussionThreadDAO) Context.getInstance().getBean(DiscussionThreadDAO.class);
			dao.delete(thread.getId());
			reload();
			Messages.addLocalizedInfo("discussion.delete.ok");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			Messages.addLocalizedError("discussion.delete.error");
		}
		return null;
	}

	/**
	 * Deletes a comment
	 */
	public String deleteComment() {
		try {
			DiscussionComment comment = (DiscussionComment) FacesContext.getCurrentInstance().getExternalContext()
					.getRequestMap().get("comment");
			comment.setDeleted(1);
			DiscussionThreadDAO dao = (DiscussionThreadDAO) Context.getInstance().getBean(DiscussionThreadDAO.class);
			dao.store(getSelectedThread());
			reload();
			Messages.addLocalizedInfo("discussion.delete.ok");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			Messages.addLocalizedError("discussion.delete.error");
		}
		return showComments();
	}

	public DiscussionComment getSelectedComment() {
		return selectedComment;
	}

	public void setSelectedComment(DiscussionComment selectedComment) {
		this.selectedComment = selectedComment;
	}

	public List<DiscussionComment> getComments() {
		return comments;
	}

	public void setComments(List<DiscussionComment> comments) {
		this.comments = comments;
	}

}