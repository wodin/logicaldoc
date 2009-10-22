package com.logicaldoc.workflow.action;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import com.logicaldoc.util.Context;
import com.logicaldoc.workflow.WorkflowConstants;
import com.logicaldoc.workflow.model.WorkflowTemplate;
import com.logicaldoc.workflow.transform.WorkflowTransformService;

public abstract class BaseEventAction implements ActionHandler {

	private static final long serialVersionUID = -8272469489065687967L;

	private WorkflowTransformService workflowTransformService;

	public BaseEventAction() {
		workflowTransformService = (WorkflowTransformService) Context
				.getInstance().getBean("workflowTransformService");
		init();
	}

	protected WorkflowTemplate obtainWorkflowTemplateFromWorkflow(
			ExecutionContext executionContext) {
		return workflowTransformService
				.retrieveWorkflowModels((String) executionContext
						.getVariable(WorkflowConstants.VAR_TEMPLATE));

	}

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		executeImpl(executionContext);
	}

	public abstract void executeImpl(ExecutionContext executionContext);

	public abstract void init();
}