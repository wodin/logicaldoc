package com.logicaldoc.gui.frontend.client.workflow;

import com.logicaldoc.gui.common.client.beans.GUIWFState;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * A box displaying a single workflow primitive
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class WorkflowState extends VLayout {

	protected Label title;

	protected HLayout commands = null;

	private WorkflowDesigner workflowDesigner = null;

	private GUIWFState wfState = null;

	public WorkflowState(WorkflowDesigner designer, GUIWFState wfState) {
		this.workflowDesigner = designer;
		this.wfState = wfState;
		setWidth(120);
		setHeight(40);
		setBorder("1px solid #dddddd");
		setCanDrag(true);
		setCanDrop(true);
		setDragType("state");
		setMembersMargin(3);

		if (designer.getWorkflow() != null && designer.getWorkflow().getStartStateId().equals(wfState.getId())) {
			setBorder("1px dotted #00ff00");
		}

		title = new Label(this.wfState.getName());
		title.setHeight(15);
		title.setWrap(false);
		title.setIcon(this.wfState.getIcon());
		title.setContents(this.wfState.getName());
		addMember(title);

		commands = new HLayout(5);
		commands.setAlign(Alignment.LEFT);
		addMember(commands);

		Label delete = ItemFactory.newLinkLabel("ddelete");
		delete.setWidth(38);
		delete.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {

			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				getDesigner().onStateDelete(getWfState());
			}
		});
		if (!designer.isOnlyVisualization())
			commands.addMember(delete);

		Label edit = ItemFactory.newLinkLabel("edit");
		edit.setWidth(38);
		edit.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {

			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				getDesigner().onStateSelect(getWfState());
			}
		});
		if (!designer.isOnlyVisualization())
			commands.addMember(edit);
	}

	public WorkflowDesigner getDesigner() {
		return workflowDesigner;
	}

	public GUIWFState getWfState() {
		return wfState;
	}
}