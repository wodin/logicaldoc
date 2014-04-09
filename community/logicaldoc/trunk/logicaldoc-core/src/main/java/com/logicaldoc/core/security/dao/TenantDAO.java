package com.logicaldoc.core.security.dao;

import com.logicaldoc.core.PersistentObjectDAO;
import com.logicaldoc.core.security.Tenant;
import com.logicaldoc.core.security.User;

public interface TenantDAO extends PersistentObjectDAO<Tenant> {

	/**
	 * Finds a tenant by name.
	 * 
	 * @param name name of wanted tenant
	 * 
	 * @return Wanted tenant or null.
	 */
	public Tenant findByName(String name);

	/**
	 * Counts the total number of tenants
	 */
	public int count();
	
	/**
	 * Retrieve the administrator for the given tenant. The general rule is that the administrator's username is:
	 * <ol>
	 *   <li>admin if the tenant is default</li>
	 *   <li>admin<b>Tenantname</b> in all other cases</li>
	 * </ol>
	 */
	public User findAdminUser(String tenantName);
}