package com.logicaldoc.bm;

import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

import com.logicaldoc.core.searchengine.FulltextSearchOptions;
import com.logicaldoc.webservice.document.WSDocument;
import com.logicaldoc.webservice.search.WSSearchResult;

public class LoaderSearchFullText extends AbstractLoaderThread {

	private Random random;

	private List<String> queries;

	private String messageRecord;

	public LoaderSearchFullText(LoaderSession session, String loaderName, long testTotal, long testLoadDepth,
			List<String> queries) {
		super(session, loaderName, testTotal, testLoadDepth);
		this.queries = queries;
		random = new Random();
	}

	@Override
	protected String doLoading(LoaderServerProxy serverProxy, long rootFolder) throws Exception {

		String query = getRandomQuery();
		int results = performFullTextSearch(serverProxy, query);
		String msg = String.format("Found %d results for query %s", results, query);
		this.messageRecord = msg;
		return msg;
	}

	@Override
	public String getSummary() {
		return super.getSummary() + messageRecord;
	}

	private String getRandomQuery() {

		if (queries != null && queries.size() > 0) {
			int idx = random.nextInt(queries.size());
			return queries.get(idx);
		}
		return null;
	}

	private int performFullTextSearch(LoaderServerProxy serverProxy, String query) throws Exception {

		int results = 0;

		FulltextSearchOptions options = new FulltextSearchOptions();

		String lang = session.getLanguage();
		if (StringUtils.isEmpty(lang))
			lang = "en";

		// This is the language of the document
		options.setLanguage(lang);
		options.setExpression(query);

		// This is the language of the query
		options.setExpressionLanguage(lang);

		// This is required and it is the maximum number of results that we want
		// for this search
		options.setMaxHits(50);

		WSSearchResult sr = serverProxy.searchClient.find(serverProxy.ticket, options);

		// System.out.println("HITS: " + sr.getTotalHits());
		// System.out.println("search completed in ms: " + sr.getTime());

		if (sr.getHits() != null) {
			for (WSDocument res : sr.getHits()) {
				// System.out.println("title: " + res.getTitle());
				// System.out.println("res.id: " + res.getDocId());
				// System.out.println("res.summary: " + res.getSummary());
				// System.out.println("res.size: " + res.getSize());
				// System.out.println("res.date: " + res.getDate());
				// System.out.println("res.type: " + res.getType());
				// System.out.println("res.score: " + res.getScore());
				results++;
			}
		}

		return results;
	}

}
