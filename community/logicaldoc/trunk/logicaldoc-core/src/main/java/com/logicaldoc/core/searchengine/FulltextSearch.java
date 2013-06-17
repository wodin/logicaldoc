package com.logicaldoc.core.searchengine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import com.ibm.icu.text.SimpleDateFormat;
import com.logicaldoc.core.document.DocumentTemplate;
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

		if (opt.getFields() == null) {
			String[] fields = new String[] { Fields.TITLE.toString(), Fields.TAGS.toString(), Fields.CONTENT.toString() };
			opt.setFields(fields);
		}

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
			filters.add(Fields.TEMPLATE_ID + ":" + opt.getTemplate());

		boolean searchInSingleFolder = (opt.getFolderId() != null && !opt.isSearchInSubPath());
		if (searchInSingleFolder)
			filters.add(Fields.FOLDER_ID + ":" + opt.getFolderId());

		if (StringUtils.isNotEmpty(opt.getLanguage()))
			filters.add(Fields.LANGUAGE + ":" + opt.getLanguage());

		if (opt.getSizeMin() != null) {
			filters.add(Fields.SIZE + ":[" + opt.getSizeMin() + " TO *]");
		}

		if (opt.getSizeMax() != null) {
			filters.add(Fields.SIZE + ":[* TO " + opt.getSizeMax() + "]");
		}

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		if (opt.getDateFrom() != null) {
			filters.add(Fields.DATE + ":[" + df.format(opt.getDateFrom()) + "T00:00:00Z TO *]");
		}
		if (opt.getDateTo() != null) {
			filters.add(Fields.DATE + ":[* TO " + df.format(opt.getDateTo()) + "T00:00:00Z]");
		}
		if (opt.getSourceDateFrom() != null) {
			filters.add(Fields.SOURCE_DATE + ":[" + df.format(opt.getSourceDateFrom()) + "T00:00:00Z TO *]");
		}
		if (opt.getSourceDateTo() != null) {
			filters.add(Fields.SOURCE_DATE + ":[* TO " + df.format(opt.getSourceDateTo()) + "T00:00:00Z]");
		}
		if (opt.getCreationFrom() != null) {
			filters.add(Fields.CREATION + ":[" + df.format(opt.getCreationFrom()) + "T00:00:00Z TO *]");
		}
		if (opt.getCreationTo() != null) {
			filters.add(Fields.CREATION + ":[* TO " + df.format(opt.getCreationTo()) + "T00:00:00Z]");
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
			log.debug("Folders search");
			accessibleFolderIds = fdao.findFolderIdByUserId(opt.getUserId(), opt.getFolderId(), true);
			log.debug("End of Folders search");
		}
		if (opt.getFolderId() != null && !accessibleFolderIds.contains(opt.getFolderId())
				&& fdao.isReadEnable(opt.getFolderId().longValue(), opt.getUserId()))
			accessibleFolderIds.add(opt.getFolderId());

		// Save here the binding between ID and Hit
		Map<Long, Hit> hitsMap = new HashMap<Long, Hit>();
		while (results != null && results.hasNext()) {
			if (hits.size() == opt.getMaxHits()) {
				// The maximum number of hits was reached for a quick query
				moreHitsPresent = true;
				break;
			}

			Hit hit = results.next();

			// Skip a document if not in the filter set
			if (opt.getFilterIds() != null && !opt.getFilterIds().isEmpty()) {
				if (!opt.getFilterIds().contains(hit.getId()))
					continue;
			}

			// When user can see document with folderId then put it into
			// result-collection.
			if ((accessibleFolderIds.isEmpty() && searchUser.isInGroup("admin"))
					|| accessibleFolderIds.contains(hit.getFolder().getId())) {
				hits.add(hit);
				hitsMap.put(hit.getId(), hit);
			}
		}

		if (hitsMap.isEmpty())
			return;

		estimatedHitsNumber = results.getEstimatedCount();

		log.debug("DB search");

		String hitsIdsStr = hitsMap.keySet().toString().replace('[', '(').replace(']', ')');
		StringBuffer richQuery = new StringBuffer();
		richQuery.append(" select A.id, A.customId, A.docRef, A.type, A.title, A.version, A.lastModified, ");
		richQuery.append(" A.date, A.publisher, A.creation, A.creator, A.fileSize, A.immutable, ");
		richQuery.append(" A.indexed, A.lockUserId, A.fileName, A.status, A.signed, A.type, A.sourceDate, ");
		richQuery.append(" A.sourceAuthor, A.rating, A.fileVersion, A.comment, A.workflowStatus, A.startPublishing, ");
		richQuery.append(" A.stopPublishing, A.published, A.folder.name, A.folder.id, A.source, A.sourceId, A.recipient, A.object, A.ld_coverage, B.id, B.name ");
		richQuery.append(" from Document as A left outer join A.template as B where A.deleted = 0 and A.id in ");
		richQuery.append(hitsIdsStr);

		Object[] values = null;
		if (!searchUser.isInGroup("admin") && !searchUser.isInGroup("publisher")) {
			/*
			 * Normal users don't see unpublished contents
			 */
			richQuery.append(" and A.published = 1 ");
			richQuery.append(" and A.startPublishing <= ? ");
			richQuery.append(" and ( A.stopPublishing is null or A.stopPublishing > ? )");

			Date now = new Date();
			values = new Object[] { now, now };
		}

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);

		List<Object> records = (List<Object>) docDao.findByQuery(richQuery.toString(), values, null);
		for (Object rec : records) {
			Object[] cols = (Object[]) rec;

			Hit hit = hitsMap.get((Long) cols[0]);
			hit.setCustomId((String) cols[1]);
			hit.setDocRef((Long) cols[2]);
			hit.setType((String) cols[3]);
			hit.setTitle((String) cols[4]);
			hit.setVersion((String) cols[5]);
			hit.setLastModified((Date) cols[6]);
			hit.setDate((Date) cols[7]);
			hit.setPublisher((String) cols[8]);
			hit.setCreation((Date) cols[9]);
			hit.setCreator((String) cols[10]);
			hit.setFileSize((Long) cols[11]);
			hit.setImmutable((Integer) cols[12]);
			hit.setIndexed((Integer) cols[13]);
			hit.setLockUserId((Long) cols[14]);
			hit.setFileName((String) cols[15]);
			hit.setStatus((Integer) cols[16]);
			hit.setSigned((Integer) cols[17]);
			hit.setType((String) cols[18]);
			hit.setSourceDate((Date) cols[19]);
			hit.setSourceAuthor((String) cols[20]);
			hit.setRating((Integer) cols[21]);
			hit.setFileVersion((String) cols[22]);
			hit.setComment((String) cols[23]);
			hit.setWorkflowStatus((String) cols[24]);
			hit.setStartPublishing((Date) cols[25]);
			hit.setStopPublishing((Date) cols[26]);
			hit.setPublished((Integer) cols[27]);
			hit.getFolder().setName((String) cols[28]);
			hit.getFolder().setId((Long) cols[29]);
			hit.setSource((String) cols[30]);
			hit.setSourceId((String) cols[31]);
			hit.setRecipient((String) cols[32]);
			hit.setObject((String) cols[33]);
			hit.setCoverage((String) cols[34]);
			
			if (cols[35] != null) {
				DocumentTemplate t = new DocumentTemplate();
				t.setId((Long) cols[35]);
				t.setName((String) cols[36]);
				hit.setTemplate(t);
				hit.setTemplateId(t.getId());
			}

			hit.setPublished(hit.isPublishing() ? 1 : 0);
		}

		log.debug("End of DB search");

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
}