package com.logicaldoc.core.searchengine;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.misc.ChainedFilter;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;

import com.logicaldoc.core.i18n.DateBean;
import com.logicaldoc.core.i18n.LanguageManager;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.SettingsConfig;

/**
 * This class executes a search against the full-text indexes
 * 
 * @author Michael Scholz
 */
public class Search {
	protected static Log log = LogFactory.getLog(Search.class);

	private int maxHits = 40;

	private boolean moreHitsPresent = false;

	private SearchOptions options;

	private List<Result> results = new ArrayList<Result>();

	private int estimatedHitsNumber = 0;

	private long execTime = 0;

	public Search(SearchOptions opt) {
		this.options = opt;
	}

	public List<Result> search() {
		log.info("Launch search");
		Date start = new Date();

		results.clear();
		moreHitsPresent = false;
		SettingsConfig conf = (SettingsConfig) Context.getInstance().getBean(SettingsConfig.class);

		try {
			String[] languages = options.getLanguages();
			if ((languages == null) || (languages.length == 0)) {
				List<String> tmp = LanguageManager.getInstance().getLanguagesAsString();
				languages = (String[]) tmp.toArray(new String[0]);
				options.setLanguages(languages);
			}

			Searcher[] searcher = new Searcher[languages.length];
			String indexPath = conf.getValue("indexdir");

			if (!indexPath.endsWith(File.pathSeparator)) {
				indexPath += "/";
			}

			for (int i = 0; i < languages.length; i++) {
				String lang = languages[i];
				searcher[i] = new IndexSearcher(indexPath + lang + "/");
			}

			MultiSearcher multiSearcher = new MultiSearcher(searcher);
			Analyzer analyzer = LanguageManager.getInstance().getLanguage(new Locale(options.getQueryLanguage()))
					.getAnalyzer();

			if (options.getFields() == null) {
				String[] fields = new String[] { LuceneDocument.FIELD_CONTENT, LuceneDocument.FIELD_TAGS };
				options.setFields(fields);
			}

			multiSearcher.setSimilarity(new SquareSimilarity());

			MultiFieldQueryParser parser = new MultiFieldQueryParser(options.getFields(), analyzer);

			Query query = parser.parse(options.getQueryStr());

			log.info("Full-text search");
			Hits hits = null;

			ArrayList<Filter> filters = new ArrayList<Filter>();

			if (options.getTemplate() != null) {
				// Prepare filter for template field
				Term templateTerm = new Term(LuceneDocument.FIELD_TEMPLATE_ID, Long.toString(options.getTemplate()));
				Query templateQuery = new TermQuery(templateTerm);
				filters.add(new QueryWrapperFilter(templateQuery));
			}

			if (options.getPath() != null) {
				// Prepare filter for path field
				Term pathTerm = new Term(LuceneDocument.FIELD_PATH, options.getPath());
				Query pathQuery = new TermQuery(pathTerm);
				if (options.isSearchInSubPath()) {
					pathQuery = new PrefixQuery(pathTerm);
				}
				filters.add(new QueryWrapperFilter(pathQuery));
			}

			if (filters.isEmpty()) {
				hits = multiSearcher.search(query);
			} else {
				ChainedFilter chainedFilter = new ChainedFilter(filters.toArray(new Filter[0]), ChainedFilter.AND);
				hits = multiSearcher.search(query, chainedFilter);
			}

			log.info("End of Full-text search");

			estimatedHitsNumber = hits.length();

			MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
			log.info("DB search");
			Set<Long> accessibleMenus = mdao.findMenuIdByUserId(options.getUserId());

			log.info("End of DB search");

			int maxNumFragmentsRequired = 4;
			String fragmentSeparator = "&nbsp;...&nbsp;";

			Highlighter highlighter = new Highlighter(new SimpleHTMLFormatter(
					"<font style='background-color:#FFFF00'>", "</font>"), new QueryScorer(query));

			for (int i = 0; i < hits.length(); i++) {
				if (results.size() == maxHits) {
					// The maximum number of hits was reached for a quick query
					moreHitsPresent = true;
					break;
				}

				Document doc = hits.doc(i);
				String path = doc.get(LuceneDocument.FIELD_PATH);
				long folderId = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));

				// When user can see document with menuId then put it into
				// result-collection.
				if (accessibleMenus.contains(folderId)) {
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

					Result result = new ResultImpl();
					result.setDocId(Long.parseLong(doc.get(LuceneDocument.FIELD_DOC_ID)));
					result.setTitle(doc.get(LuceneDocument.FIELD_TITLE));
					result.setSize(Long.parseLong(size));
					result.setDate(DateBean.dateFromCompactString(doc.get(LuceneDocument.FIELD_DATE)));
					result.setSourceDate(DateBean.dateFromCompactString(doc.get(LuceneDocument.FIELD_SOURCE_DATE)));
					result.setCreation(DateBean.dateFromCompactString(doc.get(LuceneDocument.FIELD_CREATION)));
					result.setType(doc.get(LuceneDocument.FIELD_TYPE));
					result.setCustomId(doc.get(LuceneDocument.FIELD_CUSTOM_ID));
					result.setSource(doc.get(LuceneDocument.FIELD_SOURCE));
					result.setPath(doc.get(LuceneDocument.FIELD_PATH));
					result.setSummary(summary);
					result.createScore(hits.score(i));

					if (result.isRelevant(options)) {
						results.add(result);
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		Date finish = new Date();
		execTime = finish.getTime() - start.getTime();
		log.info("Search finished in " + execTime + "ms");
		return results;
	}

	public List<Result> getResults() {
		return results;
	}

	public boolean isMoreHitsPresent() {
		return moreHitsPresent;
	}

	public void setMoreHitsPresent(boolean moreHitsPresent) {
		this.moreHitsPresent = moreHitsPresent;
	}

	public int getMaxHits() {
		return maxHits;
	}

	public void setMaxHits(int maxHits) {
		this.maxHits = maxHits;
	}

	public int getEstimatedHitsNumber() {
		return estimatedHitsNumber;
	}

	/**
	 * Query execution time in milliseconds
	 */
	public long getExecTime() {
		return execTime;
	}

	public SearchOptions getOptions() {
		return options;
	}

	public void setOptions(SearchOptions options) {
		this.options = options;
	}
}