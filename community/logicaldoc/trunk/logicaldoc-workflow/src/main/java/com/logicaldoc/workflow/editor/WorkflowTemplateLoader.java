package com.logicaldoc.workflow.editor;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.logicaldoc.workflow.model.WorkflowDefinition;
import com.logicaldoc.workflow.persistence.WorkflowPersistenceTemplate;

public class WorkflowTemplateLoader extends HibernateDaoSupport{
	
	
	@SuppressWarnings("unchecked")
	public List<WorkflowPersistenceTemplate> getAvailableWorkflowTemplates(){
		
		List<WorkflowPersistenceTemplate> workflowTemplateList = getHibernateTemplate().find(" from WorkflowPersistenceTemplate");
		
		return workflowTemplateList;
	}
	
	public Long saveWorkflowTemplate(WorkflowPersistenceTemplate persistenceTemplate){
		
		Long id = null;
		Object ret = getHibernateTemplate().get(WorkflowPersistenceTemplate.class, persistenceTemplate.getId());
		
		if(ret == null)
			id = (Long)getHibernateTemplate().save(persistenceTemplate);
		else {
			id = persistenceTemplate.getId();
			getHibernateTemplate().update(persistenceTemplate);
		}
		
		return (Long)id;
		
	}
	
	public void deleteWorkflowTemplate(WorkflowPersistenceTemplate persistenceTemplate){
		getHibernateTemplate().delete(persistenceTemplate);
	}
	
	public WorkflowPersistenceTemplate loadWorkflowTemplate(Long id){
		return (WorkflowPersistenceTemplate)getHibernateTemplate().load(WorkflowPersistenceTemplate.class, id);
	}
	
	public WorkflowPersistenceTemplate loaWorkflowTemplate(WorkflowPersistenceTemplate workflowTemplate){
		return loadWorkflowTemplate(workflowTemplate.getId());
	}

}
