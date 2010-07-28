package com.logicaldoc.workflow.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;
import com.logicaldoc.gui.frontend.client.services.WorkflowService;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.service.SecurityServiceImpl;
import com.logicaldoc.web.util.SessionUtil;
import com.logicaldoc.workflow.editor.WorkflowPersistenceTemplate;
import com.logicaldoc.workflow.editor.WorkflowPersistenceTemplateDAO;
import com.logicaldoc.workflow.editor.model.BaseWorkflowModel;
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
	public GUIWorkflow get(String sid, long workflowId) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		try {
			WorkflowPersistenceTemplateDAO dao = (WorkflowPersistenceTemplateDAO) Context.getInstance().getBean(
					WorkflowPersistenceTemplateDAO.class);
			WorkflowTemplate workflowTemplate = new WorkflowTemplate();
			WorkflowPersistenceTemplate persistenceTemplate = new WorkflowPersistenceTemplate();
			WorkflowTransformService workflowTransformService = (WorkflowTransformService) Context.getInstance()
					.getBean("workflowTransformService");

			persistenceTemplate = dao.load(workflowId, WorkflowPersistenceTemplateDAO.WORKFLOW_STAGE.SAVED);
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
			if(supervisorUser != null){
				wfl.setSupervisor(Long.toString(supervisorUser.getId()));
			} else {
				System.out.println("Supervisor error!!!!");
				System.out.println("workflowTemplate.getSupervisor(): "+workflowTemplate.getSupervisor());
			}
			
			wfl.setStartState(workflowTemplate.getStartState());

			// TODO Manage the GUIWFStates of the GUIWorkflow

			return wfl;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void delete(String sid, long workflowId) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		WorkflowPersistenceTemplateDAO dao = (WorkflowPersistenceTemplateDAO) Context.getInstance().getBean(
				WorkflowPersistenceTemplateDAO.class);
		try {
			WorkflowPersistenceTemplate workflowTemplate = dao.load(workflowId,
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

		WorkflowPersistenceTemplateDAO dao = (WorkflowPersistenceTemplateDAO) Context.getInstance().getBean(
				WorkflowPersistenceTemplateDAO.class);
		WorkflowTemplate workflowTemplate = new WorkflowTemplate();
		WorkflowPersistenceTemplate persistenceTemplate = new WorkflowPersistenceTemplate();

		try {
			workflowTemplate.setName(workflow.getName());
			workflowTemplate.setDescription(workflow.getDescription());
			workflowTemplate.setAssignmentMessage(new WorkflowMessage(workflow.getTaskAssignmentSubject(), workflow
					.getTaskAssignmentBody()));
			workflowTemplate.setReminderMessage(new WorkflowMessage(workflow.getReminderSubject(), workflow
					.getReminderBody()));
			SecurityServiceImpl securityService = new SecurityServiceImpl();
			workflowTemplate.setSupervisor(securityService.getUser(sid, Long.parseLong(workflow.getSupervisor()))
					.getUserName());

			persistenceTemplate.setName(workflow.getName());

			XStream xstream = new XStream();
			String xmlData = xstream.toXML(workflowTemplate);
			persistenceTemplate.setXmldata(xmlData);
			dao.save(persistenceTemplate, WorkflowPersistenceTemplateDAO.WORKFLOW_STAGE.SAVED);
			persistenceTemplate.setDescription(workflowTemplate.getDescription());
			// TODO Set the workflow start state
			// persistenceTemplate.setStartState(workflowTemplate.getStartState());

			workflow.setId(persistenceTemplate.getId());
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
		WorkflowPersistenceTemplate persistenceTemplate = new WorkflowPersistenceTemplate();

		com.logicaldoc.workflow.WorkflowService workflowService = (com.logicaldoc.workflow.WorkflowService) Context
				.getInstance().getBean("workflowService");

		try {
			workflowTemplate.setName(workflow.getName());
			workflowTemplate.setDescription(workflow.getDescription());
			workflowTemplate.setAssignmentMessage(new WorkflowMessage(workflow.getTaskAssignmentSubject(), workflow
					.getTaskAssignmentBody()));
			workflowTemplate.setReminderMessage(new WorkflowMessage(workflow.getReminderSubject(), workflow
					.getReminderBody()));
			SecurityServiceImpl securityService = new SecurityServiceImpl();
			workflowTemplate.setSupervisor(Long.toString(securityService.getUser(sid,
					Long.parseLong(workflow.getSupervisor())).getId()));

			persistenceTemplate.setName(workflow.getName());

			XStream xstream = new XStream();
			String xmlData = xstream.toXML(workflowTemplate);
			persistenceTemplate.setXmldata(xmlData);
			dao.save(persistenceTemplate, WorkflowPersistenceTemplateDAO.WORKFLOW_STAGE.SAVED);
			persistenceTemplate.setDescription(workflowTemplate.getDescription());
			// TODO Set the workflow start state
			// persistenceTemplate.setStartState(workflowTemplate.getStartState());

			// TODO Missing class 'DeployMessage'
			// LinkedList<DeployMessage> errorMessages = new
			// LinkedList<DeployMessage>();
			//
			// if (workflowTemplate.getWorkflowComponents().size() == 0)
			// errorMessages.add(new DeployMessage(workflowTemplate,
			// "No workflow-component have been added"));

			boolean workflowTaskExist = false;
			for (BaseWorkflowModel model : workflowTemplate.getWorkflowComponents()) {

				if (model instanceof WorkflowTask)
					workflowTaskExist = true;

				// model.checkForDeploy(errorMessages);
			}

			// if (workflowTaskExist == false)
			// errorMessages.add(new DeployMessage(workflowTemplate,
			// "There must at least exist one Workflow-Task"));
			//
			// if (errorMessages.size() > 0)
			// return;

			// at first we have to delete the current workflow instance
			// TODO:we should add API-improvements to handle more clearer this
			// List<WorkflowDefinition> definitions =
			// this.workflowService.getAllDefinitions();
			//
			// for (WorkflowDefinition definition : definitions) {
			// if (definition.getName().equals(workflowTemplate.getName()))
			// this.workflowService.undeployWorkflow(definition.getDefinitionId());
			// }

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
				workflows[i] = get(sid, workflow.getId());
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