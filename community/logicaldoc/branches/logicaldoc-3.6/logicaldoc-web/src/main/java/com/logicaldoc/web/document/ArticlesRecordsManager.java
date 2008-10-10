package com.logicaldoc.web.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.faces.application.FacesMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.logicaldoc.core.document.Article;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.ArticleDAO;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.util.Context;

import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.navigation.PageContentBean;

/**
 * Control that allows the user to list and select articles
 * 
 * @author Marco Meschieri
 * @version $Id: ArticlesRecordsManager.java,v 1.3 2006/09/03 16:24:37 marco Exp $
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
			int menuId = selectedDocument.getMenuId();
			int docId = selectedDocument.getDocId();

			DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			Document document = docDao.findByMenuId(menuId);
			docId = document.getDocId();

			ArticleDAO artDao = (ArticleDAO) Context.getInstance().getBean(ArticleDAO.class);
			Collection<Article> coll = artDao.findByDocId(docId);

			for (Article article : coll) {
				articles.add(new ArticleRecord(article, this));
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
			selectedArticle = null;
		}

		editing = false;

		return null;
	}

	public String save() {
		if (SessionManagement.isValid()) {
			try {
				int docId = selectedDocument.getDocId();
				String username = SessionManagement.getUsername();
				selectedArticle.setArticleDate(String.valueOf(new Date().getTime()));
				selectedArticle.setUsername(username);
				selectedArticle.setDocId(docId);

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
		selectedArticle = new ArticleRecord(new Article(), this);
		selectedArticle.setUsername(SessionManagement.getUsername());

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

	public void setSelectedArticle(ArticleRecord selectedArticle) {
		this.selectedArticle = selectedArticle;
	}

	public void setDocumentNavigation(DocumentNavigation documentNavigation) {
		this.documentNavigation = documentNavigation;
	}
}
