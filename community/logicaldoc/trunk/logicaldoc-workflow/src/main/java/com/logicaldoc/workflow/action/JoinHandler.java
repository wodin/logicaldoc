package com.logicaldoc.workflow.action;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import com.logicaldoc.workflow.WorkflowConstants;
import com.logicaldoc.workflow.model.WorkflowTemplate;
import com.thoughtworks.xstream.XStream;

public class JoinHandler extends BaseEventAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3177740964090532273L;

	@Override
	public void executeImpl(ExecutionContext executionContext){
		
		XStream xStream = new XStream();
		
		WorkflowTemplate workflowTemplate = (WorkflowTemplate)xStream.fromXML((String)executionContext.getVariable(WorkflowConstants.VAR_TEMPLATE));
		
	}

}
