package com.logicaldoc.workflow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.jbpm.JbpmContext;
import org.jbpm.JbpmException;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.node.EndState;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springmodules.workflow.jbpm31.JbpmCallback;
import org.springmodules.workflow.jbpm31.JbpmTemplate;

import com.logicaldoc.util.Context;
import com.logicaldoc.workflow.editor.WorkflowPersistenceTemplate;
import com.logicaldoc.workflow.editor.WorkflowPersistenceTemplateDAO;
import com.logicaldoc.workflow.exception.WorkflowException;
import com.logicaldoc.workflow.model.WorkflowDefinition;
import com.logicaldoc.workflow.model.WorkflowInstance;
import com.logicaldoc.workflow.model.WorkflowTaskInstance;
import com.logicaldoc.workflow.model.WorkflowTemplate;

public class JBPMWorkflowEngine implements WorkflowEngine {

	protected static Log log = LogFactory.getLog(JBPMWorkflowEngine.class);

	private JbpmTemplate jbpmTemplate;

	public void setJbpmTemplate(JbpmTemplate jbpmTemplate) {
		this.jbpmTemplate = jbpmTemplate;
	}

	public void undeployWorkflow(final String processId) {
		this.jbpmTemplate.execute(new JbpmCallback() {
			@Override
			public Object doInJbpm(JbpmContext context) throws JbpmException {

				log.debug("delete processdefinition with id " + processId);
				context.getGraphSession()
						.deleteProcessDefinition(WorkflowFactory.getJbpmProcessDefinitionId(processId));
				return null;
			}
		});
	}

	public void deployWorkflow(WorkflowTemplate template, Serializable _processDefinition) {

		if (_processDefinition instanceof String) {
			try {
				_processDefinition = ProcessDefinition.parseXmlString((String) _processDefinition);
			} catch (Exception e) {
				throw new WorkflowException(e);
			}
		}

		if ((_processDefinition instanceof ProcessDefinition) == false)
			throw new WorkflowException("The given processdefinition must be serializable!");

		final ProcessDefinition processDefinition = (ProcessDefinition) _processDefinition;
		processDefinition.setDescription(template.getDescription());

		// Map <String, Serializable> properties = new HashMap<String,
		// Serializable>();
		// properties.put("WF_TID", template.getId());
		// properties.put("WF_TEMPLATE", template.getXmldata());

		// processDefinition.setDefinitions(properties);

		this.jbpmTemplate.execute(new JbpmCallback() {

			@Override
			public Object doInJbpm(JbpmContext context) throws JbpmException {

				context.deployProcessDefinition(processDefinition);

				log.info("deploy processdefinition with name " + processDefinition.getName());

				return null;
			}

		});
	}

	public void processTaskToEnd(final String taskId) {
		this.jbpmTemplate.execute(new JbpmCallback() {

			@Override
			public Object doInJbpm(JbpmContext context) throws JbpmException {

				long jbpmTaskId = WorkflowFactory.getJbpmTaskId(taskId);

				TaskInstance taskInstance = context.loadTaskInstance(jbpmTaskId);
				taskInstance.end();
				log.info("end task + " + taskInstance.getName() + " with default transition "
						+ taskInstance.getTask().getTaskNode().getDefaultLeavingTransition().getName());

				return null;
			}

		});
	}

	public void processTaskToEnd(final String taskId, final String transitionName) {
		this.jbpmTemplate.execute(new JbpmCallback() {

			@SuppressWarnings("unchecked")
			@Override
			public Object doInJbpm(JbpmContext context) throws JbpmException {

				long jbpmTaskId = WorkflowFactory.getJbpmTaskId(taskId);

				TaskInstance taskInstance = context.loadTaskInstance(jbpmTaskId);
				List<Transition> transitions = taskInstance.getAvailableTransitions();
				boolean isEndState = false;

				// TODO: JUST FOR TEST PURPOSES:
				// The taken transition will be check if its leaving into a
				// endstate.
				// if yes, we finish the task to clear resources

				for (Transition trans : transitions) {
					if (trans.getName().equals(transitionName) && (trans.getTo() instanceof EndState)) {
						isEndState = true;
						break;
					}
				}

				taskInstance.end(transitionName);

				if (isEndState)
					taskInstance.getProcessInstance().end();

				log.info("end task + " + taskInstance.getName() + " with transition " + transitionName);

				return null;
			}

		});
	}

