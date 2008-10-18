package com.logicaldoc.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.Article;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.ArticleDAO;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.document.DocumentRecord;
import com.logicaldoc.web.document.DocumentsRecordsManager;

public class HomeBean {

	protected static Log log = LogFactory.getLog(HomeBean.class);

	private boolean messagesExpanded = true;

	private boolean lastArticlesExpanded = false;

	private boolean lastDocumentsExpanded = false;

	private boolean lastDownloadsExpanded = false;

	private boolean lastCheckoutExpanded = false;

	private boolean tagCloudsExpanded = true;

	public boolean isMessagesExpanded() {
		return messagesExpanded;
	}

	public void setMessagesExpanded(boolean messagesExpanded) {
		this.messagesExpanded = messagesExpanded;
	}

	/**
	 * Retrieves the list of last changed documents (by user in session) from
	 * the database
	 */
	public List<DocumentRecord> getLastModifiedDocuments() {

		List<DocumentRecord> lastModified = new ArrayList<DocumentRecord>();

		if (SessionManagement.isValid()) {
			try {
				String username = SessionManagement.getUsername();

				DocumentDAO docdao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
				Collection<Document> documents = (Collection<Document>) docdao.findLastModifiedByUserName(username, 10);
				for (Document document : documents) {
					lastModified.add(new DocumentRecord(document.getId(), null,
							DocumentsRecordsManager.GROUP_INDENT_STYLE_CLASS,
							DocumentsRecordsManager.GROUP_ROW_STYLE_CLASS));
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

		return lastModified;
	}

	/**
	 * Retrieves the list of last accessed documents (by user in session) from
	 * the database
	 */
	public List<DocumentRecord> getLastDownloads() {
		List<DocumentRecord> lastDownloads = new ArrayList<DocumentRecord>();

		if (SessionManagement.isValid()) {
			try {
				String username = SessionManagement.getUsername();
				DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);

				Collection<Document> docColl = docDao.findLastDownloadsByUserName(username, 10);
				for (Document doc : docColl) {
					lastDownloads.add(new DocumentRecord(doc.getId(), null,
							DocumentsRecordsManager.GROUP_INDENT_STYLE_CLASS,
							DocumentsRecordsManager.GROUP_ROW_STYLE_CLASS));
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

		return lastDownloads;
	}

	/**
	 * Retrieves the list of last articles from the database
	 */
	public Collection<Article> getLastArticles() {
		List<Article> lastarticles = new ArrayList<Article>();

		if (SessionManagement.isValid()) {
			try {
				String username = SessionManagement.getUsername();
				ArticleDAO artDao = (ArticleDAO) Context.getInstance().getBean(ArticleDAO.class);
				Collection<Article> articles = artDao.findByUserName(username);

				if (articles != null) {
					// revert the list, it should be in asc order by time
					lastarticles.addAll(articles);
					Collections.reverse(lastarticles);
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

		return lastarticles;
	}

	/**
	 * Retrieves the list of Checkout documents (by user in session) from the
	 * database
	 */
	public List<DocumentRecord> getCheckoutDocs() {

		List<DocumentRecord> lastdocs = new ArrayList<DocumentRecord>();

		if (SessionManagement.isValid()) {
			try {
				String username = SessionManagement.getUsername();
				DocumentDAO docdao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
				Collection<Document> documents = (Collection<Document>) docdao.findCheckoutByUserName(username);
				for (Document document : documents) {
					lastdocs.add(new DocumentRecord(document.getId(), null,
							DocumentsRecordsManager.GROUP_INDENT_STYLE_CLASS,
							DocumentsRecordsManager.GROUP_ROW_STYLE_CLASS));
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		return lastdocs;
	}

	public boolean isLastArticlesExpanded() {
		return lastArticlesExpanded;
	}

	public void setLastArticlesExpanded(boolean lastArticlesExpanded) {
		this.lastArticlesExpanded = lastArticlesExpanded;
	}

	public boolean isLastDocumentsExpanded() {
		return lastDocumentsExpanded;
	}

	public void setLastDocumentsExpanded(boolean lastDocumentsExpanded) {
		this.lastDocumentsExpanded = lastDocumentsExpanded;
	}

	public boolean isLastDownloadsExpanded() {
		return lastDownloadsExpanded;
	}

	public void setLastDownloadsExpanded(boolean lastDownloadsExpanded) {
		this.lastDownloadsExpanded = lastDownloadsExpanded;
	}

	public boolean isLastCheckoutExpanded() {
		return lastCheckoutExpanded;
	}

	public void setLastCheckoutExpanded(boolean lastCheckoutExpanded) {
		this.lastCheckoutExpanded = lastCheckoutExpanded;
	}

	/**
	 * Generate tag clouds from lists of keywords
	 */
	public List<TagCloud> getTagClouds() {

		List<TagCloud> tags = new ArrayList<TagCloud>();

		DocumentDAO docdao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		HashMap<String, Integer> keywords = (HashMap<String, Integer>) docdao.findAllKeywords();
		if (keywords.isEmpty())
			return tags;

		for (String key : keywords.keySet()) {
			Integer val = keywords.get(key);
			TagCloud tc = new TagCloud(key, val);
			tags.add(tc);
		}

		// order the list by occurrences
		Comparator<TagCloud> compOccurrence = new TagCloudComparatorOccurrence();
		Collections.sort(tags, compOccurrence);
		Collections.reverse(tags);

		// keep only the first 30 elements
		if (tags.size() > 30)
			tags = tags.subList(0, 30);

		// Find the Max frequency
		int maxValue = tags.get(0).getOccurence();
		log.debug("maxValue = " + maxValue);

		for (TagCloud cloud : tags) {
			double scale = cloud.getOccurence().doubleValue() / maxValue;
			int scaleInt = (int) Math.ceil(scale * 10);
			cloud.setScale(scaleInt);
		}

		// Sort the tags collection by name
		Comparator<TagCloud> compName = new TagCloudComparatorName();
		Collections.sort(tags, compName);

		return tags;
	}

	class TagCloudComparatorOccurrence implements Comparator<TagCloud> {
		public int compare(TagCloud tc0, TagCloud tc1) {
			return tc0.getOccurence().compareTo(tc1.getOccurence());
		}
	}

	class TagCloudComparatorName implements Comparator<TagCloud> {
		public int compare(TagCloud tc0, TagCloud tc1) {
			return tc0.getKeyword().compareTo(tc1.getKeyword());
		}
	}

	public boolean isTagCloudsExpanded() {
		return tagCloudsExpanded;
	}

	public void setTagCloudsExpanded(boolean tagCloudsExpanded) {
		this.tagCloudsExpanded = tagCloudsExpanded;
	}
}
