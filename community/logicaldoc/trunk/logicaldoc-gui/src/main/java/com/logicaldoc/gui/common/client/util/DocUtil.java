package com.logicaldoc.gui.common.client.util;

import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.util.DocumentProtectionManager.DocumentProtectionHandler;

/**
 * Some utility methods for the documents
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.6
 */
public class DocUtil {

	public static void download(final long docId, final String fileVersion) {
		DocumentProtectionManager.askForPassword(docId, new DocumentProtectionHandler() {

			@Override
			public void onUnprotected(GUIDocument document) {
				WindowUtils.openUrl(Util.downloadURL(docId, fileVersion, false));
			}
		});
	}

}
