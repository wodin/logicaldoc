package com.logicaldoc.core.security.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.MenuGroup;
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.core.security.User;

/**
 * Hibernate implementation of <code>MenuDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class HibernateMenuDAO extends HibernatePersistentObjectDAO<Menu> implements MenuDAO {

	private UserDAO userDAO;

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

	public boolean store(Menu menu) {
		boolean result = true;

		try {
			menu.setPath(menu.getPath().replaceAll("//", "/"));
			getHibernateTemplate().saveOrUpdate(menu);
			updatePathExtended(menu);
		} catch (Exception e) {
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
		return findByWhere("_entity.parentId = ? and _entity.id!=_entity.parentId", new Object[] { parentId });
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
	@SuppressWarnings("unchecked")
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
	 *      <b>NOTE:</b> This implementation performs direct JDBC query, this
	 *      is required in order to obtain acceptable performances during
	 *      searches.
	 */
	@SuppressWarnings( { "unchecked", "deprecation" })
	public Set<Long> findMenuIdByUserId(long userId) {
		Set<Long> ids = new HashSet<Long>();
		try {
			User user = userDAO.findById(userId);
			Set<Group> precoll = user.getGroups();
			Iterator iter = precoll.iterator();

			if (!precoll.isEmpty()) {
				StringBuffer query = new StringBuffer("select distinct(A.ld_menuid) from ld_menugroup A "
						+ " where A.ld_groupid in (");
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
						+ " where A.ld_menuid=B.ld_id AND B.ld_parentid=" + parentId + " AND A.ld_groupid in (");
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
		StringBuffer query = new StringBuffer("_entity.text like '" + text + "' ");
		if (parent != null)
			query.append(" AND _entity.parentId = " + parent.getId());
		if (type != null)
			query.append(" AND _entity.type = " + type.intValue());
		return findByWhere(query.toString());
	}

	@Override
	public Menu createFolder(Menu parent, String name) {
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

		if (store(menu) == false)
			return null;
		return menu;
	}

	private void setUniqueFolderName(Menu menu) {
		int counter = 1;
		String folderName = menu.getText();
		while (findByMenuTextAndParentId(menu.getText(), menu.getParentId()).size() > 0) {
			menu.setText(folderName + "(" + (counter++) + ")");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Menu> findByMenuTextAndParentId(String text, long parentId) {
		return findByWhere("_entity.parentId = " + parentId + " and _entity.text like '" + text + "'");
	}

	@Override
	public Menu createFolders(Menu parent, String path) {
		StringTokenizer st = new StringTokenizer(path, "/", false);

		Menu menu = parent;
		while (st.hasMoreTokens()) {
			String name = st.nextToken();
			List<Menu> childs = findByText(menu, name, Menu.MENUTYPE_DIRECTORY);
			Menu dir;
			if (childs.isEmpty())
				dir = createFolder(menu, name);
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
	private void updatePathExtended(Menu menu) {
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
		List<Menu> children = findByParentId(menu.getId());
		for (Menu child : children) {
			updatePathExtended(child);
		}
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#isPermissionEnabled(java.lang.String,
	 *      long, long)
	 */
	@SuppressWarnings("unchecked")
	public boolean isPermissionEnabled(Permission permission, long menuId, long userId) {
		if (Permission.READ == permission)
			return isReadEnable(menuId, userId);

		boolean result = true;

		try {
			User user = userDAO.findById(userId);
			Set<Group> groups = user.getGroups();
			if (groups.isEmpty())
				return false;
			Iterator<Group> iter = groups.iterator();

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
			query.append(") and _group." + permission.getName() + "=1 and _entity.id=?");

			List<MenuGroup> coll = (List<MenuGroup>) getHibernateTemplate().find(query.toString(),
					new Object[] { new Long(menuId) });

			result = coll.size() > 0;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	@Override
	public List<Menu> findFoldersByPathExtended(String path) {
		List<Menu> specified_menu = new ArrayList<Menu>();
		specified_menu = (List<Menu>) findByWhere("_entity.pathExtended = '" + path + "'");
		if (specified_menu != null && specified_menu.size() > 0)
			return specified_menu;
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Menu findFolder(String folderName, String pathExtended) {
		List<Menu> specified_menu = findByWhere("_entity.text = '" + folderName + "' AND _entity.pathExtended = '"
				+ pathExtended + "'");
		if (specified_menu != null && specified_menu.size() > 0)
			return specified_menu.iterator().next();
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void restore(long menuId, boolean parents) {
		Connection con = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		try {
			con = getSession().connection();

			StringBuffer query = new StringBuffer("update ld_menu _entity  ");
			query.append(" set _entity.ld_deleted=0 ");
			query.append(" where _entity.ld_id = ?");
			stmt = con.prepareStatement(query.toString());

			// Restore the menu
			stmt.setLong(1, menuId);
			stmt.execute();

			// Restore parents
			if (parents) {
				query = new StringBuffer("select _entity.ld_parentid from ld_menu _entity  ");
				query.append(" where _entity.ld_id = ?");
				stmt2 = con.prepareStatement(query.toString());

				stmt2.setLong(1, menuId);
				ResultSet rs = stmt2.executeQuery();
				long parent = -1;
				while (rs.next() && parent != rs.getLong(1)) {
					parent = rs.getLong(1);
					stmt.setLong(1, parent);
					stmt.execute();
					stmt2.setLong(1, parent);
					rs = stmt2.executeQuery();
				}
				rs.close();
			}
			stmt.close();
			stmt2.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}