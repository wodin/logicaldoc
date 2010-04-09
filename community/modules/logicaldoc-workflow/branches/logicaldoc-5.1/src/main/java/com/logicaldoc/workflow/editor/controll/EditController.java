package com.logicaldoc.workflow.editor.controll;

import com.logicaldoc.workflow.editor.model.BaseWorkflowModel;

public interface EditController {
	
	public void initialize(BaseWorkflowModel baseWorkflowModel);
	
	public void invalidate();

	public void persist();
	
	public BaseWorkflowModel instantiateNew();
}
