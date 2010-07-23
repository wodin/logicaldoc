package com.logicaldoc.gui.frontend.client.template;

import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.frontend.client.administration.AdminPanel;
import com.logicaldoc.gui.frontend.client.workflow.WorkflowDesigner;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This panel shows the administration document metadata and workflow menu
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class MetadataAndWorkflowMenu extends VLayout {

	public MetadataAndWorkflowMenu() {
		setMargin(10);
		setMembersMargin(5);

		Button templates = new Button(I18N.message("templates"));
		templates.setWidth100();
		templates.setHeight(25);

		if (Feature.visible(Feature.TEMPLATE)) {
			setMembers(templates);
			if (!Feature.enabled(Feature.TEMPLATE)) {
				templates.setDisabled(true);
				templates.setTooltip(I18N.message("featuredisabled"));
			}
		}
		templates.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new TemplatesPanel());
			}
		});

		Button workflow = new Button(I18N.message("workflow"));
		workflow.setWidth100();
		workflow.setHeight(25);

		if (Feature.visible(Feature.WORKFLOW_BASIC)) {
			setMembers(workflow);
			if (!Feature.enabled(Feature.WORKFLOW_BASIC)) {
				workflow.setDisabled(true);
				workflow.setTooltip(I18N.message("featuredisabled"));
			}
		}
		workflow.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new WorkflowDesigner(null));
			}
		});
	}
}
