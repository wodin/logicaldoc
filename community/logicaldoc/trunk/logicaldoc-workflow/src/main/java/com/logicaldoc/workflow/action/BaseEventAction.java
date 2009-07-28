package com.logicaldoc.workflow.action;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import com.logicaldoc.workflow.WorkflowConstants;
import com.logicaldoc.workflow.model.WorkflowTemplate;
import com.thoughtworks.xstream.XStream;

public abstract class BaseEventAction implements ActionHandler{

	private String componentId;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8272469489065687967L;
	private XStream xStream = new XStream();
	
	protected WorkflowTemplate obtainWorkflowTemplateFromWorkflow(ExecutionContext executionContext){
		return (WorkflowTemplate)xStream.fromXML((String)executionContext.getVariable(WorkflowConstants.VAR_TEMPLATE));
		
	}
	
	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		executeImpl(executionContext);
	}

	public abstract void executeImpl(ExecutionContext executionContext);
}
