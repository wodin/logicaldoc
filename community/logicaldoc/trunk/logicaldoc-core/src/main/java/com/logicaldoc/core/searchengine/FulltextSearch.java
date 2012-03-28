package com.logicaldoc.core.searchengine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import com.ibm.icu.text.SimpleDateFormat;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.util.Context;

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
		SearchEngine engine = (SearchEngine) Context.getInstance().getBean(SearchEngine.class);

		/*
		 * Prepare the query: the expression must be applied to all requested
		 * fields.
		 */
		StringBuffer query = new StringBuffer();
		for (String field : opt.getFields()) {
			if (query.length() > 0)
				query.append(" OR ");
			query.append("(" + field + ":" + opt.getExpression() + ")");
		}

		/*
		 * Prepare the filters
		 */
		ArrayList<String> filters = new ArrayList<String>();
		if (opt.getTemplate() != null)
			filters.add(Fields.TEMPLATE_ID.name() + ":" + opt.getTemplate());

		boolean searchInSingleFolder = (opt.getFolderId() != null && !opt.isSearchInSubPath());
		if (searchInSingleFolder)
			filters.add(Fields.FOLDER_ID.name() + ":" + opt.getFolderId());

		if (StringUtils.isNotEmpty(opt.getLanguage()))
			filters.add(Fields.LANGUAGE.name() + ":" + opt.getLanguage());

		if (opt.getSizeMin() != null) {
			filters.add(Fields.SIZE.name() + ":[" + opt.getSizeMin() + " TO *]");
		}

		if (opt.getSizeMax() != null) {
			filters.add(Fields.SIZE.name() + ":[* TO " + opt.getSizeMax() + "]");
		}

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		if (opt.getDateFrom() != null) {
			filters.add(Fields.DATE.name() + ":[" + df.format(opt.getDateFrom()) + "T00:00:00Z TO *]");
		}
		if (opt.getDateTo() != null) {
			filters.add(Fields.DATE.name() + ":[* TO " + df.format(opt.getDateTo()) + "T00:00:00Z]");
		}
		if (opt.getSourceDateFrom() != null) {
			filters.add(Fields.SOURCE_DATE.name() + ":[" + df.format(opt.getSourceDateFrom()) + "T00:00:00Z TO *]");
		}
		if (opt.getSourceDateTo() != null) {
			filters.add(Fields.SOURCE_DATE.name() + ":[* TO " + df.format(opt.getSourceDateTo()) + "T00:00:00Z]");
		}
		if (opt.getCreationFrom() != null) {
			filters.add(Fields.CREATION.name() + ":[" + df.format(opt.getCreationFrom()) + "T00:00:00Z TO *]");
		}
		if (opt.getCreationTo() != null) {
			filters.add(Fields.CREATION.name() + ":[* TO " + df.format(opt.getCreationTo()) + "T00:00:00Z]");
		}

		/*
		 * Launch the search
		 */
		log.debug("Full-text seach: " + query);
		Hits results = engine.search(query.toString(), filters.toArray(new String[0]), opt.getExpressionLanguage(),
				null);
		log.debug("End of Full-text search");

		/*
		 * We have to see what folders the user can access. But we need to
		 * perform this check only if the search is not restricted to one folder
		 * only.
		 */
		FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		Collection<Long> accessibleFolderIds = new TreeSet<Long>();
		if (!searchInSingleFolder) {
			log.info("DB search");
			if (opt.getFolderId() == null)
				accessibleFolderIds = fdao.findFolderIdByUserId(opt.getUserId());
			else {
				accessibleFolderIds = new HashSet<Long>();
				fdao.findTreeIds(opt.getFolderId(), opt.getUserId(), opt.getDepth(),
						(HashSet<Long>) accessibleFolderIds);
			}
			log.info("End of DB search");
		}
		if (opt.getFolderId() != null && fdao.isReadEnable(opt.getFolderId(), opt.getUserId()))
			accessibleFolderIds.add(opt.getFolderId());

		/*
		 * Prepare the list of published documents
		 */
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);

		Collection<Long> publishedIds = docDao.findPublishedIds(accessibleFolderIds);

		while (results.hasNext()) {
			if (hits.size() == opt.getMaxHits()) {
				// The maximum number of hits was reached for a quick query
				moreHitsPresent = true;
				break;
			}

			Hit hit = results.next();

			// Skip a document if not in the filter set
			if (opt.getFilterIds() != null && !opt.getFilterIds().isEmpty()) {
				if (!opt.getFilterIds().contains(hit.getDocId()))
					continue;
			}

			// When user can see document with folderId then put it into
			// result-collection.
			if (accessibleFolderIds.contains(hit.getFolderId())) {
				hit.setPublished(publishedIds.contains(hit.getDocId()) ? 1 : 0);

				if (isRelevant(hit))
					hits.add(hit);
			}
		}

		estimatedHitsNumber = results.getEstimatedCount();

		/*
		 * Check for suggestions
		 */
		Map<String, String> suggestions = (Map<String, String>) results.getSuggestions();
		if (!results.getSuggestions().isEmpty()) {
			suggestion = options.getExpression();
			for (String token : results.getSuggestions().keySet()) {
				suggestion = suggestion.replaceFirst(token, suggestions.get(token));
			}
		}
	}

	protected boolean isRelevant(Hit hit) {
		boolean relevant = true;

		if (hit.getPublished() != 1 && !searchUser.isInGroup("admin") && !searchUser.isInGroup("publisher"))
			relevant = false;

		return relevant;
	}
}