package com.logicaldoc.core.searchengine;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.misc.ChainedFilter;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;

import com.logicaldoc.core.i18n.DateBean;
import com.logicaldoc.core.i18n.LanguageManager;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.PropertiesBean;

/**
 * Search specialization for the Full text search.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class FulltextSearch extends Search {

	protected FulltextSearch() {
	}

	@Override
	public void internalSearch() throws Exception {
		FulltextSearchOptions opt = (FulltextSearchOptions) options;
		PropertiesBean conf = (PropertiesBean) Context.getInstance().getBean("ContextProperties");

		String[] languages = null;
		if (StringUtils.isEmpty(opt.getLanguage())) {
			List<String> tmp = LanguageManager.getInstance().getLanguagesAsString();
			languages = (String[]) tmp.toArray(new String[0]);
		} else {
			languages = new String[] { opt.getLanguage() };
		}

		Searcher[] searcher = new Searcher[languages.length];
		String indexPath = conf.getPropertyWithSubstitutions("conf.indexdir");

		if (!indexPath.endsWith(File.pathSeparator)) {
			indexPath += "/";
		}

		for (int i = 0; i < languages.length; i++) {
			String lang = languages[i];
			searcher[i] = new IndexSearcher(Indexer.getIndexDirectory(lang));
		}

		MultiSearcher multiSearcher = new MultiSearcher(searcher);
		
		// Creo il locale partendo dalla rappresentazione in formato stringa dello stesso
		// importante per i casi delle varianti di linguaggio ex: pt_BR
		Locale expressionLocale = LocaleUtils.toLocale(opt.getExpressionLanguage());
		
        Analyzer analyzer = LanguageManager.getInstance().getLanguage(expressionLocale).getAnalyzer();

		if (opt.getFields() == null) {
			String[] fields = new String[] { LuceneDocument.FIELD_CONTENT, LuceneDocument.FIELD_TAGS };
			opt.setFields(fields);
		}

		multiSearcher.setSimilarity(new SquareSimilarity());

		MultiFieldQueryParser parser = new MultiFieldQueryParser(Indexer.LUCENE_VERSION, opt.getFields(), analyzer);

		Query query = parser.parse(opt.getExpression());

		TopDocs topDocs = null;

		ArrayList<Filter> filters = new ArrayList<Filter>();

		if (opt.getTemplate() != null) {
			// Prepare filter for template field
			Term templateTerm = new Term(LuceneDocument.FIELD_TEMPLATE_ID, Long.toString(opt.getTemplate()));
			Query templateQuery = new TermQuery(templateTerm);
			filters.add(new QueryWrapperFilter(templateQuery));
		}

		/*
		 * We need to check the permissions against the DB only if the user
		 * performs a search on the whole archive or if he wants to search in a
		 * tree of folders.
		 */
		boolean searchInSingleFolder = (opt.getFolderId() != null && !opt.isSearchInSubPath());

		if (searchInSingleFolder) {
			// Not searching in sub-folders so only search for a match on
			// the folderId
			Term folderTerm = new Term(LuceneDocument.FIELD_FOLDER_ID, Long.toString(opt.getFolderId()));
			Query folderQuery = new TermQuery(folderTerm);
			filters.add(new QueryWrapperFilter(folderQuery));
		}

		log.info("Fulltext query: " + query.toString());
		
		if (filters.isEmpty()) {
			topDocs = multiSearcher.search(query, 1000);
		} else {
			ChainedFilter chainedFilter = new ChainedFilter(filters.toArray(new Filter[0]), ChainedFilter.AND);
			topDocs = multiSearcher.search(query, chainedFilter, 1000);
		}

		log.info("End of Full-text search");

		estimatedHitsNumber = topDocs.totalHits;

		/*
		 * We have to see what folders the user can access. But we need do
		 * perform this check only if the search is not restricted to one folder
		 * only.
		 */
		List<Long> accessibleIds = new ArrayList<Long>();
		if (opt.getFolderId() != null)
			accessibleIds.add(opt.getFolderId());
		if (searchInSingleFolder) {
			accessibleIds.add(opt.getFolderId());
		} else {
			log.info("DB search");
			MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
			if (opt.getFolderId() == null)
				accessibleIds = mdao.findMenuIdByUserId(opt.getUserId());
			else {
				accessibleIds = mdao.findIdByUserId(opt.getUserId(), opt.getFolderId(), Menu.MENUTYPE_DIRECTORY);
			}
			log.info("End of DB search");
		}

		int maxNumFragmentsRequired = 4;
		String fragmentSeparator = "&nbsp;...&nbsp;";

		Highlighter highlighter = new Highlighter(new SimpleHTMLFormatter("<font style='background-color:#FFFF00'>",
				"</font>"), new QueryScorer(query));

		if (topDocs.scoreDocs.length > 0) {
			float maxScore = topDocs.scoreDocs[0].score;
			for (int i = 0; i < topDocs.totalHits; i++) {
				float score = topDocs.scoreDocs[i].score;
				if (hits.size() == opt.getMaxHits()) {
					// The maximum number of hits was reached for a quick query
					moreHitsPresent = true;
					break;
				}

				Document doc = multiSearcher.doc(topDocs.scoreDocs[i].doc);
				String path = doc.get(LuceneDocument.FIELD_FOLDER_ID);
				long folderId = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));

				// When user can see document with menuId then put it into
				// result-collection.
				if (accessibleIds.contains(folderId)) {
					String size = doc.get(LuceneDocument.FIELD_SIZE);

					if (size.equals("0")) {
						size = "1";
					}

					String content = doc.get(LuceneDocument.FIELD_CONTENT);

					TokenStream stream = analyzer.tokenStream(LuceneDocument.FIELD_CONTENT, new StringReader(content));
					String summary = highlighter.getBestFragments(stream, content, maxNumFragmentsRequired,
							fragmentSeparator);

					if ((summary == null) || summary.equals("")) {
						// If no fragments are available, use an extract from
						// the content
						content = doc.get(LuceneDocument.FIELD_CONTENT);
						int summarysize = Math.min(content.length(), 500);
						summary = content.substring(0, summarysize);
					}

					Hit result = new HitImpl();
					result.setDocId(Long.parseLong(doc.get(LuceneDocument.FIELD_DOC_ID)));
					result.setTitle(doc.get(LuceneDocument.FIELD_TITLE));
					result.setSize(Long.parseLong(size));
					result.setDate(DateBean.dateFromCompactString(doc.get(LuceneDocument.FIELD_DATE)));
					result.setSourceDate(DateBean.dateFromCompactString(doc.get(LuceneDocument.FIELD_SOURCE_DATE)));
					result.setCreation(DateBean.dateFromCompactString(doc.get(LuceneDocument.FIELD_CREATION)));
					result.setType(doc.get(LuceneDocument.FIELD_TYPE));
					result.setCustomId(doc.get(LuceneDocument.FIELD_CUSTOM_ID));
					result.setSource(doc.get(LuceneDocument.FIELD_SOURCE));
					result.setPath(doc.get(LuceneDocument.FIELD_FOLDER_ID));
					result.setFolderId(Long.parseLong(doc.get(LuceneDocument.FIELD_FOLDER_ID)));
					result.setSummary(summary);
					result.setScore(createScore(maxScore, score));

					if (isRelevant(result)) {
						hits.add(result);
					}
				}
			}
		}
	}

	protected boolean isRelevant(Hit hit) {
		boolean result = true;

		FulltextSearchOptions opt = (FulltextSearchOptions) options;

		if (!StringUtils.isEmpty(opt.getFormat()) && !opt.getFormat().equals("all")) {
			if (!hit.getType().toLowerCase().equals(opt.getFormat())) {
				result = false;
			}
		}

		if (opt.getSizeMin() != null && hit.getSize() < opt.getSizeMin().longValue())
			result = false;

		if (opt.getSizeMax() != null && hit.getSize() > opt.getSizeMax().longValue())
			result = false;

		if (opt.getCreationFrom() != null) {
			if (hit.getCreation().before(opt.getCreationFrom()))
				result = false;
		}

		if (opt.getCreationTo() != null) {
			if (hit.getCreation().after(opt.getDateTo()))
				result = false;
		}

		if (opt.getDateTo() != null) {
			if (hit.getDate().after(opt.getDateTo()))
				result = false;
		}

		if (opt.getDateFrom() != null && hit.getDate() != null) {
			if (hit.getDate().before(opt.getDateFrom()))
				result = false;
		}

		if (opt.getSourceDateFrom() != null && hit.getSourceDate() != null) {
			if (hit.getSourceDate().before(opt.getSourceDateFrom()))
				result = false;
		}

		if (opt.getSourceDateTo() != null && hit.getSourceDate() != null) {
			if (hit.getSourceDate().after(opt.getSourceDateTo()))
				result = false;
		}

		return result;
	}

	protected static int createScore(float max, float score) {
		float normalized = 1;
		if (score != max) {
			normalized = score / max;
		}

		float temp = normalized * 100;
		int tgreen = Math.round(temp);

		if (tgreen < 1) {
			tgreen = 1;
		}

		return new Integer(tgreen);
	}
}
