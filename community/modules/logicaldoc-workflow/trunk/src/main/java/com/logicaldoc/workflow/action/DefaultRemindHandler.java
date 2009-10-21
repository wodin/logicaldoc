package com.logicaldoc.workflow.action;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbpm.graph.exe.ExecutionContext;

import com.logicaldoc.core.communication.EMail;
import com.logicaldoc.core.communication.EMailSender;
import com.logicaldoc.core.communication.Recipient;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.workflow.TemplateService;
import com.logicaldoc.workflow.WorkflowConstants;
import com.logicaldoc.workflow.WorkflowFactory;
import com.logicaldoc.workflow.editor.model.WorkflowTask;
import com.logicaldoc.workflow.model.WorkflowInstanceInfo;
import com.logicaldoc.workflow.model.WorkflowTaskInstanceInfo;
import com.logicaldoc.workflow.model.WorkflowTemplate;

public class DefaultRemindHandler extends BaseEventAction {

	private EMailSender mailSender;

	private DocumentDAO documentDAO;

	private UserDAO userDAO;

	private TemplateService templateService;

	@Override
	public void init() {
		this.mailSender = (EMailSender) Context.getInstance().getBean(
				"EMailSender");
		this.userDAO = (UserDAO) Context.getInstance().getBean("UserDAO");
		this.documentDAO = (DocumentDAO) Context.getInstance().getBean(
				"DocumentDAO");
		this.templateService = (TemplateService) Context.getInstance().getBean(
				"templateService");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8776461973750383870L;

	@SuppressWarnings("unchecked")
	@Override
	public void executeImpl(ExecutionContext executionContext) {

		WorkflowTemplate workflowTemplate = obtainWorkflowTemplateFromWorkflow(executionContext);
		WorkflowTask workflowTask = (WorkflowTask) workflowTemplate
				.getWorkflowComponentById(executionContext.getNode().getName());
		Set<Long> documentRecords = (Set<Long>) executionContext
				.getVariable(WorkflowConstants.VAR_DOCUMENTS);

		List<Document> documents = new LinkedList<Document>();

		for (Long docId : documentRecords)
			documents.add(documentDAO.findById(docId));

		User assignee = userDAO.findByUserName(executionContext
				.getTaskInstance().getActorId());

		boolean userExist = true;

		if (assignee == null) {
			userExist = false;
			assignee = userDAO.findByUserName("admin");
		}

		EMail eMail = new EMail();

		Map<String, Object> modelProperties = new HashMap<String, Object>();
		modelProperties.put("documents", documents);

		Set<Recipient> recipients = new LinkedHashSet<Recipient>();
		Recipient ad = new Recipient();
		ad.setAddress(assignee.getEmail());
		recipients.add(ad);

		String subject = workflowTemplate.getReminderMessage().getSubject();

		if (subject == null
				|| (subject != null && subject.trim().length() == 0))
			subject = "No subect specified";

		if (userExist == false)
			subject = subject + " [WARN=User"
					+ executionContext.getTaskInstance().getActorId()
					+ " can not be found]";

		String bodyText = templateService
				.transformWorkflowTask(workflowTask, new WorkflowInstanceInfo(
						WorkflowFactory.createWorkflowInstance(executionContext
								.getProcessInstance())),
						new WorkflowTaskInstanceInfo(WorkflowFactory
								.createTaskInstance(executionContext
										.getTaskInstance())), workflowTemplate
								.getReminderMessage().getBody());

		eMail.setSubject(subject);
		eMail.setRecipients(recipients);
		eMail.setMessageText(subject);
		eMail.setAuthor("[BPM] - " + workflowTemplate.getName());
		eMail.setSentDate(new Date());

		eMail.setMessageText(bodyText);

		try {
			// System.out.println(eMail.getMessageText());
			mailSender.send(eMail);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
