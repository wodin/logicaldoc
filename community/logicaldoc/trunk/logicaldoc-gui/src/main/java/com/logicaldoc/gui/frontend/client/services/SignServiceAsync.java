package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SignServiceAsync {

	void extractSubjectSignatures(String sid, Long docId, String fileVersion, AsyncCallback<String[]> callback);

	void storeSignature(String sid, AsyncCallback<String> callback);

	void resetSignature(String sid, long userId, AsyncCallback<Boolean> callback);

	void resetPrivateKey(String sid, long userId, AsyncCallback<Boolean> callback);

	void storePrivateKey(String sid, String keyPassword, AsyncCallback<String> callback);

	void extractKeyDigest(String sid, AsyncCallback<String> callback);

	void signDocuments(String sid, long[] docIds, AsyncCallback<String> callback);

	void storeSignedDocument(String sid, long docId, AsyncCallback<String> callback);
}
