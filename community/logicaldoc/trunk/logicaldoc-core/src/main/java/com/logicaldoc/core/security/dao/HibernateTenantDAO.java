package com.logicaldoc.core.security.dao;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.Tenant;
import com.logicaldoc.core.security.User;
import com.logicaldoc.util.sql.SqlUtil;

@SuppressWarnings("unchecked")
public class HibernateTenantDAO extends HibernatePersistentObjectDAO<Tenant> implements TenantDAO {

	private FolderDAO folderDao;

	private GroupDAO groupDao;

	private UserDAO userDao;

	protected HibernateTenantDAO() {
		super(Tenant.class);
	}

	@Override
	public boolean delete(long tenantId) {
		boolean result = true;

		try {
			Tenant tenant = findById(tenantId);
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
			group.setTenantId(tenant.getId());
			stored = groupDao.store(group);
			if (!stored)
				return stored;

			User user = new User();
			user.setUserName("admin" + StringUtils.capitalize(tenant.getName()));
			user.setDecodedPassword("admin");
			user.setTenantId(tenant.getId());
			user.setName(tenant.toString());
			user.setEmail(tenant.getEmail());
			stored = userDao.store(user);
			if (!stored)
				return stored;

			flush();

			userDao.jdbcUpdate("insert into ld_usergroup(ld_groupid,ld_userid) values (?,?)",
					new Object[] { group.getId(), user.getId() });
		}

		return stored;
	}

	public void setGroupDao(GroupDAO groupDao) {
		this.groupDao = groupDao;
	}

	public void setUserDao(UserDAO userDao) {
		this.userDao = userDao;
	}
}