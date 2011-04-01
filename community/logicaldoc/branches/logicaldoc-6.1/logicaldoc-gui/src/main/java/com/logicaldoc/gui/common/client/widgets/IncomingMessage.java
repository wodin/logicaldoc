package com.logicaldoc.gui.common.client.widgets;

import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * An useful panel to show incoming messages to the user
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
public class IncomingMessage extends HLayout {

	private Label label = null;

	private ClickHandler clickHandler = null;

	private HTMLPane spacer = null;

	private Img closeImg = null;

	public IncomingMessage(String message, ClickHandler handler) {
		setHeight(20);
		setStyleName("warn");
		setVisible(false);
		setAlign(Alignment.LEFT);

		setMessage(message);

		this.clickHandler = handler;
	}

	public void setMessage(String message) {
		if (label != null)
			removeMember(label);
		if (spacer != null)
			removeMember(spacer);
		if (closeImg != null)
			removeMember(closeImg);

		label = new Label(message);
		label.setWrap(false);
		label.setMargin(3);
		addMember(label);

		spacer = new HTMLPane();
		spacer.setContents("<div>&nbsp;</div>");
		spacer.setWidth("60%");
		spacer.setOverflow(Overflow.HIDDEN);

		addMember(spacer);

		closeImg = ItemFactory.newImgIcon("delete.png");
		closeImg.setLayoutAlign(Alignment.RIGHT);
		closeImg.setHeight("16px");
		closeImg.setTooltip(I18N.message("close"));
		closeImg.setCursor(Cursor.HAND);
		closeImg.setLayoutAlign(VerticalAlignment.CENTER);
		if (clickHandler != null)
			closeImg.addClickHandler(clickHandler);
		else
			closeImg.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					destroy();
				}
			});

		addMember(closeImg);
	}

	public void setClickHandler(ClickHandler clickHandler) {
		this.clickHandler = clickHandler;
	}
}
