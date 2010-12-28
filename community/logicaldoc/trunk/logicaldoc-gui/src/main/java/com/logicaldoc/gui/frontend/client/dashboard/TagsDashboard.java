package com.logicaldoc.gui.frontend.client.dashboard;

import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
import com.smartgwt.client.widgets.layout.PortalLayout;

/**
 * User dashboard that displays several portlets like a portal page.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class TagsDashboard extends PortalLayout {

	private TagCloudPortlet cloud = null;

	private MostUsedTagsPortlet mostUsed = null;
	
	private TagsPortlet tags = null;

	public TagsDashboard() {
		setShowColumnMenus(false);
		setShowEdges(false);
		setShowShadow(false);
		setCanDrag(false);
		setCanDrop(false);
		setColumnBorder("0px");

		refresh();

		addResizedHandler(new ResizedHandler() {

			@Override
			public void onResized(ResizedEvent event) {
				if (cloud != null)
					cloud.refresh();
			}
		});
	}

	public void refresh() {
		if (cloud != null)
			removePortlet(cloud);

		if (mostUsed != null)
			removePortlet(mostUsed);
		
		if (tags != null)
			removePortlet(tags);

		// Place the portlets
		mostUsed = new MostUsedTagsPortlet();
		addPortlet(mostUsed, 0, 0);

		tags = new TagsPortlet();
		addPortlet(tags, 0, 1);
		
		cloud = new TagCloudPortlet();
		addPortlet(cloud, 1, 0);

		cloud.refresh();
	}
}