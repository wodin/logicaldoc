package com.logicaldoc.gui.common.client.widgets;

import com.google.gwt.user.client.Window;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;

/**
 * Simple label showing a warning message.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class WarningLabel extends Label {

	public WarningLabel(String message, final String url) {
		super(message);
		setHeight(25);
		setWrap(false);
		setIcon("[SKIN]/Dialog/warn.png");
		if (url != null) {
			setCursor(Cursor.HAND);
			addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					Window.open(url, "_self", "");
				}
			});
		}
	}
}