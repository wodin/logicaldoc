package com.logicaldoc.workflow.editor.controll;

import com.logicaldoc.workflow.editor.model.BaseWorkflowModel;

public abstract class DragAndDropSupportController implements EditController {

	public static class Container {
		public BaseWorkflowModel draggedObject;
		public BaseWorkflowModel droppingZone;
	}
	
	public abstract void droppedObject(Container container);
}
