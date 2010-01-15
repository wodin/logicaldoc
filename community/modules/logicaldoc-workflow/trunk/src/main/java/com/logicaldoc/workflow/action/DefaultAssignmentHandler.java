package com.logicaldoc.workflow.action;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ExecutionContext;

import com.logicaldoc.core.communication.Recipient;
import com.logicaldoc.core.communication.SystemMessage;
import com.logicaldoc.core.communication.dao.SystemMessageDAO;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.workflow.TemplateService;
import com.logicaldoc.workflow.WorkflowConstants;
import com.logicaldoc.workflow.WorkflowFactory;
import com.logicaldoc.workflow.WorkflowUtil;
import com.logicaldoc.workflow.editor.model.WorkflowTask;
import com.logicaldoc.workflow.model.WorkflowInstanceInfo;
import com.logicaldoc.workflow.model.WorkflowTaskInstanceInfo;
import com.logicaldoc.workflow.model.WorkflowTemplate;
import com.logicaldoc.workflow.model.script.UserScriptObject;

/**
 * Default assignment handler that notifies assignments to users using a system
 * message.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 5.0
 */
public class DefaultAssignmentHandler extends AbstractAssignmentHandler {
	private static final long serialVersionUID = -2251308861234738682L;

	protected static Log log = LogFactory.getLog(DefaultAssignmentHandler.class);

	@Override
	public void init() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void executeImpl(List<String> assignees, ExecutionContext executionContext) {

		WorkflowTemplate workflowTemplate = getWorkflowTransformService().retrieveWorkflowModels(
				(Serializable) executionContext.getVariable(WorkflowConstants.VAR_TEMPLATE));

		WorkflowTask workflowTask = WorkflowUtil.getWorkflowTaskById(executionContext.getNode().getName(),
				workflowTemplate.getWorkflowComponents());

		UserDAO userDAO = (UserDAO) Context.getInstance().getBean(UserDAO.class);

		DocumentDAO documentDAO = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);

		SystemMessageDAO systemMessageDAO = (SystemMessageDAO) Context.getInstance().getBean(SystemMessageDAO.class);

		Set<Long> documentRecords = (Set<Long>) executionContext.getVariable(WorkflowConstants.VAR_DOCUMENTS);

		List<Document> documents = new LinkedList<Document>();

		for (Long docId : documentRecords)
			documents.add(documentDAO.findById(docId));

		TemplateService templateService = (TemplateService) Context.getInstance().getBean("templateService");

		Map<String, Object> modelProperties = new HashMap<String, Object>();
		modelProperties.put("documents", documents);

		LinkedHashSet<Recipient> addresses = new LinkedHashSet<Recipient>();
		for (String assignee : assignees) {
			Recipient ad = new Recipient();

			User user = userDAO.findByUserName(assignee);

			ad.setAddress(user.getEmail());
			addresses.add(ad);

			preparteTemplateModelWithRecipient(user, modelProperties);

			String assignmentText = templateService
					.transformWorkflowTask(workflowTask, new WorkflowInstanceInfo(WorkflowFactory
							.createWorkflowInstance(executionContext.getProcessInstance())),
							new WorkflowTaskInstanceInfo(WorkflowFactory.createTaskInstance(executionContext
									.getTaskInstance())), workflowTemplate.getAssignmentMessage().getBody());
			String subject = workflowTemplate.getAssignmentMessage().getSubject();
			if (subject == null || (subject != null && subject.trim().length() == 0))
				subject = "No subect specified";

			Recipient recipient = new Recipient();
			Set<Recipient> recipients = new HashSet<Recipient>();
			recipient.setName(user.getUserName());
			recipient.setAddress(user.getUserName());
			recipient.setType(SystemMessage.TYPE_SYSTEM);
			recipients.add(recipient);

			SystemMessage message = new SystemMessage();
			message.setAuthor("[BPM] - " + workflowTemplate.getName());
			message.setMessageText(assignmentText);
			message.setSentDate(new Date());
			message.setSubject(subject);
			message.setRecipients(recipients);

			systemMessageDAO.store(message);

			notifySupervisor(workflowTemplate, assignmentText, subject);
		}
	}

	private void notifySupervisor(WorkflowTemplate workflowTemplate, String assignmentText, String subject) {
		UserDAO userDAO = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		SystemMessageDAO systemMessageDAO = (SystemMessageDAO) Context.getInstance().getBean(SystemMessageDAO.class);

		// Add the supervisor/s
		Recipient supervisorRecipient = null;
		Set<Recipient> supervisorRecipients = new HashSet<Recipient>();
		// Checks if the supervisor is a user
		User supervisorUser = userDAO.findByUserName(workflowTemplate.getSupervisor());
		Locale supervisorLocale = Locale.getDefault();
		if (supervisorUser != null) {
			supervisorRecipient = new Recipient();
			supervisorRecipient.setName(supervisorUser.getUserName());
			supervisorRecipient.setAddress(supervisorUser.getUserName());
			supervisorRecipient.setType(SystemMessage.TYPE_SYSTEM);
			supervisorRecipients.add(supervisorRecipient);
			supervisorLocale = supervisorUser.getLocale();
		} else {
			// Checks if the supervisor is a group
			GroupDAO groupDAO = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
			Group supervisorGroup = groupDAO.findByName(workflowTemplate.getSupervisor());
			if (supervisorGroup != null) {
				for (User supervisorGroupUser : supervisorGroup.getUsers()) {
					supervisorRecipient = new Recipient();
					supervisorRecipient.setName(supervisorGroupUser.getUserName());
					supervisorRecipient.setAddress(supervisorGroupUser.getUserName());
					supervisorRecipient.setType(SystemMessage.TYPE_SYSTEM);
					supervisorRecipients.add(supervisorRecipient);
				}
			}
		}

		SystemMessage supervisorMessage = new SystemMessage();
		supervisorMessage.setAuthor("[BPM] - " + workflowTemplate.getName());
		StringBuffer messageText = new StringBuffer();
		messageText.append(Messages.getMessage("workflow.editor_settings_supervisors_message", supervisorLocale,
				new Object[] { workflowTemplate.getName() }));
		messageText.append("\n");
		supervisorMessage.setMessageText(messageText.toString() + assignmentText);
		supervisorMessage.setSentDate(new Date());
		supervisorMessage.setSubject(subject);
		supervisorMessage.setRecipients(supervisorRecipients);

		systemMessageDAO.store(supervisorMessage);
	}

	private void preparteTemplateModelWithRecipient(User user, Map<String, Object> modelProperties) {
		UserScriptObject userObject = new UserScriptObject(user);
		modelProperties.put("user", userObject);
	}
}