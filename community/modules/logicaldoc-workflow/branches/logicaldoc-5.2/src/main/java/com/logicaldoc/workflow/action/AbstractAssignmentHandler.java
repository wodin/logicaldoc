package com.logicaldoc.workflow.action;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.def.AssignmentHandler;
import org.jbpm.taskmgmt.exe.Assignable;

import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.workflow.WorkflowConstants;
import com.logicaldoc.workflow.WorkflowUtil;
import com.logicaldoc.workflow.editor.model.Assignee;
import com.logicaldoc.workflow.editor.model.WorkflowTask;
import com.logicaldoc.workflow.model.WorkflowTemplate;
import com.logicaldoc.workflow.transform.WorkflowTransformService;

public abstract class AbstractAssignmentHandler implements AssignmentHandler {

	protected static Log log = LogFactory.getLog(AbstractAssignmentHandler.class);

	private String taskId;

	private WorkflowTransformService workflowTransformService;

	private UserDAO userDAO;

	protected final String getTaskId() {
		return taskId;
	}

	public AbstractAssignmentHandler() {
		this.workflowTransformService = (WorkflowTransformService) Context.getInstance().getBean(
				"workflowTransformService");
		this.userDAO = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		this.init();
	}

	public WorkflowTransformService getWorkflowTransformService() {
		return workflowTransformService;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6145523287668659543L;

	@Override
	public void assign(Assignable assignable, ExecutionContext executionContext) throws Exception {

		WorkflowTemplate workflowTemplate = workflowTransformService
				.retrieveWorkflowModels((Serializable) (executionContext.getVariable(WorkflowConstants.VAR_TEMPLATE)));

		WorkflowTask workflowTask = WorkflowUtil.getWorkflowTaskById(taskId, workflowTemplate.getWorkflowComponents());

		List<String> assignees = new LinkedList<String>();

		if (workflowTask.getAssignees().size() > 1) {
			List<String> pooledActors = new LinkedList<String>();

			for (Assignee assignee : workflowTask.getAssignees()) {
				pooledActors.add(assignee.getValue());
				assignees.add(assignee.getValue());
			}

			assignable.setPooledActors(pooledActors.toArray(new String[] {}));

			log.info("users " + pooledActors + " has been assigned to task: " + taskId);

		} else {

			String assignee = null;
			if (workflowTask.getAssignees().size() > 0)
				assignee = workflowTask.getAssignees().get(0).getValue();

			if (assignee != null) {
				if (this.userDAO.findByUserName(assignee) == null)
					assignee = null;
			}

			if (assignee == null) {
				assignee = "admin";
			}

			assignable.setActorId(assignee);
			assignees.add(assignee);
			log.info("user " + assignee + " has been assigned to task: " + taskId);

		}

		executeImpl(assignees, executionContext);
	}

	public abstract void executeImpl(List<String> assignees, ExecutionContext executionContext);

	public abstract void init();

}
