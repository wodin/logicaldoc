package com.logicaldoc.core.security;

import java.util.Map;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentListener;
import com.logicaldoc.core.document.History;

/**
 * Counter class optimized for documents folder size counting. An internal
 * static counter is maintained and can be altered using
 * <code>increment()</code> and <code>decrement()</code>.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
public class SystemQuota implements DocumentListener {

	/**
	 * Checks if the quota was exceeded.
	 */
	public static synchronized void checkOverQuota() throws Exception {

	}

	/**
	 * Checks if the quota threshold was exceeded.
	 */
	public static synchronized boolean checkOverThreshold() {
		return false;
	}

	/**
	 * Checks if the quota will be exceeded adding a new document with the given
	 * size.
	 */
	public static synchronized void checkOverQuota(long docSize) throws Exception {

	}

	public static synchronized void increment(long docSize) {

	}

	public static synchronized void decrement(long docSize) {

	}

	@Override
	public void afterCheckin(Document document, History transaction, Map<String, Object> dictionary) throws Exception {

	}

	@Override
	public void afterStore(Document document, History transaction, Map<String, Object> dictionary) throws Exception {

	}

	@Override
	public void beforeCheckin(Document document, History transaction, Map<String, Object> dictionary) throws Exception {

	}

	@Override
	public void beforeStore(Document document, History transaction, Map<String, Object> dictionary) throws Exception {

	}
}