	public WorkflowInstance startWorkflow(final String processDefinitionId, final Map<String, Serializable> properties) {
		return (WorkflowInstance) this.jbpmTemplate.execute(new JbpmCallback() {

			@Override
			public WorkflowInstance doInJbpm(JbpmContext context) throws JbpmException {

				ProcessDefinition processDefinition = context.getGraphSession().getProcessDefinition(
						WorkflowFactory.getJbpmProcessDefinitionId(processDefinitionId));

				ProcessInstance processInstance = context.newProcessInstance(processDefinition.getName());
				processInstance.getContextInstance().addVariables(properties);
				context.save(processInstance);

				return WorkflowFactory.createWorkflowInstance(processInstance);
			}

		});
	}

	public WorkflowDefinition getWorkflowDefinitionByName(final String processdefinitionName) {
		return (WorkflowDefinition) this.jbpmTemplate.execute(new JbpmCallback() {

			@Override
			public Object doInJbpm(JbpmContext context) throws JbpmException {

				ProcessDefinition processDefinition = context.getGraphSession().findProcessDefinition(
						processdefinitionName, 1);

				return WorkflowFactory.createWorkflowDefinition(processDefinition);
			}

		});

	}

	public WorkflowDefinition getWorkflowDefinitionById(final String defId) {
		return (WorkflowDefinition) this.jbpmTemplate.execute(new JbpmCallback() {

			@Override
			public Object doInJbpm(JbpmContext context) throws JbpmException {

				ProcessDefinition processDefinition = context.getGraphSession().getProcessDefinition(
						Long.parseLong(defId));

				return WorkflowFactory.createWorkflowDefinition(processDefinition);
			}

		});

	}

	@SuppressWarnings("unchecked")
	public List<WorkflowDefinition> getAllProcessDefinitions() {
		return (List<WorkflowDefinition>) this.jbpmTemplate.execute(new JbpmCallback() {

			@Override
			public Object doInJbpm(JbpmContext context) throws JbpmException {

				List<WorkflowDefinition> definitions = new LinkedList<WorkflowDefinition>();

				List<ProcessDefinition> processDefinitions = context.getGraphSession().findLatestProcessDefinitions();
				for (ProcessDefinition definition : processDefinitions) {
					// Must be listed to the user (into the workflow wizard) only the workflow definitions
					// associated to existing workflow template
					WorkflowPersistenceTemplateDAO workflowDao = (WorkflowPersistenceTemplateDAO) Context.getInstance()
							.getBean(WorkflowPersistenceTemplateDAO.class);
					List<String> allTemplateNames = new ArrayList<String>();
					for (WorkflowPersistenceTemplate template : workflowDao.findAllDeployed()) {
						allTemplateNames.add(template.getName());
					}
					if (allTemplateNames.contains(definition.getName()))
						definitions.add(WorkflowFactory.createWorkflowDefinition(definition));
				}
				return definitions;
			}
		});
	}

	@Override
	public WorkflowTaskInstance getTaskInstanceById(final String taskId) {

		return (WorkflowTaskInstance) this.jbpmTemplate.execute(new JbpmCallback() {

			@Override
			public WorkflowTaskInstance doInJbpm(JbpmContext context) throws JbpmException {

				long jbpmTaskId = WorkflowFactory.getJbpmTaskId(taskId);

				TaskInstance taskInstance = context.getTaskMgmtSession().getTaskInstance(jbpmTaskId);

				WorkflowTaskInstance wfti = WorkflowFactory.createTaskInstance(taskInstance);

				return wfti;
			}

		});

	}

