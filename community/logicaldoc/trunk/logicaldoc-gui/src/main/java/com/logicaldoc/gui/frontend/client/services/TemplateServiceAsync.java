package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUITemplate;

public interface TemplateServiceAsync {

	void delete(long templateId, AsyncCallback<Void> callback);

	void save(GUITemplate template, AsyncCallback<GUITemplate> callback);

	void getTemplate(long templateId, AsyncCallback<GUITemplate> callback);

	void saveOptions(long templateId, String attribute, String[] values, AsyncCallback<Void> callback);

	void deleteOptions(long templateId, String attribute, String[] values, AsyncCallback<Void> callback);

	void parseOptions(long templateId, String attribute, AsyncCallback<String[]> callback);
}