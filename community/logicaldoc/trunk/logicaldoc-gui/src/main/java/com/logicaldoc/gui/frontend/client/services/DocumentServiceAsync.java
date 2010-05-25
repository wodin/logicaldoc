package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIBookmark;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIEmail;
import com.logicaldoc.gui.common.client.beans.GUIExtendedAttribute;
import com.logicaldoc.gui.common.client.beans.GUIVersion;

public interface DocumentServiceAsync {
	void getVersionsById(String sid, long id1, long id2, AsyncCallback<GUIVersion[]> callback);

	void getAttributes(String sid, long templateId, AsyncCallback<GUIExtendedAttribute[]> callback);

	void getById(String sid, long docId, AsyncCallback<GUIDocument> callback);

	void save(String sid, GUIDocument document, AsyncCallback<GUIDocument> callback);

	void sendAsEmail(String sid, GUIEmail email, AsyncCallback<String> callback);

	void updateLink(String sid, long id, String type, AsyncCallback<Void> callback);

	void deleteLinks(String sid, long[] ids, AsyncCallback<Void> callback);

	void delete(String sid, long[] ids, AsyncCallback<Void> callback);

	void deleteDiscussions(String sid, long[] ids, AsyncCallback<Void> callback);

	void startDiscussion(String sid, long docId, String title, String message, AsyncCallback<Long> callback);

	void replyPost(String sid, long discussionId, int replyTo, String title, String message,
			AsyncCallback<Integer> callback);

	void deletePosts(String sid, long discussionId, int[] ids, AsyncCallback<Void> callback);

	void makeImmutable(String sid, long[] ids, String comment, AsyncCallback<Void> callback);

	void lock(String sid, long[] ids, String comment, AsyncCallback<Void> callback);

	void unlock(String sid, long[] ids, AsyncCallback<Void> callback);

	void addDocuments(String sid, String language, boolean importZip, AsyncCallback<Void> callback);

	void checkout(String sid, long id, AsyncCallback<Void> callback);

	void checkin(String sid, long docId, boolean major, AsyncCallback<Void> callback);

	void linkDocuments(String sid, long[] inDocIds, long[] outDocIds, AsyncCallback<Void> callback);

	void restore(String sid, long docId, AsyncCallback<Void> callback);

	void addBookmarks(String sid, long[] docIds, AsyncCallback<Void> callback);

	void deleteBookmarks(String sid, long[] bookmarkIds, AsyncCallback<Void> callback);

	void updateBookmark(String sid, GUIBookmark bookmark, AsyncCallback<Void> callback);

	void markHistoryAsRead(String sid, String event, AsyncCallback<Void> callback);

	void markIndexable(String sid, long[] docIds, AsyncCallback<Void> callback);

	void markUnindexable(String sid, long[] docIds, AsyncCallback<Void> callback);
}
