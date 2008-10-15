package com.logicaldoc.core.searchengine;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
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
import com.logicaldoc.core.searchengine.util.SquareSimilarity;
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

	private String language;

	private List<Result> results = new ArrayList<Result>();

	private int estimatedHitsNumber = 0;

	private long execTime = 0;

	public Search(SearchOptions opt, String language) {
		this.options = opt;
		this.language = language;
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
				List<String> iso639_2Languages = LanguageManager.getInstance().getISO639_2Languages();
				languages = (String[]) iso639_2Languages.toArray(new String[0]);
				options.setLanguages(languages);
			}

			Searcher[] searcher = new Searcher[languages.length];
			String indexPath = conf.getValue("indexdir");

			if (!indexPath.endsWith(File.pathSeparator)) {
				indexPath += "/";
			}

			for (int i = 0; i < languages.length; i++) {
				String lang = languages[i];
				String dir = new Locale(lang).getDisplayLanguage(Locale.ENGLISH).toLowerCase();
				searcher[i] = new IndexSearcher(indexPath + dir + "/");
			}

			MultiSearcher multiSearcher = new MultiSearcher(searcher);
			Analyzer analyzer = LuceneAnalyzerFactory.getAnalyzer(language);

			if (options.getFields() == null) {
				String[] fields = new String[] { "content", "keywords" };
				options.setFields(fields);
			}

			multiSearcher.setSimilarity(new SquareSimilarity());

			MultiFieldQueryParser parser = new MultiFieldQueryParser(options.getFields(), analyzer);

			Query query = parser.parse(options.getQueryStr());

			log.info("Full-text search");
			Hits hits = null;

			if (StringUtils.isEmpty(options.getPath())) {
				hits = multiSearcher.search(query);
			} else {
				Term prfixTerm = new Term("path", options.getPath());
				Query filterQuery = new TermQuery(prfixTerm);

				if (options.isSearchInSubPath()) {
					filterQuery = new PrefixQuery(prfixTerm);
				}

				QueryWrapperFilter qwf = new QueryWrapperFilter(filterQuery);
				hits = multiSearcher.search(query, qwf);
			}

			log.info("End of Full-text search");

			estimatedHitsNumber = hits.length();

			MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
			log.info("DB search");
			Set<Integer> accessibleMenues = mdao.findMenuIdByUserName(options.getUsername());
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
				Integer menuId = new Integer(doc.get("menuId"));

				// When user can see document with menuId then put it into
				// result-collection.
				if (accessibleMenues.contains(menuId)) {
					String size = doc.get("size");

					if (size.equals("0")) {
						size = "1";
					}

					String content = doc.get("content");

					TokenStream stream = analyzer.tokenStream("content", new StringReader(content));
					String summary = highlighter.getBestFragments(stream, content, maxNumFragmentsRequired,
							fragmentSeparator);

					if ((summary == null) || summary.equals("")) {
						summary = doc.get("summary");
					}

					Result result = new ResultImpl();
					result.setDocId(Long.parseLong(doc.get("docId")));
					result.setTitle(doc.get("name"));
					result.setSize(Integer.parseInt(size));
					result.setDate(DateBean.dateFromCompactString(doc.get("date")));
					result.setSourceDate(DateBean.dateFromCompactString(doc.get("sourceDate")));
					result.setType(doc.get("type"));
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