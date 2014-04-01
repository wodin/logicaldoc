package com.logicaldoc.core.security.dao;

import java.util.Collection;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.security.Tenant;
import com.logicaldoc.util.sql.SqlUtil;

@SuppressWarnings("unchecked")
public class HibernateTenantDAO extends HibernatePersistentObjectDAO<Tenant> implements TenantDAO {

	protected HibernateTenantDAO() {
		super(Tenant.class);
	}

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
}