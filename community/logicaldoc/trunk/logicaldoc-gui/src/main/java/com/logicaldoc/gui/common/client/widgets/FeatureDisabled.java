package com.logicaldoc.gui.common.client.widgets;

import com.logicaldoc.gui.common.client.i18n.I18N;
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
		this(null);
	}

	public FeatureDisabled(Integer feature) {
		Label label = new Label();
		label.setHeight(30);
		label.setPadding(10);
		label.setAlign(Alignment.CENTER);
		label.setValign(VerticalAlignment.CENTER);
		label.setWrap(false);
		label.setIcon("[SKIN]/Dialog/warn.png");
		label.setShowEdges(false);
		if (feature == null)
			label.setContents("<b>" + I18N.message("featuredisabled") + "</b>");
		else
			label.setContents("<b>" + I18N.message("feature_" + feature) + " " + I18N.message("disabled") + "</b>");
		addMember(label);
	}
}