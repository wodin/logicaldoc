package com.logicaldoc.gui.frontend.client.workflow;

import com.logicaldoc.gui.common.client.beans.GUIWFState;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.common.client.util.Util;
import com.orange.links.client.DiagramController;
import com.orange.links.client.connection.Connection;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.ValueCallback;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

/**
 * Base visual representation of a Workflow object (a state or a transition).
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.3
 */
public class StateWidget extends Label {

	private GUIWFState wfState;

	private DrawingPanel drawingPanel;

	private DiagramController diagramController;

	private Connection connection;

	private boolean readonly = false;

	/**
	 * Constructor used by transitions.
	 */
	public StateWidget(Connection connection, DiagramController diagramController, String name) {
		super(name);
		this.connection = connection;
		this.diagramController = diagramController;

		addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				if (readonly)
					return;

				Menu contextMenu = new Menu();

				MenuItem edit = new MenuItem();
				edit.setTitle(I18N.message("edit"));
				edit.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
					public void onClick(MenuItemClickEvent event) {
						edit();
					}
				});

				MenuItem delete = new MenuItem();
				delete.setTitle(I18N.message("ddelete"));
				delete.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
					public void onClick(MenuItemClickEvent event) {
						LD.ask(I18N.message("ddelete"), I18N.message("confirmdelete"), new BooleanCallback() {
							@Override
							public void execute(Boolean value) {
								if (value)
									delete();
							}
						});
					}
				});

				MenuItem makeStart = new MenuItem();
				makeStart.setTitle(I18N.message("startstate"));
				makeStart.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
					public void onClick(MenuItemClickEvent event) {
						makeStartState();
					}
				});

				if (isTask())
					contextMenu.setItems(edit, makeStart, delete);
				else
					contextMenu.setItems(edit, delete);
				contextMenu.showContextMenu();
				event.cancel();
			}
		});
	}

	public StateWidget(DrawingPanel dp, GUIWFState state) {
		this(null, dp.getDiagramController(), "<b>" + state.getName() + "</b>&nbsp;");
		this.wfState = state;
		this.drawingPanel = dp;
		this.diagramController = dp.getDiagramController();

		update();
	}

	public void update() {
		if (wfState == null)
			return;

		setPadding(4);
		setMargin(3);
		setWrap(false);
		setAlign(Alignment.CENTER);
		setValign(VerticalAlignment.CENTER);
		setHeight(40);
		setAutoWidth();
		setOpacity(100);
		setBackgroundColor("#FFFFFF");
		setContents("<b>" + wfState.getName() + "</b>&nbsp;");
		int type = wfState.getType();
		if (type == GUIWFState.TYPE_END) {
			setIcon(Util.imageUrl("endState.png"));
			setBorder("3px solid #444444");
		} else if (type == GUIWFState.TYPE_TASK) {
			setIcon(Util.imageUrl("task.png"));
			if (isStartState())
				setBorder("3px solid #15F219");
			else
				setBorder("3px solid #2281D0");
		} else if (type == GUIWFState.TYPE_JOIN) {
			setIcon(Util.imageUrl("join.png"));
			setBorder("3px solid #F2EE07");
		} else if (type == GUIWFState.TYPE_FORK) {
			setIcon(Util.imageUrl("fork.png"));
			setBorder("3px solid #F2EE07");
		}
	}

	public void edit() {
		if (isTask()) {
			TaskDialog taskDialog = new TaskDialog(StateWidget.this);
			taskDialog.show();
		} else {
			String oldName = getContents().replaceAll("<b>", "").replaceAll("</b>", "").replaceAll("&nbsp;", "");
			LD.askforValue(I18N.message("name"), "<b>" + I18N.message("name") + ":</b>", oldName, "200",
					new ValueCallback() {
						@Override
						public void execute(String value) {
							if (value == null || "".equals(value.trim()))
								return;
							if (wfState != null) {
								wfState.setName(value);
								update();
							} else {
								// This is a transaction's decorator
								setContents(value);
							}
						}
					});
		}
	}

	public void delete() {
		if (wfState != null) {
			diagramController.deleteWidget(this);
		} else {
			connection.getStartShape().removeConnection(connection);
			diagramController.deleteConnection(connection);
		}
	}

	public void makeStartState() {
		WorkflowDesigner workflowDesigner = getDrawingPanel().getWorkflowDesigner();
		workflowDesigner.saveModel();
		workflowDesigner.getWorkflow().setStartStateId(wfState.getId());
		workflowDesigner.saveModel();
		workflowDesigner.redraw(workflowDesigner.getWorkflow());
	}

	public boolean isEnd() {
		return wfState != null && wfState.getType() == GUIWFState.TYPE_END;
	}

	public boolean isTask() {
		return wfState != null && wfState.getType() == GUIWFState.TYPE_TASK;
	}

	public boolean isJoin() {
		return wfState != null && wfState.getType() == GUIWFState.TYPE_JOIN;
	}

	public boolean isStartState() {
		if (wfState != null)
			return getDrawingPanel().getWorkflowDesigner().getWorkflow().getStartStateId().equals(wfState.getId());
		else
			return false;
	}

	public GUIWFState getWfState() {
		return wfState;
	}

	public DrawingPanel getDrawingPanel() {
		return drawingPanel;
	}

	public DiagramController getDiagramController() {
		return diagramController;
	}

	public void setDiagramController(DiagramController diagramController) {
		this.diagramController = diagramController;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}
}