package com.logicaldoc.gui.frontend.client.workflow;

import com.google.gwt.core.client.GWT;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.services.SystemService;
import com.logicaldoc.gui.frontend.client.services.SystemServiceAsync;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * A States toolbar.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class StateToolstrip extends ToolStrip {
	protected SystemServiceAsync systemService = (SystemServiceAsync) GWT.create(SystemService.class);

	public StateToolstrip() {
		super();
		setWidth100();

		ToolStripButton newTask = new ToolStripButton(I18N.message("addtask"));
		newTask.setIcon(Util.imageUrl("task.png"));
		newTask.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SC.say("New Task");
			}
		});
		addButton(newTask);
		addSeparator();
		
		ToolStripButton newEndState = new ToolStripButton(I18N.message("addendstate"));
		newEndState.setIcon(Util.imageUrl("endState.png"));
		newEndState.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SC.say("New End State");
			}
		});
		addButton(newEndState);
		addSeparator();
		
		ToolStripButton newFork = new ToolStripButton(I18N.message("addfork"));
		newFork.setIcon(Util.imageUrl("fork.png"));
		newFork.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SC.say("New Fork");
			}
		});
		addButton(newFork);
		addSeparator();

		ToolStripButton newJoin = new ToolStripButton(I18N.message("addjoin"));
		newJoin.setIcon(Util.imageUrl("join.png"));
		newJoin.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SC.say("New Join");
			}
		});
		addButton(newJoin);
		addSeparator();

		ComboBoxItem template = ItemFactory.newGroupSelector("template", I18N.message("startstate"));
		addFormItem(template);
		
		addFill();
	}
}
