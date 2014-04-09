package com.logicaldoc.gui.frontend.client.dashboard;

import com.logicaldoc.gui.common.client.Session;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.events.VisibilityChangedEvent;
import com.smartgwt.client.widgets.events.VisibilityChangedHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This represent a tag cloud using a 3-D ball.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class TagCloud extends VLayout {

	private HTMLFlow container = null;

	public TagCloud() {
		addVisibilityChangedHandler(new VisibilityChangedHandler() {

			@Override
			public void onVisibilityChanged(VisibilityChangedEvent event) {
				refresh();
			}
		});
	}

	/**
	 * Refresh the display of the tag cloud. Usually used after an adding or
	 * deletion of word.
	 */
	public void refresh() {

		if (container != null) {
			removeMember(container);
			container = null;
		}

		if (isVisible()) {
			container = new HTMLFlow() {

				@Override
				public String getInnerHTML() {
					return "<iframe src='tagcloud/cloud.jsp?sid=" + Session.get().getSid()
							+ "' style='border: 0px solid white; width:100%; height:" + TagCloud.this.getHeight()
							+ ";' height='" + TagCloud.this.getHeight() + "' scrolling='no'>";
				}

			};
			addMember(container);
		}

	}
}