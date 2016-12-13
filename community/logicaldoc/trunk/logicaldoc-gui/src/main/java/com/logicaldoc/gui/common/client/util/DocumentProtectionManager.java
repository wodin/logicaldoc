package com.logicaldoc.gui.common.client.util;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.util.ValueCallback;

/**
 * Utility class for handling passords on documents.
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.6
 */
public class DocumentProtectionManager {

	protected static DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	// Stores the doc's ID and the password used to un-protect it
	private static Map<Long, String> unprotectedDocs = new HashMap<Long, String>();

	public static boolean isUnprotected(long docId) {
		return unprotectedDocs.containsKey(docId);
	}

	/**
	 * Controls the document and sks the user for a password(if needed).
	 * 
	 * @param docId
	 * @param handler Optional handler to react something when the documents
	 *        gets unlocked
	 */
	public static void askForPassword(final Long docId, final DocumentProtectionHandler handler) {
		documentService.getById(docId, new AsyncCallback<GUIDocument>() {
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(final GUIDocument result) {
				if (result.isPasswordProtected()) {
					if (unprotectedDocs.containsKey(docId)) {
						// The document was already unlocked
						if (handler != null)
							handler.onUnprotected(result);
					} else {
						LD.askForPassword(I18N.message("security"), I18N.message("enterprotpassword"), null,
								new ValueCallback() {

									@Override
									public void execute(final String password) {
										if (password == null)
											SC.warn(I18N.message("accesdenied"));
										documentService.unprotect(result.getId(), password,
												new AsyncCallback<Boolean>() {

													@Override
													public void onFailure(Throwable caught) {
														Log.serverError(caught);
													}

													@Override
													public void onSuccess(Boolean granted) {
														if (granted) {
															// Save the password
															// for
															// further reference
															unprotectedDocs.put(docId, password);
															if (handler != null)
																handler.onUnprotected(result);
														} else if (handler != null) {
															SC.warn(I18N.message("accesdenied"));
														}
													}
												});
									}
								});
					}
				} else if (handler != null) {
					handler.onUnprotected(result);
				}
			}
		});
	}

	public static abstract class DocumentProtectionHandler {
		public abstract void onUnprotected(GUIDocument document);
	}
}
