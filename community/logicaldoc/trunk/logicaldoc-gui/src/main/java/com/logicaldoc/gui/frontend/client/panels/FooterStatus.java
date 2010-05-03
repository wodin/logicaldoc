package com.logicaldoc.gui.frontend.client.panels;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Image;
import com.logicaldoc.gui.common.client.beans.GUIEvent;
import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * Small panel showing the last event message. If the user clicks it, a list of
 * all recent events is displayed.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class FooterStatus extends HLayout {
	private static FooterStatus instance = new FooterStatus();

	List<GUIEvent> events = new ArrayList<GUIEvent>();

	private Label statusLabel;

	private FooterStatus() {
		setWidth100();
		setAlign(Alignment.RIGHT);
		setMargin(2);
		setMembersMargin(2);

		Image icon = new Image(Util.imageUrl("application/logging.png"));
		icon.setWidth("16px");
		icon.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {
			@Override
			public void onClick(com.google.gwt.event.dom.client.ClickEvent arg0) {
				EventsWindow.get().show();
				if (statusLabel != null)
					statusLabel.setContents("");
			}
		});
		addMember(icon);
	}

	public static FooterStatus getInstance() {
		return instance;
	}

	private void prepareLabel(final String text, String style) {
		if (statusLabel != null && contains(statusLabel))
			removeMember(statusLabel);
		statusLabel = new Label(text);
		addMember(statusLabel, 0);

		statusLabel.setStyleName(style);
		statusLabel.setWrap(false);
		statusLabel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				EventsWindow.get().show();
				statusLabel.setContents("");
			}
		});
	}

	public void error(String message, String detail) {
		prepareLabel(message, "footerError");
		GUIEvent event = new GUIEvent();
		event.setMessage(message);
		event.setDetail(detail != null ? detail : message);
		event.setSeverity(GUIEvent.ERROR);
		EventsWindow.get().addEvent(event);
	}

	public void warn(String message, String detail) {
		prepareLabel(message, "footerWarn");
		GUIEvent event = new GUIEvent();
		event.setMessage(message);
		event.setDetail(detail != null ? detail : message);
		event.setSeverity(GUIEvent.WARNING);
		EventsWindow.get().addEvent(event);
	}

	public void info(String message, String detail) {
		prepareLabel(message, "footerInfo");
		GUIEvent event = new GUIEvent();
		event.setMessage(message);
		event.setDetail(detail != null ? detail : message);
		event.setSeverity(GUIEvent.INFO);
		EventsWindow.get().addEvent(event);
	}
}