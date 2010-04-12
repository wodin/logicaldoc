package com.logicaldoc.workflow.transform;

import com.logicaldoc.workflow.editor.model.BaseWorkflowModel;

public interface TransformModel {
	
	public Object open(TransformContext ctx);
	
	public Object end(TransformContext ctx);
	
	public boolean matches(BaseWorkflowModel model);
	
}
