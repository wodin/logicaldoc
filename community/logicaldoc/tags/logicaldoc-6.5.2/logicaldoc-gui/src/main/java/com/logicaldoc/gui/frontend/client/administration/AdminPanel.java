package com.logicaldoc.gui.frontend.client.administration;

import com.logicaldoc.gui.frontend.client.system.GeneralPanel;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This panel is used to show the user a list of search results
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class AdminPanel extends HLayout {

	private static AdminPanel instance;

	private VLayout right = new VLayout();

	private Canvas content;

	private AdminPanel() {
		setWidth100();

		// Prepare the collapsible menu
		AdminMenu leftMenu = AdminMenu.get();
		leftMenu.setWidth(250);
		leftMenu.setShowResizeBar(true);

		addMember(leftMenu);
		addMember(right);

		setContent(new GeneralPanel());
	}

	public void setContent(Canvas content) {
		if (this.content != null) {
			if (right.contains(this.content))
				right.removeChild(this.content);
			this.content.destroy();
		}
		this.content = content;
		right.addMember(this.content);
	}

	public static AdminPanel get() {
		if (instance == null)
			instance = new AdminPanel();
		return instance;
	}

	public Canvas getContent() {
		return content;
	}
}