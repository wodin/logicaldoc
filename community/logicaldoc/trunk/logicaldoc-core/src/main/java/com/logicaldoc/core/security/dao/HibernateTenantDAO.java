package com.logicaldoc.core.security.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.generic.Generic;
import com.logicaldoc.core.generic.GenericDAO;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.Tenant;
import com.logicaldoc.core.security.User;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.sql.SqlUtil;

@SuppressWarnings("unchecked")
public class HibernateTenantDAO extends HibernatePersistentObjectDAO<Tenant> implements TenantDAO {

	private ContextProperties conf;

	private FolderDAO folderDao;

	private GroupDAO groupDao;

	private UserDAO userDao;

	private GenericDAO genericDao;

	protected HibernateTenantDAO() {
		super(Tenant.class);
	}

	@Override
	public boolean delete(long tenantId) {
		boolean result = true;

		String tenantName = null;
		try {
			Tenant tenant = findById(tenantId);
			tenantName = tenant.getName();
			refresh(tenant);
			if (tenant != null) {
				tenant.setName(tenant.getName() + "." + tenant.getId());
				tenant.setDeleted(1);
				saveOrUpdate(tenant);
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			result = false;
		}

		/*
		 * Now remove all the tenant configuration properties
		 */
		if (tenantName != null) {
			List<String> toBeRemoved = new ArrayList<String>();
			for (Object key : conf.keySet()) {
				if (key.toString().startsWith(tenantName))
					toBeRemoved.add(key.toString());
			}
			if (!toBeRemoved.isEmpty()) {
				for (String name : toBeRemoved) {
					conf.remove(name);
				}
				try {
					conf.write();
				} catch (IOException e) {
					log.warn("Unable to remove configuration settings", e);
				}
			}
		}

		return result;
	}

	@Override
	public Tenant findByName(String name) {
		Tenant tenant = null;
		Collection<Tenant> coll = findByWhere("_entity.name = '" + SqlUtil.doubleQuotes(name) + "'", null, null);
		if (coll.size() > 0) {
			tenant = coll.iterator().next();
			if (tenant.getDeleted() == 1)
				tenant = null;
		}

		return tenant;
	}

	@Override
	public int count() {
		String query = "select count(*) from ld_tenant where ld_deleted=0";
		return queryForInt(query);
	}

	public void setFolderDao(FolderDAO folderDao) {
		this.folderDao = folderDao;
	}

	@Override
	public boolean store(Tenant tenant) {
		boolean newTenant = tenant.getId() == 0L;
		boolean stored = super.store(tenant);
		if (!stored)
			return stored;

		if (newTenant) {
			Folder rootFolder = new Folder("/");
			rootFolder.setType(Folder.TYPE_WORKSPACE);
			rootFolder.setTenantId(tenant.getId());
			stored = folderDao.store(rootFolder);
			if (!stored)
				return stored;

			rootFolder.setParentId(rootFolder.getId());
			stored = folderDao.store(rootFolder);
			if (!stored)
				return stored;

			Folder folder = new Folder("Default");
			folder.setTenantId(tenant.getId());
			folder.setType(Folder.TYPE_WORKSPACE);
			folder.setParentId(rootFolder.getId());
			stored = folderDao.store(folder);
			if (!stored)
				return stored;

			Group group = new Group();
			group.setName("admin");
			group.setName("Group of administrators");
			group.setTenantId(tenant.getId());
			stored = groupDao.store(group);
			if (!stored)
				return stored;

			User user = new User();
			user.setUserName("admin" + StringUtils.capitalize(tenant.getName()));
			user.setDecodedPassword("admin");
			user.setTenantId(tenant.getId());
			user.setName(tenant.toString());
			user.setFirstName("Administrator");
			user.setEmail(tenant.getEmail());
			user.setLanguage("en");
			stored = userDao.store(user);
			if (!stored)
				return stored;

			flush();

			userDao.jdbcUpdate("insert into ld_usergroup(ld_groupid,ld_userid) values (?,?)",
					new Object[] { group.getId(), user.getId() });

			/*
			 * Add a guests group
			 */
			Group guest = new Group();
			guest.setName("guest");
			guest.setDescription("Group of guests");
			groupDao.store(group);

			long[] menuIds = new long[] { Menu.DOCUMENTS, 5L, 1510L, 1520L, 1530L };
			for (long id : menuIds)
				userDao.jdbcUpdate("insert into ld_menugroup(ld_groupid,ld_userid) values (?,?)",
						new Object[] { group.getId(), id });
			long[] folderIds = new long[] { folder.getId(), folder.getParentId() };
			for (long id : folderIds)
				userDao.jdbcUpdate(
						"insert into ld_foldergroup(ld_folderid, ld_groupid, ld_write , ld_add, ld_security, ld_immutable, ld_delete, ld_rename, ld_import, ld_export, ld_sign, ld_archive, ld_workflow, ld_download, ld_calendar) values (?,?,0,0,0,0,0,0,0,0,0,0,0,0,0)",
						new Object[] { id, group.getId() });

			/*
			 * Now some minor records
			 */
			Generic generic = new Generic("customid-scheme", "default", null, tenant.getId());
			generic.setString1("<id>");
			genericDao.store(generic);

			/*
			 * Now replicate the configuration properties of the default tenant
			 */
			Map<String, String> newProps = new HashMap<String, String>();
			for (Object key : conf.keySet()) {
				String name = key.toString();
				if (name.startsWith(Tenant.DEFAULT_NAME + ".")) {
					String val = conf.getProperty(name);
					newProps.put(tenant.getName() + "." + name.substring(name.indexOf('.')), val);
				}
			}
			if (!newProps.isEmpty()) {
				for (String name : newProps.keySet())
					conf.setProperty(name, newProps.get(name));
				try {
					conf.write();
				} catch (IOException e) {
					log.warn("Unable update the configuration settings", e);
				}
			}
		}

		return stored;
	}

	public void setGroupDao(GroupDAO groupDao) {
		this.groupDao = groupDao;
	}

	public void setUserDao(UserDAO userDao) {
		this.userDao = userDao;
	}

	@Override
	public User findAdminUser(String tenantName) {
		if ("default".equals(tenantName))
			return userDao.findByUserName("admin");
		else
			return userDao.findByUserName("admin" + StringUtils.capitalize(tenantName));
	}

	public void setConf(ContextProperties conf) {
		this.conf = conf;
	}

	public void setGenericDao(GenericDAO genericDao) {
		this.genericDao = genericDao;
	}
}