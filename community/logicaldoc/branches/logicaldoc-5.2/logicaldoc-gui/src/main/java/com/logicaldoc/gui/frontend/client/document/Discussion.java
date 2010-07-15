package com.logicaldoc.gui.frontend.client.document;

import com.logicaldoc.gui.common.client.beans.GUIDocument;

/**
 * This panel contains forums or posts
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class Discussion extends DocumentDetailTab {

	private DocumentDetailTab panel;

	public Discussion(GUIDocument document) {
		super(document, null);
		setWidth100();
		setHeight100();
		panel = new DiscussionsPanel(document, this);
		initGUI();
	}

	private void initGUI() {
		if (panel != null && contains(panel))
			removeMember(panel);
		addMember(panel);
	}

	@Override
	public void destroy() {
		if (panel != null)
			panel.destroy();
	}

	public void showPosts(long discussionId) {
		panel.destroy();
		panel = new PostsPanel(document, discussionId, this);
		initGUI();
	}

	public void showDiscussions() {
		panel.destroy();
		panel = new DiscussionsPanel(document, this);
		initGUI();
	}

}