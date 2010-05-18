package com.logicaldoc.gui.frontend.client.system;

import com.google.gwt.core.client.GWT;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.smartgwt.client.types.ContentsType;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Main log panel
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class LogPanel extends VLayout {
	public LogPanel(String appender) {
		setHeight100();

		final HTMLPane htmlPane = new HTMLPane();
		htmlPane.setWidth100();
		htmlPane.setHeight100();
		htmlPane.setShowEdges(true);
		htmlPane.setContentsURL(GWT.getHostPageBaseURL() + "log?sid=" + Session.get().getSid() + "&appender="
				+ appender);
		htmlPane.setContentsType(ContentsType.PAGE);

		HStack hStack = new HStack();
		hStack.setHeight(50);
		hStack.setLayoutMargin(10);
		hStack.setMembersMargin(10);

		IButton refresh = new IButton(I18N.getMessage("refresh"));
		refresh.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				htmlPane.redraw();
				htmlPane.setWidth100();
				htmlPane.setHeight100();
			}
		});
		hStack.addMember(refresh);

		addMember(hStack);
		addMember(htmlPane);
		draw();
	}
}
