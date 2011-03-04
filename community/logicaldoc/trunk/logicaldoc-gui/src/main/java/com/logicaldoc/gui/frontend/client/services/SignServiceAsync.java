package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SignServiceAsync {

	void extractSubjectSignatures(String sid, long userId, AsyncCallback<String[]> callback);

	void storeSignature(String sid, long userId, String signerName, AsyncCallback<String> callback);

	void signDocument(String sid, long userId, long docId, AsyncCallback<String> callback);
}
