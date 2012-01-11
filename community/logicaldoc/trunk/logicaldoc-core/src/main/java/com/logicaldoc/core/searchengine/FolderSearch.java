package com.logicaldoc.core.searchengine;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.util.Context;

/**
 * Search specialization for Folder searches.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.4
 */
public class FolderSearch extends Search {

	protected FolderSearch() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void internalSearch() throws Exception {
		prepareExpression();

		DocumentDAO dao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		FolderSearchOptions fso = (FolderSearchOptions) getOptions();

		List<Object> params = new ArrayList<Object>();
		if (fso.getCreationFrom() != null)
			params.add(new java.sql.Date(fso.getCreationFrom().getTime()));
		if (fso.getCreationTo() != null)
			params.add(new java.sql.Date(fso.getCreationTo().getTime()));

		hits.addAll((List<Hit>) dao.query(options.getExpression(), params.toArray(new Object[0]), new HitMapper(),
				options.getMaxHits()));

		moreHitsPresent = (hits.size() >= options.getMaxHits());
	}

	/**
	 * Utility method that prepare the query expression.
	 */
	private void prepareExpression() {
		// Find all real documents
		StringBuffer query = new StringBuffer(
				"select A.ld_id, A.ld_parentid, A.ld_name, A.ld_description, A.ld_creation, A.ld_lastmodified from ld_folder A ");
		query.append(" where A.ld_deleted=0 and A.ld_type=0 ");

		FolderSearchOptions fso = (FolderSearchOptions) getOptions();

		if (StringUtils.isNotEmpty(fso.getFolderName())) {
			query.append(" and lower(ld_name) like '%");
			query.append(fso.getFolderName().toLowerCase().trim());
			query.append("%' ");
		}

		if (StringUtils.isNotEmpty(fso.getFolderDescription())) {
			query.append(" and lower(ld_description) like '%");
			query.append(fso.getFolderDescription().toLowerCase().trim());
			query.append("%' ");
		}

		if (fso.getCreationFrom() != null) {
			query.append(" and ld_creation > ? ");
		}

		if (fso.getCreationTo() != null) {
			query.append(" and ld_creation < ? ");
		}

		Collection<Long> accessibleIds = getAccessibleFolderIds();
		if (!accessibleIds.isEmpty()) {
			query.append(" and ld_id in (");
			query.append(accessibleIds.toString().replace('[', ' ').replace(']', ' '));
			query.append(") ");
		}

		log.info("executing folder search query=" + query.toString());

		options.setExpression(query.toString());
	}

	public class HitMapper implements RowMapper<Hit> {

		public Hit mapRow(ResultSet rs, int rowNum) throws SQLException {
			Hit hit = new HitImpl();
			hit.setDocId(rs.getLong(1));
			hit.setFolderId(rs.getLong(2));
			hit.setTitle(rs.getString(3));
			hit.setType("folder");
			hit.setCustomId(Long.toString(rs.getLong(1)));
			hit.setSize(0);
			hit.setDocRef(null);
			hit.setDate(rs.getTimestamp(6));
			hit.setSourceDate(null);
			hit.setCreation(rs.getTimestamp(5));
			hit.setSource(null);
			hit.setComment(rs.getString(4));
			hit.setFolderName(rs.getString(3));
			hit.setPublished(1);
			return hit;
		}
	};

	private Collection<Long> getAccessibleFolderIds() {
		Collection<Long> ids = new HashSet<Long>();
		FolderDAO folderDAO = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);

		// Check if there is a folder specification in the criteria
		if (((FolderSearchOptions) options).getFolderId() != null) {
			if (folderDAO.isReadEnable(((FolderSearchOptions) options).getFolderId(), searchUser.getId()))
				ids.add(((FolderSearchOptions) options).getFolderId());

			if (((FolderSearchOptions) options).isSearchInSubPath()) {
				folderDAO.findTreeIds(((FolderSearchOptions) options).getFolderId(), searchUser.getId(),
						((FolderSearchOptions) options).getDepth(), ids);
			}
		}

		/*
		 * In case of normal user and without a folder criterion, we have to
		 * collect all accessible folders.
		 */
		if (ids.isEmpty() && !searchUser.isInGroup("admin"))
			ids = folderDAO.findFolderIdByUserId(options.getUserId());

		return ids;
	}
}