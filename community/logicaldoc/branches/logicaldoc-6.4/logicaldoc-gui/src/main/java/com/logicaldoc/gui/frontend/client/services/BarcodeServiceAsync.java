package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIBarcodeEngine;
import com.logicaldoc.gui.common.client.beans.GUIBarcodePattern;

public interface BarcodeServiceAsync {

	void getInfo(String sid, AsyncCallback<GUIBarcodeEngine> callback);

	void save(String sid, GUIBarcodeEngine engine, AsyncCallback<Void> callback);

	void rescheduleAll(String sid, AsyncCallback<Void> callback);

	void markUnprocessable(String sid, long[] ids, AsyncCallback<Void> asyncCallback);

	void loadPatterns(String sid, Long templateId, AsyncCallback<GUIBarcodePattern[]> callback);

	void savePatterns(String sid, String[] patterns, Long templateId, AsyncCallback<Void> callback);

}