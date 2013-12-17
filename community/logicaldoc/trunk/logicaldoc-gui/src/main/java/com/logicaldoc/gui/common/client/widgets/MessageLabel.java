package com.logicaldoc.gui.common.client.widgets;

import com.google.gwt.user.client.Window;
import com.logicaldoc.gui.common.client.beans.GUIMessage;
import com.logicaldoc.gui.common.client.i18n.I18N;
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
public class MessageLabel extends Label {

	public MessageLabel(final GUIMessage message) {
		super("<span style='text-decoration: underline'>"+message.getMessage()+"</span>");
		setHeight(25);
		setWrap(false);
		if (message.getPriority() == GUIMessage.PRIO_INFO)
			setIcon("[SKIN]/Dialog/notify.png");
		else if (message.getPriority() == GUIMessage.PRIO_WARN)
			setIcon("[SKIN]/Dialog/warn.png");
		if (message.getUrl() != null) {
			setCursor(Cursor.HAND);
			addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Window.open(message.getUrl(), "_self", "");
				}
			});
		}
	}
}