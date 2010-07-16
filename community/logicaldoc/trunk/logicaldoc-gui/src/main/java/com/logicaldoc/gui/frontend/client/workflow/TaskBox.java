package com.logicaldoc.gui.frontend.client.workflow;

import com.google.gwt.user.client.ui.HTML;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VStack;

/**
 * A box displaying a single task.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class TaskBox extends VStack {

	public TaskBox() {
		setHeight(40);
		setWidth(150);
		setBorder("1px solid #dddddd");
		setCanDrag(true);
		setCanDrop(true);
		setDragType("task");

		Label title = new Label("Task Name");
		addMember(title);
		title.setHeight(21);
		title.setWrap(false);
		title.setIcon("icons/16/approved.png");

		HLayout commands = new HLayout();
		commands.setHeight(12);
		commands.setWidth(1);
		commands.setAlign(Alignment.RIGHT);
		addMember(commands);

		HTML delete = new HTML("<a href='#'>" + I18N.message("delete").toLowerCase() + "</a>");
		delete.setWidth("1px");
		delete.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {
			@Override
			public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
				SC.say("Hello World");
			}
		});
		commands.addMember(delete);

		HTML edit = new HTML("&nbsp;&nbsp;<a href='#'>" + I18N.message("edit").toLowerCase() + "</a>");
		edit.setWidth("1px");
		edit.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {
			@Override
			public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
				SC.say("Hello World");
			}
		});
		commands.addMember(edit);
	}

	public void select() {
		setBorder("1px solid 4040ff");
	}
}