package com.logicaldoc.workflow.editor.controll;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;

import com.logicaldoc.workflow.editor.model.BaseWorkflowModel;
import com.logicaldoc.workflow.editor.model.Fork;
import com.logicaldoc.workflow.editor.model.WorkflowEditorException;
import com.logicaldoc.workflow.editor.model.WorkflowTask;

public class ForkController extends DragAndDropSupportController {

	@Override
	public void initialize(BaseWorkflowModel baseWorkflowModel) {
	
	}

	@Override
	public BaseWorkflowModel instantiateNew() {
		return new Fork();
	}

	@Override
	public void invalidate() {
	
	}

	@Override
	public void persist() {

	}

	public void removeTask(ActionEvent actionEvent) {

		UIParameter param = ((UIParameter) ((UIComponent) actionEvent
				.getSource()).getChildren().get(0));
		
		UIParameter param2 = ((UIParameter) ((UIComponent) actionEvent
				.getSource()).getChildren().get(1));

		Object val = param.getValue();
		
		Object val2 = param2.getValue();
		
		if ((val instanceof WorkflowTask) == false)
			throw new WorkflowEditorException(
					"Passed Parameter does not match to a WorkflowTask");
		
		
		
		WorkflowTask workflowTask = (WorkflowTask) val;
		Fork fork = (Fork)val2;
		fork.getWorkflowTasks().remove(workflowTask);

	}

	@Override
	public void droppedObject(Container container) {
		Fork fork = (Fork) container.droppingZone;
		BaseWorkflowModel workflowModel = container.draggedObject;
		
		if(workflowModel instanceof WorkflowTask)
			fork.getWorkflowTasks().add((WorkflowTask) container.draggedObject);
	}

	
}
