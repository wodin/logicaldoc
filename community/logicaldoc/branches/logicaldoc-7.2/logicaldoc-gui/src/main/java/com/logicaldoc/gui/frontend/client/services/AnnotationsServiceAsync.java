package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AnnotationsServiceAsync {

	void prepareAnnotations(String sid, long docId, String fileVersion, AsyncCallback<Integer> callback);

	void addAnnotation(String sid, long docId, int page, String snippet, String text, AsyncCallback<Long> callback);

	void savePage(String sid, long docId, int page, String content, AsyncCallback<Void> callback);

}
