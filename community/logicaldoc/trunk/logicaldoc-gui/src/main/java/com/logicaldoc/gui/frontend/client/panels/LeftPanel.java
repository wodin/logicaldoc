package com.logicaldoc.gui.frontend.client.panels;

import com.google.gwt.user.client.ui.DockPanel;
import com.smartgwt.client.widgets.HTMLFlow;

/**
 * The left-side panel
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class LeftPanel extends DockPanel {
	public LeftPanel() {
		// Use a table to layout all top widgets
		add(new HTMLFlow("left"), DockPanel.WEST);
	}
}