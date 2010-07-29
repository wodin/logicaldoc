package com.logicaldoc.workflow.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUITransition;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.beans.GUIWFState;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;
import com.logicaldoc.gui.frontend.client.services.WorkflowService;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.service.SecurityServiceImpl;
import com.logicaldoc.web.util.SessionUtil;
import com.logicaldoc.workflow.editor.WorkflowPersistenceTemplate;
import com.logicaldoc.workflow.editor.WorkflowPersistenceTemplateDAO;
import com.logicaldoc.workflow.editor.model.Assignee;
import com.logicaldoc.workflow.editor.model.BaseWorkflowModel;
import com.logicaldoc.workflow.editor.model.EndState;
import com.logicaldoc.workflow.editor.model.Fork;
import com.logicaldoc.workflow.editor.model.Join;
import com.logicaldoc.workflow.editor.model.Transition;
import com.logicaldoc.workflow.editor.model.WorkflowTask;
import com.logicaldoc.workflow.model.WorkflowMessage;
import com.logicaldoc.workflow.model.WorkflowTemplate;
import com.logicaldoc.workflow.transform.WorkflowTransformService;
import com.thoughtworks.xstream.XStream;

/**
 * Implementation of the WorkflowService
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class WorkflowServiceImpl extends RemoteServiceServlet implements WorkflowService {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(WorkflowServiceImpl.class);

	@Override
	public GUIWorkflow get(String sid, String workflowName) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		try {
			WorkflowPersistenceTemplateDAO dao = (WorkflowPersistenceTemplateDAO) Context.getInstance().getBean(
					WorkflowPersistenceTemplateDAO.class);
			WorkflowTemplate workflowTemplate = new WorkflowTemplate();
			WorkflowPersistenceTemplate persistenceTemplate = new WorkflowPersistenceTemplate();
			WorkflowTransformService workflowTransformService = (WorkflowTransformService) Context.getInstance()
					.getBean("workflowTransformService");

			persistenceTemplate = dao.load(workflowName, WorkflowPersistenceTemplateDAO.WORKFLOW_STAGE.SAVED);
			if (persistenceTemplate.getXmldata() != null
					&& ((String) persistenceTemplate.getXmldata()).getBytes().length > 0) {
				workflowTemplate = workflowTransformService.fromWorkflowDefinitionToObject(persistenceTemplate);
			}

			GUIWorkflow wfl = new GUIWorkflow();
			wfl.setId(persistenceTemplate.getId());
			wfl.setName(workflowTemplate.getName());
			wfl.setDescription(workflowTemplate.getDescription());
			wfl.setTaskAssignmentSubject(workflowTemplate.getAssignmentMessage().getSubject());
			wfl.setTaskAssignmentBody(workflowTemplate.getAssignmentMessage().getBody());
			wfl.setReminderSubject(workflowTemplate.getReminderMessage().getSubject());
			wfl.setReminderBody(workflowTemplate.getReminderMessage().getBody());
			UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
			User supervisorUser = userDao.findByUserName(workflowTemplate.getSupervisor());
			if (supervisorUser != null) {
				wfl.setSupervisor(Long.toString(supervisorUser.getId()));
			}
			wfl.setStartState(workflowTemplate.getStartState());

			if (workflowTemplate.getWorkflowComponents().size() > 0) {
				GUIWFState[] states = new GUIWFState[workflowTemplate.getWorkflowComponents().size()];
				for (BaseWorkflowModel workflowModel : workflowTemplate.getWorkflowComponents()) {
					GUIWFState state = new GUIWFState();
					state.setId(workflowModel.getId());
					state.setName(workflowModel.getName());
					if (workflowModel instanceof WorkflowTask) {
						state.setType(GUIWFState.TYPE_TASK);
						state.setDescription(((WorkflowTask) workflowModel).getDescription());
						state.setDueDateNumber(((WorkflowTask) workflowModel).getDueDateValue());
						state.setDueDateUnit(((WorkflowTask) workflowModel).getDueDateUnit());
						state.setReminderNumber(((WorkflowTask) workflowModel).getRemindTimeValue());
						state.setReminderUnit(((WorkflowTask) workflowModel).getRemindTimeUnit());
						SecurityServiceImpl securityService = new SecurityServiceImpl();

						if (((WorkflowTask) workflowModel).getAssignees().size() > 0) {
							GUIUser[] participants = new GUIUser[((WorkflowTask) workflowModel).getAssignees().size()];
							int i = 0;
							for (Assignee assignee : ((WorkflowTask) workflowModel).getAssignees()) {
								GUIUser user = securityService.getUser(sid, Long.parseLong(assignee.getId()));
								participants[i] = user;
								i++;
							}
							state.setParticipants(participants);
						}
					} else if (workflowModel instanceof EndState) {
						state.setType(GUIWFState.TYPE_END);
					} else if (workflowModel instanceof Fork) {
						state.setType(GUIWFState.TYPE_FORK);
					} else if (workflowModel instanceof Join) {
						state.setType(GUIWFState.TYPE_JOIN);
					}
				}
				wfl.setStates(states);

				if (wfl.getStates() != null && wfl.getStates().length > 0
						&& workflowTemplate.getWorkflowComponents().size() > 0) {
					for (BaseWorkflowModel workflowModel : workflowTemplate.getWorkflowComponents()) {

						System.out.println("1: workflow: " + wfl.getName() + " states number: "
								+ wfl.getStates().length + " name searched: " + workflowModel.getName());
						GUIWFState state = wfl.getStateByName(workflowModel.getName());
						if (workflowModel instanceof WorkflowTask) {
							if (((WorkflowTask) workflowModel).getTransitions().size() > 0) {
								GUITransition[] transitions = new GUITransition[((WorkflowTask) workflowModel)
										.getTransitions().size()];
								int j = 0;
								for (Transition transition : ((WorkflowTask) workflowModel).getTransitions()) {
									System.out.println("2: workflow: " + wfl.getName() + " states number: "
											+ wfl.getStates().length + " name searched: "
											+ transition.getDestination().getName());
									GUITransition t = new GUITransition(transition.getName(),
											wfl.getStateByName(transition.getDestination().getName()));
									transitions[j] = t;
									j++;
								}
								state.setTransitions(transitions);
							}
						} else if (workflowModel instanceof Fork) {
							if (((Fork) workflowModel).getWorkflowTasks().size() > 0) {
								GUITransition[] transitions = new GUITransition[((Fork) workflowModel)
										.getWorkflowTasks().size()];
								int j = 0;
								for (BaseWorkflowModel model : ((Fork) workflowModel).getWorkflowTasks()) {
									System.out.println("3: workflow: " + wfl.getName() + " states number: "
											+ wfl.getStates().length + " name searched: " + model.getName());
									GUITransition t = new GUITransition(model.getName(), wfl.getStateByName(model
											.getName()));
									transitions[j] = t;
									j++;
								}
								state.setTransitions(transitions);
							}
						} else if (workflowModel instanceof Join) {
							if (((Join) workflowModel).getDestination() != null) {
								GUITransition[] transitions = new GUITransition[1];
								BaseWorkflowModel model = ((Join) workflowModel).getDestination();
								System.out.println("4: workflow: " + wfl.getName() + " states number: "
										+ wfl.getStates().length + " name searched: " + model.getName());
								GUITransition t = new GUITransition(model.getName(),
										wfl.getStateByName(model.getName()));
								transitions[0] = t;
								state.setTransitions(transitions);
							}
						}
					}
				}
			}

			System.out.println("Retrieved workflow id: " + wfl.getId());

			return wfl;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void delete(String sid, String workflowName) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		WorkflowPersistenceTemplateDAO dao = (WorkflowPersistenceTemplateDAO) Context.getInstance().getBean(
				WorkflowPersistenceTemplateDAO.class);
		try {
			WorkflowPersistenceTemplate workflowTemplate = dao.load(workflowName,
					WorkflowPersistenceTemplateDAO.WORKFLOW_STAGE.SAVED);
			dao.delete(workflowTemplate);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public GUIWorkflow save(String sid, GUIWorkflow workflow) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		System.out.println("SAVING workflow id: " + workflow.getId());

		WorkflowPersistenceTemplateDAO dao = (WorkflowPersistenceTemplateDAO) Context.getInstance().getBean(
				WorkflowPersistenceTemplateDAO.class);
		try {
			WorkflowTemplate workflowTemplate = new WorkflowTemplate();
			WorkflowPersistenceTemplate persistenceTemplate = dao.load(workflow.getName(),
					WorkflowPersistenceTemplateDAO.WORKFLOW_STAGE.SAVED);
			if (persistenceTemplate == null)
				// It is the first workflow save
				persistenceTemplate = new WorkflowPersistenceTemplate();

			workflowTemplate.setName(workflow.getName());
			workflowTemplate.setDescription(workflow.getDescription());
			workflowTemplate.setAssignmentMessage(new WorkflowMessage(workflow.getTaskAssignmentSubject(), workflow
					.getTaskAssignmentBody()));
			workflowTemplate.setReminderMessage(new WorkflowMessage(workflow.getReminderSubject(), workflow
					.getReminderBody()));
			if (workflow.getSupervisor() != null && !workflow.getSupervisor().trim().isEmpty()) {
				SecurityServiceImpl securityService = new SecurityServiceImpl();
				workflowTemplate.setSupervisor(securityService.getUser(sid, Long.parseLong(workflow.getSupervisor()))
						.getUserName());
			}
			workflowTemplate.setStartState(workflow.getStartState());

			if (workflow.getStates() != null) {
				System.out.println("---- workflow states number: " + workflow.getStates().length);

				for (GUIWFState state : workflow.getStates()) {
					if (state.getType() == GUIWFState.TYPE_TASK) {
						WorkflowTask taskModel = new WorkflowTask();
						taskModel.setId(state.getId());
						taskModel.setName(state.getName());
						taskModel.setDescription(state.getDescription());
						taskModel.setDueDateValue(state.getDueDateNumber());
						taskModel.setDueDateUnit(state.getDueDateUnit());
						taskModel.setRemindTimeValue(state.getReminderNumber());
						taskModel.setRemindTimeUnit(state.getDueDateUnit());
						if (state.getParticipants() != null)
							for (GUIUser user : state.getParticipants()) {
								((WorkflowTask) taskModel).getAssignees().add(
										new Assignee(Long.toString(user.getId()), user.getUserName()));
							}

						workflowTemplate.getWorkflowComponents().add(taskModel);
					} else if (state.getType() == GUIWFState.TYPE_END) {
						EndState endModel = new EndState();
						endModel.setId(state.getId());
						endModel.setName(state.getName());

						workflowTemplate.getWorkflowComponents().add(endModel);
					} else if (state.getType() == GUIWFState.TYPE_FORK) {
						Fork forkModel = new Fork();
						forkModel.setId(state.getId());
						forkModel.setName(state.getName());

						workflowTemplate.getWorkflowComponents().add(forkModel);
					} else if (state.getType() == GUIWFState.TYPE_JOIN) {
						Join joinModel = new Join();
						joinModel.setId(state.getId());
						joinModel.setName(state.getName());

						workflowTemplate.getWorkflowComponents().add(joinModel);
					}
				}
			}

			if (workflow.getStates() != null) {
				// List again the workflow states to set the transitions
				for (GUIWFState state : workflow.getStates()) {
					if (state.getType() == GUIWFState.TYPE_TASK) {
						WorkflowTask workflowTask = (WorkflowTask) workflowTemplate.getWorkflowComponentById(state
								.getId());
						if (state.getTransitions() != null)
							for (GUITransition transition : state.getTransitions()) {
								Transition t = new Transition();
								t.setName(transition.getText());
								t.setDestination(workflowTemplate.getWorkflowComponentById(transition.getTargetState()
										.getId()));
								workflowTask.getTransitions().add(t);
							}
					} else if (state.getType() == GUIWFState.TYPE_FORK) {
						Fork fork = (Fork) workflowTemplate.getWorkflowComponentById(state.getId());
						if (state.getTransitions() != null)
							for (GUITransition transition : state.getTransitions()) {
								fork.getWorkflowTasks().add(
										(WorkflowTask) workflowTemplate.getWorkflowComponentById(transition
												.getTargetState().getId()));
							}
					} else if (state.getType() == GUIWFState.TYPE_JOIN) {
						Join join = (Join) workflowTemplate.getWorkflowComponentById(state.getId());
						if (state.getTransitions() != null) {
							// A join element must have only one transition
							GUITransition transition = state.getTransitions()[0];
							join.setDestination(workflowTemplate.getWorkflowComponentById(transition.getTargetState()
									.getId()));
						}
					}
				}
			}

			persistenceTemplate.setName(workflow.getName());

			XStream xstream = new XStream();
			String xmlData = xstream.toXML(workflowTemplate);
			persistenceTemplate.setXmldata(xmlData);
			dao.save(persistenceTemplate, WorkflowPersistenceTemplateDAO.WORKFLOW_STAGE.SAVED);
			persistenceTemplate.setDescription(workflowTemplate.getDescription());
			persistenceTemplate.setStartState(workflowTemplate.getStartState());

			workflow.setId(persistenceTemplate.getId());

			System.out.println("SAVED workflow id: " + workflow.getId());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}

		return workflow;
	}

	@Override
	public void deploy(String sid, GUIWorkflow workflow) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		WorkflowPersistenceTemplateDAO dao = (WorkflowPersistenceTemplateDAO) Context.getInstance().getBean(
				WorkflowPersistenceTemplateDAO.class);
		WorkflowTemplate workflowTemplate = new WorkflowTemplate();
		WorkflowPersistenceTemplate persistenceTemplate = dao.load(workflow.getName(),
				WorkflowPersistenceTemplateDAO.WORKFLOW_STAGE.SAVED);
		if (persistenceTemplate == null)
			return;

		com.logicaldoc.workflow.WorkflowService workflowService = (com.logicaldoc.workflow.WorkflowService) Context
				.getInstance().getBean("workflowService");

		try {
			workflowTemplate.setName(workflow.getName());
			workflowTemplate.setDescription(workflow.getDescription());
			workflowTemplate.setAssignmentMessage(new WorkflowMessage(workflow.getTaskAssignmentSubject(), workflow
					.getTaskAssignmentBody()));
			workflowTemplate.setReminderMessage(new WorkflowMessage(workflow.getReminderSubject(), workflow
					.getReminderBody()));
			if (workflow.getSupervisor() != null && !workflow.getSupervisor().trim().isEmpty()) {
				SecurityServiceImpl securityService = new SecurityServiceImpl();
				workflowTemplate.setSupervisor(Long.toString(securityService.getUser(sid,
						Long.parseLong(workflow.getSupervisor())).getId()));
			}

			persistenceTemplate.setName(workflow.getName());

			XStream xstream = new XStream();
			String xmlData = xstream.toXML(workflowTemplate);
			persistenceTemplate.setXmldata(xmlData);
			dao.save(persistenceTemplate, WorkflowPersistenceTemplateDAO.WORKFLOW_STAGE.SAVED);
			persistenceTemplate.setDescription(workflowTemplate.getDescription());
			persistenceTemplate.setStartState(workflowTemplate.getStartState());

			persistenceTemplate.setXmldata(xstream.toXML(workflowTemplate));
			dao.deploy(persistenceTemplate);
			workflowService.deployWorkflow(workflowTemplate);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public GUIWorkflow[] list(String sid) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		try {
			WorkflowPersistenceTemplateDAO dao = (WorkflowPersistenceTemplateDAO) Context.getInstance().getBean(
					WorkflowPersistenceTemplateDAO.class);
			GUIWorkflow[] workflows = new GUIWorkflow[dao.findAll().size()];
			int i = 0;
			for (WorkflowPersistenceTemplate workflow : dao.findAll()) {
				workflows[i] = get(sid, workflow.getName());
				i++;
			}

			return workflows;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}

		return null;
	}
}