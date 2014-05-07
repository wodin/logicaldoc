package com.logicaldoc.core.sequence;

import java.util.List;

import com.logicaldoc.core.generic.Generic;

/**
 * Utility DAO that can manage sequences persisted on the DB
 * <p>
 * <b>Important:</b> Implementations of this interface must grant
 * synchronization.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 4.0
 */
public interface SequenceDAO {

	/**
	 * Returns the next value of the sequence
	 * 
	 * @param sequence The sequence name
	 * @param tenantId ID of the owning tenant
	 * 
	 * @return The next value
	 */
	public long next(String sequence, long tenantId);

	/**
	 * Returns the next value of the sequence by incrementing by the given increment
	 * 
	 * @param sequence The sequence name
	 * @param tenantId ID of the owning tenant
	 * @param increment ID of the owning tenant
	 * 
	 * @return The next value
	 */
	public long next(String sequence, long tenantId, long increment);
	
	/**
	 * Initializes the sequence value
	 * 
	 * @param sequence The sequence name
	 * @param value The value
	 * @param tenantId ID of the owning tenant
	 * 
	 * @param value The initial value
	 */
	public void reset(String sequence, long tenantId, long value);

	/**
	 * Finds all sequences whose name starts with the passed name
	 */
	public List<Generic> findByName(String name, long tenantId);

	/**
	 * Gets the current value
	 */
	long getCurrentValue(String sequence, long tenantId);
}