	@Override
	@SuppressWarnings("unchecked")
	public List<WorkflowTaskInstance> getAllTaskInstances() {
		return (List<WorkflowTaskInstance>) this.jbpmTemplate.execute(

		new JbpmCallback() {

			public List<WorkflowTaskInstance> doInJbpm(JbpmContext context) throws JbpmException {

				Query query = context.getSession().createQuery(
						" select ti from org.jbpm.taskmgmt.exe.TaskInstance as ti"
								+ " where ti.isSuspended != true and ti.isOpen = true");

				List<TaskInstance> list = query.list();

				List<WorkflowTaskInstance> workflowTaskInstances = new ArrayList<WorkflowTaskInstance>();

				for (TaskInstance taskInstance : list)
					workflowTaskInstances.add(WorkflowFactory.createTaskInstance(taskInstance));

				return workflowTaskInstances;

			}
		});
	}

	public void signal(final String id) {
		this.jbpmTemplate.execute(new JbpmCallback() {

			public Object doInJbpm(JbpmContext context) throws JbpmException {
				long jbpmProcessInstanceId = WorkflowFactory.getJbpmProcessInstanceId(id);

				ProcessInstance processInstance = context.loadProcessInstance(jbpmProcessInstanceId);
				processInstance.signal();
				return null;
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<WorkflowTaskInstance> getTasksByActiveWorkflowId(final String processInstanceId) {
		return (List<WorkflowTaskInstance>) this.jbpmTemplate.execute(new JbpmCallback() {

			public Object doInJbpm(JbpmContext context) throws JbpmException {

				List<WorkflowTaskInstance> workflowTaskInstances = new ArrayList<WorkflowTaskInstance>();

				long jbpmProcessInstanceId = WorkflowFactory.getJbpmProcessInstanceId(processInstanceId);

				ProcessInstance processInstance = context.loadProcessInstance(jbpmProcessInstanceId);
				Collection<TaskInstance> taskInstances = processInstance.getTaskMgmtInstance().getTaskInstances();

				for (TaskInstance taskInstance : taskInstances) {

					if (taskInstance.hasEnded())
						continue;

					workflowTaskInstances.add(WorkflowFactory.createTaskInstance(taskInstance));

				}

				return workflowTaskInstances;
			}
		});
	}

	public WorkflowInstance getWorkflowInstanceById(final String processInstanceId) {
		return (WorkflowInstance) this.jbpmTemplate.execute(new JbpmCallback() {

			public Object doInJbpm(JbpmContext context) throws JbpmException {

				long jbpmProcessInstanceId = WorkflowFactory.getJbpmProcessInstanceId(processInstanceId);

				ProcessInstance processInstance = context.loadProcessInstance(jbpmProcessInstanceId);

				return WorkflowFactory.createWorkflowInstance(processInstance);
			}
		});
	}

	@Override
	public void stopWorkflow(final String processId) {
		this.jbpmTemplate.execute(new JbpmCallback() {

			public Object doInJbpm(JbpmContext context) throws JbpmException {
				long jbpmProcessInstanceId = WorkflowFactory.getJbpmProcessInstanceId(processId);

				context.getGraphSession().deleteProcessInstance(jbpmProcessInstanceId);

				return null;
			}
		});
	}

	public void updateWorkflowInstance(final WorkflowInstance workflowInstance) {
		this.jbpmTemplate.execute(new JbpmCallback() {

			@SuppressWarnings("unchecked")
			public Object doInJbpm(JbpmContext context) throws JbpmException {

				long jbpmProcessId = WorkflowFactory.getJbpmProcessInstanceId(workflowInstance.getId());

				ProcessInstance processInstance = context.getProcessInstance(jbpmProcessId);

				Map<String, Object> taskVariables = workflowInstance.getProperties();

				for (String key : taskVariables.keySet()) {

					Object val = taskVariables.get(key);

					processInstance.getContextInstance().setVariable(key.toString(), val);

				}
				context.save(processInstance);

				return null;
			}
		});
	}

	public void updateTaskInstance(final WorkflowTaskInstance wti) {
		this.jbpmTemplate.execute(new JbpmCallback() {

			@SuppressWarnings("unchecked")
			public Object doInJbpm(JbpmContext context) throws JbpmException {

				long jbpmTaskId = WorkflowFactory.getJbpmTaskId(wti.getId());

				TaskInstance taskInstance = context.getTaskInstance(jbpmTaskId);
				Map<String, Object> taskVariables = wti.getProperties();

				for (String key : taskVariables.keySet()) {

					Object val = taskVariables.get(key);

					// updating actor
					if (key.equals(WorkflowConstants.VAR_OWNER)) {
						if (val == null)
							taskInstance.setActorId(null);
						else if (!val.equals(taskInstance.getActorId()))
							taskInstance.setActorId(val.toString());
					}

					else if (key.equals(WorkflowConstants.VAR_TASKSTATE)) {
						String state = (String) val;

						// only two
						if (WorkflowTaskInstance.STATE.STARTED.getVal().equals(state)) {

							if (taskInstance.isSuspended() == true) {
								taskInstance.resume();
							}

							else if (taskInstance.getStart() == null) {
								taskInstance.start();
							}

						} else if (WorkflowTaskInstance.STATE.SUSPENDED.getVal().equals(state)) {
							if (taskInstance.isSuspended() == false) {
								taskInstance.suspend();

							}
						}

						/*
						 * if(ti.getEnd() != null) taskInstance.state =
						 * WorkflowTaskInstance.STATE.DONE; else
						 * if(ti.getStart() == null) taskInstance.state =
						 * WorkflowTaskInstance.STATE.NOT_YET_STARTED; else
						 * if(ti.getStart() != null && ti.isSuspended() != true)
						 * taskInstance.state =
						 * WorkflowTaskInstance.STATE.STARTED; else
						 * if(ti.getStart() != null && ti.isSuspended())
						 * taskInstance.state =
						 * WorkflowTaskInstance.STATE.SUSPENDED;
						 */
					} else {
						taskInstance.setVariableLocally(key.toString(), val);
					}
				}
				context.save(taskInstance);

				return null;
			}
		});
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<WorkflowTaskInstance> getAllActionTasksByUser(final String username) {
		return (List<WorkflowTaskInstance>) this.jbpmTemplate.execute(new JbpmCallback() {

			public Object doInJbpm(JbpmContext context) throws JbpmException {

				List<WorkflowTaskInstance> returnedTaskInstances = new LinkedList<WorkflowTaskInstance>();
				List<TaskInstance> taskInstances = context.getTaskMgmtSession().findTaskInstances(username);

				for (TaskInstance taskInstance : taskInstances) {

					returnedTaskInstances.add(WorkflowFactory.createTaskInstance(taskInstance));
				}

				return returnedTaskInstances;
			}
		});

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<WorkflowTaskInstance> getAllActionPooledTasksByUser(final String username) {

		return (List<WorkflowTaskInstance>) this.jbpmTemplate.execute(new JbpmCallback() {

			public Object doInJbpm(JbpmContext context) throws JbpmException {

				List<WorkflowTaskInstance> returnedTaskInstances = new LinkedList<WorkflowTaskInstance>();

				List<TaskInstance> taskInstances = context.getTaskMgmtSession().findPooledTaskInstances(username);
				for (TaskInstance taskInstance : taskInstances) {

					// we return only those tasks that are currently not
					// assigned to a user:
					// In Brief: If you have a pooled task and one
					// person takes ownership on it, other personst
					// should not be able to assign this task as well as
					// long as this task has been finished from
					// the assigned person
					if (taskInstance.getActorId() == null)
						returnedTaskInstances.add(WorkflowFactory.createTaskInstance(taskInstance));
				}

				return returnedTaskInstances;
			}
		});
	}

	public void deleteAllActiveWorkflows() {
		this.jbpmTemplate.execute(new JbpmCallback() {

			@SuppressWarnings("unchecked")
			public Object doInJbpm(JbpmContext context) throws JbpmException {

				List<ProcessInstance> processInstances = (List<ProcessInstance>) context.getSession().createQuery(
						" from org.jbpm.graph.exe.ProcessInstance").list();

				for (ProcessInstance pi : processInstances) {
					context.getGraphSession().deleteProcessInstance(pi, true, true);

					log.info("Delete WorkflowInstance with id " + pi.getId());
				}

				return null;
			}
		});
	}

	@Override
	public void assignUserToTask(final String taskId, final String assignee) {
		this.jbpmTemplate.execute(new JbpmCallback() {

			public Object doInJbpm(JbpmContext context) throws JbpmException {

				TaskInstance taskInstance = context.getTaskInstance(WorkflowFactory.getJbpmTaskId(taskId));
				taskInstance.setActorId(assignee);
				log.info("Assign '" + assignee + "' to " + taskId);
				return null;
			}
		});
	}

	@SuppressWarnings( { "unchecked" })
	public List<WorkflowTaskInstance> getTaskInstancesByActiveWorkflow(final String workflow_id) {
		return (List<WorkflowTaskInstance>) this.jbpmTemplate.execute(new JbpmCallback() {

			public List<WorkflowTaskInstance> doInJbpm(JbpmContext context) throws JbpmException {

				ProcessInstance processInstance = context.getProcessInstance(WorkflowFactory
						.getJbpmProcessInstanceId(workflow_id));

				Collection<TaskInstance> taskInstances = processInstance.getTaskMgmtInstance().getTaskInstances();
				List<WorkflowTaskInstance> workflowTaskInstances = new LinkedList<WorkflowTaskInstance>();

				for (TaskInstance taskInstance : taskInstances)
					workflowTaskInstances.add(WorkflowFactory.createTaskInstance(taskInstance));

				return workflowTaskInstances;

			}
		});
	}

	public WorkflowInstance getWorkflowInstanceByTaskInstance(final String workflowTaskId) {

		return (WorkflowInstance) this.jbpmTemplate.execute(new JbpmCallback() {

			public WorkflowInstance doInJbpm(JbpmContext context) throws JbpmException {

				Long processId = WorkflowFactory.getJbpmProcessDefinitionIdFromTaskInstance(workflowTaskId);

				ProcessInstance processInstance = context.getProcessInstance(processId);

				return WorkflowFactory.createWorkflowInstance(processInstance);
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<WorkflowInstance> getAllWorkflows() {

		return (List<WorkflowInstance>) this.jbpmTemplate.execute(new JbpmCallback() {

			public List<WorkflowInstance> doInJbpm(JbpmContext context) throws JbpmException {

				List<ProcessInstance> processInstances = context.getSession().createQuery(
						"from org.jbpm.graph.exe.ProcessInstance").list();
				List<WorkflowInstance> workflowInstances = new LinkedList<WorkflowInstance>();

				for (ProcessInstance instance : processInstances)
					workflowInstances.add(WorkflowFactory.createWorkflowInstance(instance));

				return workflowInstances;
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<WorkflowTaskInstance> getAllSuspendedTaskInstances(final String actorId) {
		return (List<WorkflowTaskInstance>) this.jbpmTemplate.execute(new JbpmCallback() {

			public List<WorkflowTaskInstance> doInJbpm(JbpmContext context) throws JbpmException {

				List<TaskInstance> taskInstances = context.getSession().createQuery(
						"from org.jbpm.taskmgmt.exe.TaskInstance as ti "
								+ "where ti.actorId = :actorId  and ti.isSuspended = true and ti.isOpen = true")
						.setString("actorId", actorId).list();

				List<WorkflowTaskInstance> workflowTaskInstances = new LinkedList<WorkflowTaskInstance>();

				for (TaskInstance instance : taskInstances)
					workflowTaskInstances.add(WorkflowFactory.createTaskInstance(instance));

				return workflowTaskInstances;
			}
		});
	}
}