package com.logicaldoc.workflow;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Hibernate;

import com.logicaldoc.util.Context;
import com.logicaldoc.workflow.editor.WorkflowTemplateLoader;
import com.logicaldoc.workflow.persistence.WorkflowPersistenceTemplate;

@SuppressWarnings({ "serial" })
public class TestServlet extends HttpServlet{
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		WorkflowTemplateLoader templateLoader = (WorkflowTemplateLoader)Context.getInstance().getBean("WorkflowTemplateLoader");
		templateLoader.getAvailableWorkflowTemplates();
		
		WorkflowPersistenceTemplate template = new WorkflowPersistenceTemplate();
		template.setDeployed(false);
		template.setDescription("This workflow introduced the whole review-process in our company!");
		template.setName("Catalloque-Review");
		
		String exampleText ="This is the string which is transformed into blob data to test blob-based columns within hibernate";
		Blob blob = Hibernate.createBlob(exampleText.getBytes());
		template.setXmldata(blob);
		
		//templateLoader.saveWorkflowTemplate(template);
		
		WorkflowPersistenceTemplate loadWorkflowTemplate = templateLoader.loadWorkflowTemplate((long)3);
		Blob xmldata = loadWorkflowTemplate.getXmldata();
		
		try {
			InputStream content = xmldata.getBinaryStream();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			int read = 0;
			byte[] buf = new byte[4192];
			
			while((read = content.read(buf)) != -1)
				bos.write(buf, 0, read);
			
			
		
			byte[] ba = bos.toByteArray();
			String string = new String(ba);
			
		}
		catch(Exception e){
			throw new RuntimeException(e);
		}
		
		/*
		String s =""; 
		 
		WorkflowService wfs = (WorkflowService)Context.getInstance().getBean("workflowService");
		
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
			      "<process-definition name='the baby process'>" +
			      "  <start-state>" +
			      "    <transition name='baby cries' to='t' />" +
			      "  </start-state>" +
			      "  <task-node name='t'>" +
			      "    <task name='change nappy'>" +
			      "   " +
			      "    </task>" +
			      "    <transition to='end' />" +
			      "  </task-node>" +
			      "  <end-state name='end' />" +
			      "</process-definition>"
			    );
		

		
		List<WorkflowTaskInstance> taskInstances = wfs.getAllActiveTaskInstances();
		
		//wfs.deployWorkflow(processDefinition);
		WorkflowInstance workflowInstance = wfs.startWorkflow(processDefinition.getName());
		
		wfs.signal(workflowInstance.id);
		*/
		resp.getWriter().println("All seems okay");
		
		
	}
}
