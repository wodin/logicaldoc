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
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.document.Directory;
import com.logicaldoc.web.document.DirectoryTreeModel;
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
import com.logicaldoc.workflow.model.FetchModel.FETCH_TYPE;
import com.logicaldoc.workflow.transform.WorkflowTransformService;

public class WorkflowManager {
	protected static Log log = LogFactory.getLog(WorkflowManager.class);

	private WorkflowService workflowService;

	private List<WorkflowTaskInstance> taskHistory;
	
	private WorkflowInstance workflowInstance;
	
	private WorkflowTaskInstance workflowTaskInstance;
	
	private WorkflowTaskInstance workingWorkflowTaskinstance;
	
	private WorkflowInstance workingWorkflowInstance;
	private NavigationBean navigationBean;

	private DirectoryTreeModel directoryModel;
	
	private Directory selectedResourceFolder;
	
	private DocumentDAO documentDAO;
	
	private boolean showFolderSelector = false;
	
	
	public WorkflowManager() {
		this.workflowService = (WorkflowService) Context.getInstance().getBean(
				"workflowService");
		
		this.navigationBean = (NavigationBean) FacesUtil
		.accessBeanFromFacesContext("navigation", FacesContext
				.getCurrentInstance(), log);
		
		this.documentDAO = (DocumentDAO) FacesUtil
		.accessBeanFromFacesContext("DocumentDAO", FacesContext
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
		this.workflowService.signal(instance.getId());
	}

	public void endTask(ActionEvent actionEvent){
		
		saveState();
		
		UIComponent component = (UIComponent)actionEvent.getSource();
		Transition transition = (Transition)((UIParameter)component.getChildren().get(1)).getValue();
		
		this.workflowService.endTask(this.workflowTaskInstance.getId(), transition.getName());
		
		setupTaskPage(this.workflowTaskInstance.getId());
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
		
		
		this.workflowService.assign(this.workflowTaskInstance.getId(), username);
		
		this.workflowTaskInstance = this.workflowService.getTaskInstanceByTaskId(this.workflowTaskInstance.getId(), FETCH_TYPE.FORUPDATE);
	}
	
	public List<WorkflowTaskInstance> getPooledTaskInstances(){
		HttpSession session = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String username = (String)session.getAttribute(Constants.AUTH_USERNAME);
		return this.workflowService.getPooledTaskInstancesForUser(username);
	}
	
	public void showWorkflowHistoryHistory(ActionEvent actionEvent){
		UIComponent component = (UIComponent)actionEvent.getSource();
		WorkflowTaskInstance workflowTaskInstance = (WorkflowTaskInstance)((UIParameter)component.getChildren().get(0)).getValue();
		WorkflowInstance instance  = this.workflowService.getWorkflowInstanceByTaskInstance(workflowTaskInstance.getId(), FETCH_TYPE.FORUPDATE);
		
		this.taskHistory = this.workflowService.getWorkflowHistory(instance);
	}
	
	public List<WorkflowTaskInstance> getWorkflowHistory(){
		return this.taskHistory = this.workflowService.getWorkflowHistory(this.workflowInstance);
	}
	
	public String showTaskDetails(ActionEvent actionEvent){
		
		this.selectedResourceFolder = null;
		this.showFolderSelector = false;
		this.directoryModel = null;
		
		HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
		//TODO: TaskId should be obtained instead of this very strange logic
		WorkflowTaskInstance taskInstance = (WorkflowTaskInstance)request.getAttribute("taskInstance");
	
		this.setupTaskPage(taskInstance.getId());
		
		PageContentBean contentBean = new PageContentBean();
		contentBean.setTemplate("workflow/workflow-view");
		contentBean.setPageContent(true);
		
		this.navigationBean.setSelectedPanel(contentBean);
		
		return null;
	}
	
	public void setupTaskPage(String taskid){
		this.workingWorkflowTaskinstance = this.workflowService.getTaskInstanceByTaskId(taskid, FETCH_TYPE.FORUPDATE);
		this.workflowTaskInstance = this.workflowService.getTaskInstanceByTaskId(taskid, FETCH_TYPE.INFO);
		this.workingWorkflowInstance = this.workflowService.getWorkflowInstanceByTaskInstance(taskid, FETCH_TYPE.FORUPDATE );
		this.workflowInstance = this.workflowService.getWorkflowInstanceByTaskInstance( taskid, FETCH_TYPE.INFO );
		
	}
	
	public WorkflowInstance getSelectedWorkflowInstance() {
		return workflowInstance;
	}
	
	public WorkflowTaskInstance getSelectedWorkflowTaskInstance() {
		return workflowTaskInstance;
	}
	
	public void turnBackToPool(ActionEvent actionEvent){
		this.workflowTaskInstance.getProperties().put(WorkflowConstants.VAR_OWNER, null);
		this.workflowService.updateWorkflow(workflowTaskInstance);
		
		this.workflowTaskInstance = this.workflowService.getTaskInstanceByTaskId(this.workflowTaskInstance.getId(), FETCH_TYPE.FORUPDATE);
	
	}
	
	public boolean isShowFolderSelector() {
		return showFolderSelector;
	}

	public void setShowFolderSelector(boolean showFolderSelector) {
		this.showFolderSelector = showFolderSelector;
	}
	
	public void openFolderSelector(ActionEvent e) {
		showFolderSelector = true;
	}

	public void closeFolderSelector(ActionEvent e) {
		showFolderSelector = false;
	}
	
	public void folderSelected(ActionEvent e) {
		this.selectedResourceFolder = getDirectoryModel().getSelectedDir();
		showFolderSelector = false;
	}
	
	public DirectoryTreeModel getDirectoryModel() {
		if (directoryModel == null) {
			loadTree();
		}
		return directoryModel;
	}
	
	void loadTree() {
		directoryModel = new DirectoryTreeModel();
	}

	public void cancelFolderSelector(ActionEvent e) {
		directoryModel.cancelSelection();
		showFolderSelector = false;
	}
	
	public Directory getSelectedResourceFolder() {
		return selectedResourceFolder;
	}
	
	public List<DocumentRecord> getSelectedFolderItems(){
		List<DocumentRecord> records = new LinkedList<DocumentRecord>();
		
		if(this.selectedResourceFolder == null)
			return records;
		
		List<Document> documents = this.documentDAO.findByFolder(this.selectedResourceFolder.getMenuId());
		
		
		
		for(Document document : documents) {
			
			boolean documentAlreadyAppended = false;
			
			//do not append already added documents
			for(DocumentRecord wfDoc : (Set<DocumentRecord>)this.workflowInstance.getProperties().get(WorkflowConstants.VAR_DOCUMENTS)){
				if(document.getId() == wfDoc.getDocId()){
					documentAlreadyAppended = true;
					break;
				}
			}
			
			if(documentAlreadyAppended)
				continue;
			
			records.add(new DocumentRecord(document.getId(), null, DocumentsRecordsManager.CHILD_INDENT_STYLE_CLASS, 
					DocumentsRecordsManager.CHILD_ROW_STYLE_CLASS) );
			
		}
		
		return records;
		
	}
	
	public void appendDocument(ActionEvent actionEvent){
		UIComponent component = (UIComponent)actionEvent.getSource();
		DocumentRecord selectedDocumentRecord = (DocumentRecord)((UIParameter)component.getChildren().get(0)).getValue();
		
		Set<Long> docIds = (Set<Long>) this.workingWorkflowInstance.getProperties().get(WorkflowConstants.VAR_DOCUMENTS);
		docIds.add( selectedDocumentRecord.getDocId() );
		
		Set<DocumentRecord> records = (Set<DocumentRecord>)this.workflowInstance.getProperties().get(WorkflowConstants.VAR_DOCUMENTS);
		
		records.add( selectedDocumentRecord );
		
	}
	
	public List<WorkflowTaskInstance> getSuspendedTaskInstances(){
		HttpSession session = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String username = (String)session.getAttribute(Constants.AUTH_USERNAME);
		return this.workflowService.getSuspendedTaskInstancesForUser(username);
	}
	
	public String saveState(){
		
		this.workflowService.updateWorkflow(this.workingWorkflowInstance);
		this.workflowService.updateWorkflow(this.workingWorkflowTaskinstance);
		
		this.setupTaskPage(workingWorkflowTaskinstance.getId());
		
		return null;
	}
	
	public void startTask(){
		this.workflowTaskInstance.getProperties().put(WorkflowConstants.VAR_TASKSTATE, WorkflowTaskInstance.STATE.STARTED.getVal());
		this.workflowService.updateWorkflow(this.workflowTaskInstance);
		
	}
	
	public void resumeTask(){
		this.workflowTaskInstance.getProperties().put(WorkflowConstants.VAR_TASKSTATE, WorkflowTaskInstance.STATE.STARTED.getVal());
		this.workflowService.updateWorkflow(workflowTaskInstance);

	}
	
	public void suspendTask(){
		this.workflowTaskInstance.getProperties().put(WorkflowConstants.VAR_TASKSTATE, WorkflowTaskInstance.STATE.SUSPENDED.getVal());
		this.workflowService.updateWorkflow(workflowTaskInstance);
	}
	
	public WorkflowInstance getWorkingWorkflowInstance() {
		return workingWorkflowInstance;
	}
	
	public WorkflowTaskInstance getWorkingWorkflowTaskinstance() {
		return workingWorkflowTaskinstance;
	}
	
}
