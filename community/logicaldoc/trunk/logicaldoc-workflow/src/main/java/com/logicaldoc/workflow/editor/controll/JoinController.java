package com.logicaldoc.workflow.editor.controll;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;

import com.logicaldoc.workflow.editor.model.BaseWorkflowModel;
import com.logicaldoc.workflow.editor.model.Join;
import com.logicaldoc.workflow.editor.model.WorkflowEditorException;

public class JoinController extends DragAndDropSupportController{

	@Override
	public void initialize(BaseWorkflowModel baseWorkflowModel) {
		
	}

	@Override
	public BaseWorkflowModel instantiateNew() {
		return new Join();
	}

	@Override
	public void invalidate() {
		
	}

	@Override
	public void persist() {
		
	}

	@Override
	public void droppedObject(Container container) {
		Join join = (Join)container.droppingZone;
		join.setDestination(container.draggedObject);
	}

	public void removeTask(ActionEvent actionEvent) {

		UIParameter param = ((UIParameter) ((UIComponent) actionEvent
				.getSource()).getChildren().get(0));

		Object val = param.getValue();

		if ((val instanceof Join) == false)
			throw new WorkflowEditorException(
					"Passed Parameter does not match to a Join");

		Join join = (Join) val;
		join.setDestination(null);
		

	}
}
