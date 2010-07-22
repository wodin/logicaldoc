package com.logicaldoc.gui.frontend.client.workflow;

import com.google.gwt.user.client.ui.HTML;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VStack;

/**
 * A box displaying a single workflow primitive
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class WorkflowState extends VStack {

	public final static int TYPE_TASK = 0;

	public final static int TYPE_END = 1;

	public final static int TYPE_JOIN = 2;

	public final static int TYPE_FORK = 3;

	protected Label title;

	protected HLayout commands = new HLayout();

	protected int type = TYPE_TASK;

	public WorkflowState(int type) {
		this.type = type;
		setHeight(40);
		setWidth(150);
		setBorder("1px solid #dddddd");
		setCanDrag(true);
		setCanDrop(true);
		setDragType("state");

		title = new Label("Task Name");
		addMember(title);
		title.setHeight(21);
		title.setWrap(false);
		if (type == TYPE_TASK) {
			title.setIcon(Util.imageUrl("task.png"));
			title.setContents(I18N.message("task") + "Name");
		} else if (type == TYPE_JOIN) {
			title.setIcon(Util.imageUrl("join.png"));
			title.setContents(I18N.message("join") + "Name");
		} else if (type == TYPE_FORK) {
			title.setIcon(Util.imageUrl("fork.png"));
			title.setContents(I18N.message("fork") + "Name");
		} else if (type == TYPE_END) {
			title.setIcon(Util.imageUrl("endState.png"));
			title.setContents(I18N.message("endstate") + "Name");
		}

		commands.setHeight(12);
		commands.setWidth(1);
		commands.setAlign(Alignment.RIGHT);
		addMember(commands);

		HTML delete = new HTML("<a href='#'>" + I18N.message("ddelete").toLowerCase() + "</a>");
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
				WorkflowDesigner.get().onStateSelect(getType());
			}
		});
		commands.addMember(edit);
	}

	public int getType() {
		return type;
	}

	public void onSave() {
		// TODO
	}
}