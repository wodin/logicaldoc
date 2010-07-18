package com.logicaldoc.gui.frontend.client.workflow;

import com.google.gwt.user.client.ui.HTML;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VStack;

/**
 * A box displaying a single workflow primitive
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class WorkflowState extends VStack {

	protected boolean dropped = false;

	protected Label title;

	public WorkflowState(boolean dropped) {
		this.dropped = dropped;
		setHeight(40);
		setWidth(150);
		setBorder("1px solid #dddddd");

		if (!dropped) {
			setCanDrag(true);
			setCanDrop(true);
			setDragType("state");
		}

		title = new Label("Task Name");
		addMember(title);
		title.setHeight(21);
		title.setWrap(false);

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

		if (!dropped) {
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
	}

	public void select() {
		setBorder("1px solid 4040ff");
	}
}