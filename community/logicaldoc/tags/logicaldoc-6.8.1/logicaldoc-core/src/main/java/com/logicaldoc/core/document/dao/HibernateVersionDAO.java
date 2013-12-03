package com.logicaldoc.core.document.dao;

import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.LoggerFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.store.Storer;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.io.FileUtil;

/**
 * Hibernate implementation of <code>DocumentDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
@SuppressWarnings("unchecked")
public class HibernateVersionDAO extends HibernatePersistentObjectDAO<Version> implements VersionDAO {

	private Storer storer;

	private HibernateVersionDAO() {
		super(Version.class);
		super.log = LoggerFactory.getLogger(HibernateVersionDAO.class);
	}

	@Override
	public List<Version> findByDocId(long docId) {
		return findByWhere(" _entity.docId=" + docId, "order by _entity.versionDate desc", null);
	}

	@Override
	public Version findByVersion(long docId, String version) {
		List<Version> versions = findByWhere(" _entity.docId=" + docId + " and _entity.version='" + version + "'",
				null, null);
		if (!versions.isEmpty())
			return versions.get(0);
		else
			return null;
	}

	@Override
	public void initialize(Version version) {
		refresh(version);

		for (String attribute : version.getAttributes().keySet()) {
			attribute.getBytes();
		}
	}

	@Override
	public boolean store(Version version) {
		boolean result = true;
		try {
			super.store(version);
			// Checks the context property 'document.maxversions'
			ContextProperties bean = new ContextProperties();
			int maxVersions = bean.getInt("document.maxversions");
			if (maxVersions > 0) {
				List<Version> versions = findByDocId(version.getDocId());
				// Order the document versions by version date and version
				// number
				if (versions.size() > maxVersions) {
					Collections.sort(versions, new Comparator<Version>() {
						public int compare(Version v1, Version v2) {
							if (v1.getVersionDate() != null && v2.getVersionDate() != null) {
								int compare = v1.getVersionDate().compareTo(v2.getVersionDate());
								if (compare != 0)
									return compare;
							}
							return (v1.getVersion()).compareTo(v2.getVersion());
						}
					});
					// Delete the overlimit versions
					int versionNumToBeDeleted = versions.size() - maxVersions;

					// Prepare a list of files(fileVersion) that must be
					// retained
					Set<String> filesToBeRetained = new HashSet<String>();
					for (int i = versionNumToBeDeleted; i < versions.size(); i++)
						if (!filesToBeRetained.contains(versions.get(i).getFileVersion()))
							filesToBeRetained.add(versions.get(i).getFileVersion());

					for (int i = 0; i < versionNumToBeDeleted; i++) {
						// Delete the version
						Version deleteVersion = versions.get(i);
						initialize(deleteVersion);
						deleteVersion.setDeleted(1);
						store(deleteVersion);
						if (!filesToBeRetained.contains(deleteVersion.getFileVersion())) {
							List<String> resources = storer.listResources(deleteVersion.getDocId(),
									deleteVersion.getFileVersion());
							for (String resource : resources) {
								storer.delete(deleteVersion.getDocId(), resource);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			result = false;
		}
		return result;
	}

	@Override
	public void updateDigest(Version version) {
		initialize(version);
		String resource = storer.getResourceName(version.getDocId(), version.getFileVersion(), null);
		if (storer.exists(version.getDocId(), resource)) {
			InputStream in = null;
			try {
				in = storer.getStream(version.getDocId(), resource);
				version.setDigest(FileUtil.computeDigest(in));
			} finally {
				if (in != null)
					try {
						in.close();
					} catch (Throwable t) {
					}
			}
			saveOrUpdate(version);
		}
	}

	public void setStorer(Storer storer) {
		this.storer = storer;
	}
}