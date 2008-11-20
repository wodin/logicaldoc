package com.logicaldoc.core.generic.dao;

import java.util.List;

import com.logicaldoc.core.generic.Generic;

/**
 * Instances of this class is a DAO-service for Generic business entities.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public interface GenericDAO {
	/**
	 * This method persists the Generic object.
	 * 
	 * @param menu Menu to be stored.
	 * @return True if successful stored in a database.
	 */
	public boolean store(Generic generic);

	/**
	 * This method deletes a Generic in database.
	 * 
	 * @param genericId Menu to be deleted.
	 * @return True if successful deleted.
	 */
	public boolean delete(long genericId);

	/**
	 * Finds a Generic by ID.
	 * 
	 * @param genericId ID of wanted generic.
	 * @return Wanted generic or null.
	 */
	public Generic findById(long genericId);

	/**
	 * Finds a Generic by it's alternate key
	 * 
	 * @param type The exact type
	 * @param subtype The exact subtype
	 * @return Wanted generic or null.
	 */
	public Generic findByAlternateKey(String type, String subtype);

	/**
	 * Finds a Generic by it's alternate key. The search uses the like operator
	 * and each parameter can be null.
	 * 
	 * @param type The type(you can use like jollies and can be null)
	 * @param subtype The subtype((you can use like jollies and can be null)
	 * @return The collection of fount Generics
	 */
	public List<Generic> findByTypeAndSubtype(String type, String subtype);
	
	/**
	 * Initializes lazy loaded collections
	 * 
	 * @param generic The Generic to be initialized
	 */
	public void initialize(Generic generic);
}