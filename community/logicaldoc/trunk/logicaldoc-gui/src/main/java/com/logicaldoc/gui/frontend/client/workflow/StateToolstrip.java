package com.logicaldoc.gui.frontend.client.workflow;

import com.google.gwt.core.client.GWT;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
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

		ToolStripButton newTemplate = new ToolStripButton(I18N.message("newwftemplate"));
		newTemplate.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SC.say("New workflow template");
			}
		});
		addButton(newTemplate);
		addSeparator();

		ComboBoxItem template = ItemFactory.newGroupSelector("template", " ");
		template.setShowTitle(false);
		addFormItem(template);
		ToolStripButton load = new ToolStripButton(I18N.message("load"));
		load.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SC.say("load the selected template");
			}
		});
		addButton(load);
		addSeparator();

		ToolStripButton save = new ToolStripButton(I18N.message("save"));
		save.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SC.say("save the selected template");
			}
		});
		addButton(save);
		addSeparator();

		ToolStripButton deploy = new ToolStripButton(I18N.message("deploy"));
		deploy.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SC.say("deploy the loaded template");
			}
		});
		addButton(deploy);
		addSeparator();

		ToolStripButton delete = new ToolStripButton(I18N.message("ddelete"));
		delete.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SC.say("delete the loaded template");
			}
		});
		addButton(delete);
		addSeparator();

		ToolStripButton close = new ToolStripButton(I18N.message("close"));
		close.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SC.say("Close the current workflow");
			}
		});
		addButton(close);

		addFill();
	}
}
