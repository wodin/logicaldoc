package com.logicaldoc.gui.common.client.widgets;

import com.logicaldoc.gui.common.client.I18N;

/**
 * Simple panel to show the user that a feature is disabled.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class FeatureDisabled extends HTMLPanel {
	public FeatureDisabled() {
		super("<b>" + I18N.getMessage("featuredisabled") + "</b>");
		setBackgroundColor("#F8FFBF");
	}
}
