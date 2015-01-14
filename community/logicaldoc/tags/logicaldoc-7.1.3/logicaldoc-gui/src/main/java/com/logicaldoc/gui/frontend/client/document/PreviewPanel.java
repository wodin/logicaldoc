package com.logicaldoc.gui.frontend.client.document;

import com.logicaldoc.gui.common.client.beans.GUIDocument;

/**
 * This panel shows the full preview of a document
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.7.1
 */
public class PreviewPanel extends DocumentDetailTab {

	private com.logicaldoc.gui.common.client.widgets.PreviewPanel panel = null;

	public PreviewPanel(final GUIDocument document) {
		super(document, null, null);
		setMembersMargin(1);
	}

	@Override
	protected void onTabSelected() {
		if (panel == null) {
			panel = new com.logicaldoc.gui.common.client.widgets.PreviewPanel(document, 100);
			setMembers(panel);
		}
	}
}