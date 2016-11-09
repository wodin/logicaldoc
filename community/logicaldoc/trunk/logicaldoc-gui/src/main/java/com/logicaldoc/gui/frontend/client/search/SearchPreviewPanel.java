package com.logicaldoc.gui.frontend.client.search;

import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.frontend.client.document.DocumentsPreviewPanel;

/**
 * Shows a preview panels in the Search workspace
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.6
 */
public class SearchPreviewPanel extends DocumentsPreviewPanel {

	public SearchPreviewPanel() {
		super();
		super.widthCookieName = Constants.COOKIE_HITSLIST_PREV_W;
	}
}