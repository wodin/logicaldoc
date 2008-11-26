package com.logicaldoc.core.sequence;

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
	 * @return The next value
	 */
	public int next(String sequence);

	/**
	 * Initializes the sequence value
	 * 
	 * @param sequence The sequence name
	 * @param value The initial value
	 */
	public void reset(String sequence, int value);

}
