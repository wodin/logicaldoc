package com.logicaldoc.core.communication.dao;

import java.util.List;

import com.logicaldoc.core.communication.SystemMessage;

/**
 * This is a DAO service for SystemMessages.
 * 
 * @author Michael Scholz
 * @author Marco Meschieri
 * @version 1.0
 */
public interface SystemMessageDAO {
	/**
	 * This method persists a systemmessage object.
	 * 
	 * @param sysmess SystemMessage which should be store.
	 * @return True if successfully stored in a database.
	 */
	public boolean store(SystemMessage sysmess);

	public boolean delete(long messageId);

	public SystemMessage findById(long messageId);

	public List<SystemMessage> findByRecipient(String recipient);

	public int getCount(String recipient);

	/**
	 * Removes all expired messages for the specified recipient
	 * 
	 * @param recipient The recipient
	 */
	public void deleteExpiredMessages(String recipient);
}