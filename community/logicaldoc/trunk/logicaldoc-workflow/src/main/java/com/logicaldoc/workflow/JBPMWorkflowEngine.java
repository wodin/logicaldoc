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
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.node.EndState;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springmodules.workflow.jbpm31.JbpmCallback;
import org.springmodules.workflow.jbpm31.JbpmTemplate;

import com.logicaldoc.workflow.exception.WorkflowException;
import com.logicaldoc.workflow.model.WorkflowDefinition;
import com.logicaldoc.workflow.model.WorkflowInstance;
import com.logicaldoc.workflow.model.WorkflowTaskInstance;
import com.logicaldoc.workflow.persistence.WorkflowPersistenceTemplate;

public class JBPMWorkflowEngine implements WorkflowEngine {

	protected final Log logger = LogFactory.getLog(getClass());

	private JbpmTemplate jbpmTemplate;

	public void setJbpmTemplate(JbpmTemplate jbpmTemplate) {
		this.jbpmTemplate = jbpmTemplate;
	}

	public void undeployWorkflow(final String processId) {

		this.jbpmTemplate.execute(new JbpmCallback() {

			@Override
			public Object doInJbpm(JbpmContext context) throws JbpmException {

				logger.debug("delete processdefinition with id " + processId);
				ProcessDefinition processDefinition = context.getGraphSession()
						.findLatestProcessDefinition("hello world");
				context.getGraphSession().deleteProcessDefinition(
						WorkflowFactory.getJbpmProcessDefinitionId(processId));

				return null;
			}

		});
	}

	public void deployWorkflow(WorkflowPersistenceTemplate template, Serializable _processDefinition) {

		if (_processDefinition instanceof String) {
			try {
				_processDefinition = ProcessDefinition
						.parseXmlString((String) _processDefinition);
			} catch (Exception e) {
				throw new WorkflowException(e);
			}
		}

		if ((_processDefinition instanceof ProcessDefinition) == false)
			throw new WorkflowException(
					"The given processdefinition must be serializable!");

		final ProcessDefinition processDefinition = (ProcessDefinition) _processDefinition;
		processDefinition.setDescription(template.getDescription());
		
		this.jbpmTemplate.execute(new JbpmCallback() {

			@Override
			public Object doInJbpm(JbpmContext context) throws JbpmException {

				context.deployProcessDefinition(processDefinition);

				logger.info("deploy processdefinition with name "
						+ processDefinition.getName());

				return null;
			}

		});
	}

	public void processTaskToEnd(final String taskId) {
		this.jbpmTemplate.execute(new JbpmCallback() {

			@Override
			public Object doInJbpm(JbpmContext context) throws JbpmException {

				long jbpmTaskId = WorkflowFactory.getJbpmTaskId(taskId);

				TaskInstance taskInstance = context
						.loadTaskInstance(jbpmTaskId);
				taskInstance.end();
				logger.info("end task + "
						+ taskInstance.getName()
						+ " with default transition "
						+ taskInstance.getTask().getTaskNode()
								.getDefaultLeavingTransition().getName());

				return null;
			}

		});
	}

	public void processTaskToEnd(final String taskId,
			final String transitionName) {
		this.jbpmTemplate.execute(new JbpmCallback() {

			@Override
			public Object doInJbpm(JbpmContext context) throws JbpmException {

				long jbpmTaskId = WorkflowFactory.getJbpmTaskId(taskId);

				TaskInstance taskInstance = context
						.loadTaskInstance(jbpmTaskId);
				List<Transition> transitions = taskInstance.getAvailableTransitions();
				boolean isEndState = false;
				
				//TODO: JUST FOR TEST PURPOSES: 
				//The taken transition will be check if its leaving into a endstate. 
				//if yes, we finish the task to clear resources
				
				for(Transition trans : transitions){
					if(trans.getName().equals(transitionName) && (trans.getTo() instanceof EndState)){
						isEndState = true;
						break;
					}
				}
				
				taskInstance.end(transitionName);
				
				if(isEndState)
					taskInstance.getProcessInstance().end();
				
				logger.info("end task + " + taskInstance.getName()
						+ " with transition " + transitionName);

				return null;
			}

		});
	}

