package com.logicaldoc.workflow.model;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import com.logicaldoc.util.Context;
import com.logicaldoc.web.document.DocumentRecord;
import com.logicaldoc.web.document.DocumentsRecordsManager;
import com.logicaldoc.workflow.TemplateService;
import com.logicaldoc.workflow.WorkflowConstants;
import com.logicaldoc.workflow.editor.WorkflowPersistenceTemplateDAO;
import com.logicaldoc.workflow.transform.WorkflowTransformService;

public class WorkflowInstanceInfo extends WorkflowInstance {

	@SuppressWarnings("unchecked")
	public WorkflowInstanceInfo(WorkflowInstance workflowInstance) {
		super(workflowInstance);
		
		WorkflowPersistenceTemplateDAO workflowTemplateDao = (WorkflowPersistenceTemplateDAO) Context
		.getInstance().getBean(WorkflowPersistenceTemplateDAO.class);
		workflowTemplateDao.fixConversionField();

		WorkflowTransformService workflowTransformService = (WorkflowTransformService) Context.getInstance().getBean(
				"workflowTransformService");

		TemplateService templateService = (TemplateService) Context.getInstance().getBean("templateService");

		Set<Long> documentSet = (Set<Long>) getProperties().get(WorkflowConstants.VAR_DOCUMENTS);

		Set<DocumentRecord> documents = new LinkedHashSet<DocumentRecord>();

		for (Long documentId : documentSet)
			documents.add(new DocumentRecord(documentId, DocumentsRecordsManager.CHILD_INDENT_STYLE_CLASS,
					DocumentsRecordsManager.CHILD_ROW_STYLE_CLASS));

		WorkflowTemplate workflowTemplate = workflowTransformService
				.retrieveWorkflowModels((Serializable) getProperties().get(WorkflowConstants.VAR_TEMPLATE));

		getProperties().put(WorkflowConstants.VAR_DOCUMENTS, documents);
		getProperties().put(WorkflowConstants.VAR_TEMPLATE, workflowTemplate);

		String description = templateService.transformWorkflowInstance(this, workflowTemplate, workflowTemplate
				.getDescription());
		getProperties().put(WorkflowConstants.VAR_DESCRIPTION, description);

		this.setName(workflowTemplate.getName());
	}
}
