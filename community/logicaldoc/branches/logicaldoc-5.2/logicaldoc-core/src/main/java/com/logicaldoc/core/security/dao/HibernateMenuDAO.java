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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.dao.HistoryDAO;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.MenuGroup;
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.core.security.User;
import com.logicaldoc.util.sql.SqlUtil;

/**
 * Hibernate implementation of <code>MenuDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class HibernateMenuDAO extends HibernatePersistentObjectDAO<Menu> implements MenuDAO {

	private UserDAO userDAO;

	private HistoryDAO historyDAO;

	protected HibernateMenuDAO() {
		super(Menu.class);
		super.log = LogFactory.getLog(HibernateMenuDAO.class);
	}

	public UserDAO getUserDAO() {
		return userDAO;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Override
	public boolean store(Menu menu) {
		return store(menu, null);
	}

	@Override
	public boolean store(Menu menu, History transaction) {
		boolean result = true;

		try {
			if (menu.getSecurityRef() != null)
				menu.getMenuGroups().clear();

			getHibernateTemplate().saveOrUpdate(menu);
			saveFolderHistory(menu, transaction);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	@Override
	public List<Menu> findByUserId(long userId) {
		return findByUserId(userId, null);
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findByUserId(long, Integer)
	 */
	@SuppressWarnings("unchecked")
	public List<Menu> findByUserId(long userId, Integer type) {
		List<Menu> coll = new ArrayList<Menu>();

		try {
			User user = userDAO.findById(userId);
			userDAO.initialize(user);
			if (user == null)
				return coll;

			// The admnistrators can see all menues
			if (user.isInGroup("admin"))
				if (type == null)
					return findAll();
				else
					return findByWhere("_entity.type=" + type, null, null);

			Set<Group> precoll = user.getGroups();
			Iterator iter = precoll.iterator();
			if (!precoll.isEmpty()) {
				// First of all collect all menues that define it's own policies
				StringBuffer query = new StringBuffer("select distinct(_menu) from Menu _menu  ");
				query.append(" left join _menu.menuGroups as _group ");
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
				if (type != null)
					query.append(" and _menu.type=" + type);
				coll = (List<Menu>) getHibernateTemplate().find(query.toString());

				// Now collect all menues that references the policies of the
				// previously found menues
				List<Menu> tmp = new ArrayList<Menu>();
				query = new StringBuffer("select _menu from Menu _menu  where _menu.securityRef in (");
				first = true;
				for (Menu menu : coll) {
					if (!first)
						query.append(",");
					query.append(Long.toString(menu.getId()));
					first = false;
				}
				query.append(")");
				if (type != null)
					query.append(" and _menu.type=" + type);
				tmp = (List<Menu>) getHibernateTemplate().find(query.toString());

				for (Menu menu : tmp) {
					if (!coll.contains(menu))
						coll.add(menu);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findByUserId(long, long)
	 */
	public List<Menu> findByUserId(long userId, long parentId) {
		return findByUserId(userId, parentId, null);
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findByUserId(userId, long,
	 *      type)
	 */
	@SuppressWarnings("unchecked")
	public List<Menu> findByUserId(long userId, long parentId, Integer type) {
		List<Menu> coll = new ArrayList<Menu>();

		try {
			User user = userDAO.findById(userId);
			if (user == null)
				return coll;
			if (user.isInGroup("admin"))
				return findByWhere("_entity.id!=_entity.parentId and _entity.parentId=" + parentId
						+ (type == null ? "" : (" and _entity.type=" + type)), " order by _entity.sort, _entity.text ",
						null);
			/*
			 * Search for all those menues that defines its own security
			 * policies
			 */
			StringBuffer query1 = new StringBuffer();
			Set<Group> precoll = user.getGroups();
			Iterator iter = precoll.iterator();
			if (precoll.isEmpty())
				return coll;

			query1.append("select distinct(_entity) from Menu _entity ");
			query1.append(" left join _entity.menuGroups as _group");
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
			if (type != null)
				query1.append(" and _entity.type = " + type.toString());

			coll = (List<Menu>) getHibernateTemplate().find(query1.toString(), parentId);

			/*
			 * Now search for all other menues that references accessible menues
			 */
			StringBuffer query2 = new StringBuffer(
					"select _entity from Menu _entity where _entity.deleted=0 and _entity.parentId=? ");
			query2.append(" and _entity.securityRef in (");
			query2.append("    select distinct(B.id) from Menu B ");
			query2.append(" left join B.menuGroups as _group");
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

			List<Menu> coll2 = (List<Menu>) getHibernateTemplate().find(query2.toString(), new Long[] { parentId });
			for (Menu menu : coll2) {
				if (!coll.contains(menu))
					coll.add(menu);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		Collections.sort(coll, new Comparator<Menu>() {
			@Override
			public int compare(Menu o1, Menu o2) {
				Integer sort1 = new Integer(o1.getSort());
				Integer sort2 = new Integer(o2.getSort());
				if (sort1.compareTo(sort2) == 0)
					return -1 * o1.getText().compareTo(o2.getText());
				else
					sort1.compareTo(sort2);
				return 0;
			}
		});
		return coll;
	}

	@Override
	public List<Menu> findChildren(long parentId, Integer max) {
		return findByWhere("_entity.parentId = ? and _entity.id!=_entity.parentId", new Object[] { parentId }, null,
				max);
	}

	@Override
	public List<Menu> findChildren(long parentId, long userId, Integer max) {
		List<Menu> coll = new ArrayList<Menu>();
		try {
			User user = userDAO.findById(userId);
			if (user.isInGroup("admin"))
				return findChildren(parentId, max);

			Set<Group> groups = user.getGroups();
			if (groups.isEmpty())
				return coll;
			Iterator iter = groups.iterator();

			/*
			 * Search for the menues that define its own policies
			 */
			StringBuffer query1 = new StringBuffer("select distinct(_entity) from Menu _entity  ");
			query1.append(" left join _entity.menuGroups as _group ");
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

			coll = (List<Menu>) getHibernateTemplate().find(query1.toString(), null);

			/*
			 * Now search for all other menues that references accessible menues
			 */
			StringBuffer query2 = new StringBuffer(
					"select _entity from Menu _entity where _entity.deleted=0 and _entity.parentId=? ");
			query2.append(" and _entity.securityRef in (");
			query2.append("    select distinct(B.id) from Menu B ");
			query2.append(" left join B.menuGroups as _group");
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

			List<Menu> coll2 = (List<Menu>) getHibernateTemplate(max).find(query2.toString(), new Long[] { parentId });
			for (Menu menu : coll2) {
				if (!coll.contains(menu))
					coll.add(menu);
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			return coll;
		}
		return coll;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findByParentId(long)
	 */
	public List<Menu> findByParentId(long parentId) {
		List<Menu> coll = new ArrayList<Menu>();
		List<Menu> temp = findChildren(parentId, null);
		Iterator<Menu> iter = temp.iterator();

		while (iter.hasNext()) {
			Menu menu = iter.next();
			coll.add(menu);

			List<Menu> coll2 = findByParentId(menu.getId());

			if (coll2 != null) {
				coll.addAll(coll2);
			}
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#isWriteEnable(long, long)
	 */
	public boolean isWriteEnable(long menuId, long userId) {
		return isPermissionEnabled(Permission.WRITE, menuId, userId);
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#isReadEnable(long, long)
	 */
	@SuppressWarnings("unchecked")
	public boolean isReadEnable(long menuId, long userId) {
		boolean result = true;
		try {
			User user = userDAO.findById(userId);
			if (user == null)
				return false;
			if (user.isInGroup("admin"))
				return true;

			long id = menuId;
			Menu menu = findById(menuId);
			if (menu.getSecurityRef() != null)
				id = menu.getSecurityRef().longValue();

			Set<Group> Groups = user.getGroups();
			if (Groups.isEmpty())
				return false;

			Iterator iter = Groups.iterator();

			StringBuffer query = new StringBuffer("select distinct(_entity) from Menu _entity  ");
			query.append(" left join _entity.menuGroups as _group ");
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

			List<MenuGroup> coll = (List<MenuGroup>) getHibernateTemplate().find(query.toString(),
					new Object[] { new Long(id) });
			result = coll.size() > 0;
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findTextById(long)
	 */
	public String findTextById(long menuId) {
		Menu menu = this.findById(menuId);
		return menu.getText();
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findMenuIdByUserId(long)
	 *      <b>NOTE:</b> This implementation performs direct JDBC query, this is
	 *      required in order to obtain acceptable performances during searches.
	 */
	public List<Long> findMenuIdByUserId(long userId) {
		return findMenuIdByUserIdAndPermission(userId, Permission.READ, null);
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#isMenuWriteable(long, long)
	 */
	public int isMenuWriteable(long menuId, long userId) {
		boolean writePrivilegeBool = isWriteEnable(menuId, userId);
		int writePrivilegeInt = 0;

		if (writePrivilegeBool) {
			writePrivilegeInt = 1;
		}

		return writePrivilegeInt;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#hasWriteAccess(com.logicaldoc.core.security.Menu,
	 *      long)
	 */
	public boolean hasWriteAccess(Menu menu, long userId) {
		if (isWriteEnable(menu.getId(), userId) == false) {
			return false;
		}

		if (menu.getType() == Menu.MENUTYPE_DIRECTORY) {
			List<Menu> children = findByParentId(menu.getId());

			for (Menu subMenu : children) {
				if (!hasWriteAccess(subMenu, userId)) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findByGroupId(long)
	 */
	@SuppressWarnings("unchecked")
	public List<Menu> findByGroupId(long groupId) {
		List<Menu> coll = new ArrayList<Menu>();

		// The administrators can see all menues
		if (groupId == Group.GROUPID_ADMIN)
			return findAll();

		try {
			/*
			 * Search for menues that define its own security policies
			 */
			StringBuffer query = new StringBuffer("select distinct(_entity) from Menu _entity  ");
			query.append(" left join _entity.menuGroups as _group ");
			query.append(" where _entity.deleted=0 and _group.groupId =" + groupId);

			coll = (List<Menu>) getHibernateTemplate().find(query.toString(), null);

			/*
			 * Now search for all other menues that references the previous ones
			 */
			if (!coll.isEmpty()) {
				StringBuffer query2 = new StringBuffer("select _entity from Menu _entity where _entity.deleted=0 ");
				query2.append(" and _entity.securityRef in (");
				boolean first = true;
				for (Menu menu : coll) {
					if (!first)
						query2.append(",");
					query2.append(Long.toString(menu.getId()));
					first = false;
				}
				query2.append(")");
				List<Menu> coll2 = (List<Menu>) getHibernateTemplate().find(query2.toString(), null);
				for (Menu menu : coll2) {
					if (!coll.contains(menu))
						coll.add(menu);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findIdByUserId(long, long,
	 *      java.lang.Integer) <b>NOTE:</b> This implementation performs direct
	 *      JDBC query, this is required in order to obtain acceptable
	 *      performances during searches.
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public List<Long> findIdByUserId(long userId, long parentId, Integer type) {
		List<Long> ids = new ArrayList<Long>();
		try {
			User user = userDAO.findById(userId);
			if (user == null)
				return ids;
			if (user.isInGroup("admin"))
				return findIdsByWhere("_entity.parentId=" + parentId
						+ (type == null ? "" : " and _entity.type=" + type), null, null);

			StringBuffer query1 = new StringBuffer();
			Set<Group> precoll = user.getGroups();
			Iterator iter = precoll.iterator();
			if (!precoll.isEmpty()) {
				query1 = new StringBuffer("select distinct(A.ld_menuid) from ld_menugroup A, ld_menu B "
						+ " where B.ld_deleted=0 and A.ld_menuid=B.ld_id AND B.ld_parentid=" + parentId
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
				if (type != null)
					query1.append(" AND B.ld_type=" + type.toString());

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
				 * Now find all menues referencing the previously found ones
				 */
				StringBuffer query2 = new StringBuffer("select B.ld_id from ld_menu B where B.ld_deleted=0 ");
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

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findByText(java.lang.String)
	 */
	public List<Menu> findByText(String text) {
		return findByText(null, text, null, true);
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findByText(com.logicaldoc.core.security.Menu,
	 *      java.lang.String, java.lang.Integer)
	 */
	@Override
	public List<Menu> findByText(Menu parent, String text, Integer type, boolean caseSensitive) {
		StringBuffer query = null;
		if (caseSensitive)
			query = new StringBuffer("_entity.text like '" + SqlUtil.doubleQuotes(text) + "' ");
		else
			query = new StringBuffer("lower(_entity.text) like '" + SqlUtil.doubleQuotes(text.toLowerCase()) + "' ");

		if (parent != null)
			query.append(" AND _entity.parentId = " + parent.getId());
		if (type != null)
			query.append(" AND _entity.type = " + type.intValue());
		return findByWhere(query.toString(), null, null);
	}

	@Override
	public String computePathExtended(long menuId) {
		Menu menu = findById(menuId);
		if (menu == null)
			return null;
		String path = menuId != Menu.MENUID_DOCUMENTS ? menu.getText() : "";
		while (menu != null && menu.getId() != menu.getParentId() && menu.getId() != Menu.MENUID_DOCUMENTS) {
			menu = findById(menu.getParentId());
			if (menu != null)
				path = (menu.getId() != Menu.MENUID_DOCUMENTS ? menu.getText() : "") + "/" + path;
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
	private void saveFolderHistory(Menu folder, History transaction) {
		if (transaction == null)
			return;

		transaction.setNotified(0);
		transaction.setFolderId(folder.getId());
		transaction.setTitle(folder.getId() != Menu.MENUID_DOCUMENTS ? folder.getText() : "/");
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
		if (folder.getId() != folder.getParentId() && folder.getId() != Menu.MENUID_DOCUMENTS) {
			Menu parent = findById(folder.getParentId());
			// The parent menu can be 'null' when the user wants to delete a
			// folder with sub-folders under it (method 'deleteAll()').
			if (parent != null) {
				History parentHistory = new History();
				parentHistory.setFolderId(parent.getId());
				parentHistory.setTitle(parent.getId() != Menu.MENUID_DOCUMENTS ? parent.getText() : "/");
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
	public List<Menu> findByTextAndParentId(String text, long parentId) {
		return findByWhere("_entity.parentId = " + parentId + " and _entity.text like '" + SqlUtil.doubleQuotes(text)
				+ "'", null, null);
	}

	@Override
	public List<Menu> findParents(long menuId) {
		Menu menu = findById(menuId);
		List<Menu> coll = new ArrayList<Menu>();
		try {
			while (menu.getId() != Menu.MENUID_DOCUMENTS && menu.getId() != menu.getParentId()) {
				menu = findById(menu.getParentId());
				if (menu != null)
					coll.add(0, menu);
			}
		} catch (Exception e) {
			;
		}
		return coll;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#isPermissionEnabled(java.lang.String,
	 *      long, long)
	 */
	public boolean isPermissionEnabled(Permission permission, long menuId, long userId) {
		Set<Permission> permissions = getEnabledPermissions(menuId, userId);
		return permissions.contains(permission);
	}

	@Override
	public void restore(long menuId, boolean parents) {
		
		super.bulkUpdate("set ld_deleted=0 where ld_id=" + menuId, null);
		
		// Restore parents
		if (parents) {
			String query = "select ld_parentid from ld_menu where ld_id =" + menuId;
			List menus = queryForList(query, Long.class);
			
			//List<Object> menus = super.findByJdbcQuery(query, 1, null);
			for (Object id : menus) {
				Long xx = (Long) id;
				if (xx.longValue() != menuId)
					restore(xx, parents);
			}
		}
	}

	@Override
	public Set<Permission> getEnabledPermissions(long menuId, long userId) {
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

			// If the menu defines a security ref, use another menu to find the
			// policies
			long id = menuId;
			Menu menu = findById(menuId);
			if (menu.getSecurityRef() != null) {
				id = menu.getSecurityRef().longValue();
				log.debug("Use the security reference " + id);
			}

			StringBuffer query = new StringBuffer(
					"select A.LD_WRITE as LDWRITE, A.LD_ADDCHILD as LDADDCHILD, A.LD_MANAGESECURITY as LDMANAGESECURITY, A.LD_MANAGEIMMUTABILITY as LDMANAGEIMMUTABILITY, A.LD_DELETE as LDDELETE, A.LD_RENAME as LDRENAME, A.LD_BULKIMPORT as LDBULKIMPORT, A.LD_BULKEXPORT as LDBULKEXPORT, A.LD_SIGN as LDSIGN, A.LD_ARCHIVE as LDARCHIVE, A.LD_WORKFLOW as LDWORKFLOW");
			query.append(" from ld_menugroup A");
			query.append(" where ");
			query.append(" A.LD_MENUID=" + id);
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
					if (rs.getInt("LDADDCHILD") == 1)
						if (!permissions.contains(Permission.ADD_CHILD))
							permissions.add(Permission.ADD_CHILD);
					if (rs.getInt("LDBULKEXPORT") == 1)
						if (!permissions.contains(Permission.BULK_EXPORT))
							permissions.add(Permission.BULK_EXPORT);
					if (rs.getInt("LDBULKIMPORT") == 1)
						if (!permissions.contains(Permission.BULK_IMPORT))
							permissions.add(Permission.BULK_IMPORT);
					if (rs.getInt("LDDELETE") == 1)
						if (!permissions.contains(Permission.DELETE))
							permissions.add(Permission.DELETE);
					if (rs.getInt("LDMANAGEIMMUTABILITY") == 1)
						if (!permissions.contains(Permission.MANAGE_IMMUTABILITY))
							permissions.add(Permission.MANAGE_IMMUTABILITY);
					if (rs.getInt("LDMANAGESECURITY") == 1)
						if (!permissions.contains(Permission.MANAGE_SECURITY))
							permissions.add(Permission.MANAGE_SECURITY);
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

	@Override
	public List<Long> findMenuIdByUserIdAndPermission(long userId, Permission permission, Integer type) {
		List<Long> ids = new ArrayList<Long>();
		try {
			User user = userDAO.findById(userId);
			if (user == null)
				return ids;

			// The administrators have all permissions on all menues
			if (user.isInGroup("admin")) {
				if (type != null)
					return findIdsByWhere("_entity.type=" + type, null, null);
				else
					return findAllIds();
			}

			Set<Group> precoll = user.getGroups();
			Iterator<Group> iter = precoll.iterator();

			if (!precoll.isEmpty()) {
				/*
				 * Check menues that specify its own permissions
				 */
				StringBuffer query1 = new StringBuffer("select distinct(A.ld_menuid) from ld_menugroup A, ld_menu B "
						+ " where A.ld_menuid=B.ld_id and B.ld_deleted=0 ");
				if (type != null)
					query1.append("and (B.ld_type=" + type + " or B.ld_id=" + Menu.MENUID_DOCUMENTS + ")");
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
				 * Now search for those menues that references the previously
				 * found ones
				 */
				StringBuffer query2 = new StringBuffer("select B.ld_id from ld_menu B where B.ld_deleted=0 ");
				if (type != null)
					query2.append(" and B.ld_type=" + type);
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
	public void deleteAll(List<Menu> menus, History transaction) {
		for (Menu menu : menus) {
			try {
				History deleteHistory = (History) transaction.clone();
				deleteHistory.setEvent(History.EVENT_FOLDER_DELETED);
				deleteHistory.setFolderId(menu.getId());
				deleteHistory.setPath(computePathExtended(menu.getId()));
				delete(menu.getId(), deleteHistory);
			} catch (CloneNotSupportedException e) {
				log.error(e.getMessage(), e);
			}
		}

	}

	@Override
	public boolean delete(long menuId, History transaction) {
		boolean result = true;
		try {
			Menu menu = (Menu) getHibernateTemplate().get(Menu.class, menuId);
			menu.setDeleted(1);
			transaction.setEvent(History.EVENT_FOLDER_DELETED);
			transaction.setFolderId(menuId);
			store(menu, transaction);
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
			Menu parent = findById(id);
			Long securityRef = id;
			if (parent.getSecurityRef() != null)
				securityRef = parent.getSecurityRef();

			// Iterate over all children setting the security reference
			List<Menu> children = findChildren(id, null);
			for (Menu menu : children) {
				if (!securityRef.equals(menu.getSecurityRef())) {
					History tr = (History) transaction.clone();
					tr.setFolderId(menu.getId());
					menu.setSecurityRef(securityRef);
					menu.getMenuGroups().clear();
					store(menu, tr);
				}
				applyRithtToTree(menu.getId(), transaction);
			}
		} catch (Throwable e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

}