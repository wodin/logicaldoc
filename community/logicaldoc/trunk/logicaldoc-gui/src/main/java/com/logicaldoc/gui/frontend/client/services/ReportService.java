package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.ServerException;
import com.logicaldoc.gui.common.client.beans.GUIExtendedAttribute;
import com.logicaldoc.gui.common.client.beans.GUIReport;

/**
 * The client side stub for the Report Service. This service gives all needed
 * methods to handle reports.
 */
@RemoteServiceRelativePath("report")
public interface ReportService extends RemoteService {
	/**
	 * Deletes a given report
	 */
	public void delete(String sid, long id) throws ServerException;

	/**
	 * Updates a report
	 */
	public GUIReport save(String sid, GUIReport report) throws ServerException;

	/**
	 * Store the uploaded design file in the given report
	 */
	public void storeUploadedDesign(String sid, long id) throws ServerException;

	/**
	 * Creates a new report
	 */
	public GUIReport create(String sid, GUIReport report) throws ServerException;

	/**
	 * Loads a given report from the database
	 */
	public GUIReport getReport(String sid, long id, boolean withLog) throws ServerException;

	/**
	 * Loads all the reports
	 */
	public GUIReport[] getReports(String sid) throws ServerException;

	/**
	 * Loads the attributes defined in the given report
	 */
	public GUIExtendedAttribute[] getReportParameters(String sid, long id) throws ServerException;

	/**
	 * Changes a report enabled/disabled status
	 */
	public void changeStatus(String sid, long id, boolean enabled) throws ServerException;

	/**
	 * Processes a report
	 */
	public void execute(String sid, long id, GUIExtendedAttribute[] parameters) throws ServerException;
}