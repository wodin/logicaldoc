package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIExtendedAttribute;
import com.logicaldoc.gui.common.client.beans.GUIReport;

public interface ReportServiceAsync {

	void changeStatus(String sid, long id, boolean enabled, AsyncCallback<Void> callback);

	void delete(String sid, long id, AsyncCallback<Void> callback);

	void getReport(String sid, long id, AsyncCallback<GUIReport> callback);

	void execute(String sid, long id, GUIExtendedAttribute[] paremeters, AsyncCallback<Void> callback);

	void save(String sid, GUIReport report, AsyncCallback<GUIReport> callback);

	void create(String sid, GUIReport report, AsyncCallback<GUIReport> callback);

	void getReports(String sid, AsyncCallback<GUIReport[]> callback);

	void getReportParameters(String sid, long id, AsyncCallback<GUIExtendedAttribute[]> callback);

	void storeUploadedDesign(String sid, long id, AsyncCallback<Void> callback);
}