package com.logicaldoc.workflow.editor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.logicaldoc.workflow.persistence.WorkflowPersistenceTemplate;

public class WorkflowTemplateLoader extends HibernateDaoSupport{
	
	private String pluginDirectory;
	
	private String resolveSystemPath(WorkflowPersistenceTemplate template){
		return this.pluginDirectory + template.getName() + "-" + template.getId() + ".jbpm";
	}
	
	public void setPluginDirectory(String pluginDirectory) {
		this.pluginDirectory = pluginDirectory;
	}
	
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
		
		//save IO 
		
		File workflowFile = new File(resolveSystemPath(persistenceTemplate));
		try {
			DataOutputStream os = new DataOutputStream(new FileOutputStream(
			        workflowFile));
			os.write(((String)persistenceTemplate.getXmldata()).getBytes("UTF-8"));
			os.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return (Long)id;
		
	}
	
	public void deleteWorkflowTemplate(WorkflowPersistenceTemplate persistenceTemplate){
		
		getHibernateTemplate().delete(persistenceTemplate);
		
		File workflowFile = new File(resolveSystemPath(persistenceTemplate));
		try {
			workflowFile.delete();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	
	public WorkflowPersistenceTemplate loadWorkflowTemplate(Long id){
		WorkflowPersistenceTemplate workflowPersistenceTemplate = 
		(WorkflowPersistenceTemplate)getHibernateTemplate().load(WorkflowPersistenceTemplate.class, id);
		
		File workflowFile = new File(resolveSystemPath(workflowPersistenceTemplate));
		try {
			FileInputStream is = new FileInputStream(
			        workflowFile);
			
			int read = 0;
			byte[] buf = new byte[4192];
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream(buf.length);

			
			while ((read = is.read(buf)) != -1)
				 bos.write(buf, 0, (int) read); 
			 
			is.close();
			bos.close();
			 
			workflowPersistenceTemplate.setXmldata( bos.toString("UTF-8") );
			 
			return workflowPersistenceTemplate;
			 
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public WorkflowPersistenceTemplate loaWorkflowTemplate(WorkflowPersistenceTemplate workflowTemplate){
		return loadWorkflowTemplate(workflowTemplate.getId());
	}

}
