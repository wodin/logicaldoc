package com.logicaldoc.core.communication.dao;

import java.util.Collection;

import com.logicaldoc.core.communication.EMail;

/**
 * This is a DAO service for EMail.
 * 
 * @author Michael Scholz
 * @author Alessandro Gasparini
 * @version 1.0
 */
public interface EMailDAO {

    /**
     * This method persists an emailaccount object.
     * 
     * @param account EMailAccount which should be store.
     * @return True if successfully stored in a database.
     */
    public boolean store(EMail email);

    /**
     * This method deletes an emailaccount.
     * 
     * @param emailId AccountId of the emailaccount which should be delete.
     */
    public boolean delete(int emailId);

    /**
     * This method finds an email by its id.
     */
    public EMail findByPrimaryKey(int emailid);

    public Collection<EMail> findByUserName(String username);

    public Collection<EMail> findByUserName(String username, String folder);

    /**
     * Loads all e-mails downloaded by the specified account
     * 
     * @param accountId
     * @return
     */
    public Collection<EMail> findByAccountId(int accountId);

    /**
     * Same as findByAccountId except for the fact that the returned collection
     * contains e-mail identifiers only
     * 
     * @param accountId
     * @return
     */
    public Collection<String> collectEmailIds(int accountId);
}