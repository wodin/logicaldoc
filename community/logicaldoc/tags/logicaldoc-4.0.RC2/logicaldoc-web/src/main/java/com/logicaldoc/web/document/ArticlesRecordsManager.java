package com.logicaldoc.web.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.Article;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.ArticleDAO;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.User;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.navigation.PageContentBean;

/**
 * Control that allows the user to list and select articles
 * 
 * @author Marco Meschieri
 * @since 3.0
 */
public class ArticlesRecordsManager {
	protected static Log log = LogFactory.getLog(ArticlesRecordsManager.class);

	private Collection<Article> articles = new ArrayList<Article>();

	private ArticleRecord selectedArticle;

	private Document selectedDocument;

	private boolean editing = false;

	private DocumentNavigation documentNavigation;

	public boolean isEditing() {
		return editing;
	}

	public boolean isReadOnly() {
		return !(editing);
	}

	/**
	 * Changes the currently selected document and updates the articles list.
	 * 
	 * @param doc
	 */
	public void selectDocument(Document doc) {
		selectedDocument = doc;

		// initiate the list
		if (articles != null) {
			articles.clear();
		} else {
			articles = new ArrayList<Article>(10);
		}

		try {
			long docId = selectedDocument.getId();

			DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			Document document = docDao.findById(docId);
			docId = document.getId();

			ArticleDAO artDao = (ArticleDAO) Context.getInstance().getBean(ArticleDAO.class);
			List<Article> coll = artDao.findByDocId(docId);
			Collections.reverse(coll);

			User user = SessionManagement.getUser();
			for (Article article : coll) {
				articles.add(new ArticleRecord(article, this, user));
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			Messages.addMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
		}
	}

	/**
	 * Cleans up the resources used by this class. This method could be called
	 * when a session destroyed event is called.
	 */
	public void dispose() {
		articles.clear();
	}

	/**
	 * Gets the list of articles which will be used by the ice:dataTable
	 * component.
	 * 
	 * @return array list of articles
	 */
	public Collection<Article> getArticles() {
		return articles;
	}

	public String back() {
		if (selectedArticle == null) {
			documentNavigation.setSelectedPanel(new PageContentBean("documents"));
		} else {
			// reload the list to avoid incorrect beahaviour during
			// edit-changesubject-back actions
			selectDocument(selectedDocument);
			selectedArticle = null;
		}

		editing = false;

		return null;
	}

	public String save() {
		if (SessionManagement.isValid()) {
			try {
				long docId = selectedDocument.getId();
				if (selectedArticle.getDate() == null) {
					selectedArticle.setDate(new Date());
				}
				selectedArticle.setDocId(docId);

				// Verify that the subject is not empty
				if (StringUtils.isEmpty(selectedArticle.getSubject())) {
					Messages.addLocalizedError("errors.required");
					return null;
				}

				ArticleDAO articleDao = (ArticleDAO) Context.getInstance().getBean(ArticleDAO.class);
				articleDao.store(selectedArticle.getWrappedArticle());

				Messages.addLocalizedInfo("msg.action.savearticle");
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				Messages.addLocalizedError("errors.action.savearticle");
			}

			selectDocument(selectedDocument);
			selectedArticle = null;
			editing = false;

			return null;
		} else {
			return "login";
		}
	}

	public String add() {
		editing = true;
		selectedArticle = new ArticleRecord(new Article(), this, null);
		selectedArticle.setUserId(SessionManagement.getUserId());
		String username = SessionManagement.getUser().getFullName();
		selectedArticle.setUsername(username);

		return null;
	}

	public Document getSelectedDocument() {
		return selectedDocument;
	}

	public void setSelectedDocument(Document selectedDocument) {
		this.selectedDocument = selectedDocument;
	}

	public ArticleRecord getSelectedArticle() {
		return selectedArticle;
	}

	public void setSelectedArticle(ArticleRecord article) {
		this.selectedArticle = article;
	}

	public void setDocumentNavigation(DocumentNavigation documentNavigation) {
		this.documentNavigation = documentNavigation;
	}

	public void editArticle(ArticleRecord article) {
		editing = true;
		setSelectedArticle(article);
	}

	public String deleteArticle(ArticleRecord record) {
		if (SessionManagement.isValid()) {
			try {
				ArticleDAO articleDao = (ArticleDAO) Context.getInstance().getBean(ArticleDAO.class);
				articleDao.delete(record.getId());

				Messages.addLocalizedInfo("msg.action.deleteitem");
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				Messages.addLocalizedError("errors.action.deleteitem");
			}

			selectDocument(selectedDocument);
			selectedArticle = null;
			editing = false;
		} else {
			return "login";
		}
		return null;
	}
}
