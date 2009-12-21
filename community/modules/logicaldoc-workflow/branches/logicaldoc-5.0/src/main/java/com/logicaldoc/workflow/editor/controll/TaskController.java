package com.logicaldoc.workflow.editor.controll;

import java.util.LinkedList;
import java.util.List;

import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.icesoft.faces.component.ext.HtmlCommandLink;
import com.icesoft.faces.component.selectinputtext.SelectInputText;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.workflow.editor.model.Assignee;
import com.logicaldoc.workflow.editor.model.BaseWorkflowModel;
import com.logicaldoc.workflow.editor.model.Transition;
import com.logicaldoc.workflow.editor.model.WorkflowEditorException;
import com.logicaldoc.workflow.editor.model.WorkflowTask;

public class TaskController extends DragAndDropSupportController {
	private WorkflowTask workflowTask;

	private List<String> possibleAssignments = new LinkedList<String>();

	@Override
	public void initialize(BaseWorkflowModel workflowModel) {
		this.workflowTask = (WorkflowTask) workflowModel;
	}

	@Override
	public void invalidate() {
		this.workflowTask = null;
	}

	public void persist() {

	}

	public void selectInputValueChanged(ValueChangeEvent event) {

		if (event.getComponent() instanceof SelectInputText) {

			// get the number of displayable records from the component
			SelectInputText autoComplete = (SelectInputText) event.getComponent();

			String currentValue = autoComplete.getValue().toString();
			UserDAO userDAO = (UserDAO) Context.getInstance().getBean(UserDAO.class);
			List<User> matchedUsers = userDAO
					.findByWhere(
							"_entity.type = 0 and (_entity.userName like concat(?,'%') OR _entity.firstName like concat(?,'%') OR _entity.name like concat(?,'%'))",
							new Object[] { currentValue, currentValue, currentValue }, null);

			possibleAssignments = new LinkedList<String>();
			for (User user : matchedUsers) {
				possibleAssignments.add(user.getUserName());
			}
		}
	}

	public List<SelectItem> getPossibleAssignments() {
		List<SelectItem> items = new LinkedList<SelectItem>();
		int c = 0;

		for (String assignee : possibleAssignments) {
			items.add(new SelectItem(c++, assignee));
		}
		return items;
	}

	public void removeAssignment(ActionEvent actionEvent) {
		UICommand commandLink = (UICommand) actionEvent.getSource();
		Object val = ((UIParameter) commandLink.getChildren().get(0)).getValue();

		Object val2 = ((UIParameter) commandLink.getChildren().get(1)).getValue();

		if ((val instanceof Assignee) == false)
			throw new WorkflowEditorException("The passed Assignment does not match to Assignee");

		if ((val2 instanceof WorkflowTask) == false)
			throw new WorkflowEditorException("No passed WorkflowTask");

		WorkflowTask wfTask = (WorkflowTask) val2;
		wfTask.getAssignees().remove(val);
	}

	public void addAssignment(ActionEvent actionEvent) {
		UIComponent commandLink = (UIComponent) actionEvent.getSource();
		Object val = ((UIParameter) commandLink.getChildren().get(0)).getValue();

		if ((val instanceof WorkflowTask) == false)
			throw new WorkflowEditorException("The passed Assignment does not match to WorkflowTask");

		WorkflowTask task = (WorkflowTask) val;

		task.addAssignee();
	}

	@Override
	public BaseWorkflowModel instantiateNew() {
		return new WorkflowTask();
	}

	public void addTransition() {

		Transition transition = new Transition();

		this.workflowTask.addTransition(transition);
	}

	public void removeTransition(ActionEvent actionEvent) {
		UIParameter param = ((UIParameter) ((UIComponent) actionEvent.getSource()).getChildren().get(0));

		Object val = param.getValue();

		if ((val instanceof Transition) == false)
			throw new WorkflowEditorException("Passed Parameter does not match to a Transition");

		this.workflowTask.getTransitions().remove(val);
	}

	@Override
	public void droppedObject(Container container) {
		addTaskDestinationToTransition(container.draggedObject, container.droppingZone);
	}

	private void addTaskDestinationToTransition(BaseWorkflowModel draggedObject, BaseWorkflowModel droppedZone) {

		if ((droppedZone instanceof Transition) == false)
			throw new WorkflowEditorException("Drop-Zone must be a Transition and nothing else");

		Transition transition = (Transition) droppedZone;
		transition.setDestination(draggedObject);

	}

	public void removeDestinationFromTransition(ActionEvent actionEvent) {
		HtmlCommandLink commandLink = (HtmlCommandLink) actionEvent.getSource();
		UIParameter param = (UIParameter) commandLink.getChildren().get(0);

		if ((param.getValue() instanceof BaseWorkflowModel) == false)
			throw new WorkflowEditorException("Given Parameter must be a base of "
					+ BaseWorkflowModel.class.getSimpleName());

		Transition transition = (Transition) param.getValue();
		transition.setDestination(null);
	}

}
