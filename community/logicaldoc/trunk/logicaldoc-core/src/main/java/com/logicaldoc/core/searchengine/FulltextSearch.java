package com.logicaldoc.core.searchengine;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.search.ChainedFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;

import com.logicaldoc.core.i18n.DateBean;
import com.logicaldoc.core.i18n.LanguageManager;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;

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

		/*
		 * Create the expression locale using the string representation. This is
		 * particularly important for the variants like pt_BR.
		 */
		Locale expressionLocale = LocaleUtils.toLocale(opt.getExpressionLanguage());
		Analyzer analyzer = LanguageManager.getInstance().getLanguage(expressionLocale).getAnalyzer();

		PerFieldAnalyzerWrapper wrapper = new PerFieldAnalyzerWrapper(analyzer);
		for (String field : getSearchedFields(opt)) {
			if (!LuceneDocument.FIELD_CONTENT.equals(field))
				wrapper.addAnalyzer(field + "_na", new KeywordAnalyzer());
		}
		search(wrapper);
	}

	private void search(Analyzer analyzer) throws Exception {
		FulltextSearchOptions opt = (FulltextSearchOptions) options;
		ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);

		String[] languages = null;
		if (StringUtils.isEmpty(opt.getLanguage())) {
			List<String> tmp = LanguageManager.getInstance().getLanguagesAsString();
			languages = (String[]) tmp.toArray(new String[0]);
		} else {
			languages = new String[] { opt.getLanguage() };
		}

		IndexReader[] readers = new IndexReader[languages.length];
		String indexPath = conf.getPropertyWithSubstitutions("conf.indexdir");

		if (!indexPath.endsWith(File.pathSeparator)) {
			indexPath += "/";
		}

		for (int i = 0; i < languages.length; i++) {
			String lang = languages[i];
			// searcher[i] = new IndexSearcher(Indexer.getIndexDirectory(lang));
			readers[i] = IndexReader.open(Indexer.getIndexDirectory(lang));
		}

		MultiReader reader = new MultiReader(readers);
		IndexSearcher indexSearcher = new IndexSearcher(reader);

		// Include even all not analized fields
		List<String> fields = getSearchedFields(opt);

		// multiSearcher.setSimilarity(new SquareSimilarity());

		MultiFieldQueryParser parser = new MultiFieldQueryParser(Indexer.LUCENE_VERSION, fields.toArray(new String[0]),
				analyzer);
		parser.setAllowLeadingWildcard(true);

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
			topDocs = indexSearcher.search(query, 1000);
		} else {
			ChainedFilter chainedFilter = new ChainedFilter(filters.toArray(new Filter[0]), ChainedFilter.AND);
			topDocs = indexSearcher.search(query, chainedFilter, 1000);
		}

		log.info("End of Full-text search");

		estimatedHitsNumber = topDocs.totalHits;

		/*
		 * We have to see what folders the user can access. But we need do
		 * perform this check only if the search is not restricted to one folder
		 * only.
		 */
		FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		List<Long> accessibleIds = new ArrayList<Long>();
		if (!searchInSingleFolder) {
			log.info("DB search");
			if (opt.getFolderId() == null)
				accessibleIds = fdao.findFolderIdByUserId(opt.getUserId());
			else {
				accessibleIds = fdao.findIdByUserId(opt.getUserId(), opt.getFolderId());
			}
			log.info("End of DB search");
		}
		if (opt.getFolderId() != null && fdao.isReadEnable(opt.getFolderId(), opt.getUserId()))
			accessibleIds.add(opt.getFolderId());

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

				Document doc = indexSearcher.doc(topDocs.scoreDocs[i].doc);

				// Skip a document if not in the filter set
				if (opt.getFilterIds() != null && !opt.getFilterIds().isEmpty()) {
					Long id = new Long(doc.get(LuceneDocument.FIELD_DOC_ID));
					if (!opt.getFilterIds().contains(id))
						continue;
				}

				String fid = doc.get(LuceneDocument.FIELD_FOLDER_ID);
				// Support the old 'path' attribute
				if (fid == null)
					fid = doc.get("path").substring(doc.get("path").lastIndexOf("/") + 1);

				long folderId = Long.parseLong(fid);

				// When user can see document with folderId then put it into
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
					result.setComment(doc.get(LuceneDocument.FIELD_COMMENT));

					fid = doc.get(LuceneDocument.FIELD_FOLDER_ID);
					// Support the old 'path' attribute
					if (fid == null)
						fid = doc.get("path").substring(doc.get("path").lastIndexOf("/") + 1);
					result.setPath(fid);
					result.setFolderId(Long.parseLong(fid));
					result.setSummary(summary);
					result.setScore(createScore(maxScore, score));

					if (isRelevant(result)) {
						hits.add(result);
					}
				}
			}
		}
	}

	/**
	 * Retrieves all searched fields, analyzed and not(ending with _na).
	 */
	private List<String> getSearchedFields(FulltextSearchOptions opt) {
		if (opt.getFields() == null) {
			String[] fields = new String[] { LuceneDocument.FIELD_CONTENT, LuceneDocument.FIELD_TAGS,
					LuceneDocument.FIELD_TITLE };
			opt.setFields(fields);
		}

		List<String> fields = new ArrayList<String>();
		for (String fld : opt.getFields()) {
			fields.add(fld);
			if (!LuceneDocument.FIELD_CONTENT.equals(fld))
				fields.add(fld + "_na");
		}
		return fields;
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
