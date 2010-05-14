package com.logicaldoc.gui.frontend.client.dashboard;

import com.logicaldoc.gui.common.client.Constants;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DragAppearance;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.LayoutPolicy;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.Portlet;

/**
 * User dashboard
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class UserDashboard extends PortalLayout {

	public UserDashboard() {
		setShowColumnMenus(false);
		setShowEdges(false);
		setShowShadow(false);
		setColumnBorder("0px");

		addPortlet(new HistoryPortlet(Constants.EVENT_LOCKED), 0, 0);

		// create portlets...
		for (int i = 1; i <= 6; i++) {
			Portlet portlet = new Portlet();
			portlet.setShowShadow(true);
			portlet.setAnimateMinimize(true);

			// Window is draggable with "outline" appearance by default.
			// "target" is the solid appearance.
			portlet.setDragAppearance(DragAppearance.OUTLINE);
			portlet.setCanDrop(true);

			// customize the appearance and order of the controls in the window
			// header
			portlet.setHeaderControls(HeaderControls.MINIMIZE_BUTTON, HeaderControls.HEADER_LABEL, new HeaderControl(
					HeaderControl.SETTINGS), new HeaderControl(HeaderControl.HELP));

			// show either a shadow, or translucency, when dragging a portlet
			// (could do both at the same time, but these are not visually
			// compatible effects)
			// setShowDragShadow(true);
			portlet.setDragOpacity(30);

			// these settings enable the portlet to autosize its height only to
			// fit its contents
			// (since width is determined from the containing layout, not the
			// portlet contents)
			portlet.setVPolicy(LayoutPolicy.NONE);
			portlet.setOverflow(Overflow.VISIBLE);

			portlet.setTitle("Portlet" + i);
			Label label = new Label();
			label.setAlign(Alignment.CENTER);
			label.setLayoutAlign(VerticalAlignment.CENTER);
			label.setContents("Portlet contents" + i);
			portlet.addItem(label);

			if (i < 5)
				addPortlet(portlet, 0, 1);
			else
				addPortlet(portlet, 1, 0);
		}
	}
}
