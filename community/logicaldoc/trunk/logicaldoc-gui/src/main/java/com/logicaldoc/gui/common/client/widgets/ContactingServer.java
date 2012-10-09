package com.logicaldoc.gui.common.client.widgets;

import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Dialog;
import com.smartgwt.client.widgets.Label;

/**
 * This is the window that must be showed to the user during a long LogicalDOC
 * computation.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class ContactingServer extends Dialog {

	public static ContactingServer instance;

	public static ContactingServer get() {
		if (instance == null)
			instance = new ContactingServer();
		return instance;
	}

	private ContactingServer() {
		setOpacity(70);
		setShowEdges(false);
		setShowHeader(false);
		centerInPage();
		setIsModal(true);
		setHeight(150);
		setVertical(true);
		setAlign(Alignment.CENTER);
		setBackgroundColor("#777777");
		
		
		Label message = new Label(I18N.message("contactingserver") + "...");
		message.setAlign(Alignment.CENTER);
		message.setIcon(Util.imageUrl("loading32.gif"));
		message.setIconSize(32);
		message.setOverflow(Overflow.HIDDEN);
		message.setStyleName("contactingserver");
		
		addItem(message);
	}
}