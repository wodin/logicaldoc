package com.logicaldoc.gui.frontend.client.workflow;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.logicaldoc.gui.common.client.beans.GUITransition;
import com.logicaldoc.gui.common.client.beans.GUIWFState;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.frontend.client.services.WorkflowService;
import com.logicaldoc.gui.frontend.client.services.WorkflowServiceAsync;
import com.orange.links.client.connection.Connection;
import com.orange.links.client.shapes.FunctionShape;
import com.orange.links.client.shapes.Point;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VStack;

/**
 * Where the workflow diagram is drawn
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class WorkflowDesigner extends VStack {

	public final static int TYPE_TASK = 0;

	public final static int TYPE_END = 1;

	public final static int TYPE_JOIN = 2;

	public final static int TYPE_FORK = 3;

	protected WorkflowServiceAsync workflowService = (WorkflowServiceAsync) GWT.create(WorkflowService.class);

	// HStack or HLayout with Accordion e Drawing Panel
	private HLayout layout = new HLayout();

	private Accordion accordion = null;

	private GUIWorkflow workflow = null;

	private DrawingPanel drawingPanel = null;

	private boolean readOnly = false;

	private WorkflowToolstrip workflowToolstrip;

	public WorkflowDesigner(GUIWorkflow workflow, boolean readOnly) {
		this.workflow = workflow;
		this.readOnly = readOnly;

		setMembersMargin(5);

		if (!readOnly) {
			workflowToolstrip = new WorkflowToolstrip(this);
			addMember(workflowToolstrip);
			addMember(new StateToolstrip(this));
		}

		if (this.workflow != null) {
			accordion = new Accordion();
			accordion.redraw(workflow);
			layout.addMember(accordion);
		}

		drawingPanel = new DrawingPanel(this);
		layout.addMember(drawingPanel);
		addMember(layout);

		if (workflow != null)
			redraw(workflow);
	}

	public GUIWorkflow getWorkflow() {
		return workflow;
	}

	public void redraw(GUIWorkflow workflow) {
		this.workflow = workflow;
		if (accordion != null && !isReadOnly())
			accordion.redraw(workflow);
		drawingPanel.redraw();
	}

	public void refresh() {
		for (GUIWFState status : workflow.getStates()) {
			StateWidget widget = getDrawingPanel().getWidget(status.getId());
			if (widget != null && widget.isTask())
				widget.update();
		}
		getDrawingPanel().redraw();
	}

	public Accordion getAccordion() {
		return accordion;
	}

	public void onAddState(int type) {
		GUIWFState state = new GUIWFState("" + new Date().getTime(), I18N.message("statename"), type);

		getWorkflow().addState(state);

		/*
		 * Check if this must be the initial state
		 */
		if (type == GUIWFState.TYPE_TASK) {
			state.setInitial(true);
			if (getWorkflow().getStates() != null)
				for (GUIWFState s : getWorkflow().getStates()) {
					if (s.getType() == GUIWFState.TYPE_TASK && s.isInitial()) {
						state.setInitial(false);
						break;
					}
				}
		}

		if (state.isInitial())
			getWorkflow().setStartStateId(state.getId());

		StateWidget sw = new StateWidget(drawingPanel, state);
		drawingPanel.getDiagramController().addWidget(sw, 10, 10);
		drawingPanel.getDiagramController().makeDraggable(sw);

		try {
			saveModel();
		} catch (Throwable t) {
		}
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public DrawingPanel getDrawingPanel() {
		return drawingPanel;
	}

	/**
	 * Saves the current diagram into the object model.
	 */
	public void saveModel() {
		// Collect all the states as drawn in the designer.
		List<GUIWFState> states = new ArrayList<GUIWFState>();
		Iterator<FunctionShape> iter = getDrawingPanel().getDiagramController().getShapes().iterator();
		int i = 0;
		while (iter.hasNext()) {
			FunctionShape shape = iter.next();
			StateWidget widget = (StateWidget) shape.getWidget();

			String id = Integer.toString(i++);

			GUIWFState wfState = widget.getWfState();
			if (wfState.getId().equals(workflow.getStartStateId())) {
				workflow.setStartStateId(id);
				wfState.setInitial(true);
			} else
				wfState.setInitial(false);
			wfState.setId(id);
			wfState.setTop(shape.getTop());
			wfState.setLeft(shape.getLeft());
			states.add(wfState);
		}
		workflow.setStates(states.toArray(new GUIWFState[0]));

		// Collect all the transitions as drawn in the designer
		iter = getDrawingPanel().getDiagramController().getShapes().iterator();
		Map<Widget, Map<Widget, Connection>> functionsMap = getDrawingPanel().getDiagramController().getFunctionsMap();
		while (iter.hasNext()) {
			FunctionShape shape = iter.next();
			StateWidget srcWidget = (StateWidget) shape.getWidget();

			List<GUITransition> transitions = new ArrayList<GUITransition>();
			Map<Widget, Connection> map = functionsMap.get(srcWidget);
			for (Entry<Widget, Connection> entry : map.entrySet()) {
				StateWidget targetWidget = (StateWidget) entry.getKey();
				Connection connection = entry.getValue();
				GUITransition transition = ((StateWidget) connection.getDecoration().getWidget()).getTransition();
				transition.setTargetState(targetWidget.getWfState());
				transitions.add(transition);

				StringBuffer sb = new StringBuffer("");
				for (Point point : connection.getMovablePoints()) {
					sb.append("" + point.getLeft());
					sb.append("," + point.getTop());
					sb.append(";");
				}
				transition.setPoints(sb.toString());
			}
			srcWidget.getWfState().setTransitions(transitions.toArray(new GUITransition[0]));
		}

		final Map<String, Object> values = getAccordion().getValues();
		if (values == null || ((String) values.get("workflowName")).trim().isEmpty())
			return;
		workflow.setName((String) values.get("workflowName"));
		if (values.get("workflowDescr") != null)
			workflow.setDescription((String) values.get("workflowDescr"));
		if (values.get("supervisor") != null)
			workflow.setSupervisor((String) values.get("supervisor"));
	}
}