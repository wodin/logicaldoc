package com.logicaldoc.gui.frontend.client.workflow;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIWFState;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.administration.AdminPanel;
import com.logicaldoc.gui.frontend.client.services.WorkflowService;
import com.logicaldoc.gui.frontend.client.services.WorkflowServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
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

	protected WorkflowServiceAsync workflowService = (WorkflowServiceAsync) GWT.create(WorkflowService.class);

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
	public void onStateSelect(GUIWFState wfState) {
		if (wfState.getType() == TYPE_TASK) {
			TaskDialog window = new TaskDialog(workflow, wfState);
			window.show();
		} else {
			final Window window = new Window();
			String typeString = "";
			if (wfState.getType() == TYPE_JOIN) {
				typeString = I18N.message("join");
			} else if (wfState.getType() == TYPE_FORK) {
				typeString = I18N.message("fork");
			} else if (wfState.getType() == TYPE_END) {
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

	@Override
	public void onStateDelete(GUIWFState wfState) {
		GUIWFState[] states = new GUIWFState[workflow.getStates().length - 1];
		int i = 0;
		for (GUIWFState state : workflow.getStates()) {
			if (!state.getName().equals(wfState.getName())) {
				states[i] = state;
				i++;
			}
		}
		workflow.setStates(states);

		SC.ask(I18N.message("question"), I18N.message("confirmdelete"), new BooleanCallback() {
			@Override
			public void execute(Boolean value) {
				if (value) {
					workflowService.save(Session.get().getSid(), workflow, new AsyncCallback<GUIWorkflow>() {
						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught);
						}

						@Override
						public void onSuccess(GUIWorkflow result) {
							AdminPanel.get().setContent(new WorkflowDesigner(workflow));
						}
					});
				}
			}
		});
	}

	@Override
	public void onDraggedStateDelete(GUIWFState fromState, GUIWFState targetState) {
		Map<String, GUIWFState> newTransitions = new HashMap<String, GUIWFState>();
		for (String key : fromState.getTransitions().keySet()) {
			if (!fromState.getTransitions().get(key).getId().equals(targetState.getId())) {
				newTransitions.put(key, fromState.getTransitions().get(key));
			}
		}
		fromState.setTransitions(newTransitions);

		GUIWFState[] states = new GUIWFState[workflow.getStates().length];
		int i = 0;
		for (GUIWFState state : workflow.getStates()) {
			if (!state.getName().equals(fromState.getName())) {
				states[i] = state;
				i++;
			} else {
				states[i] = fromState;
				i++;
			}
		}
		workflow.setStates(states);

		SC.ask(I18N.message("question"), I18N.message("confirmdelete"), new BooleanCallback() {
			@Override
			public void execute(Boolean value) {
				if (value) {
					workflowService.save(Session.get().getSid(), workflow, new AsyncCallback<GUIWorkflow>() {
						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught);
						}

						@Override
						public void onSuccess(GUIWorkflow result) {
							AdminPanel.get().setContent(new WorkflowDesigner(workflow));
						}
					});
				}
			}
		});
	}

	@Override
	public void onAddTransition(GUIWFState fromState, GUIWFState targetState) {
		Map<String, GUIWFState> transitions = new HashMap<String, GUIWFState>();
		transitions.put(targetState.getName(), targetState);
		fromState.setTransitions(transitions);

		GUIWFState[] states = new GUIWFState[workflow.getStates().length];
		int i = 0;
		for (GUIWFState state : workflow.getStates()) {
			if (!state.getName().equals(fromState.getName())) {
				states[i] = state;
				i++;
			} else {
				states[i] = fromState;
				i++;
			}
		}
		workflow.setStates(states);

		workflowService.save(Session.get().getSid(), workflow, new AsyncCallback<GUIWorkflow>() {
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(GUIWorkflow result) {
				AdminPanel.get().setContent(new WorkflowDesigner(workflow));
			}
		});
	}
}