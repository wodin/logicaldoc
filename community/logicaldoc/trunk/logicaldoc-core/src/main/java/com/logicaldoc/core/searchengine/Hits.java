package com.logicaldoc.core.searchengine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Suggestion;
import org.apache.solr.common.SolrDocument;

import com.logicaldoc.core.security.Folder;

/**
 * Iterator on the collection of hits, plus some statistical informations about
 * the query. Attention: each hit's data is lazy loaded.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.5
 */
public class Hits implements Iterator<Hit> {
	private Iterator<SolrDocument> internal;

	private QueryResponse rsp;

	public Hits() {
	}

	public Hits(QueryResponse rsp) {
		super();
		this.rsp = rsp;
		this.internal = rsp.getResults().iterator();
	}

	public long getEstimatedCount() {
		return rsp.getResults().getNumFound();
	}

	public long getElapsedTime() {
		return rsp.getElapsedTime();
	}

	public long getCount() {
		return rsp.getResults().size();
	}

	@Override
	public boolean hasNext() {
		return internal.hasNext();
	}

	@Override
	public Hit next() {
		SolrDocument doc = internal.next();
		Hit hit = toHit(doc);

		// Compose the summary as concatenation of snippets
		StringBuffer summary = new StringBuffer();
		Object id = doc.getFieldValue("id");
		if (rsp.getHighlighting() != null && rsp.getHighlighting().get(id) != null) {
			List<String> snippets = rsp.getHighlighting().get(id).get("content");
			if (snippets != null)
				for (String string : snippets) {
					if (summary.length() != 0)
						summary.append(" ... ");
					summary.append(string);
				}
		}

		Float score = (Float) doc.getFieldValue("score");
		if (score != null)
			hit.setScore(createScore(rsp.getResults().getMaxScore(), score));
		hit.setSummary(summary.toString());

		return hit;
	}

	@Override
	public void remove() {

	}

	public static Hit toHit(SolrDocument sdoc) {
		Hit hit = new Hit();
		hit.setId(Long.parseLong((String) sdoc.get(Fields.ID.getName())));

		if (sdoc.getFieldValue("score") != null) {
			Float score = (Float) sdoc.getFieldValue("score");
			hit.setScore((int) (score * 100));
		}

		if (sdoc.get(Fields.FOLDER_ID.getName()) != null) {
			Folder folder = new Folder();
			folder.setId((Long) sdoc.get(Fields.FOLDER_ID.getName()));
			hit.setFolder(folder);
		}

		if (sdoc.getFieldValue(Fields.LANGUAGE.getName()) != null) {
			hit.setLanguage(sdoc.getFieldValue(Fields.LANGUAGE.getName()).toString());
		}
		
		return hit;
	}

	/**
	 * Retrieve a token->suggestion map.
	 */
	public Map<String, String> getSuggestions() {
		Map<String, String> suggestions = new HashMap<String, String>();

		if (rsp.getSpellCheckResponse() != null) {
			List<Suggestion> list = rsp.getSpellCheckResponse().getSuggestions();
			for (Suggestion suggestion : list)
				suggestions.put(suggestion.getToken(), suggestion.getAlternatives().get(0));
		}

		return suggestions;
	}

	private static int createScore(float max, float score) {
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
