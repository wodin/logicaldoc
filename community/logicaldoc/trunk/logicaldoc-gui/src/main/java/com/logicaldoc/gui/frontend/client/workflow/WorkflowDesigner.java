package com.logicaldoc.gui.frontend.client.workflow;

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

	private static WorkflowDesigner instance;

	public final static int TYPE_TASK = 0;

	public final static int TYPE_END = 1;

	public final static int TYPE_JOIN = 2;

	public final static int TYPE_FORK = 3;

	// HStack or HLayout with Accordion e Drawing Panel
	private HLayout layout = new HLayout();

	public WorkflowDesigner() {
		setMembersMargin(5);

		addMember(new WorkflowToolstrip());
		addMember(new StateToolstrip());

		accordion = new Accordion(false);
		layout.addMember(accordion);
		layout.addMember(new DrawingPanel());
		addMember(layout);
	}

	public static WorkflowDesigner get() {
		if (instance == null)
			instance = new WorkflowDesigner();
		return instance;
	}

	@Override
	public void onStateSelect(int type) {
		if (type == TYPE_TASK) {
			TaskDialog window = new TaskDialog();
			window.show();

			// accordion.showTaskSection();

			// if(!layout.contains(accordion))
			// SC.say("xxxxxxxxxxxxxx");
			// layout.removeMember(accordion);
			// accordion=new Accordion(true);
			// layout.addMember(accordion, 0);
			// layout.destroy();
			// if (contains(layout))
			// removeMember(layout);
			// if (contains(layout))
			// removeChild(layout);
			// layout = new HLayout();
			// accordion = new Accordion(true);
			// layout.addMember(accordion);
			// layout.addMember(new DrawingPanel());
			// addMember(layout);
			// redraw();
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
	public void onWorkflowSelect() {
		// TODO Auto-generated method stub
	}
}