	public WorkflowInstance startWorkflow(final String processDefinitionId) {
		return (WorkflowInstance) this.jbpmTemplate.execute(new JbpmCallback() {

			@Override
			public WorkflowInstance doInJbpm(JbpmContext context)
					throws JbpmException {

				ProcessDefinition processDefinition = context.getGraphSession()
						.getProcessDefinition( WorkflowFactory.getJbpmProcessDefinitionId(processDefinitionId));

				ProcessInstance processInstance = context
						.newProcessInstance(processDefinition.getName());
				context.save(processInstance);

				return WorkflowFactory.createWorkflowInstance(processInstance);
			}

		});
	}

	public ProcessDefinition getProcessDefinitionByName(
			final String processdefinitionName) {
		return (ProcessDefinition) this.jbpmTemplate
				.execute(new JbpmCallback() {

					@Override
					public Object doInJbpm(JbpmContext context)
							throws JbpmException {

						ProcessDefinition processDefinition = context
								.getGraphSession().findProcessDefinition(
										processdefinitionName, 1);

						return processDefinition;
					}

				});

	}

	@SuppressWarnings("unchecked")
	public List<WorkflowDefinition> getAllProcessDefinitions() {
		return (List<WorkflowDefinition>) this.jbpmTemplate
				.execute(new JbpmCallback() {

					@Override
					public Object doInJbpm(JbpmContext context)
							throws JbpmException {

						List<WorkflowDefinition> definitions = new LinkedList<WorkflowDefinition>();

						List<ProcessDefinition> processDefinitions = context
								.getGraphSession().findAllProcessDefinitions();
						for (ProcessDefinition definition : processDefinitions) {
							WorkflowDefinition workflowDefinition = new WorkflowDefinition();
							workflowDefinition.setName(definition.getName());
							workflowDefinition.setDescription(definition
									.getDescription());
							workflowDefinition.setDefinitionId(WorkflowFactory
									.createProcessDefintionId(definition
											.getId()));
							
							definitions.add(workflowDefinition);
						}
						return definitions;
					}

				});

	}

	@Override
	public WorkflowTaskInstance getTaskInstanceById(final String taskId) {

		return (WorkflowTaskInstance) this.jbpmTemplate
				.execute(new JbpmCallback() {

					@Override
					public WorkflowTaskInstance doInJbpm(JbpmContext context)
							throws JbpmException {

						long jbpmTaskId = WorkflowFactory.getJbpmTaskId(taskId);

						TaskInstance taskInstance = context
								.getTaskMgmtSession().getTaskInstance(
										jbpmTaskId);

						WorkflowTaskInstance wfti = WorkflowFactory
								.createTaskInstance(taskInstance);

						return wfti;
					}

				});

	}

	@Override
	@SuppressWarnings("unchecked")
	public List<WorkflowTaskInstance> getAllTaskInstances() {
		return (List<WorkflowTaskInstance>) this.jbpmTemplate.execute(

		new JbpmCallback() {

			public List<WorkflowTaskInstance> doInJbpm(JbpmContext context)
					throws JbpmException {

				Query query = context
						.getSession()
						.createQuery(
								" select ti from org.jbpm.taskmgmt.exe.TaskInstance as ti"
										+ " where ti.isSuspended != true and ti.isOpen = true");

				List<TaskInstance> list = query.list();

				List<WorkflowTaskInstance> workflowTaskInstances = new ArrayList<WorkflowTaskInstance>();

				for (TaskInstance taskInstance : list)
					workflowTaskInstances.add(WorkflowFactory
							.createTaskInstance(taskInstance));

				return workflowTaskInstances;

			}
		});
	}

	public Token getToken(final long id) {
		return (Token) this.jbpmTemplate.execute(

		new JbpmCallback() {

			public Token doInJbpm(JbpmContext context) throws JbpmException {

				ProcessInstance processInstance = context
						.loadProcessInstance(id);
				return processInstance.getRootToken();
			}
		});
	}

