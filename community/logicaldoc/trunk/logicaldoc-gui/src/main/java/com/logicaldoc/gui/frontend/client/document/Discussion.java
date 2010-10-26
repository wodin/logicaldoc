package com.logicaldoc.gui.frontend.client.document;

import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.widgets.FeatureDisabled;

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
		if (Feature.enabled(Feature.FORUMS)) {
			setWidth100();
			setHeight100();
			panel = new DiscussionsPanel(document, this);
			initGUI();
		} else
			addMember(new FeatureDisabled());
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