package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIBookmark;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIEmail;
import com.logicaldoc.gui.common.client.beans.GUIExtendedAttribute;
import com.logicaldoc.gui.common.client.beans.GUIRating;
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

	void makeImmutable(String sid, long[] ids, String comment, AsyncCallback<Void> callback);

	void lock(String sid, long[] ids, String comment, AsyncCallback<Void> callback);

	void unlock(String sid, long[] ids, AsyncCallback<Void> callback);

	void addDocuments(String sid, String language, long folderId, String encoding, boolean importZip, Long templateId,
			AsyncCallback<Void> callback);

	void checkout(String sid, long id, AsyncCallback<Void> callback);

	void checkin(String sid, GUIDocument document, boolean major, AsyncCallback<GUIDocument> callback);

	void linkDocuments(String sid, long[] inDocIds, long[] outDocIds, AsyncCallback<Void> callback);

	void restore(String sid, long docId, long folderId, AsyncCallback<Void> callback);

	void addBookmarks(String sid,  long[] targetIds, int type, AsyncCallback<Void> callback);

	void deleteBookmarks(String sid, long[] bookmarkIds, AsyncCallback<Void> callback);

	void updateBookmark(String sid, GUIBookmark bookmark, AsyncCallback<Void> callback);

	void markHistoryAsRead(String sid, String event, AsyncCallback<Void> callback);

	void markIndexable(String sid, long[] docIds, AsyncCallback<Void> callback);

	void markUnindexable(String sid, long[] docIds, AsyncCallback<Void> callback);

	void cleanUploadedFileFolder(String sid, AsyncCallback<Void> callback);

	void getRating(String sid, long docId, AsyncCallback<GUIRating> callback);

	void saveRating(String sid, GUIRating rating, AsyncCallback<Integer> callback);

	void deleteNotes(String sid, long[] ids, AsyncCallback<Void> callback);

	void addNote(String sid, long docId, String message, AsyncCallback<Long> callback);

	void bulkUpdate(String sid, long[] ids, GUIDocument vo, AsyncCallback<Void> callback);

	void addDocuments(String sid, String encoding, boolean importZip, GUIDocument metadata, AsyncCallback<Void> callback);

	void updateNote(String sid, long docId, long noteId, String message, AsyncCallback<Void> callback);

	void deleteVersions(String sid, long[] ids, AsyncCallback<GUIDocument> callback);
}
