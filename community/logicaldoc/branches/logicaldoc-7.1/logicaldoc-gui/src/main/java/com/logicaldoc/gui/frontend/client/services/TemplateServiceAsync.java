package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUITemplate;

public interface TemplateServiceAsync {

	void delete(String sid, long templateId, AsyncCallback<Void> callback);

	void save(String sid, GUITemplate template, AsyncCallback<GUITemplate> callback);

	void getTemplate(String sid, long templateId, AsyncCallback<GUITemplate> callback);

	void saveOptions(String sid, long templateId, String attribute, String[] values, AsyncCallback<Void> callback);

	void deleteOptions(String sid, long templateId, String attribute, String[] values, AsyncCallback<Void> callback);

	void parseOptions(String sid, long templateId, String attribute, AsyncCallback<String[]> callback);
}