package com.logicaldoc.core.security.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.HistoryDAO;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.FolderGroup;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.core.security.User;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.sql.SqlUtil;

/**
 * Hibernate implementation of <code>FolderDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class HibernateFolderDAO extends HibernatePersistentObjectDAO<Folder> implements FolderDAO {

	private UserDAO userDAO;

	private HistoryDAO historyDAO;

	protected HibernateFolderDAO() {
		super(Folder.class);
		super.log = LogFactory.getLog(HibernateFolderDAO.class);
	}

	public UserDAO getUserDAO() {
		return userDAO;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Override
	public boolean store(Folder folder) {
		return store(folder, null);
	}

	@Override
	public boolean store(Folder folder, History transaction) {
		boolean result = true;

		try {
			if (folder.getSecurityRef() != null)
				folder.getFolderGroups().clear();

			getHibernateTemplate().saveOrUpdate(folder);
			saveFolderHistory(folder, transaction);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Folder> findByUserId(long userId) {
		List<Folder> coll = new ArrayList<Folder>();

		try {
			User user = userDAO.findById(userId);
			if (user == null)
				return coll;

			// The administrators can see all folders
			if (user.isInGroup("admin"))
				return findAll();

			Set<Group> precoll = user.getGroups();
			@SuppressWarnings("rawtypes")
			Iterator iter = precoll.iterator();
			if (!precoll.isEmpty()) {
				// First of all collect all folders that define it's own
				// policies
				StringBuffer query = new StringBuffer("select distinct(_folder) from Folder _folder  ");
				query.append(" left join _folder.folderGroups as _group ");
				query.append(" where _group.groupId in (");

				boolean first = true;
				while (iter.hasNext()) {
					if (!first)
						query.append(",");
					Group ug = (Group) iter.next();
					query.append(Long.toString(ug.getId()));
					first = false;
				}
				query.append(")");
				coll = (List<Folder>) getHibernateTemplate().find(query.toString());

				if (coll.isEmpty()) {
					return coll;
				} else {

					// Now collect all folders that references the policies of
					// the
					// previously found folders
					List<Folder> tmp = new ArrayList<Folder>();
					query = new StringBuffer("select _folder from Folder _folder  where _folder.securityRef in (");
					first = true;
					for (Folder folder : coll) {
						if (!first)
							query.append(",");
						query.append(Long.toString(folder.getId()));
						first = false;
					}
					query.append(")");
					tmp = (List<Folder>) getHibernateTemplate().find(query.toString());

					for (Folder folder : tmp) {
						if (!coll.contains(folder))
							coll.add(folder);
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return coll;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Folder> findByUserId(long userId, long parentId) {
		List<Folder> coll = new ArrayList<Folder>();

		try {
			User user = userDAO.findById(userId);
			if (user == null)
				return coll;
			if (user.isInGroup("admin"))
				return findByWhere("_entity.id!=_entity.parentId and _entity.parentId=" + parentId,
						" order by _entity.name ", null);
			/*
			 * Search for all those folderes that defines its own security
			 * policies
			 */
			StringBuffer query1 = new StringBuffer();
			Set<Group> precoll = user.getGroups();
			Iterator iter = precoll.iterator();
			if (precoll.isEmpty())
				return coll;

			query1.append("select distinct(_entity) from Folder _entity ");
			query1.append(" left join _entity.folderGroups as _group");
			query1.append(" where _group.groupId in (");

			boolean first = true;
			while (iter.hasNext()) {
				if (!first)
					query1.append(",");
				Group ug = (Group) iter.next();
				query1.append(Long.toString(ug.getId()));
				first = false;
			}
			query1.append(") and _entity.parentId = ? and _entity.id!=_entity.parentId");

			coll = (List<Folder>) getHibernateTemplate().find(query1.toString(), parentId);

			/*
			 * Now search for all other folders that references accessible
			 * folders
			 */
			StringBuffer query2 = new StringBuffer(
					"select _entity from Folder _entity where _entity.deleted=0 and _entity.parentId=? ");
			query2.append(" and _entity.securityRef in (");
			query2.append("    select distinct(B.id) from Folder B ");
			query2.append(" left join B.folderGroups as _group");
			query2.append(" where _group.groupId in (");

			first = true;
			iter = precoll.iterator();
			while (iter.hasNext()) {
				if (!first)
					query2.append(",");
				Group ug = (Group) iter.next();
				query2.append(Long.toString(ug.getId()));
				first = false;
			}
			query2.append("))");

			List<Folder> coll2 = (List<Folder>) getHibernateTemplate().find(query2.toString(), new Long[] { parentId });
			for (Folder folder : coll2) {
				if (!coll.contains(folder))
					coll.add(folder);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		Collections.sort(coll, new Comparator<Folder>() {
			@Override
			public int compare(Folder o1, Folder o2) {
				return -1 * o1.getName().compareTo(o2.getName());
			}
		});
		return coll;
	}

	@Override
	public List<Folder> findChildren(long parentId, Integer max) {
		return findByWhere("_entity.parentId = ? and _entity.id!=_entity.parentId", new Object[] { parentId }, null,
				max);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Folder> findChildren(long parentId, long userId, Integer max) {
		List<Folder> coll = new ArrayList<Folder>();
		try {
			User user = userDAO.findById(userId);
			if (user.isInGroup("admin"))
				return findChildren(parentId, max);

			Set<Group> groups = user.getGroups();
			if (groups.isEmpty())
				return coll;
			Iterator iter = groups.iterator();

			/*
			 * Search for the folders that define its own policies
			 */
			StringBuffer query1 = new StringBuffer("select distinct(_entity) from Folder _entity  ");
			query1.append(" left join _entity.folderGroups as _group ");
			query1.append(" where _group.groupId in (");

			boolean first = true;
			while (iter.hasNext()) {
				if (!first)
					query1.append(",");
				Group ug = (Group) iter.next();
				query1.append(Long.toString(ug.getId()));
				first = false;
			}
			query1.append(") and _entity.parentId=" + parentId);

			coll = (List<Folder>) getHibernateTemplate().find(query1.toString(), null);

			/*
			 * Now search for all other folders that references accessible
			 * folders
			 */
			StringBuffer query2 = new StringBuffer(
					"select _entity from Folder _entity where _entity.deleted=0 and _entity.parentId=? ");
			query2.append(" and _entity.securityRef in (");
			query2.append("    select distinct(B.id) from Folder B ");
			query2.append(" left join B.folderGroups as _group");
			query2.append(" where _group.groupId in (");

			first = true;
			iter = groups.iterator();
			while (iter.hasNext()) {
				if (!first)
					query2.append(",");
				Group ug = (Group) iter.next();
				query2.append(Long.toString(ug.getId()));
				first = false;
			}
			query2.append("))");

			List<Folder> coll2 = (List<Folder>) getHibernateTemplate(max).find(query2.toString(),
					new Long[] { parentId });
			for (Folder folder : coll2) {
				if (!coll.contains(folder))
					coll.add(folder);
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			return coll;
		}
		return coll;
	}

	@Override
	public List<Folder> findByParentId(long parentId) {
		List<Folder> coll = new ArrayList<Folder>();
		List<Folder> temp = findChildren(parentId, null);
		Iterator<Folder> iter = temp.iterator();

		while (iter.hasNext()) {
			Folder folder = iter.next();
			coll.add(folder);

			List<Folder> coll2 = findByParentId(folder.getId());

			if (coll2 != null) {
				coll.addAll(coll2);
			}
		}

		return coll;
	}

	@Override
	public boolean isWriteEnable(long folderId, long userId) {
		return isPermissionEnabled(Permission.WRITE, folderId, userId);
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean isReadEnable(long folderId, long userId) {
		boolean result = true;
		try {
			User user = userDAO.findById(userId);
			if (user == null)
				return false;
			if (user.isInGroup("admin"))
				return true;

			long id = folderId;
			Folder folder = findById(folderId);
			if (folder.getSecurityRef() != null)
				id = folder.getSecurityRef().longValue();

			Set<Group> Groups = user.getGroups();
			if (Groups.isEmpty())
				return false;

			Iterator iter = Groups.iterator();

			StringBuffer query = new StringBuffer("select distinct(_entity) from Folder _entity  ");
			query.append(" left join _entity.folderGroups as _group ");
			query.append(" where _group.groupId in (");

			boolean first = true;
			while (iter.hasNext()) {
				if (!first)
					query.append(",");
				Group ug = (Group) iter.next();
				query.append(Long.toString(ug.getId()));
				first = false;
			}
			query.append(") and _entity.id=?");

			List<FolderGroup> coll = (List<FolderGroup>) getHibernateTemplate().find(query.toString(),
					new Object[] { new Long(id) });
			result = coll.size() > 0;
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	@Override
	public List<Long> findFolderIdByUserId(long userId) {
		return findFolderIdByUserIdAndPermission(userId, Permission.READ);
	}

	@Override
	public boolean hasWriteAccess(Folder folder, long userId) {
		if (isWriteEnable(folder.getId(), userId) == false) {
			return false;
		}

		List<Folder> children = findByParentId(folder.getId());

		for (Folder subFolder : children) {
			if (!hasWriteAccess(subFolder, userId)) {
				return false;
			}
		}

		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Folder> findByGroupId(long groupId) {
		List<Folder> coll = new ArrayList<Folder>();

		// The administrators can see all folderes
		if (groupId == Group.GROUPID_ADMIN)
			return findAll();

		try {
			/*
			 * Search for folderes that define its own security policies
			 */
			StringBuffer query = new StringBuffer("select distinct(_entity) from Folder _entity  ");
			query.append(" left join _entity.folderGroups as _group ");
			query.append(" where _entity.deleted=0 and _group.groupId =" + groupId);

			coll = (List<Folder>) getHibernateTemplate().find(query.toString(), null);

			/*
			 * Now search for all other folderes that references the previous
			 * ones
			 */
			if (!coll.isEmpty()) {
				StringBuffer query2 = new StringBuffer("select _entity from Folder _entity where _entity.deleted=0 ");
				query2.append(" and _entity.securityRef in (");
				boolean first = true;
				for (Folder folder : coll) {
					if (!first)
						query2.append(",");
					query2.append(Long.toString(folder.getId()));
					first = false;
				}
				query2.append(")");
				List<Folder> coll2 = (List<Folder>) getHibernateTemplate().find(query2.toString(), null);
				for (Folder folder : coll2) {
					if (!coll.contains(folder))
						coll.add(folder);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return coll;
	}

	@Override
	@SuppressWarnings({ "deprecation", "rawtypes" })
	public List<Long> findIdByUserId(long userId, long parentId) {
		List<Long> ids = new ArrayList<Long>();
		try {
			User user = userDAO.findById(userId);
			if (user == null)
				return ids;
			if (user.isInGroup("admin"))
				return findIdsByWhere("_entity.parentId=" + parentId, null, null);

			StringBuffer query1 = new StringBuffer();
			Set<Group> precoll = user.getGroups();
			Iterator iter = precoll.iterator();
			if (!precoll.isEmpty()) {
				query1 = new StringBuffer("select distinct(A.ld_folderid) from ld_foldergroup A, ld_folder B "
						+ " where B.ld_deleted=0 and A.ld_folderid=B.ld_id AND B.ld_parentid=" + parentId
						+ " AND A.ld_groupid in (");
				boolean first = true;
				while (iter.hasNext()) {
					if (!first)
						query1.append(",");
					Group ug = (Group) iter.next();
					query1.append(Long.toString(ug.getId()));
					first = false;
				}
				query1.append(")");

				Connection con = null;
				Statement stmt = null;
				ResultSet rs = null;
				try {
					con = getSession().connection();
					stmt = con.createStatement();
					rs = stmt.executeQuery(query1.toString());
					while (rs.next()) {
						Long id = null;
						if (rs.getObject(1) instanceof Long)
							id = (Long) rs.getObject(1);
						else
							id = new Long(rs.getInt(1));
						ids.add(id);
					}
				} finally {
					if (rs != null)
						rs.close();
					if (stmt != null)
						stmt.close();
					if (con != null)
						con.close();
				}

				/*
				 * Now find all folderes referencing the previously found ones
				 */
				StringBuffer query2 = new StringBuffer("select B.ld_id from ld_folder B where B.ld_deleted=0 ");
				query2.append(" and B.ld_parentid=" + parentId);
				query2.append("	and B.ld_securityref in (");
				query2.append(query1.toString());
				query2.append(")");

				con = null;
				stmt = null;
				rs = null;
				try {
					con = getSession().connection();
					stmt = con.createStatement();
					rs = stmt.executeQuery(query2.toString());
					while (rs.next()) {
						Long id = null;
						if (rs.getObject(1) instanceof Long)
							id = (Long) rs.getObject(1);
						else
							id = new Long(rs.getInt(1));
						if (!ids.contains(id))
							ids.add(id);
					}
				} finally {
					if (rs != null)
						rs.close();
					if (stmt != null)
						stmt.close();
					if (con != null)
						con.close();
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return ids;
	}

	@Override
	public List<Folder> findByName(String name) {
		return findByName(null, name, true);
	}

	@Override
	public List<Folder> findByName(Folder parent, String name, boolean caseSensitive) {
		StringBuffer query = null;
		if (caseSensitive)
			query = new StringBuffer("_entity.name like '" + SqlUtil.doubleQuotes(name) + "' ");
		else
			query = new StringBuffer("lower(_entity.name) like '" + SqlUtil.doubleQuotes(name.toLowerCase()) + "' ");

		if (parent != null)
			query.append(" AND _entity.parentId = " + parent.getId());
		return findByWhere(query.toString(), null, null);
	}

	@Override
	public String computePathExtended(long folderId) {
		Folder folder = findById(folderId);
		if (folder == null)
			return null;
		String path = folderId != Folder.ROOTID ? folder.getName() : "";
		while (folder != null && folder.getId() != folder.getParentId() && folder.getId() != Folder.ROOTID) {
			folder = findById(folder.getParentId());
			if (folder != null)
				path = (folder.getId() != Folder.ROOTID ? folder.getName() : "") + "/" + path;
		}
		if (!path.startsWith("/"))
			path = "/" + path;
		return path;
	}

	/**
	 * Utility method that logs into the DB the transaction that involved the
	 * passed folder. The transaction must be provided with userId and userName.
	 * 
	 * @param folder
	 * @param transaction
	 */
	private void saveFolderHistory(Folder folder, History transaction) {
		if (transaction == null)
			return;

		transaction.setNotified(0);
		transaction.setFolderId(folder.getId());
		transaction.setTitle(folder.getId() != Folder.ROOTID ? folder.getName() : "/");
		String deletedFolderPathExtended = null;
		if (StringUtils.isEmpty(transaction.getPath()))
			transaction.setPath(computePathExtended(folder.getId()));
		else
			deletedFolderPathExtended = transaction.getPath();
		transaction.setComment("");

		historyDAO.store(transaction);

		// Check if is necessary to add a new history entry for the parent
		// folder. This operation is not recursive, because we want to notify
		// only the parent folder.
		if (folder.getId() != folder.getParentId() && folder.getId() != Folder.ROOTID) {
			Folder parent = findById(folder.getParentId());
			// The parent folder can be 'null' when the user wants to delete a
			// folder with sub-folders under it (method 'deleteAll()').
			if (parent != null) {
				History parentHistory = new History();
				parentHistory.setFolderId(parent.getId());
				parentHistory.setTitle(parent.getId() != Folder.ROOTID ? parent.getName() : "/");
				if (deletedFolderPathExtended != null)
					parentHistory.setPath(deletedFolderPathExtended);
				else
					parentHistory.setPath(computePathExtended(folder.getId()));

				parentHistory.setUser(transaction.getUser());
				if (transaction.getEvent().equals(History.EVENT_FOLDER_CREATED)
						|| transaction.getEvent().equals(History.EVENT_FOLDER_MOVED)) {
					parentHistory.setEvent(History.EVENT_FOLDER_SUBFOLDER_CREATED);
				} else if (transaction.getEvent().equals(History.EVENT_FOLDER_RENAMED)) {
					parentHistory.setEvent(History.EVENT_FOLDER_SUBFOLDER_RENAMED);
				} else if (transaction.getEvent().equals(History.EVENT_FOLDER_PERMISSION)) {
					parentHistory.setEvent(History.EVENT_FOLDER_SUBFOLDER_PERMISSION);
				} else if (transaction.getEvent().equals(History.EVENT_FOLDER_DELETED)) {
					parentHistory.setEvent(History.EVENT_FOLDER_SUBFOLDER_DELETED);
				}
				parentHistory.setComment("");
				parentHistory.setSessionId(transaction.getSessionId());
				parentHistory.setComment(transaction.getComment());

				historyDAO.store(parentHistory);
			}
		}
	}

	@Override
	public List<Folder> findByNameAndParentId(String name, long parentId) {
		return findByWhere("_entity.parentId = " + parentId + " and _entity.name like '" + SqlUtil.doubleQuotes(name)
				+ "'", null, null);
	}

	@Override
	public List<Folder> findParents(long folderId) {
		Folder folder = findById(folderId);
		List<Folder> coll = new ArrayList<Folder>();
		try {
			while (folder.getId() != Folder.ROOTID && folder.getId() != folder.getParentId()) {
				folder = findById(folder.getParentId());
				if (folder != null)
					coll.add(0, folder);
			}
		} catch (Exception e) {
			;
		}
		return coll;
	}

	@Override
	public boolean isPermissionEnabled(Permission permission, long folderId, long userId) {
		Set<Permission> permissions = getEnabledPermissions(folderId, userId);
		return permissions.contains(permission);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void restore(long folderId, boolean parents) {
		bulkUpdate("set ld_deleted=0 where ld_id=" + folderId, null);

		// Restore parents
		if (parents) {
			String query = "select ld_parentid from ld_folder where ld_id =" + folderId;
			List<Long> folders = (List<Long>) super.queryForList(query, null, Long.class, null);
			for (Long id : folders) {
				if (id.longValue() != folderId)
					restore(id, parents);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public Set<Permission> getEnabledPermissions(long folderId, long userId) {
		Set<Permission> permissions = new HashSet<Permission>();

		try {
			User user = userDAO.findById(userId);
			if (user == null)
				return permissions;

			// If the user is an administrator bypass all controls
			if (user.isInGroup("admin")) {
				return Permission.all();
			}

			Set<Group> groups = user.getGroups();
			if (groups.isEmpty())
				return permissions;
			Iterator<Group> iter = groups.iterator();

			// If the folder defines a security ref, use another folder to find
			// the
			// policies
			long id = folderId;
			Folder folder = findById(folderId);
			if (folder.getSecurityRef() != null) {
				id = folder.getSecurityRef().longValue();
				log.debug("Use the security reference " + id);
			}

			StringBuffer query = new StringBuffer(
					"select A.LD_WRITE as LDWRITE, A.LD_ADD as LDADD, A.ld_security as LDSECURITY, A.ld_immutable as LDIMMUTABLE, A.LD_DELETE as LDDELETE, A.LD_RENAME as LDRENAME, A.ld_import as LDIMPORT, A.ld_export as LDEXPORT, A.LD_SIGN as LDSIGN, A.LD_ARCHIVE as LDARCHIVE, A.LD_WORKFLOW as LDWORKFLOW");
			query.append(" from ld_foldergroup A");
			query.append(" where ");
			query.append(" A.LD_FOLDERID=" + id);
			query.append(" and A.LD_GROUPID in (");

			boolean first = true;
			while (iter.hasNext()) {
				if (!first)
					query.append(",");
				Group ug = (Group) iter.next();
				query.append(Long.toString(ug.getId()));
				first = false;
			}
			query.append(")");

			Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;

			try {
				con = getSession().connection();
				stmt = con.createStatement();
				rs = stmt.executeQuery(query.toString());
				while (rs.next()) {
					if (!permissions.contains(Permission.READ))
						permissions.add(Permission.READ);
					if (rs.getInt("LDADD") == 1)
						if (!permissions.contains(Permission.ADD))
							permissions.add(Permission.ADD);
					if (rs.getInt("LDEXPORT") == 1)
						if (!permissions.contains(Permission.EXPORT))
							permissions.add(Permission.EXPORT);
					if (rs.getInt("LDIMPORT") == 1)
						if (!permissions.contains(Permission.IMPORT))
							permissions.add(Permission.IMPORT);
					if (rs.getInt("LDDELETE") == 1)
						if (!permissions.contains(Permission.DELETE))
							permissions.add(Permission.DELETE);
					if (rs.getInt("LDIMMUTABLE") == 1)
						if (!permissions.contains(Permission.IMMUTABLE))
							permissions.add(Permission.IMMUTABLE);
					if (rs.getInt("LDSECURITY") == 1)
						if (!permissions.contains(Permission.SECURITY))
							permissions.add(Permission.SECURITY);
					if (rs.getInt("LDRENAME") == 1)
						if (!permissions.contains(Permission.RENAME))
							permissions.add(Permission.RENAME);
					if (rs.getInt("LDWRITE") == 1)
						if (!permissions.contains(Permission.WRITE))
							permissions.add(Permission.WRITE);
					if (rs.getInt("LDDELETE") == 1)
						if (!permissions.contains(Permission.DELETE))
							permissions.add(Permission.DELETE);
					if (rs.getInt("LDSIGN") == 1)
						if (!permissions.contains(Permission.SIGN))
							permissions.add(Permission.SIGN);
					if (rs.getInt("LDARCHIVE") == 1)
						if (!permissions.contains(Permission.ARCHIVE))
							permissions.add(Permission.ARCHIVE);
					if (rs.getInt("LDWORKFLOW") == 1)
						if (!permissions.contains(Permission.WORKFLOW))
							permissions.add(Permission.WORKFLOW);
				}
			} finally {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (con != null)
					con.close();
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return permissions;
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<Long> findFolderIdByUserIdAndPermission(long userId, Permission permission) {
		List<Long> ids = new ArrayList<Long>();
		try {
			User user = userDAO.findById(userId);
			if (user == null)
				return ids;

			// The administrators have all permissions on all folders
			if (user.isInGroup("admin"))
				return findAllIds();

			Set<Group> precoll = user.getGroups();
			Iterator<Group> iter = precoll.iterator();

			if (!precoll.isEmpty()) {
				/*
				 * Check folders that specify its own permissions
				 */
				StringBuffer query1 = new StringBuffer(
						"select distinct(A.ld_folderid) from ld_foldergroup A, ld_folder B "
								+ " where A.ld_folderid=B.ld_id and B.ld_deleted=0 ");
				if (permission != Permission.READ)
					query1.append(" and A.ld_" + permission.getName() + "=1 ");
				query1.append(" and A.ld_groupid in (");
				boolean first = true;
				while (iter.hasNext()) {
					if (!first)
						query1.append(",");
					Group ug = (Group) iter.next();
					query1.append(Long.toString(ug.getId()));
					first = false;
				}
				query1.append(")");

				Connection con = null;
				Statement stmt = null;
				ResultSet rs = null;
				try {
					con = getSession().connection();
					stmt = con.createStatement();
					rs = stmt.executeQuery(query1.toString());
					while (rs.next()) {
						Long id = null;
						if (rs.getObject(1) instanceof Long)
							id = (Long) rs.getObject(1);
						else
							id = new Long(rs.getInt(1));
						ids.add(id);
					}
				} finally {
					if (rs != null)
						rs.close();
					if (stmt != null)
						stmt.close();
					if (con != null)
						con.close();
				}

				/*
				 * Now search for those folderes that references the previously
				 * found ones
				 */
				StringBuffer query2 = new StringBuffer("select B.ld_id from ld_folder B where B.ld_deleted=0 ");
				query2.append(" and B.ld_securityref in (" + query1.toString() + ")");

				con = null;
				stmt = null;
				rs = null;
				try {
					con = getSession().connection();
					stmt = con.createStatement();
					rs = stmt.executeQuery(query2.toString());
					while (rs.next()) {
						Long id = null;
						if (rs.getObject(1) instanceof Long)
							id = (Long) rs.getObject(1);
						else
							id = new Long(rs.getInt(1));
						if (!ids.contains(id))
							ids.add(id);
					}
				} finally {
					if (rs != null)
						rs.close();
					if (stmt != null)
						stmt.close();
					if (con != null)
						con.close();
				}

			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return ids;
	}

	public HistoryDAO getHistoryDAO() {
		return historyDAO;
	}

	public void setHistoryDAO(HistoryDAO historyDAO) {
		this.historyDAO = historyDAO;
	}

	@Override
	public void deleteAll(List<Folder> folders, History transaction) {
		for (Folder folder : folders) {
			try {
				History deleteHistory = (History) transaction.clone();
				deleteHistory.setEvent(History.EVENT_FOLDER_DELETED);
				deleteHistory.setFolderId(folder.getId());
				deleteHistory.setPath(computePathExtended(folder.getId()));
				delete(folder.getId(), deleteHistory);
			} catch (CloneNotSupportedException e) {
				log.error(e.getMessage(), e);
			}
		}

	}

	@Override
	public boolean delete(long folderId, History transaction) {
		boolean result = true;
		try {
			Folder folder = (Folder) getHibernateTemplate().get(Folder.class, folderId);
			folder.setDeleted(1);
			transaction.setEvent(History.EVENT_FOLDER_DELETED);
			transaction.setFolderId(folderId);
			store(folder, transaction);
		} catch (Throwable e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	@Override
	public boolean applyRithtToTree(long id, History transaction) {
		boolean result = true;
		try {
			transaction.setEvent(History.EVENT_FOLDER_PERMISSION);
			Folder parent = findById(id);
			Long securityRef = id;
			if (parent.getSecurityRef() != null)
				securityRef = parent.getSecurityRef();

			// Iterate over all children setting the security reference
			List<Folder> children = findChildren(id, null);
			for (Folder folder : children) {
				if (!securityRef.equals(folder.getSecurityRef())) {
					History tr = (History) transaction.clone();
					tr.setFolderId(folder.getId());
					folder.setSecurityRef(securityRef);
					folder.getFolderGroups().clear();
					store(folder, tr);
				}
				applyRithtToTree(folder.getId(), transaction);
			}
		} catch (Throwable e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	@Override
	public Folder create(Folder parent, String name, History transaction) {
		Folder folder = new Folder();
		folder.setName(name);
		folder.setParentId(parent.getId());

		if (parent.getSecurityRef() != null)
			folder.setSecurityRef(parent.getSecurityRef());
		else
			folder.setSecurityRef(parent.getId());

		setUniqueName(folder);
		if (transaction != null)
			transaction.setEvent(History.EVENT_FOLDER_CREATED);
		if (store(folder, transaction) == false)
			return null;
		return folder;
	}

	@Override
	public Folder createPath(Folder parent, String path, History transaction) {
		StringTokenizer st = new StringTokenizer(path, "/", false);

		Folder folder = parent;
		while (st.hasMoreTokens()) {
			String name = st.nextToken();
			List<Folder> childs = findByName(folder, name, true);
			Folder dir;
			if (childs.isEmpty())
				dir = create(folder, name, transaction);
			else {
				dir = childs.iterator().next();
			}
			folder = dir;
		}
		return folder;
	}

	@Override
	public Folder find(String name, String pathExtended) {
		StringTokenizer st = new StringTokenizer(pathExtended, "/", false);
		Folder parent = findById(Folder.ROOTID);
		while (st.hasMoreTokens()) {
			List<Folder> list = findByName(parent, st.nextToken(), true);
			if (list.isEmpty())
				return null;
			parent = list.get(0);

		}

		List<Folder> specified_folder = findByName(parent, name, true);
		if (specified_folder != null && specified_folder.size() > 0)
			return specified_folder.iterator().next();
		return null;
	}

	@Override
	public void setUniqueName(Folder folder) {
		int counter = 1;
		String folderName = folder.getName();
		while (findByNameAndParentId(folder.getName(), folder.getParentId()).size() > 0) {
			folder.setName(folderName + "(" + (counter++) + ")");
		}
	}

	@Override
	public void move(Folder source, Folder target, History transaction) throws Exception {
		assert (source != null);
		assert (target != null);
		assert (transaction != null);
		assert (transaction.getUser() != null);

		if (isInPath(source.getId(), target.getId()))
			throw new IllegalArgumentException("Cannot move a dolder inside the same path");

		// Change the parent folder
		source.setParentId(target.getId());

		// Ensure unique folder name in a folder
		setUniqueName(source);

		// Modify folder history entry
		transaction.setEvent(History.EVENT_FOLDER_MOVED);

		store(source, transaction);
	}

	@Override
	public List<Folder> deleteTree(long folderId, History transaction) throws Exception {
		return deleteTree(findById(folderId), transaction);
	}

	@Override
	public List<Folder> deleteTree(Folder folder, History transaction) throws Exception {
		assert (folder != null);
		assert (transaction != null);
		assert (transaction.getUser() != null);

		List<Folder> deletableFolders = new ArrayList<Folder>();
		List<Folder> notDeletableFolders = new ArrayList<Folder>();

		List<Long> deletableIds = findFolderIdByUserIdAndPermission(transaction.getUserId(), Permission.DELETE);

		if (deletableIds.contains(folder.getId())) {
			deletableFolders.add(folder);
		} else {
			notDeletableFolders.add(folder);
			return notDeletableFolders;
		}

		try {
			// Retrieve all the sub-folders
			List<Folder> subfolders = findByParentId(folder.getId());

			for (Folder subfolder : subfolders) {
				if (deletableIds.contains(subfolder.getId())) {
					deletableFolders.add(subfolder);
				} else {
					notDeletableFolders.add(subfolder);
				}
			}

			for (Folder deletableFolder : deletableFolders) {
				boolean foundDocImmutable = false;
				boolean foundDocLocked = false;

				DocumentDAO documentDAO = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
				List<Document> docs = documentDAO.findByFolder(deletableFolder.getId(), null);

				for (Document doc : docs) {
					if (doc.getImmutable() == 1 && !transaction.getUser().isInGroup("admin")) {
						// If it he isn't an administrator he cannot delete a
						// folder containing immutable documents
						foundDocImmutable = true;
						continue;
					}
				}
				if (foundDocImmutable || foundDocLocked) {
					notDeletableFolders.add(deletableFolder);
				}
			}

			// Avoid deletion of the entire path of an undeletable folder
			for (Folder notDeletable : notDeletableFolders) {
				Folder parent = notDeletable;
				while (true) {
					if (deletableFolders.contains(parent))
						deletableFolders.remove(parent);
					if (parent.equals(folder))
						break;
					parent = findById(parent.getParentId());
				}
			}

			// Modify document history entry
			deleteAll(deletableFolders, transaction);
			return notDeletableFolders;
		} catch (Throwable e) {
			log.error(e);
			return notDeletableFolders;
		}
	}

	@Override
	public List<Folder> find(String name) {
		return findByName(null, "%" + name + "%", false);
	}

	@Override
	public boolean isInPath(long folderId, long targetId) {
		for (Folder folder : findParents(targetId)) {
			if (folder.getId() == folderId)
				return true;
		}
		return false;
	}
}