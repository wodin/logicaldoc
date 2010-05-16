package com.logicaldoc.gui.common.client.widgets;

import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Simple panel to show the user that a feature is disabled.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class FeatureDisabled extends VLayout {
	public FeatureDisabled() {
		Label label = new Label();
		label.setHeight(30);
		label.setPadding(10);
		label.setAlign(Alignment.CENTER);
		label.setValign(VerticalAlignment.CENTER);
		label.setWrap(false);
		label.setIcon(Util.imageUrl("Dialog/warn.png"));
		label.setShowEdges(true);
		label.setContents("<b>" + I18N.getMessage("featuredisabled") + "</b>");
		addMember(label);
	}
}