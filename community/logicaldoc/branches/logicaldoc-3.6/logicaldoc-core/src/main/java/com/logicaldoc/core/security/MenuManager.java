package com.logicaldoc.core.security;


/**
 * Manager for business operations on menues
 * 
 * @author Marco Meschieri
 * @version $Id: MenuManager.java,v 1.1 2007/07/10 16:36:26 marco Exp $
 * @since 3.0
 */
public interface MenuManager {

    /**
     * Deletes the specified menu updating all related entities
     * 
     * @param menu The menu to be deleted
     * @param userName The user that requires the deletion
     * @throws Exception It may be AccessControlException, SqlException
     */
    public void deleteMenu(Menu menu, String userName) throws Exception;

}