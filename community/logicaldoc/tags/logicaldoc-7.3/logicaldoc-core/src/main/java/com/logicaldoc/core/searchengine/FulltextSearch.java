package com.logicaldoc.core.searchengine;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import com.ibm.icu.text.SimpleDateFormat;
import com.logicaldoc.core.document.AbstractDocument;
import com.logicaldoc.core.document.DocumentTemplate;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.Tenant;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.core.security.dao.TenantDAO;
import com.logicaldoc.util.Context;

/**
 * Search specialization for the Full text search.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class FulltextSearch extends Search {

	public class HitMapper implements RowMapper<Hit> {

		private Map<Long, Hit> hitsMap;

		public HitMapper(Map<Long, Hit> hitsMap) {
			super();
			this.hitsMap = hitsMap;
		}

		public Hit mapRow(ResultSet rs, int rowNum) throws SQLException {
			Hit hit = hitsMap.get(rs.getLong(1));
			if (hit == null) {
				// This is an alias
				hit = new Hit();
				hitsMap.put(rs.getLong(1), hit);
			}

			hit.setId(rs.getLong(1));
			hit.setCustomId(rs.getString(2));
			if (rs.getLong(3) != 0L) {
				hit.setDocRef(rs.getLong(3));
				Hit master = hitsMap.get(rs.getLong(3));
				if (master != null) {
					hit.setContent(master.getContent());
					hit.setSummary(master.getSummary());
				}
				hit.setDocRefType(rs.getString(40));
			}
			hit.setType(rs.getString(4));
			hit.setTitle(rs.getString(5));
			hit.setVersion(rs.getString(6));
			hit.setLastModified(rs.getTimestamp(7));
			hit.setDate(rs.getTimestamp(8));
			hit.setPublisher(rs.getString(9));
			hit.setCreation(rs.getTimestamp(10));
			hit.setCreator(rs.getString(11));
			hit.setFileSize(rs.getLong(12));
			hit.setImmutable(rs.getInt(13));
			hit.setIndexed(rs.getInt(14));
			hit.setLockUserId(rs.getLong(15));
			hit.setFileName(rs.getString(16));
			hit.setStatus(rs.getInt(17));
			hit.setSigned(rs.getInt(18));
			hit.setType(rs.getString(19));
			hit.setSourceDate(rs.getTimestamp(20));
			hit.setSourceAuthor(rs.getString(21));
			hit.setRating(rs.getInt(22));
			hit.setFileVersion(rs.getString(23));
			hit.setComment(rs.getString(24));
			hit.setWorkflowStatus(rs.getString(25));
			hit.setStartPublishing(rs.getTimestamp(26));
			hit.setStopPublishing(rs.getTimestamp(27));
			hit.setPublished(rs.getInt(28));
			hit.setSource(rs.getString(29));
			hit.setSourceId(rs.getString(30));
			hit.setRecipient(rs.getString(31));
			hit.setObject(rs.getString(32));
			hit.setCoverage(rs.getString(33));

			Folder folder = new Folder();
			folder.setId(rs.getLong(35));
			folder.setName(rs.getString(34));
			hit.setFolder(folder);

			if (rs.getLong(37) != 0L) {
				DocumentTemplate t = new DocumentTemplate();
				t.setId(rs.getLong(37));
				t.setName(rs.getString(38));
				hit.setTemplate(t);
				hit.setTemplateId(t.getId());
			}

			hit.setTenantId(rs.getLong(39));
			hit.setStamped(rs.getInt(41));

			return hit;
		}
	};

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
			query.append(field + ":(" + opt.getExpression() + ")");
		}

		/*
		 * Prepare the filters
		 */
		ArrayList<String> filters = new ArrayList<String>();

		TenantDAO tdao = (TenantDAO) Context.getInstance().getBean(TenantDAO.class);
		long tenantId = Tenant.DEFAULT_ID;
		if (opt.getTenantId() != null)
			tenantId = opt.getTenantId().longValue();
		else if (searchUser != null)
			tenantId = searchUser.getTenantId();

		if (searchUser != null && tdao.count() > 1)
			filters.add(Fields.TENANT_ID + ":" + tenantId);

		if (opt.getTemplate() != null)
			filters.add(Fields.TEMPLATE_ID + ":" + opt.getTemplate());

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
		 * We have to see what folders the user can access. But we need to
		 * perform this check only if the search is not restricted to one folder
		 * only.
		 */
		FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		Collection<Long> accessibleFolderIds = new TreeSet<Long>();
		boolean searchInSingleFolder = (opt.getFolderId() != null && !opt.isSearchInSubPath());
		if (!searchInSingleFolder) {
			log.debug("Folders search");
			accessibleFolderIds = fdao.findFolderIdByUserId(opt.getUserId(), opt.getFolderId(), true);
			log.debug("End of Folders search");
		}
		if (opt.getFolderId() != null && !accessibleFolderIds.contains(opt.getFolderId())
				&& fdao.isReadEnable(opt.getFolderId().longValue(), opt.getUserId()))
			accessibleFolderIds.add(opt.getFolderId());

		if (!accessibleFolderIds.isEmpty() && opt.getFolderId() != null) {
			StringBuffer folderFilter = new StringBuffer();
			for (Long id : accessibleFolderIds) {
				if (folderFilter.length() > 0)
					folderFilter.append(" or ");
				folderFilter.append(Fields.FOLDER_ID + ":" + id);
			}
			filters.add(folderFilter.toString());
		}

		/*
		 * Launch the search
		 */
		log.debug("Full-text seach: " + query);
		Hits results = engine.search(query.toString(), filters.toArray(new String[0]), opt.getExpressionLanguage(),
				null);
		log.debug("End of Full-text search");
		log.debug("Fulltext hits count: " + results.getCount());

		// Save here the binding between ID and Hit
		Map<Long, Hit> hitsMap = new HashMap<Long, Hit>();
		while (results != null && results.hasNext()) {
			Hit hit = results.next();

			// Skip a document if not in the filter set
			if (opt.getFilterIds() != null && !opt.getFilterIds().isEmpty()) {
				if (!opt.getFilterIds().contains(hit.getId()))
					continue;
			}
			hitsMap.put(hit.getId(), hit);
		}

		if (hitsMap.isEmpty())
			return;

		estimatedHitsNumber = results.getEstimatedCount();

		log.debug("DB search");

		String hitsIdsStr = hitsMap.keySet().toString().replace('[', '(').replace(']', ')');

		StringBuffer richQuery = new StringBuffer();
		// Find real documents
		richQuery = new StringBuffer(
				"select A.ld_id, A.ld_customid, A.ld_docref, A.ld_type, A.ld_title, A.ld_version, A.ld_lastmodified, ");
		richQuery.append(" A.ld_date, A.ld_publisher, A.ld_creation, A.ld_creator, A.ld_filesize, A.ld_immutable, ");
		richQuery
				.append(" A.ld_indexed, A.ld_lockuserid, A.ld_filename, A.ld_status, A.ld_signed, A.ld_type, A.ld_sourcedate, ");
		richQuery
				.append(" A.ld_sourceauthor, A.ld_rating, A.ld_fileversion, A.ld_comment, A.ld_workflowstatus, A.ld_startpublishing, ");
		richQuery
				.append(" A.ld_stoppublishing, A.ld_published, A.ld_source, A.ld_sourceid, A.ld_recipient, A.ld_object, A.ld_coverage, ");
		richQuery
				.append(" FOLD.ld_name, A.ld_folderid, A.ld_tgs tags, A.ld_templateid, C.ld_name, A.ld_tenantid, A.ld_docreftype, ");
		richQuery.append(" A.ld_stamped ");
		richQuery.append(" from ld_document A ");
		richQuery.append(" join ld_folder FOLD on A.ld_folderid=FOLD.ld_id ");
		richQuery.append(" left outer join ld_template C on A.ld_templateid=C.ld_id ");
		richQuery.append(" where A.ld_deleted=0 and A.ld_nature=" + AbstractDocument.NATURE_DOC
				+ " and A.ld_folderid=FOLD.ld_id  ");
		richQuery.append(" and A.ld_tenantid = " + tenantId);
		// For normal users we have to exclude not published documents
		if (searchUser != null && !searchUser.isInGroup("admin") && !searchUser.isInGroup("publisher")) {
			richQuery.append(" and A.ld_published = 1 ");
			richQuery.append(" and A.ld_startpublishing <= CURRENT_TIMESTAMP ");
			richQuery.append(" and ( A.ld_stoppublishing is null or A.ld_stoppublishing > CURRENT_TIMESTAMP )");
		}
		richQuery.append("  and A.ld_docref is null ");
		richQuery.append("  and A.ld_id in ");
		richQuery.append(hitsIdsStr);

		// Append all aliases
		richQuery
				.append(" UNION select A.ld_id, REF.ld_customid, A.ld_docref, REF.ld_type, REF.ld_title, REF.ld_version, REF.ld_lastmodified, ");
		richQuery
				.append(" REF.ld_date, REF.ld_publisher, REF.ld_creation, REF.ld_creator, REF.ld_filesize, REF.ld_immutable, ");
		richQuery
				.append(" REF.ld_indexed, REF.ld_lockuserid, REF.ld_filename, REF.ld_status, REF.ld_signed, REF.ld_type, REF.ld_sourcedate, ");
		richQuery
				.append(" REF.ld_sourceauthor, REF.ld_rating, REF.ld_fileversion, A.ld_comment, REF.ld_workflowstatus, REF.ld_startpublishing, ");
		richQuery
				.append(" A.ld_stoppublishing, A.ld_published, REF.ld_source, REF.ld_sourceid, REF.ld_recipient, REF.ld_object, REF.ld_coverage, ");
		richQuery
				.append(" FOLD.ld_name, A.ld_folderid, A.ld_tgs tags, REF.ld_templateid, C.ld_name, A.ld_tenantid, A.ld_docreftype, ");
		richQuery.append(" REF.ld_stamped ");
		richQuery.append(" from ld_document A  ");
		richQuery.append(" join ld_folder FOLD on A.ld_folderid=FOLD.ld_id ");
		richQuery.append(" join ld_document REF on A.ld_docref=REF.ld_id ");
		richQuery.append(" left outer join ld_template C on REF.ld_templateid=C.ld_id ");
		richQuery.append(" where A.ld_deleted=0 and A.ld_nature=" + AbstractDocument.NATURE_DOC
				+ " and A.ld_folderid=FOLD.ld_id ");
		richQuery.append(" and A.ld_tenantid = " + tenantId);
		// For normal users we have to exclude not published documents
		if (searchUser != null && !searchUser.isInGroup("admin") && !searchUser.isInGroup("publisher")) {
			richQuery.append(" and REF.ld_published = 1 ");
			richQuery.append(" and REF.ld_startpublishing <= CURRENT_TIMESTAMP ");
			richQuery.append(" and ( REF.ld_stoppublishing is null or REF.ld_stoppublishing > CURRENT_TIMESTAMP )");
		}
		richQuery.append("  and A.ld_docref is not null and REF.ld_deleted=0 and A.ld_docref = REF.ld_id ");
		richQuery.append("  and A.ld_id in ");
		richQuery.append(hitsIdsStr);

		log.debug("Execute query\n" + richQuery.toString());

		DocumentDAO dao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		dao.query(richQuery.toString(), null, new HitMapper(hitsMap), null);

		// Now sort the hits by score desc
		List<Hit> sortedHitsList = new ArrayList<Hit>(hitsMap.values());
		Collections.sort(sortedHitsList);

		// Populate the hits list discarding unexisting documents
		Iterator<Hit> iter = sortedHitsList.iterator();
		while (iter.hasNext()) {
			if (options.getMaxHits() > 0 && hits.size() >= options.getMaxHits()) {
				// The maximum number of hits was reached
				moreHitsPresent = true;
				break;
			}
			Hit hit = iter.next();

			if (StringUtils.isEmpty(hit.getTitle()) || StringUtils.isEmpty(hit.getFileName()))
				continue;

			if ((searchUser.isInGroup("admin") && opt.getFolderId() == null)
					|| (accessibleFolderIds != null && accessibleFolderIds.contains(hit.getFolder().getId())))
				hits.add(hit);
		}

		/*
		 * Check for suggestions
		 */
		Map<String, String> suggestions = (Map<String, String>) results.getSuggestions();
		if (!results.getSuggestions().isEmpty()) {
			suggestion = options.getExpression();
			for (String token : results.getSuggestions().keySet())
				suggestion = suggestion.replaceFirst(token, suggestions.get(token));
		}
	}
}