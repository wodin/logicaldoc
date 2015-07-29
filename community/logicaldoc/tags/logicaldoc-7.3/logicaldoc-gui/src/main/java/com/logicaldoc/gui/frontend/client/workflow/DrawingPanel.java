package com.logicaldoc.gui.frontend.client.workflow;

import java.util.HashMap;
import java.util.Map;

import com.logicaldoc.gui.common.client.beans.GUITransition;
import com.logicaldoc.gui.common.client.beans.GUIWFState;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;
import com.logicaldoc.gui.common.client.log.Log;
import com.orange.links.client.DiagramController;
import com.orange.links.client.connection.Connection;
import com.orange.links.client.shapes.Point;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.events.ScrolledEvent;
import com.smartgwt.client.widgets.events.ScrolledHandler;
import com.smartgwt.client.widgets.layout.VStack;

/**
 * In this panel the graphical design of the workflow takes place.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class DrawingPanel extends VStack {

	private WorkflowDesigner workflowDesigner;

	private DiagramController controller;

	// stateId - widget instance
	private Map<String, StateWidget> widgets = new HashMap<String, StateWidget>();

	public DrawingPanel(WorkflowDesigner designer) {
		super();
		setMembersMargin(5);
		setOverflow(Overflow.SCROLL);
		setWidth100();
		setHeight100();
		this.workflowDesigner = designer;

		controller = new DiagramController(2000, 2000);
		controller.showGrid(false);
		addMember(controller.getView());

		addScrolledHandler(new ScrolledHandler() {

			@Override
			public void onScrolled(ScrolledEvent event) {
				// This will avoid connection explosions when scrolling
				controller.unsynchronizedShapes();
			}
		});
	}

	public WorkflowDesigner getWorkflowDesigner() {
		return workflowDesigner;
	}

	public DiagramController getDiagramController() {
		return controller;
	}

	public void redraw() {
		controller.clearDiagram();

		GUIWorkflow workflow = workflowDesigner.getWorkflow();
		if (workflow.getStates() == null)
			return;

		try {
			// Put all the states
			widgets.clear();
			for (GUIWFState state : workflow.getStates()) {
				StateWidget widget = new StateWidget(this, state);
				controller.addWidget(widget, state.getLeft(), state.getTop());
				widgets.put(state.getId(), widget);
			}

			for (StateWidget widget : widgets.values()) {
				controller.makeDraggable(widget);
			}

			// Draw the transitions
			for (GUIWFState state : workflow.getStates()) {
				if (state.getTransitions() != null)
					for (GUITransition trans : state.getTransitions()) {
						StateWidget src = widgets.get(state.getId());
						StateWidget dst = widgets.get(trans.getTargetState().getId());
						Connection c = controller.drawStraightArrowConnection(src, dst, trans);
						c.setSynchronized(true);
						c.setAllowSynchronized(true);

						String points = trans.getPoints();
						if (points != null) {
							String[] tokens = points.split(";");
							for (String token : tokens) {
								if (token == null || token.equals(""))
									continue;
								String coords[] = token.split(",");
								c.addMovablePoint(new Point(Integer.parseInt(coords[0]), Integer.parseInt(coords[1])));
							}
						}
					}
			}

			controller.unsynchronizedShapes();
		} catch (Throwable t) {
			Log.error(t.getMessage(), null, t);
		}
	}

	public StateWidget getWidget(String id) {
		return widgets.get(id);
	}
}