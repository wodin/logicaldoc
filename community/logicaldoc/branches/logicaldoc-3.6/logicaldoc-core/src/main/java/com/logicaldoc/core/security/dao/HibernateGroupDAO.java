package com.logicaldoc.core.security.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.MenuGroup;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * @author Alessandro Gasparini
 * @since 3.0
 */
public class HibernateGroupDAO extends HibernateDaoSupport implements GroupDAO {

	protected static Log log = LogFactory.getLog(HibernateGroupDAO.class);

	private MenuDAO menuDAO;

	private HibernateGroupDAO() {
	}

	public MenuDAO getMenuDAO() {
		return menuDAO;
	}

	public void setMenuDAO(MenuDAO menuDAO) {
		this.menuDAO = menuDAO;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.GroupDAO#delete(java.lang.String)
	 */
	public boolean delete(String groupname) {
		boolean result = true;

		try {
			Group group = findByPrimaryKey(groupname);
			if (group != null) {
				// Delete menu-group assignments
				getSession().connection().createStatement().execute(
						"delete from co_menugroup where co_groupname='" + groupname + "'");

				// Finally delete the found Group
				getHibernateTemplate().delete(group);
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.GroupDAO#exists(java.lang.String)
	 */
	public boolean exists(String groupname) {
		boolean result = false;

		try {
			Group group = findByPrimaryKey(groupname);
			result = (group != null);
		} catch (Exception e) {
			if (log.isWarnEnabled())
				log.warn(e.getMessage());
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.GroupDAO#findAll()
	 */
	@SuppressWarnings("unchecked")
	public Collection<Group> findAll() {
		Collection<Group> coll = new ArrayList<Group>();

		try {
			coll = getHibernateTemplate().loadAll(Group.class);
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(e.getMessage());
			}
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.GroupDAO#findAllGroupNames()
	 */
	public Collection<String> findAllGroupNames() {
		Collection<String> coll = new ArrayList<String>();

		try {
			Collection coll2 = findAll();
			Iterator iter = coll2.iterator();
			while (iter.hasNext()) {
				Group group = (Group) iter.next();
				coll.add(group.getGroupName());
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage());
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.GroupDAO#findByPrimaryKey(java.lang.String)
	 */
	public Group findByPrimaryKey(String groupname) {
		Group group = null;

		try {
			group = (Group) getHibernateTemplate().get(Group.class, groupname);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return group;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.GroupDAO#insert(com.logicaldoc.core.security.Group)
	 */
	public boolean insert(Group group, String parentGroup) {
		boolean result = true;

		if (group == null)
			return false;

		try {
			getHibernateTemplate().saveOrUpdate(group);

			if (StringUtils.isNotEmpty(parentGroup)) {
				Collection<Menu> menues = menuDAO.findByGroupName(parentGroup);
				for (Menu menu : menues) {
					addMenuGroup(group, menu.getMenuId(), menu.getMenuGroup(parentGroup).getWriteEnable());
				}
			} else {
				// if no parent group was given, the new group will have default
				// access rights
				addMenuGroup(group, Menu.MENUID_HOME, 0); // home
				addMenuGroup(group, Menu.MENUID_PERSONAL, 0); // personal
				addMenuGroup(group, Menu.MENUID_DOCUMENTS, 1); // root folder for documents
				addMenuGroup(group, Menu.MENUID_MESSAGES, 0); // messages
				addMenuGroup(group, Menu.MENUID_EDITME, 0); // edit me
				addMenuGroup(group, 20, 0); // emails
				addMenuGroup(group, 23, 0); // smtp
				addMenuGroup(group, 24, 0); // email accounts
				addMenuGroup(group, 26, 1); // keywords
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	/**
	 * Assigns the given rights for a certain group to a menu
	 * 
	 * @param group the group
	 * @param menuId the menu
	 * @param writeable the rights to assign (0=read; 1=read/write)
	 */
	private void addMenuGroup(Group group, int menuId, int writeable) {
		MenuDAO menuDAO = getMenuDAO();
		Menu menu = menuDAO.findByPrimaryKey(menuId);

		MenuGroup mgroup = new MenuGroup();
		mgroup.setGroupName(group.getGroupName());
		mgroup.setWriteEnable(writeable);
		if (!menu.getMenuGroups().contains(mgroup))
			menu.getMenuGroups().add(mgroup);
		menuDAO.store(menu);
	}

	/**
	 * @see com.logicaldoc.core.security.dao.GroupDAO#store(com.logicaldoc.core.security.Group)
	 */
	public boolean store(Group group) {
		boolean result = true;

		try {
			getHibernateTemplate().saveOrUpdate(group);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage());
			result = false;
			e.printStackTrace();
		}
		return result;
	}
}