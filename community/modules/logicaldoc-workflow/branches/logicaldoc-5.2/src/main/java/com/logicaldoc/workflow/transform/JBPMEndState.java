package com.logicaldoc.workflow.transform;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.logicaldoc.workflow.editor.model.BaseWorkflowModel;
import com.logicaldoc.workflow.editor.model.EndState;

public class JBPMEndState implements TransformModel {

	@Override
	public Object open(TransformContext ctx) {
		Document wr = ((JBPMTransformContext) ctx).getDocumentBuildObject();

		EndState endStateModel = (EndState) ctx.getCurrentBaseModel();
		
		Element endState = wr.createElement("end-state");
		endState.setAttribute("name", endStateModel.getId());

		return endState;
	}

	@Override
	public Object end(TransformContext ctx) {
		return null;
	}
	
	@Override
	public boolean matches(BaseWorkflowModel model) {
		return model instanceof EndState;
	}
	
}
