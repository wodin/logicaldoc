package com.logicaldoc.email.dao;

import java.util.Collection;

import com.logicaldoc.email.EmailAccount;

/**
 * DAO for <code>EmailAccount</code> handling.
 * 
 * @author Michael Scholz
 * @author Alessandro Gasparini - Logical Objects
 * @since 4.0
 */
public interface EmailAccountDAO {

	/**
	 * This method persists an EmailAccount object.
	 * 
	 * @param account EmailAccount which should be store.
	 * @return True if successfully stored in a database.
	 */
	public boolean store(EmailAccount account);

	/**
	 * This method deletes an EmailAccount.
	 * 
	 * @param accountId Id of the EmailAccount which should be delete.
	 */
	public boolean delete(long accountId);

	/**
	 * This method finds an EmailAccount by its accountId.
	 */
	public EmailAccount findById(long accountId);

	/**
	 * Loads all accounts
	 * 
	 * @return
	 */
	public Collection<EmailAccount> findAll();
}