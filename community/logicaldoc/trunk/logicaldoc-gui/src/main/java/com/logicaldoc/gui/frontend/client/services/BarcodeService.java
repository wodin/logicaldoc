package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.ServerException;
import com.logicaldoc.gui.common.client.beans.GUIBarcodeEngine;
import com.logicaldoc.gui.common.client.beans.GUIBarcodePattern;

/**
 * The client side stub for the Barcode Engine Service.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.1
 */
@RemoteServiceRelativePath("barcode")
public interface BarcodeService extends RemoteService {
	/**
	 * Loads a bean that contains all engine infos.
	 */
	public GUIBarcodeEngine getInfo(String sid) throws ServerException;

	/**
	 * Saves the engine settings
	 */
	public void save(String sid, GUIBarcodeEngine engine) throws ServerException;

	/**
	 * Reschedule all documents for processing
	 */
	public void rescheduleAll(String sid) throws ServerException;

	/**
	 * Marks a set of documents as not processable.
	 */
	public void markUnprocessable(String sid, long[] ids) throws ServerException;

	/**
	 * Loads the patterns configured for a given template.
	 */
	public GUIBarcodePattern[] loadPatterns(String sid, Long templateId) throws ServerException;

	/**
	 * Saves the patterns for the given template ordered by position
	 */
	public void savePatterns(String sid, String[] patterns, Long templateId) throws ServerException;

}