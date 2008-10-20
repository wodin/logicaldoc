package com.logicaldoc.core.communication.dao;

import java.util.Collection;

import com.logicaldoc.core.communication.EMailAccount;

/**
 * DAO for <code>EMailAccount</code> handling.
 * 
 * @author Michael Scholz
 * @author Alessandro Gasparini
 */
public interface EMailAccountDAO {

	/**
	 * This method persists an emailaccount object.
	 * 
	 * @param account EMailAccount which should be store.
	 * @return True if successfully stored in a database.
	 */
	public boolean store(EMailAccount account);

	/**
	 * This method deletes an emailaccount.
	 * 
	 * @param accountId Id of the emailaccount which should be delete.
	 */
	public boolean delete(long accountId);

	/**
	 * This method finds an emailaccount by its accountId.
	 */
	public EMailAccount findByPrimaryKey(long accountId);

	/**
	 * Loads all accounts
	 * 
	 * @return
	 */
	public Collection<EMailAccount> findAll();

	public Collection<EMailAccount> findByUserId(long userId);

	/**
	 * This method deletes an emailaccount.
	 * 
	 * @param userId ID of the emailaccount which should be delete.
	 */
	public boolean deleteByUserId(long userId);
}