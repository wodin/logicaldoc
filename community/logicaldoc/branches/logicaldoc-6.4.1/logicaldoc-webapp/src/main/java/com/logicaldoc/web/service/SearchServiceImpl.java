package com.logicaldoc.web.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.i18n.Language;
import com.logicaldoc.core.i18n.LanguageManager;
import com.logicaldoc.core.searchengine.FolderSearchOptions;
import com.logicaldoc.core.searchengine.FulltextSearchOptions;
import com.logicaldoc.core.searchengine.Hit;
import com.logicaldoc.core.searchengine.Indexer;
import com.logicaldoc.core.searchengine.LuceneDocument;
import com.logicaldoc.core.searchengine.Search;
import com.logicaldoc.core.searchengine.SearchOptions;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.text.analyzer.AnalyzerManager;
import com.logicaldoc.core.util.UserUtil;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUICriterion;
import com.logicaldoc.gui.common.client.beans.GUIHit;
import com.logicaldoc.gui.common.client.beans.GUIResult;
import com.logicaldoc.gui.common.client.beans.GUISearchOptions;
import com.logicaldoc.gui.frontend.client.services.SearchService;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.LocaleUtil;
import com.logicaldoc.web.util.SessionUtil;

/**
 * Implementation of the SecurityService
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class SearchServiceImpl extends RemoteServiceServlet implements SearchService {

	private static final long serialVersionUID = 1L;

	protected static Log log = LogFactory.getLog(SearchServiceImpl.class);

	@Override
	public GUIResult search(String sid, GUISearchOptions options) throws InvalidSessionException {
		UserSession session = SessionUtil.validateSession(sid);
		options.setUserId(session.getUserId());

		GUIResult result = new GUIResult();

		try {
			SearchOptions searchOptions = toSearchOptions(options);

			if (searchOptions instanceof FulltextSearchOptions) {
				Locale exprLoc = LocaleUtil.toLocale(options.getExpressionLanguage());

				Language lang = LanguageManager.getInstance().getLanguage(exprLoc);
				if (lang == null) {
					// Try to find another supported language
					exprLoc = LocaleUtil.toLocale(exprLoc.getLanguage());
					lang = LanguageManager.getInstance().getLanguage(exprLoc);

					if (exprLoc != null)
						((FulltextSearchOptions) searchOptions).setExpressionLanguage(exprLoc.getLanguage());
				}
			}

			// Retrieve the search machinery
			Search search = Search.get(searchOptions);

			try {
				search.search();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}

			List<Hit> hits = search.getHits();

			result.setTime(search.getExecTime());
			result.setHasMore(search.isMoreHitsPresent());

			List<GUIHit> guiResults = new ArrayList<GUIHit>();
			for (Hit hit : hits) {
				GUIHit h = new GUIHit();
				if (hit.getCustomId() == null) {
					// This document is a shortcut
					DocumentDAO docDAO = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
					// This settings are necessary for the 'open in folder'
					// feature.
					Document shortcutDoc = docDAO.findById(hit.getDocId());
					h.setId(shortcutDoc.getDocRef());
					h.setFolderId(shortcutDoc.getFolder().getId());
				} else {
					h.setId(hit.getDocId());
					h.setFolderId(hit.getFolderId());
				}
				h.setDate(hit.getDate());
				h.setCreation(hit.getCreation());
				h.setTitle(hit.getTitle());
				h.setCustomId(hit.getCustomId());
				h.setType(hit.getType());
				h.setSummary(hit.getSummary());
				h.setSize(hit.getSize());
				h.setScore(hit.getScore());
				h.setSourceDate(hit.getSourceDate());
				h.setComment(hit.getComment());
				h.setFolderName(hit.getFolderName());
				h.setPublished(hit.getPublished());

				// Check if the document is not an alias to visualize the
				// correct icon: if the document is an alias the FULL-TEXT
				// search returns a custom id null, the PARAMETRIC and TAG
				// searches return a doc ref equals to 0 or null
				if ((hit.getDocRef() == null || hit.getDocRef() == 0) && hit.getCustomId() != null)
					h.setIcon(FilenameUtils.getBaseName(hit.getIcon()));
				else
					h.setIcon("alias");
				if ("folder".equals(hit.getType()))
					h.setIcon("folder_closed");
				guiResults.add(h);
			}
			result.setHits(guiResults.toArray(new GUIHit[guiResults.size()]));

			return result;
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	@Override
	public boolean save(String sid, GUISearchOptions options) throws InvalidSessionException {
		UserSession session = SessionUtil.validateSession(sid);

		try {
			SearchOptions opt = toSearchOptions(options);

			File file = UserUtil.getUserResource(session.getUserId(), "queries");
			file = new File(file, opt.getName() + ".ser");
			if (file.exists()) {
				return false;
			}

			try {
				opt.write(file);
			} catch (Exception e) {
				log.error(e.getMessage());
			}

			log.debug("Saved query " + opt.getName());
			return true;
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	@Override
	public void delete(String sid, String[] names) throws InvalidSessionException {
		UserSession session = SessionUtil.validateSession(sid);

		try {
			File dir = UserUtil.getUserResource(session.getUserId(), "queries");

			for (String name : names) {
				File file = new File(dir, name + ".ser");
				try {
					FileUtils.forceDelete(file);
				} catch (IOException e) {
					log.error(e.getMessage());
				}
			}
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	@Override
	public GUISearchOptions load(String sid, String name) throws InvalidSessionException {
		UserSession session = SessionUtil.validateSession(sid);

		try {
			File dir = UserUtil.getUserResource(session.getUserId(), "queries");
			File file = new File(dir, name + ".ser");
			SearchOptions opt = null;
			try {
				opt = SearchOptions.read(file);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			return toGUIOptions(opt);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	@Override
	public GUISearchOptions getSimilarityOptions(String sid, long docId, String locale) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		try {
			Indexer indexer = (Indexer) Context.getInstance().getBean(Indexer.class);
			String text = indexer.getDocument(Long.toString(docId)).get(LuceneDocument.FIELD_CONTENT);

			// Extracts the most used 10 words
			AnalyzerManager analyzer = (AnalyzerManager) Context.getInstance().getBean(AnalyzerManager.class);

			String terms = "";
			try {
				terms = analyzer.getTermsAsString(10, text, LocaleUtil.toLocale(locale));
				terms = terms.replaceAll(",", " ");
			} catch (Exception e) {
				log.error(e);
			}

			GUISearchOptions opt = new GUISearchOptions();
			opt.setExpression(terms);
			return opt;
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	/**
	 * Load all the search options associated to all the searches saved for the
	 * current user.
	 * 
	 * @return the list of search options
	 */
	public List<SearchOptions> getSearches(UserSession user) {
		File file = UserUtil.getUserResource(user.getUserId(), "queries");
		if (!file.exists()) {
			return null;
		}

		// initiate the list
		List<SearchOptions> queries = new ArrayList<SearchOptions>();

		File[] searchesFiles = file.listFiles();
		for (int i = 0; i < searchesFiles.length; i++) {
			File searchFile = searchesFiles[i];
			SearchOptions opt = null;
			try {
				opt = SearchOptions.read(searchFile);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}

			if (opt != null)
				queries.add(opt);
		}

		return queries;
	}

	protected GUISearchOptions toGUIOptions(SearchOptions searchOptions) {
		GUISearchOptions op = new GUISearchOptions();
		op.setType(searchOptions.getType());
		op.setDescription(searchOptions.getDescription());
		op.setExpression(searchOptions.getExpression());
		op.setMaxHits(searchOptions.getMaxHits());
		op.setName(searchOptions.getName());
		op.setUserId(searchOptions.getUserId());
		op.setTopOperator(searchOptions.getTopOperator());

		if (searchOptions.getType() == SearchOptions.TYPE_FULLTEXT) {
			op.setTemplate(((FulltextSearchOptions) searchOptions).getTemplate());
			op.setDateFrom(((FulltextSearchOptions) searchOptions).getDateFrom());
			op.setDateTo(((FulltextSearchOptions) searchOptions).getDateTo());
			op.setSourceDateFrom(((FulltextSearchOptions) searchOptions).getSourceDateFrom());
			op.setSourceDateTo(((FulltextSearchOptions) searchOptions).getSourceDateTo());
			op.setCreationFrom(((FulltextSearchOptions) searchOptions).getCreationFrom());
			op.setCreationTo(((FulltextSearchOptions) searchOptions).getCreationTo());
			op.setExpressionLanguage(((FulltextSearchOptions) searchOptions).getExpressionLanguage());
			op.setFields(((FulltextSearchOptions) searchOptions).getFields());
			op.setFolder(((FulltextSearchOptions) searchOptions).getFolderId());
			op.setFormat(((FulltextSearchOptions) searchOptions).getFormat());
			op.setLanguage(((FulltextSearchOptions) searchOptions).getLanguage());
			op.setSearchInSubPath(((FulltextSearchOptions) searchOptions).isSearchInSubPath());
			op.setDepth(((FulltextSearchOptions) searchOptions).getDepth());
			op.setSizeMax(((FulltextSearchOptions) searchOptions).getSizeMax());
			op.setSizeMin(((FulltextSearchOptions) searchOptions).getSizeMin());
		} else if (searchOptions.getType() == SearchOptions.TYPE_FOLDERS) {
			List<GUICriterion> criteria = new ArrayList<GUICriterion>();

			if (((FolderSearchOptions) searchOptions).getName() != null) {
				GUICriterion criterion = new GUICriterion();
				criterion.setField("name");
				criterion.setStringValue(((FolderSearchOptions) searchOptions).getName());
				criteria.add(criterion);
			}
			if (((FolderSearchOptions) searchOptions).getDescription() != null) {
				GUICriterion criterion = new GUICriterion();
				criterion.setField("description");
				criterion.setStringValue(((FolderSearchOptions) searchOptions).getDescription());
				criteria.add(criterion);
			}

			op.setCriteria(criteria.toArray(new GUICriterion[0]));
			op.setCreationFrom(((FolderSearchOptions) searchOptions).getCreationFrom());
			op.setCreationTo(((FolderSearchOptions) searchOptions).getCreationTo());
			op.setDepth(((FolderSearchOptions) searchOptions).getDepth());
			op.setFolder(((FolderSearchOptions) searchOptions).getFolderId());
			op.setSearchInSubPath(((FolderSearchOptions) searchOptions).isSearchInSubPath());
		}

		if (!searchOptions.getFilterIds().isEmpty()) {
			op.setFilterIds(searchOptions.getFilterIds().toArray(new Long[0]));
		}

		return op;
	}

	protected SearchOptions toSearchOptions(GUISearchOptions options) {
		SearchOptions searchOptions = Search.newOptions(options.getType());
		searchOptions.setTopOperator(options.getTopOperator());
		searchOptions.setDescription(options.getDescription());
		searchOptions.setExpression(options.getExpression());
		searchOptions.setMaxHits(options.getMaxHits());
		searchOptions.setName(options.getName());
		searchOptions.setUserId(options.getUserId());

		if (options.getType() == SearchOptions.TYPE_FULLTEXT) {
			((FulltextSearchOptions) searchOptions).setTemplate(options.getTemplate());
			((FulltextSearchOptions) searchOptions).setDateFrom(options.getDateFrom());
			((FulltextSearchOptions) searchOptions).setDateTo(options.getDateTo());
			((FulltextSearchOptions) searchOptions).setSourceDateFrom(options.getSourceDateFrom());
			((FulltextSearchOptions) searchOptions).setSourceDateTo(options.getSourceDateTo());
			((FulltextSearchOptions) searchOptions).setCreationFrom(options.getCreationFrom());
			((FulltextSearchOptions) searchOptions).setCreationTo(options.getCreationTo());
			((FulltextSearchOptions) searchOptions).setExpressionLanguage(options.getExpressionLanguage());
			((FulltextSearchOptions) searchOptions).setFields(options.getFields());
			((FulltextSearchOptions) searchOptions).setFolderId(options.getFolder());
			((FulltextSearchOptions) searchOptions).setFormat(options.getFormat());
			((FulltextSearchOptions) searchOptions).setLanguage(options.getLanguage());
			((FulltextSearchOptions) searchOptions).setSearchInSubPath(options.isSearchInSubPath());
			((FulltextSearchOptions) searchOptions).setDepth(options.getDepth());
			((FulltextSearchOptions) searchOptions).setSizeMax(options.getSizeMax());
			((FulltextSearchOptions) searchOptions).setSizeMin(options.getSizeMin());
		} else if (options.getType() == SearchOptions.TYPE_FOLDERS) {
			for (GUICriterion criterion : options.getCriteria()) {
				if ("name".equals(criterion.getField())) {
					((FolderSearchOptions) searchOptions).setFolderName(criterion.getStringValue());
				}
				if ("description".equals(criterion.getField())) {
					((FolderSearchOptions) searchOptions).setFolderDescription(criterion.getStringValue());
				}
			}

			((FolderSearchOptions) searchOptions).setFolderId(options.getFolder());
			((FolderSearchOptions) searchOptions).setSearchInSubPath(options.isSearchInSubPath());
			((FolderSearchOptions) searchOptions).setCreationFrom(options.getCreationFrom());
			((FolderSearchOptions) searchOptions).setCreationTo(options.getCreationTo());
			((FolderSearchOptions) searchOptions).setDepth(options.getDepth());
		}

		if (options.getFilterIds() != null && options.getFilterIds().length > 0) {
			Set<Long> ids = new HashSet<Long>();
			for (Long id : options.getFilterIds()) {
				ids.add(id);
			}
			searchOptions.setFilterIds(ids);
		}

		return searchOptions;
	}
}