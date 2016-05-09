package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SignServiceAsync {

	void extractSubjectSignatures(Long docId, String fileVersion, AsyncCallback<String[]> callback);

	void storeSignature(AsyncCallback<String> callback);

	void resetSignature(long userId, AsyncCallback<Boolean> callback);

	void resetPrivateKey(long userId, AsyncCallback<Boolean> callback);

	void storePrivateKey(String keyPassword, AsyncCallback<String> callback);

	void extractKeyDigest(AsyncCallback<String> callback);

	void signDocuments(long[] docIds, AsyncCallback<String> callback);

	void storeSignedDocument(long docId, AsyncCallback<String> callback);
}
