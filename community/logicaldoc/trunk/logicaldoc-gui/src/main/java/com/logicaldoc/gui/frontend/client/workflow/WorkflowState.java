package com.logicaldoc.gui.frontend.client.workflow;

import com.logicaldoc.gui.common.client.beans.GUIWFState;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.util.ValueCallback;
import com.smartgwt.client.widgets.Dialog;
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
		setWidth(150);
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
		commands.setMembersMargin(7);
		addMember(commands);

		Label delete = ItemFactory.newLinkLabel("ddelete");
		delete.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {

			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				getDesigner().onStateDelete(getWfState());
			}
		});

		Label edit = ItemFactory.newLinkLabel("edit");
		edit.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {

			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				getDesigner().onStateSelect(getWfState());
			}
		});

		Label addtransition = ItemFactory.newLinkLabel("addtransition");
		addtransition.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {

			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				Dialog dialog = new Dialog();
				dialog.setWidth(200);

				SC.askforValue(I18N.message("addtransition"), "<b>" + I18N.message("name") + ":</b>", "",
						new ValueCallback() {
							@Override
							public void execute(String value) {
								if (value == null || "".equals(value.trim()))
									return;

								workflowDesigner.onAddTransition(getWfState(), null, value);
							}
						}, dialog);
			}
		});

		if (!designer.isReadOnly()) {
			commands.addMember(delete);
			commands.addMember(edit);
			if (wfState.getType() == GUIWFState.TYPE_TASK)
				commands.addMember(addtransition);
		}
	}

	public WorkflowDesigner getDesigner() {
		return workflowDesigner;
	}

	public GUIWFState getWfState() {
		return wfState;
	}
}