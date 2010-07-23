package com.logicaldoc.gui.frontend.client.workflow;

import com.logicaldoc.gui.common.client.beans.GUIWorkflow;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SubmitItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VStack;

/**
 * Where the workflow diagram is drawn
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class WorkflowDesigner extends VStack implements WorkflowObserver {

	private Accordion accordion = null;

	public final static int TYPE_TASK = 0;

	public final static int TYPE_END = 1;

	public final static int TYPE_JOIN = 2;

	public final static int TYPE_FORK = 3;

	// HStack or HLayout with Accordion e Drawing Panel
	private HLayout layout = new HLayout();

	private GUIWorkflow workflow = null;

	public WorkflowDesigner(GUIWorkflow workflow) {
		this.workflow = workflow;

		setMembersMargin(5);

		addMember(new WorkflowToolstrip(this));
		addMember(new StateToolstrip(this));

		accordion = new Accordion(workflow);
		layout.addMember(accordion);
		layout.addMember(new DrawingPanel(this));
		addMember(layout);
	}

	@Override
	public void onStateSelect(int type) {
		if (type == TYPE_TASK) {
			TaskDialog window = new TaskDialog();
			window.show();
		} else {
			final Window window = new Window();
			String typeString = "";
			if (type == TYPE_JOIN) {
				typeString = I18N.message("join");
			} else if (type == TYPE_FORK) {
				typeString = I18N.message("fork");
			} else if (type == TYPE_END) {
				typeString = I18N.message("endstate");
			}

			window.setTitle(I18N.message("editworkflowstate", typeString));
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

	@Override
	public void onWorkflowSelect(GUIWorkflow workflow) {
		removeMember(layout);

		// layout.removeMember(accordion);
		// accordion.destroy();
		// accordion = new Accordion(workflow);
		// layout.addMember(accordion);
		// accordion.refresh(workflow);
	}

	public GUIWorkflow getWorkflow() {
		return workflow;
	}
}