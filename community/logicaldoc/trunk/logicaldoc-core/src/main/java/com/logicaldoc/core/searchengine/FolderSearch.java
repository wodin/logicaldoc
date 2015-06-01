package com.logicaldoc.core.searchengine;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.Tenant;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.sql.SqlUtil;

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

		FolderDAO dao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		FolderSearchOptions fso = (FolderSearchOptions) getOptions();

		List<Object> params = new ArrayList<Object>();
		if (fso.getCreationFrom() != null)
			params.add(new java.sql.Date(fso.getCreationFrom().getTime()));
		if (fso.getCreationTo() != null)
			params.add(new java.sql.Date(fso.getCreationTo().getTime()));

		hits.addAll((List<Hit>) dao.query(options.getExpression(), params.toArray(new Object[0]), new HitMapper(),
				options.getMaxHits()));

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
				"select A.ld_id, A.ld_parentid, A.ld_name, A.ld_description, A.ld_creation, A.ld_lastmodified from ld_folder A ");
		query.append(" where A.ld_deleted=0 and A.ld_type=0 and A.ld_hidden=0 ");

		long tenantId = Tenant.DEFAULT_ID;
		if (options.getTenantId() != null)
			tenantId = options.getTenantId().longValue();
		else if (searchUser != null)
			tenantId = searchUser.getTenantId();

		query.append(" and A.ld_tenantid= " + tenantId + " ");

		FolderSearchOptions fso = (FolderSearchOptions) getOptions();

		FolderDAO dao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		if (StringUtils.isNotEmpty(fso.getFolderName())) {
			query.append(" and " + (dao.getDbms().endsWith("hsqldb") ? "lcase" : "lower") + "(ld_name) like '%");
			query.append(SqlUtil.doubleQuotes(fso.getFolderName().trim().toLowerCase()));
			query.append("%' ");
		}

		if (StringUtils.isNotEmpty(fso.getFolderDescription())) {
			query.append(" and " + (dao.getDbms().endsWith("hsqldb") ? "lcase" : "lower") + "(ld_description) like '%");
			query.append(SqlUtil.doubleQuotes(fso.getFolderDescription().trim().toLowerCase()));
			query.append("%' ");
		}

		if (fso.getCreationFrom() != null) {
			query.append(" and ld_creation > ? ");
		}

		if (fso.getCreationTo() != null) {
			query.append(" and ld_creation < ? ");
		}

		boolean searchInSingleFolder = (options.getFolderId() != null && !options.isSearchInSubPath());

		FolderDAO folderDAO = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);

		if (!searchInSingleFolder) {
			if (!(options.getFolderId() == null && searchUser.isInGroup("admin"))) {
				Collection<Long> accessibleIds = folderDAO.findFolderIdByUserId(options.getUserId(),
						options.getFolderId(), true);
				query.append(" and ld_id in ");
				query.append(accessibleIds.toString().replace('[', '(').replace(']', ')'));
			}
		} else if (folderDAO.isReadEnable(options.getFolderId(), options.getUserId())) {
			query.append(" and ld_id = " + options.getFolderId());
		}

		log.info("executing folder search query=" + query.toString());

		options.setExpression(query.toString());
	}

	public class HitMapper implements RowMapper<Hit> {

		public Hit mapRow(ResultSet rs, int rowNum) throws SQLException {
			Hit hit = new Hit();
			hit.setId(rs.getLong(1));

			Folder folder = new Folder();
			folder.setId(rs.getLong(2));
			folder.setName(rs.getString(3));
			hit.setFolder(folder);
			hit.setTitle(rs.getString(3));
			hit.setType("folder");
			hit.setCustomId(Long.toString(rs.getLong(1)));
			hit.setDate(rs.getTimestamp(6));
			hit.setCreation(rs.getTimestamp(5));
			hit.setSource(null);
			hit.setComment(rs.getString(4));
			hit.setPublished(1);
			return hit;
		}
	};
}