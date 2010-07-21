package com.logicaldoc.gui.frontend.client.workflow;

import com.google.gwt.user.client.ui.HTML;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SubmitItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
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
				if (getType() == TYPE_TASK) {
//					Accordion.get().getWfSettingsSection().setExpanded(false);
//					Accordion.get().getTaskSettingsSection().setExpanded(true);
					Accordion.get().showTaskSection();
				} else {
					final Window window = new Window();
					String type = "";
					if (getType() == TYPE_JOIN) {
						type = I18N.message("join");
					} else if (getType() == TYPE_FORK) {
						type = I18N.message("fork");
					} else if (getType() == TYPE_END) {
						type = I18N.message("endstate");
					}
					
					window.setTitle(I18N.message("editworkflowstate", type));
					window.setWidth(250);
					window.setHeight(200);
					window.setCanDragResize(true);
					window.setIsModal(true);
					window.setShowModalMask(true);
					window.centerInPage();

					DynamicForm form = new DynamicForm();
					form.setTitleOrientation(TitleOrientation.TOP);
					form.setNumCols(1);
					TextItem name = ItemFactory.newTextItem("name", "name", null);
					name.setRequired(true);

					SubmitItem saveButton = new SubmitItem("save", I18N.message("save"));
					saveButton.setAlign(Alignment.LEFT);
					saveButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							// onSave();
							window.destroy();
						}
					});

					form.setFields(name, saveButton);

					window.addItem(form);
					window.show();
				}
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