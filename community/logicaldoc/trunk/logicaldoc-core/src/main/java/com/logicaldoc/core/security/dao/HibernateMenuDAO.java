package com.logicaldoc.core.security.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.MenuGroup;
import com.logicaldoc.core.security.User;

/**
 * Hibernate implementation of <code>MenuDAO</code>
 * 
 * @author Marco Meschieri
 * @version $Id: HibernateMenuDAO.java,v 1.1 2007/06/29 06:28:25 marco Exp $
 * @since 3.0
 */
public class HibernateMenuDAO extends HibernateDaoSupport implements MenuDAO {

	protected static Log log = LogFactory.getLog(HibernateMenuDAO.class);

	private UserDAO userDAO;

	private HibernateMenuDAO() {
	}

	public UserDAO getUserDAO() {
		return userDAO;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#store(com.logicaldoc.core.security.Menu)
	 */
	public boolean store(Menu menu) {
		boolean result = true;

		try {
			getHibernateTemplate().saveOrUpdate(menu);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result = false;
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#delete(int)
	 */
	public boolean delete(long menuId) {
		boolean result = true;

		try {
			Menu menu = findByPrimaryKey(menuId);
			if (menu != null) {
				menu.getMenuGroups().clear();
				getHibernateTemplate().delete(menu);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findByPrimaryKey(long)
	 */
	public Menu findByPrimaryKey(long menuId) {
		Menu menu = new Menu();

		try {
			menu = (Menu) getHibernateTemplate().get(Menu.class, menuId);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return menu;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findByUserName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Menu> findByUserName(String username) {
		Collection<Menu> coll = new ArrayList<Menu>();
		try {
			User user = userDAO.findByUserName(username);
			Collection<Group> precoll = user.getGroups();
			Iterator iter = precoll.iterator();

			if (!precoll.isEmpty()) {
				StringBuffer query = new StringBuffer(
						"select distinct(_menu) from Menu _menu  ");
				query.append(" left outer join _menu.menuGroups as _group ");
				query.append(" where _group.groupName in (");

				boolean first = true;
				while (iter.hasNext()) {
					if (!first)
						query.append(",");
					Group ug = (Group) iter.next();
					query.append("'" + ug.getGroupName() + "'");
					first = false;
				}
				query.append(")");
				coll = (Collection<Menu>) getHibernateTemplate().find(
						query.toString());
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findByUserName(java.lang.String,
	 *      long)
	 */
	public Collection<Menu> findByUserName(String username, long parentId) {
		return findByUserName(username, parentId, null);
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findByUserName(java.lang.String,
	 *      long, type)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Menu> findByUserName(String username, long parentId,
			Integer type) {
		Collection<Menu> coll = new ArrayList<Menu>();

		try {
			User user = userDAO.findByUserName(username);
			Collection<Group> precoll = user.getGroups();
			Iterator iter = precoll.iterator();
			if (precoll.isEmpty())
				return coll;

			StringBuffer query = new StringBuffer(
					"select  distinct(_menu) from Menu _menu ");
			query.append(" left outer join _menu.menuGroups as _group");
			query.append(" where _group.groupName in (");

			boolean first = true;
			while (iter.hasNext()) {
				if (!first)
					query.append(",");
				Group ug = (Group) iter.next();
				query.append("'" + ug.getGroupName() + "'");
				first = false;
			}
			query.append(") and _menu.parentId = ?");
			if (type != null)
				query.append(" and _menu.type = " + type.toString());
			query.append(" order by _menu.type, _menu.sort, _menu.text");

			coll = (Collection<Menu>) getHibernateTemplate().find(
					query.toString(), parentId);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#countByUserName(java.lang.String,
	 *      long, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	public long countByUserName(String username, long parentId, Integer type) {
		long count = 0;
		try {
			User user = userDAO.findByUserName(username);
			Collection<Group> precoll = user.getGroups();
			Iterator iter = precoll.iterator();
			if (precoll.isEmpty())
				return count;
			StringBuffer query = new StringBuffer(
					"select  count(_menu.id) from Menu _menu ");
			query.append(" left outer join _menu.menuGroups as _group");
			query.append(" where _group.groupName in (");

			boolean first = true;
			while (iter.hasNext()) {
				if (!first)
					query.append(",");
				Group ug = (Group) iter.next();
				query.append("'" + ug.getGroupName() + "'");
				first = false;
			}
			query.append(") and _menu.parentId = ?");
			if (type != null)
				query.append(" and _menu.type = " + type.toString());
			count = ((Long) getHibernateTemplate().find(query.toString(),
					parentId).get(0)).intValue();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return count;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findChildren(long)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Menu> findChildren(long parentId) {
		Collection<Menu> coll = null;

		try {
			coll = (Collection<Menu>) getHibernateTemplate().find(
					"from Menu _menu where _menu.parentId = ?",
					new Object[] { parentId });
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findByParentId(long)
	 */
	public Collection<Menu> findByParentId(long parentId) {
		Collection<Menu> coll = new ArrayList<Menu>();
		Collection<Menu> temp = findChildren(parentId);
		Iterator<Menu> iter = temp.iterator();

		while (iter.hasNext()) {
			Menu menu = iter.next();
			coll.add(menu);

			Collection<Menu> coll2 = findByParentId(menu.getId());

			if (coll2 != null) {
				coll.addAll(coll2);
			}
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#isWriteEnable(long,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public boolean isWriteEnable(long menuId, String username) {
		boolean result = true;

		try {
			User user = userDAO.findByUserName(username);
			Collection<Group> Groups = user.getGroups();
			if (Groups.isEmpty())
				return false;
			Iterator iter = Groups.iterator();

			StringBuffer query = new StringBuffer(
					"select distinct(_menu) from Menu _menu  ");
			query.append(" left outer join _menu.menuGroups as _group ");
			query.append(" where _group.groupName in (");

			boolean first = true;
			while (iter.hasNext()) {
				if (!first)
					query.append(",");
				Group ug = (Group) iter.next();
				query.append("'" + ug.getGroupName() + "'");
				first = false;
			}
			query.append(") and _group.writeEnable=1 and _menu.id=?");

			Collection<MenuGroup> coll = (Collection<MenuGroup>) getHibernateTemplate()
					.find(query.toString(), new Object[] { new Long(menuId) });

			result = coll.size() > 0;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#isReadEnable(long,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public boolean isReadEnable(long menuId, String username) {
		boolean result = true;

		try {
			try {
				User user = userDAO.findByUserName(username);
				Collection<Group> Groups = user.getGroups();
				if (Groups.isEmpty())
					return false;
				Iterator iter = Groups.iterator();

				StringBuffer query = new StringBuffer(
						"select distinct(_menu) from Menu _menu  ");
				query.append(" left outer join _menu.menuGroups as _group ");
				query.append(" where _group.groupName in (");

				boolean first = true;
				while (iter.hasNext()) {
					if (!first)
						query.append(",");
					Group ug = (Group) iter.next();
					query.append("'" + ug.getGroupName() + "'");
					first = false;
				}
				query.append(") and _menu.id=?");

				Collection<MenuGroup> coll = (Collection<MenuGroup>) getHibernateTemplate()
						.find(query.toString(),
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
		Menu menu = this.findByPrimaryKey(menuId);
		return menu.getText();
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findMenuIdByUserName(java.lang.String)
	 *      <b>NOTE:</b> This implementation performs direct JDBC query, this
	 *      is required in order to obtain acceptable performances during
	 *      searches.
	 */
	@SuppressWarnings("unchecked")
	public Set<Long> findMenuIdByUserName(String username) {
		Set<Long> ids = new HashSet<Long>();
		try {
			User user = userDAO.findByUserName(username);
			Collection<Group> precoll = user.getGroups();
			Iterator iter = precoll.iterator();

			if (!precoll.isEmpty()) {
				StringBuffer query = new StringBuffer(
						"select distinct(A.ld_menuid) from ld_menugroup A "
								+ " where A.ld_groupname in (");
				boolean first = true;
				while (iter.hasNext()) {
					if (!first)
						query.append(",");
					Group ug = (Group) iter.next();
					query.append("'" + ug.getGroupName() + "'");
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
	 * @see com.logicaldoc.core.security.dao.MenuDAO#isMenuWriteable(long,
	 *      java.lang.String)
	 */
	public Integer isMenuWriteable(long menuId, String userName) {
		boolean writePrivilegeBool = isWriteEnable(menuId, userName);
		int writePrivilegeInt = 0;

		if (writePrivilegeBool) {
			writePrivilegeInt = 1;
		}

		return new Integer(writePrivilegeInt);
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#hasWriteAccess(com.logicaldoc.core.security.Menu,
	 *      java.lang.String)
	 */
	public boolean hasWriteAccess(Menu menu, String p_userName) {
		if (isWriteEnable(menu.getId(), p_userName) == false) {
			return false;
		}

		if (menu.getType() == Menu.MENUTYPE_DIRECTORY) {
			Collection<Menu> children = findByParentId(menu.getId());

			for (Menu subMenu : children) {
				if (!hasWriteAccess(subMenu, p_userName)) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findByGroup(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Menu> findByGroupName(String groupName) {
		Collection<Menu> coll = new ArrayList<Menu>();

		try {
			StringBuffer query = new StringBuffer(
					"select distinct(_menu) from Menu _menu  ");
			query.append(" left outer join _menu.menuGroups as _group ");
			query.append(" where _group.groupName = ?");

			coll = (Collection<Menu>) getHibernateTemplate().find(
					query.toString(), groupName);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findMenuIdByUserNameAndParent(java.lang.String,
	 *      long, java.lang.Integer) <b>NOTE:</b> This implementation performs
	 *      direct JDBC query, this is required in order to obtain acceptable
	 *      performances during searches.
	 */
	@SuppressWarnings("unchecked")
	public Set<Long> findMenuIdByUserName(String username, long parentId,
			Integer type) {
		Set<Long> ids = new HashSet<Long>();
		try {
			User user = userDAO.findByUserName(username);
			Collection<Group> precoll = user.getGroups();
			Iterator iter = precoll.iterator();

			if (!precoll.isEmpty()) {
				StringBuffer query = new StringBuffer(
						"select distinct(A.ld_menuid) from ld_menugroup A, ld_menu B "
								+ " where A.ld_menuid=B.ld_id AND B.ld_parentid="
								+ parentId + " AND A.ld_groupname in (");
				boolean first = true;
				while (iter.hasNext()) {
					if (!first)
						query.append(",");
					Group ug = (Group) iter.next();
					query.append("'" + ug.getGroupName() + "'");
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
	public Collection<Menu> findByText(String text) {
		return findByText(null, text, null);
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findByText(com.logicaldoc.core.security.Menu,
	 *      java.lang.String, java.lang.Integer)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Collection<Menu> findByText(Menu parent, String text, Integer type) {
		Collection<Menu> coll = new ArrayList<Menu>();
		StringBuffer query = new StringBuffer(
				"from Menu _menu where _menu.text = ? ");
		if (parent != null)
			query.append(" AND _menu.parentId = " + parent.getId());
		if (type != null)
			query.append(" AND _menu.type = " + type.intValue());

		try {
			coll = (Collection<Menu>) getHibernateTemplate().find(
					query.toString(), new Object[] { text });
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return coll;
	}

	@Override
	public Menu createFolder(Menu parent, String name) {
		Menu menu = new Menu();
		menu.setText(name);
		menu.setParentId(parent.getId());
		menu.setSort(0);
		menu.setIcon("folder.gif");
		menu.setPath((parent.getPath() + "/" + parent.getId()).replaceAll("//", "/"));
		menu.setType(Menu.MENUTYPE_DIRECTORY);
		for (MenuGroup mg : parent.getMenuGroups()) {
			menu.getMenuGroups().add(mg);
		}

		if (store(menu) == false)
			return null;
		return menu;
	}

	@Override
	public Menu createFolders(Menu parent, String path) {
		StringTokenizer st = new StringTokenizer(path, "/", false);

		Menu menu = parent;
		while (st.hasMoreTokens()) {
			String name = st.nextToken();
			Collection<Menu> childs = findByText(menu, name,
					Menu.MENUTYPE_DIRECTORY);
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
		Menu menu = findByPrimaryKey(menuId);
		List<Menu> coll = new ArrayList<Menu>();
		try {
			while (menu.getId() != Menu.MENUID_DOCUMENTS) {
				menu = findByPrimaryKey(menu.getParentId());
				if (menu != null)
					coll.add(0, menu);
			}
		} catch (Exception e) {
			;
		}
		return coll;

	}
}