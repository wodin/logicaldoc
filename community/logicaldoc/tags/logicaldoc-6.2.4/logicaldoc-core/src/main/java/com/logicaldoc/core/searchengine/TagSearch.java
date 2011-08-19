package com.logicaldoc.core.searchengine;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import com.logicaldoc.core.document.dao.DocumentDAO;
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
	}

	/**
	 * Utility method that prepare the query expression.
	 */
	private void prepareExpression() {
		// Find all real documents
		StringBuffer query = new StringBuffer(
				"select A.ld_id,A.ld_folderid,A.ld_title,A.ld_type,A.ld_customid,A.ld_filesize,A.ld_docref,A.ld_date,A.ld_sourcedate,A.ld_creation,A.ld_source "
						+ "from ld_document A ");
		appendWhereClause(false, query);

		// Append all shortcuts
		query.append(" UNION select REF.ld_id,A.ld_folderid,REF.ld_title,REF.ld_type,REF.ld_customid,REF.ld_filesize,A.ld_docref,REF.ld_date,REF.ld_sourcedate,REF.ld_creation,REF.ld_source "
				+ "from ld_document A, ld_document REF ");
		appendWhereClause(true, query);

		log.info("executing tag search query=" + query.toString());

		options.setExpression(query.toString());
	}

	/**
	 * This method appends the where clause considering or not the shortcut on
	 * the search.
	 * 
	 * @param searchShortcut If true, also the shortcut must be considered in
	 *        the search
	 * @param query
	 */
	private void appendWhereClause(boolean searchShortcut, StringBuffer query) {
		query.append(" where A.ld_deleted=0 ");

		// Ids string to be used in the query
		String ids = null;
		if (getOptions().getFilterIds() != null && !getOptions().getFilterIds().isEmpty()) {
			ids = getOptions().getFilterIds().toString();
			ids = ids.replace("[", "(").replace("]", ")");
		}

		if (StringUtils.isNotEmpty(ids)) {
			query.append(" and A.ld_id in ");
			query.append(ids);
		}

		if (searchShortcut)
			query.append(" and REF.ld_deleted=0 and A.ld_docref = REF.ld_id ");
		else
			query.append(" and A.ld_docref is null ");

		/*
		 * Search for all docs with specific tag
		 */
		if (searchShortcut)
			query.append(" and REF.ld_id in ");
		else
			query.append(" and A.ld_id in ");

		DocumentDAO docDAO = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Set<Long> precoll = docDAO.findDocIdByUserIdAndTag(options.getUserId(), options.getExpression());
		String buf = precoll.toString().replace("[", "(").replace("]", ")");
		query.append(buf);
	}

	public class HitMapper implements RowMapper<Hit> {

		public Hit mapRow(ResultSet rs, int rowNum) throws SQLException {
			Hit hit = new HitImpl();
			hit.setDocId(rs.getLong(1));
			hit.setFolderId(rs.getLong(2));
			hit.setTitle(rs.getString(3));
			hit.setType(rs.getString(4));
			hit.setCustomId(rs.getString(5));
			hit.setSize(rs.getLong(6));
			hit.setDocRef(rs.getLong(7));
			hit.setDate(rs.getTimestamp(8));
			hit.setSourceDate(rs.getTimestamp(9));
			hit.setCreation(rs.getTimestamp(10));
			hit.setSource(rs.getString(11));

			return hit;
		}
	};
}