package com.logicaldoc.gui.common.client.widgets;

import com.logicaldoc.gui.common.client.i18n.I18N;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;

/**
 * This is the window that must be showed to the user during a long LogicalDOC
 * computation.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class ContactingServer extends Window {

	public static ContactingServer instance;

	public static ContactingServer get() {
		if (instance == null)
			instance = new ContactingServer();
		return instance;
	}

	private ContactingServer() {
		setHeaderControls();
		setWidth(350);
		setHeight(80);
		centerInPage();
		setPadding(5);
		setLayoutAlign(Alignment.CENTER);
		setLayoutMargin(20);
		setLayoutTopMargin(15);

		Label message = new Label(I18N.message("contactingserver") + "...");
		message.setWidth100();
		message.setHeight100();
		message.setAlign(Alignment.CENTER);
		message.setOverflow(Overflow.HIDDEN);
		setOverflow(Overflow.HIDDEN);
		
		addItem(message);
	}
}