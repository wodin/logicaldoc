package com.logicaldoc.core.security.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.logicaldoc.core.FileBean;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.DownloadTicketDAO;
import com.logicaldoc.core.document.dao.TermDAO;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.MenuGroup;
import com.logicaldoc.core.security.User;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.SettingsConfig;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Hibernate implementation of <code>MenuDAO</code>
 * 
 * @author Marco Meschieri
 * @version $Id: HibernateMenuDAO.java,v 1.1 2007/06/29 06:28:25 marco Exp $
 * @since 3.0
 */
public class HibernateMenuDAO extends HibernateDaoSupport implements MenuDAO {

	protected static Log log = LogFactory.getLog(HibernateMenuDAO.class);

	private DownloadTicketDAO ticketDAO;

	private UserDAO userDAO;

	private TermDAO termDAO;

	private UserDocDAO userDocDAO;

	private SettingsConfig settings;

	private HibernateMenuDAO() {
	}

	public SettingsConfig getSettings() {
		return settings;
	}

	public void setSettings(SettingsConfig settings) {
		this.settings = settings;
	}

	public UserDAO getUserDAO() {
		return userDAO;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public TermDAO getTermDAO() {
		return termDAO;
	}

	public void setTermDAO(TermDAO termDAO) {
		this.termDAO = termDAO;
	}

	public DownloadTicketDAO getTicketDAO() {
		return ticketDAO;
	}

	public void setTicketDAO(DownloadTicketDAO ticketDAO) {
		this.ticketDAO = ticketDAO;
	}

	public UserDocDAO getUserDocDAO() {
		return userDocDAO;
	}

	public void setUserDocDAO(UserDocDAO userDocDAO) {
		this.userDocDAO = userDocDAO;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#store(com.logicaldoc.core.security.Menu)
	 */
	public boolean store(Menu menu) {
		boolean result = true;

		try {
			if (menu.getMenuType() == Menu.MENUTYPE_FILE) {
				long size = FileBean.getSize(settings.getValue("docdir") + "/" + menu.getMenuPath() + "/"
						+ menu.getMenuId() + "/" + menu.getMenuRef());
				menu.setMenuSize(size);
			}
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
	public boolean delete(int menuId) {
		boolean result = true;

		try {
			termDAO.delete(menuId);
			ticketDAO.deleteByMenuId(menuId);
			userDocDAO.delete(menuId);
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
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findByPrimaryKey(int)
	 */
	public Menu findByPrimaryKey(int menuId) {
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
			User user = userDAO.findByPrimaryKey(username);
			Collection<Group> precoll = user.getGroups();
			Iterator iter = precoll.iterator();

			if (!precoll.isEmpty()) {
				StringBuffer query = new StringBuffer(
						"select distinct(_menu) from com.logicaldoc.core.security.Menu _menu  ");
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
				coll = (Collection<Menu>) getHibernateTemplate().find(query.toString());
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findByUserNameAndKeyword(java.lang.String,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Menu> findByUserNameAndKeyword(String username, String keyword) {
		Collection<Menu> coll = new ArrayList<Menu>();
		try {
			User user = userDAO.findByPrimaryKey(username);
			Collection<Group> precoll = user.getGroups();
			Iterator iter = precoll.iterator();
			if (precoll.isEmpty())
				return coll;

			StringBuffer query = new StringBuffer("select distinct(_menu) from com.logicaldoc.core.security.Menu _menu  ");
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

			DocumentDAO documentDAO = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			Collection<Integer> ids = documentDAO.findMenuIdByKeyword(keyword);
			iter = ids.iterator();
			if (ids.isEmpty())
				return coll;
			query.append("and _menu.menuId in (");
			first = true;
			while (iter.hasNext()) {
				if (!first)
					query.append(",");
				query.append("'" + iter.next() + "'");
				first = false;
			}
			query.append(")");

			coll = (Collection<Menu>) getHibernateTemplate().find(query.toString());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findByUserNameAndKeyword(java.lang.String,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Menu> findMenuIdByUserNameAndKeyword(String username, String keyword) {
		Collection<Menu> coll = new ArrayList<Menu>();
		try {
			User user = userDAO.findByPrimaryKey(username);
			Collection<Group> precoll = user.getGroups();
			Iterator iter = precoll.iterator();
			if (precoll.isEmpty())
				return coll;

			StringBuffer query = new StringBuffer("select distinct(_menu) from com.logicaldoc.core.security.Menu _menu  ");
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

			DocumentDAO documentDAO = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			Collection<Integer> ids = documentDAO.findMenuIdByKeyword(keyword);
			iter = ids.iterator();
			if (ids.isEmpty())
				return coll;
			query.append("and _menu.menuId in (");
			first = true;
			while (iter.hasNext()) {
				if (!first)
					query.append(",");
				query.append("'" + iter.next() + "'");
				first = false;
			}
			query.append(")");

			coll = (Collection<Menu>) getHibernateTemplate().find(query.toString());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findByUserName(java.lang.String,
	 *      int)
	 */
	public Collection<Menu> findByUserName(String username, int parentId) {
		return findByUserName(username, parentId, null);
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findByUserName(java.lang.String,
	 *      int, type)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Menu> findByUserName(String username, int parentId, Integer type) {
		Collection<Menu> coll = new ArrayList<Menu>();

		try {
			User user = userDAO.findByPrimaryKey(username);
			Collection<Group> precoll = user.getGroups();
			Iterator iter = precoll.iterator();
			if (precoll.isEmpty())
				return coll;

			StringBuffer query = new StringBuffer("select  distinct(_menu) from com.logicaldoc.core.security.Menu _menu ");
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
			query.append(") and _menu.menuParent = ?");
			if (type != null)
				query.append(" and _menu.menuType = " + type.toString());
			query.append(" order by _menu.menuType, _menu.menuSort, _menu.menuText");

			coll = (Collection<Menu>) getHibernateTemplate().find(query.toString(), parentId);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#countByUserName(java.lang.String,
	 *      int, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	public long countByUserName(String username, int parentId, Integer type) {
		int count = 0;
		try {
			User user = userDAO.findByPrimaryKey(username);
			Collection<Group> precoll = user.getGroups();
			Iterator iter = precoll.iterator();
			if (precoll.isEmpty())
				return count;
			StringBuffer query = new StringBuffer(
					"select  count(_menu.menuId) from com.logicaldoc.core.security.Menu _menu ");
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
			query.append(") and _menu.menuParent = ?");
			if (type != null)
				query.append(" and _menu.menuType = " + type.toString());
			count = ((Long) getHibernateTemplate().find(query.toString(), parentId).get(0)).intValue();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return count;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findChildren(int)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Menu> findChildren(int parentId) {
		Collection<Menu> coll = null;

		try {
			coll = (Collection<Menu>) getHibernateTemplate().find(
					"from com.logicaldoc.core.security.Menu _menu where _menu.menuParent = ?", new Object[] { parentId });
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findByParentId(int)
	 */
	public Collection<Menu> findByParentId(int parentId) {
		Collection<Menu> coll = new ArrayList<Menu>();
		Collection<Menu> temp = findChildren(parentId);
		Iterator iter = temp.iterator();

		while (iter.hasNext()) {
			Menu menu = (Menu) iter.next();
			coll.add(menu);

			Collection<Menu> coll2 = findByParentId(menu.getMenuId());

			if (coll2 != null) {
				coll.addAll(coll2);
			}
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#isWriteEnable(int,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public boolean isWriteEnable(int menuId, String username) {
		boolean result = true;

		try {
			User user = userDAO.findByPrimaryKey(username);
			Collection<Group> Groups = user.getGroups();
			if (Groups.isEmpty())
				return false;
			Iterator iter = Groups.iterator();

			StringBuffer query = new StringBuffer("select distinct(_menu) from com.logicaldoc.core.security.Menu _menu  ");
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
			query.append(") and _group.writeEnable=1 and _menu.menuId=?");

			Collection<MenuGroup> coll = (Collection<MenuGroup>) getHibernateTemplate().find(query.toString(),
					new Object[] { new Integer(menuId) });

			result = coll.size() > 0;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#isReadEnable(int,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public boolean isReadEnable(int menuId, String username) {
		boolean result = true;

		try {
			try {
				User user = userDAO.findByPrimaryKey(username);
				Collection<Group> Groups = user.getGroups();
				if (Groups.isEmpty())
					return false;
				Iterator iter = Groups.iterator();

				StringBuffer query = new StringBuffer(
						"select distinct(_menu) from com.logicaldoc.core.security.Menu _menu  ");
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
				query.append(") and _menu.menuId=?");

				Collection<MenuGroup> coll = (Collection<MenuGroup>) getHibernateTemplate().find(query.toString(),
						new Object[] { new Integer(menuId) });
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
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findMenuTextByMenuId(int)
	 */
	public String findMenuTextByMenuId(int menuId) {
		Menu menu = this.findByPrimaryKey(menuId);
		return menu.getMenuText();
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findMenuIdByUserName(java.lang.String)
	 *      <b>NOTE:</b> This implementation performs direct JDBC query, this
	 *      is required in order to obtain acceptable performances during
	 *      searches.
	 */
	public Set<Integer> findMenuIdByUserName(String username) {
		Set<Integer> ids = new HashSet<Integer>();
		try {
			User user = userDAO.findByPrimaryKey(username);
			Collection<Group> precoll = user.getGroups();
			Iterator iter = precoll.iterator();

			if (!precoll.isEmpty()) {
				StringBuffer query = new StringBuffer("select distinct(A.co_menuid) from co_menugroup A "
						+ " where A.co_groupname in (");
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
						ids.add(new Integer(rs.getInt(1)));
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
	 * @see com.logicaldoc.core.security.dao.MenuDAO#getContainedMenus(int,
	 *      java.lang.String)
	 */
	public Collection<Menu> getContainedMenus(int menuId, String userName) {
		Collection<Menu> coll = findByUserName(userName, menuId);
		Iterator<Menu> iter = coll.iterator();
		String docpath = settings.getValue("docdir");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HHmmss");
		DocumentDAO documentDAO = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);

		while (iter.hasNext()) {
			Menu menu = (Menu) iter.next();

			// calculate size of menu
			long size = 0;
			if (menu.getMenuType() == Menu.MENUTYPE_FILE) {
				size = menu.getMenuSize();
				if (size == 0) {
					/*
					 * The menu is a file and the size was not already computed,
					 * so calculate it now.
					 */
					size = FileBean.getSize(docpath + "/" + menu.getMenuPath() + "/" + menu.getMenuId() + "/"
							+ menu.getMenuRef());
					menu.setMenuSize(size);
				}
				size = size / 1024;
			} else {
				size = findByUserName(userName, menu.getMenuId()).size();
			}
			menu.setMenuSize(size);

			// check if menu is writable
			boolean writable = false;
			if (isWriteEnable(menu.getMenuId(), userName)) {
				writable = true;
			} else {
				writable = false;
			}
			menu.setWriteable(writable);

			// only done on documents
			// set the checkout/checkin status of the document
			// set the checkout user, if the document is checked out
			if (menu.getMenuType() == Menu.MENUTYPE_FILE) {
				Document doc = documentDAO.findByMenuId(menu.getMenuId());

				if (doc != null) {
					menu.setDocStatus(doc.getDocStatus());
					menu.setCheckoutUser(doc.getCheckoutUser());
					menu.setDocName(doc.getDocName());

					try {
						Date date = dateFormat.parse(doc.getDocDate());
						menu.setDate(date);
					} catch (Exception e) {
						menu.setDate(new Date());
					}
				}
			}
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#isMenuWriteable(int,
	 *      java.lang.String)
	 */
	public Integer isMenuWriteable(int menuId, String userName) {
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
		if (isWriteEnable(menu.getMenuId(), p_userName) == false) {
			return false;
		}

		if (menu.getMenuType() == Menu.MENUTYPE_DIRECTORY) {
			Collection<Menu> children = findByParentId(menu.getMenuId());

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
			StringBuffer query = new StringBuffer("select distinct(_menu) from com.logicaldoc.core.security.Menu _menu  ");
			query.append(" left outer join _menu.menuGroups as _group ");
			query.append(" where _group.groupName = ?");

			coll = (Collection<Menu>) getHibernateTemplate().find(query.toString(), groupName);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findLastDownloadsByUserName(java.lang.String,
	 *      int)
	 */
	public Collection<Menu> findLastDownloadsByUserName(String username, int maxResults) {
		Collection<Menu> coll = new ArrayList<Menu>();

		try {
			StringBuffer query = new StringBuffer(
					"select _userdoc.id.menuId from com.logicaldoc.core.security.UserDoc _userdoc");
			query.append(" where _userdoc.id.userName = ?");
			query.append(" order by _userdoc.timeStamp desc");

			Collection<Integer> results = (Collection<Integer>) getHibernateTemplate().find(query.toString(), username);
			ArrayList<Integer> tmpal = new ArrayList<Integer>(results);
			List<Integer> menuIds = tmpal;

			if (menuIds.isEmpty())
				return coll;

			if (menuIds.size() > maxResults) {
				tmpal.subList(0, maxResults - 1);
			}

			query = new StringBuffer("from com.logicaldoc.core.security.Menu _menu  ");
			query.append(" where _menu.menuId in (");

			for (int i = 0; i < menuIds.size(); i++) {
				Integer menuId = menuIds.get(i);
				if (i > 0)
					query.append(",");
				query.append(menuId);
			}
			query.append(")");

			// execute the query
			Collection<Menu> unorderdColl = (Collection<Menu>) getHibernateTemplate().find(query.toString());

			// put all elements in a map
			HashMap<Integer, Menu> hm = new HashMap<Integer, Menu>();
			for (Menu menu : unorderdColl) {
				hm.put(menu.getMenuId(), menu);
			}

			// Access the map using the menuIds
			// if a match is found, put it in the original list
			for (Integer menuId : menuIds) {
				Menu myMenu = hm.get(menuId);
				if (myMenu != null)
					coll.add(myMenu);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findMenuIdByUserNameAndParent(java.lang.String,
	 *      int, java.lang.Integer)
	 */
	public Set<Integer> findMenuIdByUserName(String username, int parentId, Integer type) {
		Set<Integer> ids = new HashSet<Integer>();
		try {
			User user = userDAO.findByPrimaryKey(username);
			Collection<Group> precoll = user.getGroups();
			Iterator iter = precoll.iterator();

			if (!precoll.isEmpty()) {
				StringBuffer query = new StringBuffer("select distinct(A.co_menuid) from co_menugroup A, co_menus B "
						+ " where A.co_menuid=B.co_menuid " + " AND B.co_menuparent=" + parentId
						+ " AND A.co_groupname in (");
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
					query.append(" AND B.co_menutype=" + type.toString());

				Connection con = null;
				Statement stmt = null;
				ResultSet rs = null;

				try {
					con = getSession().connection();
					stmt = con.createStatement();
					rs = stmt.executeQuery(query.toString());
					while (rs.next()) {
						ids.add(new Integer(rs.getInt(1)));
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
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findMenuIdByUsernameAndKeyword(java.lang.String,
	 *      java.lang.String)
	 */
	public Set<Integer> findMenuIdByUsernameAndKeyword(String username, String keyword) {
		Set<Integer> ids = new HashSet<Integer>();
		try {
			User user = userDAO.findByPrimaryKey(username);
			Collection<Group> precoll = user.getGroups();
			Iterator iter = precoll.iterator();

			if (!precoll.isEmpty()) {
				StringBuffer query = new StringBuffer(
						"select distinct(A.co_menuid) from co_menugroup A, co_menus B, co_document C, co_keywords D "
								+ " where A.co_menuid=B.co_menuid AND B.co_menuid=C.co_menuid AND C.co_docid=D.co_docid"
								+ " AND A.co_groupname in (");
				boolean first = true;
				while (iter.hasNext()) {
					if (!first)
						query.append(",");
					Group ug = (Group) iter.next();
					query.append("'" + ug.getGroupName() + "'");
					first = false;
				}
				query.append(")");
				query.append(" AND B.co_menutype=" + Menu.MENUTYPE_FILE);
				query.append(" AND lower(D.co_keyword)='" + keyword + "'");

				Connection con = null;
				Statement stmt = null;
				ResultSet rs = null;

				try {
					con = getSession().connection();
					stmt = con.createStatement();
					rs = stmt.executeQuery(query.toString());
					while (rs.next()) {
						ids.add(new Integer(rs.getInt(1)));
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
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findByMenuText(java.lang.String)
	 */
	public Collection<Menu> findByMenuText(String menutext) {
		return findByMenuText(null, menutext, null);
	}

	/**
	 * @see com.logicaldoc.core.security.dao.MenuDAO#findByMenuText(com.logicaldoc.core.security.Menu, java.lang.String, java.lang.Integer)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Collection<Menu> findByMenuText(Menu parent, String menutext, Integer type) {
		Collection<Menu> coll = new ArrayList<Menu>();
		StringBuffer query = new StringBuffer("from com.logicaldoc.core.security.Menu _menu where _menu.menuText = ? ");
		if (parent != null)
			query.append(" AND _menu.menuParent = " + parent.getMenuId());
		if (type != null)
			query.append(" AND _menu.menuType = " + type.intValue());

		try {
			coll = (Collection<Menu>) getHibernateTemplate().find(query.toString(), new Object[] { menutext });
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return coll;
	}
}