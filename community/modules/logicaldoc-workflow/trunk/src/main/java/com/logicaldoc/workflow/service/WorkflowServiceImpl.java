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
import com.logicaldoc.workflow.editor.WorkflowPersistenceTemplateDAO.WORKFLOW_STAGE;
import com.logicaldoc.workflow.model.WorkflowTemplate;
import com.logicaldoc.workflow.transform.WorkflowTransformService;

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
		WorkflowPersistenceTemplateDAO dao = (WorkflowPersistenceTemplateDAO) Context.getInstance().getBean(
				WorkflowPersistenceTemplateDAO.class);
		WorkflowPersistenceTemplate workflow = dao.findById(workflowId);
		if (workflow != null) {
			dao.initialize(workflow);

			WorkflowTemplate workflowTemplate = null;
			WorkflowTransformService workflowTransformService = (WorkflowTransformService) Context.getInstance()
					.getBean("workflowTransformService");

			GUIWorkflow wfl = new GUIWorkflow();
			wfl.setId(workflowId);
			wfl.setName(workflow.getName());
			wfl.setDescription(workflow.getDescription());
			if (workflow.getXmldata() != null && ((String) workflow.getXmldata()).getBytes().length > 0) {
				workflowTemplate = workflowTransformService.fromWorkflowDefinitionToObject(workflow);

				wfl.setTaskAssignmentSubject(workflowTemplate.getAssignmentMessage().getSubject());
				wfl.setTaskAssignmentBody(workflowTemplate.getAssignmentMessage().getBody());
				wfl.setReminderSubject(workflowTemplate.getReminderMessage().getSubject());
				wfl.setReminderBody(workflowTemplate.getReminderMessage().getBody());
				UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
				User supervisorUser = userDao.findByUserName(workflowTemplate.getSupervisor());
				SecurityServiceImpl securityService = new SecurityServiceImpl();
				wfl.setSupervisor(securityService.getUser(sid, supervisorUser.getId()));
				wfl.setStartState(workflowTemplate.getStartState());
			}

			// TODO Manage the GUIWFStates of the GUIWorkflow

			return wfl;
		}
		return null;
	}

	@Override
	public void delete(String sid, long workflowId) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		WorkflowPersistenceTemplateDAO dao = (WorkflowPersistenceTemplateDAO) Context.getInstance().getBean(
				WorkflowPersistenceTemplateDAO.class);
		dao.delete(workflowId);
	}

	@Override
	public GUIWorkflow save(String sid, GUIWorkflow workflow) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		WorkflowPersistenceTemplateDAO dao = (WorkflowPersistenceTemplateDAO) Context.getInstance().getBean(
				WorkflowPersistenceTemplateDAO.class);
		WorkflowPersistenceTemplate wfl = dao.findById(workflow.getId());
		dao.save(wfl, WORKFLOW_STAGE.SAVED);

		return workflow;
	}

	@Override
	public void deploy(String sid, long workflowId) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		WorkflowPersistenceTemplateDAO dao = (WorkflowPersistenceTemplateDAO) Context.getInstance().getBean(
				WorkflowPersistenceTemplateDAO.class);
		WorkflowPersistenceTemplate wfl = dao.findById(workflowId);
		dao.deploy(wfl);
	}

	@Override
	public GUIWorkflow[] list(String sid) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		WorkflowPersistenceTemplateDAO dao = (WorkflowPersistenceTemplateDAO) Context.getInstance().getBean(
				WorkflowPersistenceTemplateDAO.class);
		GUIWorkflow[] workflows = new GUIWorkflow[dao.findAll().size()];
		int i = 0;
		for (WorkflowPersistenceTemplate workflow : dao.findAll()) {
			workflows[i] = get(sid, workflow.getId());
			i++;
		}

		return workflows;
	}
}
