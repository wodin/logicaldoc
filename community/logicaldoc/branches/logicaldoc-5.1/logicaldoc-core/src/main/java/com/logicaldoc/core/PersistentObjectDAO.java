package com.logicaldoc.core;

import java.util.Collection;
import java.util.List;

/**
 * Interface for DAOs that operate on persistent objects
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public interface PersistentObjectDAO<T extends PersistentObject> {
	/**
	 * This method persists the entity object.
	 * 
	 * @param entity entity to be stored.
	 * @return True if successfully stored in a database.
	 */
	public boolean store(T entity);

	/**
	 * This method deletes an entity.
	 * 
	 * @param id ID of the entity which should be deleted.
	 */
	public boolean delete(long id);

	/**
	 * This method finds an entity by ID.
	 * 
	 * @param doc ID of the entity.
	 * @return Entity with given ID.
	 */
	public T findById(long id);

	/**
	 * Finds all entities in the database
	 * 
	 * @return The list of all entities
	 */
	public List<T> findAll();

	/**
	 * Finds all entities ids
	 * 
	 * @param where The where clause expression
	 * @return The list of all entities ids
	 */
	public List<Long> findAllIds();

	/**
	 * Finds all entities by the given expression. Use _entity alias to
	 * reference attributes in the where expression.
	 * 
	 * @param where The where clause expression
	 * @param order The order clause expression
	 * @return The list of marching entities
	 */
	public List<T> findByWhere(String where, String order);

	/**
	 * Finds all entities by the given expression. Use _entity alias to
	 * reference attributes in the where expression.
	 * 
	 * @param where The where clause expression
	 * @param values Parameters used in the where expression
	 * @param order The order clause expression
	 * @return The list of marching entities
	 */
	public List<T> findByWhere(String where, Object[] values, String order);

	/**
	 * Find everything you want from the DB using the ORM query language
	 * 
	 * @param query The query to execute
	 * @param values Array of paramaters
	 * @return Query result
	 */
	public List<Object> findByQuery(String query, Object[] values);

	/**
	 * Finds all entities ids by the given expression. Use _entity alias to
	 * reference attributes in the where expression.
	 * 
	 * @param where The where clause expression
	 * @param order The order clause expression
	 * @return The list of marching entities ids
	 */
	public List<Long> findIdsByWhere(String where, String order);

	/**
	 * Finds all entities ids by the given expression. Use _entity alias to
	 * reference attributes in the where expression.
	 * 
	 * @param where The where clause expression
	 * @param values Parameters used in the where expression
	 * @param order The order clause expression
	 * @return The list of marching entities ids
	 */
	public List<Long> findIdsByWhere(String where, Object[] values, String order);

	/**
	 * Initialises lazy loaded data such as collections
	 * 
	 * @param entity The entity to be initialised
	 */
	public void initialize(T entity);

	/**
	 * Executes a free-form SQL query against the database, using direct JDBC
	 * access
	 * 
	 * @param query The query to be executed
	 * @param returnedColumns Number of returned columns (must be >=)
	 * @return The result set content as list
	 */
	public List<Object> findByJdbcQuery(String query, int returnedColumns, Object[] values);

	/**
	 * Deletes all entries form the database
	 * 
	 * @param entities The entities to be deleted
	 */
	public void deleteAll(Collection<T> entities);

	/**
	 * Executes a bulk update as specified by the given expression
	 * 
	 * @return the number of modified records
	 */
	public int bulkUpdate(String expression, Object[] values);
}