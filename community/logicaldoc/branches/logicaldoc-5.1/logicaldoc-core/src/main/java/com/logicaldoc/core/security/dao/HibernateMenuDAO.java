package com.logicaldoc.core.security.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

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

	private HibernateMenuDAO() {
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
		return store(menu, true, null);
	}

	@Override
	public boolean store(Menu menu, History transaction) {
		return store(menu, true, transaction);
	}

	@Override
	public boolean store(Menu menu, boolean updatePathExtended, History transaction) {
		boolean result = true;

		try {
			List<Object> old = (List<Object>) findByJdbcQuery(
					"select ld_text,ld_pathextended from ld_menu where ld_id=?", 2, new Object[] { menu.getId() });
			String oldText = menu.getText();
			String oldPathExt = menu.getPathExtended();
			if (!old.isEmpty()) {
				oldText = (String) (((Object[]) old.get(0))[0]);
				oldPathExt = (String) (((Object[]) old.get(0))[1]);
			}

			menu.setPath(menu.getPath().replaceAll("//", "/"));
			getHibernateTemplate().saveOrUpdate(menu);

			if (updatePathExtended) {
				// We need to update the path extended
				updatePathExtended(menu, !oldText.equals(menu.getText()) || !menu.getPathExtended().equals(oldPathExt));
			}

			saveFolderHistory(menu, transaction);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findByUserId(long)
	 */
	@SuppressWarnings("unchecked")
	public List<Menu> findByUserId(long userId) {
		List<Menu> coll = new ArrayList<Menu>();
		try {
			User user = userDAO.findById(userId);
			Set<Group> precoll = user.getGroups();
			Iterator iter = precoll.iterator();

			if (!precoll.isEmpty()) {
				StringBuffer query = new StringBuffer("select distinct(_menu) from Menu _menu  ");
				query.append(" left outer join _menu.menuGroups as _group ");
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
				coll = (List<Menu>) getHibernateTemplate().find(query.toString());
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
			Set<Group> precoll = user.getGroups();
			Iterator iter = precoll.iterator();
			if (precoll.isEmpty())
				return coll;

			StringBuffer query = new StringBuffer("select distinct(_entity) from Menu _entity ");
			query.append(" left outer join _entity.menuGroups as _group");
			query.append(" where _group.groupId in (");

			boolean first = true;
			while (iter.hasNext()) {
				if (!first)
					query.append(",");
				Group ug = (Group) iter.next();
				query.append(Long.toString(ug.getId()));
				first = false;
			}
			query.append(") and _entity.parentId = ? and _entity.id!=_entity.parentId");
			if (type != null)
				query.append(" and _entity.type = " + type.toString());
			query.append(" order by _entity.type, _entity.sort, _entity.text");

			coll = (List<Menu>) getHibernateTemplate().find(query.toString(), parentId);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#countByUserId(long, long,
	 *      java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	public long countByUserId(long userId, long parentId, Integer type) {
		long count = 0;
		try {
			User user = userDAO.findById(userId);
			Set<Group> precoll = user.getGroups();
			Iterator iter = precoll.iterator();
			if (precoll.isEmpty())
				return count;
			StringBuffer query = new StringBuffer("select  count(_entity.id) from Menu _entity ");
			query.append(" left outer join _entity.menuGroups as _group");
			query.append(" where _group.groupId in (");

			boolean first = true;
			while (iter.hasNext()) {
				if (!first)
					query.append(",");
				Group ug = (Group) iter.next();
				query.append(Long.toString(ug.getId()));
				first = false;
			}
			query.append(") and _entity.parentId = ?");
			if (type != null)
				query.append(" and _entity.type = " + type.toString());
			count = ((Long) getHibernateTemplate().find(query.toString(), parentId).get(0)).intValue();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return count;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findChildren(long)
	 */
	public List<Menu> findChildren(long parentId) {
		return findByWhere("_entity.parentId = ? and _entity.id!=_entity.parentId", new Object[] { parentId }, null);
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findByParentId(long)
	 */
	public List<Menu> findByParentId(long parentId) {
		List<Menu> coll = new ArrayList<Menu>();
		List<Menu> temp = findChildren(parentId);
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
			try {
				User user = userDAO.findById(userId);
				Set<Group> Groups = user.getGroups();
				if (Groups.isEmpty())
					return false;
				Iterator iter = Groups.iterator();

				StringBuffer query = new StringBuffer("select distinct(_entity) from Menu _entity  ");
				query.append(" left outer join _entity.menuGroups as _group ");
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
						new Object[] { new Long(menuId) });
				result = coll.size() > 0;
			} catch (Exception e) {
				if (log.isErrorEnabled())
					log.error(e.getMessage(), e);
				result = false;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findTextByMenuId(long)
	 */
	public String findTextByMenuId(long menuId) {
		Menu menu = this.findById(menuId);
		return menu.getText();
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findMenuIdByUserId(long)
	 *      <b>NOTE:</b> This implementation performs direct JDBC query, this is
	 *      required in order to obtain acceptable performances during searches.
	 */
	public Set<Long> findMenuIdByUserId(long userId) {
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

		try {
			StringBuffer query = new StringBuffer("select distinct(_entity) from Menu _entity  ");
			query.append(" left outer join _entity.menuGroups as _group ");
			query.append(" where _group.groupId = ?");

			coll = (List<Menu>) getHibernateTemplate().find(query.toString(), new Long(groupId));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findMenuIdByUserId(long,
	 *      long, java.lang.Integer) <b>NOTE:</b> This implementation performs
	 *      direct JDBC query, this is required in order to obtain acceptable
	 *      performances during searches.
	 */
	@SuppressWarnings( { "unchecked", "deprecation" })
	public Set<Long> findMenuIdByUserId(long userId, long parentId, Integer type) {
		Set<Long> ids = new HashSet<Long>();
		try {
			User user = userDAO.findById(userId);
			Set<Group> precoll = user.getGroups();
			Iterator iter = precoll.iterator();

			if (!precoll.isEmpty()) {
				StringBuffer query = new StringBuffer("select distinct(A.ld_menuid) from ld_menugroup A, ld_menu B "
						+ " where B.ld_deleted=0 and A.ld_menuid=B.ld_id AND B.ld_parentid=" + parentId
						+ " AND A.ld_groupid in (");
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
					query.append(" AND B.ld_type=" + type.toString());

				Connection con = null;
				Statement stmt = null;
				ResultSet rs = null;

				try {
					con = getSession().connection();
					stmt = con.createStatement();
					rs = stmt.executeQuery(query.toString());
					while (rs.next()) {
						ids.add(new Long(rs.getInt(1)));
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
		return findByText(null, text, null);
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findByText(com.logicaldoc.core.security.Menu,
	 *      java.lang.String, java.lang.Integer)
	 */
	@Override
	public List<Menu> findByText(Menu parent, String text, Integer type) {
		StringBuffer query = new StringBuffer("_entity.text like '" + SqlUtil.doubleQuotes(text) + "' ");
		if (parent != null)
			query.append(" AND _entity.parentId = " + parent.getId());
		if (type != null)
			query.append(" AND _entity.type = " + type.intValue());
		return findByWhere(query.toString(), null);
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
		transaction.setTitle(folder.getText());
		transaction.setPath(folder.getPathExtended() + "/" + folder.getText());
		transaction.setPath(transaction.getPath().replaceAll("//", "/"));
		transaction.setPath(transaction.getPath().replaceFirst("/menu.documents/", "/"));
		transaction.setPath(transaction.getPath().replaceFirst("/menu.documents", "/"));
		transaction.setComment("");

		historyDAO.store(transaction);

		// Check if is necessary to add a new history entry for the parent
		// folder. This operation is not recursive, because we want to notify
		// only the parent folder.
		if (folder.getId() != folder.getParentId() && folder.getId() != Menu.MENUID_DOCUMENTS) {
			Menu parent = findById(folder.getParentId());
			// The parent menu can be 'null' when the user wants to delete a
			// folder with subfolders under it (method 'deleteAll()').
			if (parent != null) {
				History parentHistory = new History();
				parentHistory.setFolderId(parent.getId());
				parentHistory.setTitle(parent.getText());

				parentHistory.setPath(parent.getPathExtended() + "/" + parent.getText() + "/" + folder.getText());
				parentHistory.setPath(parentHistory.getPath().replaceAll("//", "/"));
				parentHistory.setPath(parentHistory.getPath().replaceFirst("/menu.documents/", "/"));
				parentHistory.setPath(parentHistory.getPath().replaceFirst("/menu.documents", "/"));

				parentHistory.setUserId(transaction.getUserId());
				parentHistory.setUserName(transaction.getUserName());
				if (transaction.getEvent().equals(History.EVENT_FOLDER_CREATED)) {
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
	public Menu createFolder(Menu parent, String name, History transaction) {
		Menu menu = new Menu();
		menu.setText(name);
		menu.setParentId(parent.getId());
		menu.setSort(0);
		menu.setIcon("folder.png");
		menu.setPath(parent.getPath() + "/" + parent.getId());
		menu.setType(Menu.MENUTYPE_DIRECTORY);
		for (MenuGroup mg : parent.getMenuGroups()) {
			menu.getMenuGroups().add(mg);
		}

		setUniqueFolderName(menu);
		if (transaction != null)
			transaction.setEvent(History.EVENT_FOLDER_CREATED);
		if (store(menu, transaction) == false)
			return null;
		return menu;
	}

	@Override
	public void setUniqueFolderName(Menu menu) {
		int counter = 1;
		String folderName = menu.getText();
		while (findByMenuTextAndParentId(menu.getText(), menu.getParentId()).size() > 0) {
			menu.setText(folderName + "(" + (counter++) + ")");
		}
	}

	@Override
	public List<Menu> findByMenuTextAndParentId(String text, long parentId) {
		return findByWhere("_entity.parentId = " + parentId + " and _entity.text like '" + SqlUtil.doubleQuotes(text)
				+ "'", null);
	}

	@Override
	public Menu createFolders(Menu parent, String path, History transaction) {
		StringTokenizer st = new StringTokenizer(path, "/", false);

		Menu menu = parent;
		while (st.hasMoreTokens()) {
			String name = st.nextToken();
			List<Menu> childs = findByText(menu, name, Menu.MENUTYPE_DIRECTORY);
			Menu dir;
			if (childs.isEmpty())
				dir = createFolder(menu, name, transaction);
			else {
				dir = childs.iterator().next();
			}
			menu = dir;
		}
		return menu;
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
	 * This utility method updates all pathExtended attributes of the hierarchy
	 * starting from the specified menu
	 */
	private void updatePathExtended(Menu menu, boolean recursive) {
		// Prepare the pathExtended for this menu
		StringBuffer pathExtended = new StringBuffer("/");
		List<Menu> parents = findParents(menu.getId());
		for (Menu parent : parents) {
			pathExtended.append(parent.getText());
			pathExtended.append("/");
		}
		// Set it and save
		menu.setPathExtended(pathExtended.toString().replaceAll("//", "/"));

		try {
			getHibernateTemplate().saveOrUpdate(menu);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		// Recursively invoke the method on all direct children
		if (recursive) {
			List<Menu> children = findByParentId(menu.getId());
			for (Menu child : children) {
				updatePathExtended(child, recursive);
			}
		}
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
	public List<Menu> findFoldersByPathExtended(String path) {
		List<Menu> specified_menu = new ArrayList<Menu>();
		specified_menu = (List<Menu>) findByWhere("_entity.pathExtended = '" + SqlUtil.doubleQuotes(path) + "'", null);
		if (specified_menu != null && specified_menu.size() > 0)
			return specified_menu;
		return null;
	}

	@Override
	public Menu findFolder(String folderName, String pathExtended) {
		List<Menu> specified_menu = findByWhere("_entity.text = '" + SqlUtil.doubleQuotes(folderName)
				+ "' AND _entity.pathExtended = '" + SqlUtil.doubleQuotes(pathExtended) + "'", null);
		if (specified_menu != null && specified_menu.size() > 0)
			return specified_menu.iterator().next();
		return null;
	}

	@Override
	public void restore(long menuId, boolean parents) {
		super.bulkUpdate("set ld_deleted=0 where ld_id=" + menuId, null);
		// Restore parents
		if (parents) {
			List<Object> menus = super.findByJdbcQuery("select ld_parentid from ld_menu where ld_id =" + menuId, 1,
					null);
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
			Set<Group> groups = user.getGroups();
			if (groups.isEmpty())
				return permissions;
			Iterator<Group> iter = groups.iterator();

			StringBuffer query = new StringBuffer(
					"select ldmenugroup.LD_WRITE as LDWRITE, ldmenugroup.LD_ADDCHILD as LDADDCHILD, ldmenugroup.LD_MANAGESECURITY as LDMANAGESECURITY, ldmenugroup.LD_MANAGEIMMUTABILITY as LDMANAGEIMMUTABILITY, ldmenugroup.LD_DELETE as LDDELETE, ldmenugroup.LD_RENAME as LDRENAME, ldmenugroup.LD_BULKIMPORT as LDBULKIMPORT, ldmenugroup.LD_BULKEXPORT as LDBULKEXPORT, ldmenugroup.LD_SIGN as LDSIGN, ldmenugroup.LD_ARCHIVE as LDARCHIVE, ldmenugroup.LD_WORKFLOW as LDWORKFLOW");
			query.append(" from ld_menugroup ldmenugroup");
			query.append(" where ");
			query.append(" ldmenugroup.LD_MENUID=" + menuId);
			query.append(" and ldmenugroup.LD_GROUPID in (");

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
	public Set<Long> findMenuIdByUserIdAndPermission(long userId, Permission permission, Integer type) {
		Set<Long> ids = new HashSet<Long>();
		try {
			User user = userDAO.findById(userId);
			Set<Group> precoll = user.getGroups();
			Iterator<Group> iter = precoll.iterator();

			if (!precoll.isEmpty()) {
				StringBuffer query = new StringBuffer("select distinct(A.ld_menuid) from ld_menugroup A, ld_menu B "
						+ " where A.ld_menuid=B.ld_id and B.ld_deleted=0 ");
				if (type != null)
					query.append("and B.ld_type=" + type);
				if (permission != Permission.READ)
					query.append(" and A.ld_" + permission.getName() + "=1 ");
				query.append(" and A.ld_groupid in (");
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
						ids.add(new Long(rs.getInt(1)));
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
			store(menu, transaction);
		} catch (Throwable e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}
}