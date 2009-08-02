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
import com.logicaldoc.core.communication.SystemMessage;
import com.logicaldoc.core.communication.dao.SystemMessageDAO;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.workflow.TemplateService;
import com.logicaldoc.workflow.WorkflowConstants;
import com.logicaldoc.workflow.WorkflowService;
import com.logicaldoc.workflow.editor.model.BaseWorkflowModel;
import com.logicaldoc.workflow.editor.model.WorkflowTask;
import com.logicaldoc.workflow.model.WorkflowTemplate;
import com.logicaldoc.workflow.model.script.UserScriptObject;
import com.thoughtworks.xstream.XStream;

public class DefaultAssignmentHandler extends AbstractAssignmentHandler{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2251308861234738682L;

	@Override
	public void executeImpl(List<String> assignees, ExecutionContext executionContext) {
		
		EMailSender eMailer = (EMailSender) Context.getInstance().getBean(
				"DevEMailSender");
		
		WorkflowService workflowService = (WorkflowService) Context.getInstance().getBean("workflowService");
		
		
		UserDAO userDAO = (UserDAO) Context.getInstance().getBean("UserDAO");
		
		DocumentDAO documentDAO = (DocumentDAO) Context.getInstance().getBean("DocumentDAO");
		
		SystemMessageDAO systemMessageDAO = (SystemMessageDAO) Context.getInstance().getBean("SystemMessageDAO");
		
		XStream xStream = new XStream();
		
		WorkflowTemplate workflowTemplate = (WorkflowTemplate)xStream.fromXML((String)executionContext.getVariable(WorkflowConstants.VAR_TEMPLATE));

		Set<Long> documentRecords = (Set<Long>)executionContext.getVariable(WorkflowConstants.VAR_DOCUMENTS);
		
		List<Document> documents = new LinkedList<Document>();
		
		for(Long docId : documentRecords)
			documents.add( documentDAO.findById(docId) );
		
		
		TemplateService templateService = (TemplateService)Context.getInstance().getBean("templateService");
		
		EMail eMail = new EMail();
		
		Map<String, Object> modelProperties = new HashMap<String, Object>();
		modelProperties.put("documents", documents);
		for(String assignee : assignees){
			LinkedHashSet<Recipient> addresses = new LinkedHashSet<Recipient>();
			Recipient ad = new Recipient();
			ad.setAddress(assignee + "@logicaldoc.com");
			addresses.add( ad );
			
			User user = userDAO.findByUserName(assignee);
			
			preparteTemplateModelWithRecipient(user, modelProperties);
				
			String assignmentText = templateService.transformToString(workflowTemplate
					.getAssignmentMailMessage().getBody(), modelProperties);
			
			eMail.setMessageText(assignmentText);
			eMail.setRecipients( addresses );
			
			String subject =  workflowTemplate.getAssignmentMailMessage().getSubject();
			
			if(subject == null || (subject != null && subject.trim().length() == 0))
				subject = "No subect specified";
			
			eMail.setSubject( subject );
		
			SystemMessage message = new SystemMessage();
			message.setAuthor("[BPM] - " + workflowTemplate.getName());
			message.setRecipient(user.getUserName());
			message.setMessageText(assignmentText);
			message.setSentDate(new Date());
			message.setSubject(subject);
			
			systemMessageDAO.store(message);
		}
		
		
		
		
		
		
		try {
			eMailer.send(eMail);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	private void preparteTemplateModelWithRecipient(User user, Map<String, Object> modelProperties){
		UserScriptObject userObject = new UserScriptObject(user);
		modelProperties.put("user", userObject);
	}
}