	public void signal(final String id) {
		this.jbpmTemplate.execute(new JbpmCallback() {

			public Object doInJbpm(JbpmContext context) throws JbpmException {

				long jbpmProcessInstanceId = WorkflowFactory
						.getJbpmProcessInstanceId(id);

				ProcessInstance processInstance = context
						.loadProcessInstance(jbpmProcessInstanceId);
				Token token = processInstance.getRootToken();
				processInstance.signal();

				return null;
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<WorkflowTaskInstance> getTasksByActiveWorkflowId(
			final String processInstanceId) {
		return (List<WorkflowTaskInstance>) this.jbpmTemplate
				.execute(new JbpmCallback() {

					public Object doInJbpm(JbpmContext context)
							throws JbpmException {

						List<WorkflowTaskInstance> workflowTaskInstances = new ArrayList<WorkflowTaskInstance>();

						long jbpmProcessInstanceId = WorkflowFactory
								.getJbpmProcessInstanceId(processInstanceId);

						ProcessInstance processInstance = context
								.loadProcessInstance(jbpmProcessInstanceId);
						Collection<TaskInstance> taskInstances = processInstance
								.getTaskMgmtInstance().getTaskInstances();

						for (TaskInstance taskInstance : taskInstances) {

							if (taskInstance.hasEnded())
								continue;

							workflowTaskInstances.add(WorkflowFactory
									.createTaskInstance(taskInstance));

						}

						return workflowTaskInstances;
					}
				});
	}

	public WorkflowInstance getWorkflowInstanceById(
			final String processInstanceId) {
		return (WorkflowInstance) this.jbpmTemplate.execute(new JbpmCallback() {

			public Object doInJbpm(JbpmContext context) throws JbpmException {

				long jbpmProcessInstanceId = WorkflowFactory
						.getJbpmProcessInstanceId(processInstanceId);

				ProcessInstance processInstance = context
						.loadProcessInstance(jbpmProcessInstanceId);

				return WorkflowFactory.createWorkflowInstance(processInstance);
			}
		});
	}

	@Override
	public void stopWorkflow(final String processId) {
		this.jbpmTemplate.execute(new JbpmCallback() {

			public Object doInJbpm(JbpmContext context) throws JbpmException {
				long jbpmProcessInstanceId = WorkflowFactory
						.getJbpmProcessInstanceId(processId);

				context.getGraphSession().deleteProcessInstance(
						jbpmProcessInstanceId);

				return null;
			}
		});
	}

	public void updateTaskInstance(final WorkflowTaskInstance wti) {
		this.jbpmTemplate.execute(new JbpmCallback() {

			@SuppressWarnings("unchecked")
			public Object doInJbpm(JbpmContext context) throws JbpmException {

				long jbpmTaskId = WorkflowFactory.getJbpmTaskId(wti.id);

				TaskInstance taskInstance = context.getTaskInstance(jbpmTaskId);
				Map<String, Object> taskVariables = taskInstance
						.getVariablesLocally();

				for (String key : taskVariables.keySet()) {

					Object val = taskVariables.get(key);

					// updating actor
					if (key.equals(WorkflowConstants.VAR_OWNER)) {
						taskInstance.setActorId(val.toString());
						continue;
					}

					taskInstance.setVariableLocally(key.toString(), val);
				}
				context.save(taskInstance);

				return null;
			}
		});
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<WorkflowTaskInstance> getAllActionTasksByUser(final String username) {
		return (List<WorkflowTaskInstance>)this.jbpmTemplate.execute(new JbpmCallback() {
			
			public Object doInJbpm(JbpmContext context) throws JbpmException {
			
				List<WorkflowTaskInstance> returnedTaskInstances = new LinkedList<WorkflowTaskInstance>();
				List<TaskInstance> taskInstances = context.getTaskMgmtSession().findTaskInstances(username);
				
				for(TaskInstance taskInstance : taskInstances){
					returnedTaskInstances.add(WorkflowFactory.createTaskInstance(taskInstance));
				}
				
				return returnedTaskInstances;
			}
		});
	
	}
	
	public void deleteAllActiveWorkflows(){
		this.jbpmTemplate.execute(new JbpmCallback() {
			
			public Object doInJbpm(JbpmContext context) throws JbpmException {
																							      
				List<ProcessInstance> processInstances = context.getSession().createQuery(" from org.jbpm.graph.exe.ProcessInstance").list();

				for(ProcessInstance pi : processInstances){
					context.getGraphSession().deleteProcessInstance(pi.getId());
					System.out.println("Delete WorkflowInstance with id " + pi.getId());
				}
				
				return null;
			}
		});
	}
}