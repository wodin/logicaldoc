package com.logicaldoc.workflow;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.workflow.editor.model.WorkflowTask;
import com.logicaldoc.workflow.model.FetchModel.FETCH_TYPE;
import com.logicaldoc.workflow.model.WorkflowInstance;
import com.logicaldoc.workflow.model.WorkflowTaskInstance;
import com.logicaldoc.workflow.model.WorkflowTemplate;
import com.logicaldoc.workflow.model.script.UserScriptObject;

import freemarker.template.Configuration;
import freemarker.template.Template;

@SuppressWarnings("unchecked")
public class FreemarkerTemplateService implements TemplateService {

	protected static Log log = LogFactory.getLog(FreemarkerTemplateService.class);

	private UserDAO userDAO;

	private WorkflowService workflowService;

	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public String transformToString(String text, Map<String, Object> modelProperties) {

		Template template = null;

		try {
			template = new Template(UUID.randomUUID().toString() + ".ftl", text != null ? new StringReader(text)
					: new StringReader(""), new Configuration());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		StringWriter sw = new StringWriter();
		try {
			template.process(modelProperties, sw);
			return sw.getBuffer().toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public String transformWorkflowInstance(WorkflowInstance workflowInstance, WorkflowTemplate workflowDefinition,
			String msg) {
		HashMap modelProperties = new HashMap();
		mapWorkflowDocumentsToWorkflowModel(modelProperties, workflowInstance);

		return this.transformToString(msg, modelProperties);
	}

	public String transformWorkflowTask(WorkflowTask workflowTask, WorkflowInstance workflowInstance,
			WorkflowTaskInstance workflowTaskInstance, String msg) {

		HashMap modelProperties = new HashMap();

		// assignee are set as multiple since we can not decide at definition
		// times how many persons can colaborate on that task
		Object assignee = workflowTaskInstance.getProperties().get(WorkflowConstants.VAR_OWNER);
		List<String> assignees = new LinkedList<String>();

		if (assignee != null)
			assignees.add(assignee.toString());
		else
			assignees = (List<String>) workflowTaskInstance.getProperties().get(WorkflowConstants.VAR_POOLEDACTORS);

		String txt_assignee = "";
		String txt_assignee_rev = "";

		List<UserScriptObject> users = new LinkedList<UserScriptObject>();

		for (int i = 0; i < assignees.size(); i++) {
			User _user = userDAO.findByUserName(assignees.get(i));
			if (_user == null)
				continue;
			txt_assignee += _user.getFirstName() + " " + _user.getName();
			txt_assignee_rev += _user.getName() + " " + _user.getFirstName();

			if (i != assignees.size() - 1)
				txt_assignee += ", ";

			users.add(new UserScriptObject(_user));

		}

		modelProperties.put("assignee", txt_assignee);
		modelProperties.put("assignee_rev", txt_assignee_rev);

		modelProperties.put("assignee_objectlist", users);

		modelProperties.put("taskname", workflowTask.getName());

		mapWorkflowDocumentsToWorkflowModel(modelProperties, workflowInstance);

		return this.transformToString(msg, modelProperties);

	}

	private void mapWorkflowDocumentsToWorkflowModel(Map modelProperties, WorkflowInstance workflowInstance) {
		Set<DocumentRecord> documentSet = (Set<DocumentRecord>) workflowInstance.getProperties().get(
				WorkflowConstants.VAR_DOCUMENTS);
		DocumentRecord[] documentRecords = documentSet.toArray(new DocumentRecord[] {});

		String txt_doclist = "";

		for (int i = 0; i < documentRecords.length; i++) {
			txt_doclist += documentRecords[i].getDisplayFilename();

			if (i != documentRecords.length - 1)
				txt_doclist += ", ";

		}

		modelProperties.put("documents", txt_doclist);
		modelProperties.put("documents_objectlist", documentSet);
	}

	public String transformWorkflowTask(WorkflowTask workflowTask, WorkflowTaskInstance workflowTaskInstance) {
		WorkflowInstance workflowInstance = this.workflowService.getWorkflowInstanceByTaskInstance(
				workflowTaskInstance.getId(), FETCH_TYPE.INFO);
		return this.transformWorkflowTask(workflowTask, workflowInstance, workflowTaskInstance,
				workflowTask.getDescription());
	}

	public static void main(String[] args) {
		TemplateService templateService = new FreemarkerTemplateService();
		HashMap test = new HashMap();

		User user = new User();
		user.setName("christoph");
		UserScriptObject uso = new UserScriptObject(user);

		test.put("user", uso);
		List<String> tesa = new LinkedList<String>();
		tesa.add("sdsada");
		tesa.add("gold");
		test.put("test", tesa);
		log.info(templateService.transformToString("hello <#list test as x>" + "${x}<#if x_has_next>,</#if></#list>",
				test));
	}

}
