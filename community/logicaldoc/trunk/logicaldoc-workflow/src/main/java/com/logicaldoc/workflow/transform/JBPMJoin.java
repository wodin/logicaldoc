package com.logicaldoc.workflow.transform;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.logicaldoc.workflow.editor.model.BaseWorkflowModel;
import com.logicaldoc.workflow.editor.model.Join;

public class JBPMJoin implements TransformModel {

	@Override
	public Object open(TransformContext ctx) {
		Document wr = ((JBPMTransformContext) ctx).getDocumentBuildObject();

		Join joinModel = (Join) ctx.getCurrentBaseModel();
		
		Element join = wr.createElement("join");
		join.setAttribute("name", joinModel.getId());
		
		BaseWorkflowModel destination = joinModel.getDestination();
		
		Element transition = wr.createElement("transition");
		transition.setAttribute("name", destination.getId());
		transition.setAttribute("to", destination.getId());
		
		join.appendChild(transition);
		
		return join;
	}

	@Override
	public Object end(TransformContext ctx) {
		return null;
	}
	
	@Override
	public boolean matches(BaseWorkflowModel model) {
		return model instanceof Join;
	}
	
	
}
