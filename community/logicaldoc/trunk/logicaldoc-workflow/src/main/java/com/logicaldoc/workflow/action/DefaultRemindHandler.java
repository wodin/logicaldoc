package com.logicaldoc.workflow.action;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

public class DefaultRemindHandler implements ActionHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8776461973750383870L;

	@Override
	public void execute(ExecutionContext arg0) throws Exception {
		System.out.println("You´ve got assigned to the task xyz!");
		System.out.println("Why is the task still open? WORK HARDER!!!!");
	}
	

}
