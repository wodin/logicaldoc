package com.logicaldoc.gui.common.client.widgets;

import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Dialog;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;

/**
 * This is the panel shown when the session timeout occurs.
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.4.1
 */
public class SessionTimeout extends Dialog {

	public static SessionTimeout instance;

	public static SessionTimeout get() {
		if (instance == null)
			instance = new SessionTimeout();
		return instance;
	}

	private SessionTimeout() {
		setShowEdges(false);
		setShowHeader(false);
		centerInPage();
		setIsModal(true);
		setVertical(true);
		setAlign(Alignment.CENTER);
		setMargin(2);
		setMembersMargin(0);
		setBodyColor("white");
		setBackgroundColor("white");
		setOverflow(Overflow.HIDDEN);
		setHeight100();
		setWidth100();

		Label message = new Label(I18N.message("sessiontimeout"));
		message.setWrap(false);
		message.setAlign(Alignment.CENTER);
		message.setStyleName("sessiontimeout");
		message.setLayoutAlign(Alignment.CENTER);
		message.setLayoutAlign(VerticalAlignment.CENTER);
		message.setHeight(50);
		message.setBackgroundColor("white");

		IButton ok = new IButton(I18N.message("ok"));
		ok.setLayoutAlign(Alignment.CENTER);
		ok.setLayoutAlign(VerticalAlignment.CENTER);
		ok.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				try {
					Session.get().logout();
				} catch (Throwable t) {
					
				}
				
				Util.redirectToLoginUrl();
			}
		});

		addMember(message);
		addMember(ok);
	}
}