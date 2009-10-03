package com.logicaldoc.workflow.editor.controll;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;

import com.logicaldoc.workflow.editor.model.BaseWorkflowModel;
import com.logicaldoc.workflow.editor.model.Transition;

public class TransitionController extends DragAndDropSupportController{

	@Override
	public void droppedObject(Container container) {
		Transition transition = (Transition)container.droppingZone;
		transition.setDestination(container.draggedObject);
	}

	@Override
	public void initialize(BaseWorkflowModel baseWorkflowModel) {
		
	}

	@Override
	public BaseWorkflowModel instantiateNew() {
		return new Transition();
	}

	@Override
	public void invalidate() {
		
	}

	@Override
	public void persist() {
		
	}

	public void removeWorkflowModelFromDestination(ActionEvent actionEvent){
		UIComponent component = (UIComponent)actionEvent.getSource();
		
		Transition driverTransition  = (Transition)
											((UIParameter)component.getChildren().get(0)).getValue();
		driverTransition.setDestination(null);
		
	}
	
}
