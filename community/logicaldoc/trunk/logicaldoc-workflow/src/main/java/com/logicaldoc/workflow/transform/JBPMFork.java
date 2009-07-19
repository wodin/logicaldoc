package com.logicaldoc.workflow.transform;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.logicaldoc.workflow.editor.model.BaseWorkflowModel;
import com.logicaldoc.workflow.editor.model.Fork;
import com.logicaldoc.workflow.editor.model.WorkflowTask;

public class JBPMFork implements TransformModel {

	@Override
	public Object open(TransformContext ctx) {
		Document wr = ((JBPMTransformContext) ctx).getDocumentBuildObject();

		Fork forkModel = (Fork) ctx.getCurrentBaseModel();
		
		Element fork = wr.createElement("fork");
		fork.setAttribute("name", forkModel.getId());
		
		List<WorkflowTask> workflowTasks = forkModel.getWorkflowTasks();
		
		for (WorkflowTask taskModel : workflowTasks) {
			Element transition = wr.createElement("transition");
			transition.setAttribute("name", taskModel.getId());
			transition.setAttribute("to", taskModel.getId());
			
			fork.appendChild(transition);
			
		}
		
		
		return fork;
	}

	@Override
	public Object end(TransformContext ctx) {
		return null;
	}
	
	@Override
	public boolean matches(BaseWorkflowModel model) {
		return model instanceof Fork;
	}
	
	
}
