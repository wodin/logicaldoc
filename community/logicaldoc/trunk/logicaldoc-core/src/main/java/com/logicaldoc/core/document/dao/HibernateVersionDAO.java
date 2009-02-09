package com.logicaldoc.core.document.dao;

import java.util.List;

import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.document.Version;

/**
 * Hibernate implementation of <code>DocumentDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class HibernateVersionDAO extends HibernatePersistentObjectDAO<Version> implements VersionDAO {

	private HibernateVersionDAO() {
		super(Version.class);
		super.log = LogFactory.getLog(HibernateVersionDAO.class);
	}

	@Override
	public List<Version> findByDocId(long docId) {
		return findByWhere(" _entity.document.id=" + docId+ " order by _entity.versionDate desc");
	}

	@Override
	public Version findByVersion(long docId, String version) {
		List<Version> versions = findByWhere(" _entity.document.id=" + docId + " and _entity.version='" + version + "'");
		if (!versions.isEmpty())
			return versions.get(0);
		else
			return null;
	}
}