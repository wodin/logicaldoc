package com.logicaldoc.workflow;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icesoft.faces.component.ext.RowSelectorEvent;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.document.DocumentRecord;
import com.logicaldoc.web.document.DocumentsRecordsManager;
import com.logicaldoc.web.navigation.NavigationBean;
import com.logicaldoc.web.navigation.PageContentBean;
import com.logicaldoc.web.util.Constants;
import com.logicaldoc.web.util.FacesUtil;
import com.logicaldoc.workflow.model.Transition;
import com.logicaldoc.workflow.model.WorkflowDefinition;
import com.logicaldoc.workflow.model.WorkflowInstance;
import com.logicaldoc.workflow.model.WorkflowTaskInstance;

public class WorkflowManager {
	protected static Log log = LogFactory.getLog(WorkflowManager.class);

	private WorkflowService workflowService;

	private List<WorkflowTaskInstance> taskHistory;
	
	private WorkflowInstance workflowInstance;
	
	private WorkflowTaskInstance workflowTaskInstance;
	
	private List<DocumentRecord> workflowDocuments;
	
	private NavigationBean navigationBean;

	public WorkflowManager() {
		this.workflowService = (WorkflowService) Context.getInstance().getBean(
				"workflowService");
		
		this.navigationBean = (NavigationBean) FacesUtil
		.accessBeanFromFacesContext("navigation", FacesContext
				.getCurrentInstance(), log);
	}

	public List<WorkflowDefinition> getWorkflowDefinitions() {
		return this.workflowService.getAllDefinitions();
	}

	public void rowSelectionListener(RowSelectorEvent event) {
		WorkflowDefinition workflowDefinition = this.workflowService
				.getAllDefinitions().get(event.getRow());

		WorkflowInstance instance = this.workflowService
				.startWorkflow(workflowDefinition, null);
		this.workflowService.signal(instance.id);
	}

	public void endTask(ActionEvent actionEvent){
		UIComponent component = (UIComponent)actionEvent.getSource();
		Transition transition = (Transition)((UIParameter)component.getChildren().get(1)).getValue();
		
		this.workflowService.endTask(this.workflowTaskInstance.id, transition.name);
		
		this.workflowTaskInstance = this.workflowService.getTaskInstanceByTaskId(workflowTaskInstance.id);
		
	}
	
	public List<WorkflowTaskInstance> getTaskInstances(){
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(false);
		
		String username = (String)session.getAttribute(Constants.AUTH_USERNAME);

		return this.workflowService.getTaskInstancesForUser(username);
		
	}
	
	public void assign(ActionEvent actionEvent){
		HttpSession session = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String username = (String)session.getAttribute(Constants.AUTH_USERNAME);
		
		
		this.workflowService.assign(this.workflowTaskInstance.id, username);
		
		this.workflowTaskInstance = this.workflowService.getTaskInstanceByTaskId(this.workflowTaskInstance.id);
	}
	
	public List<WorkflowTaskInstance> getPooledTaskInstances(){
		HttpSession session = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String username = (String)session.getAttribute(Constants.AUTH_USERNAME);
		return this.workflowService.getPooledTaskInstancesForUser(username);
	}
	
	public void showWorkflowHistoryHistory(ActionEvent actionEvent){
		UIComponent component = (UIComponent)actionEvent.getSource();
		WorkflowTaskInstance workflowTaskInstance = (WorkflowTaskInstance)((UIParameter)component.getChildren().get(0)).getValue();
		WorkflowInstance instance  = this.workflowService.getWorkflowInstanceByTaskInstance(workflowTaskInstance.id);
		
		this.taskHistory = this.workflowService.getWorkflowHistory(instance);
	}
	
	public List<WorkflowTaskInstance> getWorkflowHistory(){
		return this.taskHistory = this.workflowService.getWorkflowHistory(this.workflowInstance);
	}
	
	public String showTaskDetails(ActionEvent actionEvent){
		
		HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
		//TODO: TaskId should be obtained instead of this very strange logic
		WorkflowTaskInstance taskInstance = (WorkflowTaskInstance)request.getAttribute("taskInstance");
	
		
		this.workflowDocuments = new LinkedList<DocumentRecord>();
		
		this.workflowTaskInstance = taskInstance;
		this.workflowInstance = this.workflowService.getWorkflowInstanceByTaskInstance( workflowTaskInstance.id );
		
		Set<Long> documents = (Set<Long>)this.workflowInstance.properties.get(WorkflowConstants.VAR_DOCUMENTS);
		
		for(Long documentId : documents)
			this.workflowDocuments.add( 
					new DocumentRecord(
							documentId, null, DocumentsRecordsManager.CHILD_INDENT_STYLE_CLASS, 
							DocumentsRecordsManager.CHILD_ROW_STYLE_CLASS
						));
	
		
		
		PageContentBean contentBean = new PageContentBean();
		contentBean.setTemplate("workflow/workflow-view");
		contentBean.setPageContent(true);
		
		this.navigationBean.setSelectedPanel(contentBean);
		
		return null;
	}
	
	public WorkflowInstance getSelectedWorkflowInstance() {
		return workflowInstance;
	}
	
	public WorkflowTaskInstance getSelectedWorkflowTaskInstance() {
		return workflowTaskInstance;
	}
	
	
	public List<DocumentRecord> getWorkflowDocuments() {
		return this.workflowDocuments;
	}
	
	public void turnBackToPool(ActionEvent actionEvent){
		this.workflowTaskInstance.properties.put(WorkflowConstants.VAR_OWNER, null);
		this.workflowService.updateTaskInstace(workflowTaskInstance);
		
		this.workflowTaskInstance = this.workflowService.getTaskInstanceByTaskId(this.workflowTaskInstance.id);
	
	}
}
