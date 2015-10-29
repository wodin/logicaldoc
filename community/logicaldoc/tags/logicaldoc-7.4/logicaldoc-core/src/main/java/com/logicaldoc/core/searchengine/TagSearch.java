package com.logicaldoc.core.searchengine;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import com.logicaldoc.core.document.AbstractDocument;
import com.logicaldoc.core.document.DocumentTemplate;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.Tenant;
import com.logicaldoc.util.Context;

/**
 * Search specialization for the Tag search.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class TagSearch extends Search {

	protected TagSearch() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void internalSearch() throws Exception {
		prepareExpression();

		DocumentDAO dao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		hits.addAll((List<Hit>) dao.query(options.getExpression(), null, new HitMapper(), options.getMaxHits()));

		moreHitsPresent = (hits.size() >= options.getMaxHits());
		if (moreHitsPresent)
			estimatedHitsNumber = hits.size() + 1;
		else
			estimatedHitsNumber = hits.size();
	}

	/**
	 * Utility method that prepare the query expression.
	 */
	private void prepareExpression() {

		// Find all real documents
		StringBuffer query = new StringBuffer(
				"select A.ld_id, A.ld_customid, A.ld_docref, A.ld_type, A.ld_title, A.ld_version, A.ld_lastmodified, ");
		query.append(" A.ld_date, A.ld_publisher, A.ld_creation, A.ld_creator, A.ld_filesize, A.ld_immutable, ");
		query.append(" A.ld_indexed, A.ld_lockuserid, A.ld_filename, A.ld_status, A.ld_signed, A.ld_type, A.ld_sourcedate, ");
		query.append(" A.ld_sourceauthor, A.ld_rating, A.ld_fileversion, A.ld_comment, A.ld_workflowstatus, A.ld_startpublishing, ");
		query.append(" A.ld_stoppublishing, A.ld_published, A.ld_source, A.ld_sourceid, A.ld_recipient, A.ld_object, A.ld_coverage, ");
		query.append(" B.ld_name, A.ld_folderid, A.ld_templateid, C.ld_name, A.ld_tenantid, A.ld_docreftype, A.ld_stamped ");
		query.append(" from ld_document A ");
		query.append(" join ld_folder B on A.ld_folderid=B.ld_id ");
		query.append(" left outer join ld_template C on A.ld_templateid=C.ld_id ");

		appendWhereClause(false, query);

		// Append all shortcuts
		query.append(" UNION select A.ld_id, REF.ld_customid, A.ld_docref, REF.ld_type, REF.ld_title, REF.ld_version, REF.ld_lastmodified, ");
		query.append(" REF.ld_date, REF.ld_publisher, REF.ld_creation, REF.ld_creator, REF.ld_filesize, REF.ld_immutable, ");
		query.append(" REF.ld_indexed, REF.ld_lockuserid, REF.ld_filename, REF.ld_status, REF.ld_signed, REF.ld_type, REF.ld_sourcedate, ");
		query.append(" REF.ld_sourceauthor, REF.ld_rating, REF.ld_fileversion, REF.ld_comment, REF.ld_workflowstatus, A.ld_startpublishing, ");
		query.append(" A.ld_stoppublishing, A.ld_published, REF.ld_source, REF.ld_sourceid, REF.ld_recipient, REF.ld_object, REF.ld_coverage, ");
		query.append(" B.ld_name, A.ld_folderid, REF.ld_templateid, C.ld_name, A.ld_tenantid, A.ld_docreftype, REF.ld_stamped ");
		query.append(" from ld_document A ");
		query.append(" join ld_folder B on A.ld_folderid=B.ld_id ");
		query.append(" join ld_document REF on A.ld_docref=REF.ld_id ");
		query.append(" left outer join ld_template C on REF.ld_templateid=C.ld_id ");

		appendWhereClause(true, query);

		log.info("executing tag search query=" + query.toString());

		options.setExpression(query.toString());
	}

	/**
	 * This method appends the where clause considering or not the shortcut on
	 * the search.
	 * 
	 * @param aliases If true, also the shortcut must be considered in the
	 *        search
	 * @param query
	 */
	private void appendWhereClause(boolean aliases, StringBuffer query) {
		long tenantId = Tenant.DEFAULT_ID;
		if (options.getTenantId() != null)
			tenantId = options.getTenantId().longValue();
		else if (searchUser != null)
			tenantId = searchUser.getTenantId();

		query.append(" where A.ld_deleted=0 and A.ld_nature="+AbstractDocument.NATURE_DOC+" and A.ld_folderid=B.ld_id and A.ld_tenantid = ");
		query.append(tenantId);
		query.append(" and not A.ld_status = "+AbstractDocument.DOC_ARCHIVED);

		// Ids string to be used in the query
		String ids = null;
		if (getOptions().getFilterIds() != null && !getOptions().getFilterIds().isEmpty()) {
			ids = getOptions().getFilterIds().toString();
			ids = ids.replace('[', '(').replace(']', ')');
		}

		if (StringUtils.isNotEmpty(ids) && !"()".equals(ids)) {
			query.append(" and A.ld_id in ");
			query.append(ids);
		}

		/*
		 * Now get the IDs of the documents tagged with searched tag and use
		 * them as filter
		 */
		if (aliases)
			query.append(" and A.ld_docref is not null and REF.ld_deleted=0 and A.ld_docref = REF.ld_id and A.ld_docref in ");
		else
			query.append(" and A.ld_docref is null and A.ld_id in ");
		DocumentDAO docDAO = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		List<Long> precoll = docDAO.findDocIdByUserIdAndTag(options.getUserId(), options.getExpression());
		String buf = precoll.toString().replace("[", "(").replace("]", ")");
		query.append(!"()".equals(buf) ? buf : "(0)");

		// For normal users we have to exclude not published documents
		if (!searchUser.isInGroup("admin") && !searchUser.isInGroup("publisher")) {
			query.append(" and A.ld_published = 1 ");
			query.append(" and A.ld_startpublishing <= CURRENT_TIMESTAMP ");
			query.append(" and ( A.ld_stoppublishing is null or A.ld_stoppublishing > CURRENT_TIMESTAMP )");
		}
	}

	public class HitMapper implements RowMapper<Hit> {

		public Hit mapRow(ResultSet rs, int rowNum) throws SQLException {
			Hit hit = new Hit();
			hit.setId(rs.getLong(1));
			hit.setCustomId(rs.getString(2));
			hit.setDocRef(rs.getLong(3));
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

			if (rs.getLong(36) != 0L) {
				DocumentTemplate t = new DocumentTemplate();
				t.setId(rs.getLong(36));
				t.setName(rs.getString(37));
				hit.setTemplate(t);
				hit.setTemplateId(t.getId());
			}

			hit.setTenantId(rs.getLong(38));
			hit.setDocRefType(rs.getString(39));
			hit.setStamped(rs.getInt(40));

			return hit;
		}
	};
}