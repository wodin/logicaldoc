package com.logicaldoc.core.security.dao;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.MenuGroup;
import com.logicaldoc.core.security.User;

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
	 * @see com.logicaldoc.core.security.dao.GroupDAO#delete(long)
	 */
	@SuppressWarnings("deprecation")
	public boolean delete(long groupId) {
		boolean result = true;

		try {
			Group group = findById(groupId);
			if (group != null) {
				// Delete menu-group assignments
				getSession().connection().createStatement().execute(
						"delete from ld_menugroup where ld_groupid=" + groupId);

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
			Group group = findByName(groupname);
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
			Collection<Group> coll2 = findAll();
			for (Group group : coll2) {
				coll.add(group.getName());
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage());
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.GroupDAO#findByName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Group findByName(String name) {
		Group group = null;

		try {
			Collection<Group> coll = (Collection<Group>) getHibernateTemplate().find(
					"from Group _group where _group.name like '"+name+"'");
			if (coll.size() > 0) {
				group = coll.iterator().next();
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return group;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.GroupDAO#insert(com.logicaldoc.core.security.Group, long)
	 */
	public boolean insert(Group group, long parentGroupId) {
		boolean result = true;

		if (group == null)
			return false;

		try {
			getHibernateTemplate().saveOrUpdate(group);

			if (parentGroupId>0) {
				Collection<Menu> menus = menuDAO.findByGroupId(parentGroupId);
				for (Menu menu : menus) {
					addMenuGroup(group, menu.getId(), menu.getMenuGroup(parentGroupId).getWrite());
				}
			} else {
				// if no parent group was given, the new group will have default
				// access rights
				addMenuGroup(group, Menu.MENUID_HOME, 0); // home
				addMenuGroup(group, Menu.MENUID_PERSONAL, 0); // personal
				addMenuGroup(group, Menu.MENUID_DOCUMENTS, 1); // root folder
				// for documents
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
	private void addMenuGroup(Group group, long menuId, int writeable) {
		MenuDAO menuDAO = getMenuDAO();
		Menu menu = menuDAO.findById(menuId);

		MenuGroup mgroup = new MenuGroup();
		mgroup.setGroupId(group.getId());
		mgroup.setWrite(writeable);
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

	@Override
	public Group findById(long groupId) {
		Group group = null;

		try {
			group = (Group) getHibernateTemplate().get(Group.class, groupId);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return group;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.GroupDAO#findByLikeName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Group> findByLikeName(String name) {
		Collection<Group> coll = new ArrayList<Group>();
		try {
			coll = (Collection<Group>) getHibernateTemplate().find("from Group _group where _group.name like ?",
					new Object[] { name });
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}	
		return coll;
	}
}