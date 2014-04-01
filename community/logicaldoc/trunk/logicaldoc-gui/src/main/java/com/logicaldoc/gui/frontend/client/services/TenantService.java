package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUITenant;

/**
 * The client side stub for the Tenant Service. This service allows r/w
 * operations on tenants.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.9
 */
@RemoteServiceRelativePath("tenant")
public interface TenantService extends RemoteService {
	/**
	 * Deletes a specific tenant by its ID
	 */
	public void delete(String sid, long tenantId) throws InvalidSessionException;

	/**
	 * Saves/Updates a given tenant
	 */
	public GUITenant save(String sid, GUITenant tenant) throws InvalidSessionException;

	/**
	 * Loads a given tenant
	 */
	public GUITenant load(String sid, long tenantId) throws InvalidSessionException;

}