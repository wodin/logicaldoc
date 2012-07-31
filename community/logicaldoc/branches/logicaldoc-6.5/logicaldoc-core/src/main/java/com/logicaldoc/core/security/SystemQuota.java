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

	public static void setTotalSize(long size) {
	}
	
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

	/**
	 * Retrieve the total size of documents folder into the file system (in
	 * bytes)
	 */
	public static synchronized long getTotalSize() {
		return -1;
	}

	/**
	 * Increments the total size counter of the document file size
	 */
	public static synchronized void increment(long docSize) {

	}

	/**
	 * Decrements the total size counter of the document file size
	 */
	public static synchronized void decrement(long docSize) {

	}

	/**
	 * Decrements user quota counter of the document file size
	 */
	public static synchronized void decrementUserQuota(Document document) {

	}

	/**
	 * Increments user quota counter of the document file size retrieving the
	 * userid from the given document
	 */
	public static synchronized void incrementUserQuota(Document document, Long filesize) {

	}

	/**
	 * Increments user quota counter of the document file size retrieving the
	 * userid from the given document id
	 */
	public static synchronized void incrementUserQuota(long docId, Long filesize) {

	}

	/**
	 * Checks if user quota will be exceeded adding a document
	 */
	public static synchronized void checkUserQuota(Document document) throws Exception {

	}

	/**
	 * Checks if user quota has be exceeded
	 */
	public static synchronized boolean isOverQuota(User user) {
		return false;
	}

	/**
	 * Checks if the quota of the user (with the given user id) will be exceeded
	 * adding a document with the given file size
	 */
	public static synchronized void checkUserQuota(long userId, Long filesize) throws Exception {

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