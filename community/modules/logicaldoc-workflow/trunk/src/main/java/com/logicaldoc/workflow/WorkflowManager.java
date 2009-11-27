package com.logicaldoc.workflow;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.document.Directory;
import com.logicaldoc.web.document.DirectoryTreeModel;
import com.logicaldoc.web.document.DocumentNavigation;
import com.logicaldoc.web.document.DocumentRecord;
import com.logicaldoc.web.document.DocumentsRecordsManager;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.navigation.NavigationBean;
import com.logicaldoc.web.navigation.PageContentBean;
import com.logicaldoc.web.util.Constants;
import com.logicaldoc.web.util.FacesUtil;
import com.logicaldoc.workflow.editor.controll.TaskController;
import com.logicaldoc.workflow.model.Transition;
import com.logicaldoc.workflow.model.WorkflowDefinition;
import com.logicaldoc.workflow.model.WorkflowInstance;
import com.logicaldoc.workflow.model.WorkflowTaskInstance;
import com.logicaldoc.workflow.model.FetchModel.FETCH_TYPE;

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

	private TaskController taskController;

	private String newAssignment = null;

	private UserDAO userDAO;

	public WorkflowManager() {
		this.workflowService = (WorkflowService) Context.getInstance().getBean("workflowService");

		this.navigationBean = (NavigationBean) FacesUtil.accessBeanFromFacesContext("navigation", FacesContext
				.getCurrentInstance(), log);

		this.documentDAO = (DocumentDAO) FacesUtil.accessBeanFromFacesContext("DocumentDAO", FacesContext
				.getCurrentInstance(), log);

		this.userDAO = (UserDAO) Context.getInstance().getBean(UserDAO.class);
	}

	public void setNewAssignment(String newAssignment) {
		this.newAssignment = newAssignment;
	}

	public String getNewAssignment() {
		return newAssignment;
	}

	public TaskController getTaskController() {
		return taskController;
	}

	public List<WorkflowDefinition> getWorkflowDefinitions() {
		return this.workflowService.getAllDefinitions();
	}

	public void rowSelectionListener(RowSelectorEvent event) {
		WorkflowDefinition workflowDefinition = this.workflowService.getAllDefinitions().get(event.getRow());

		WorkflowInstance instance = this.workflowService.startWorkflow(workflowDefinition, null);
		this.workflowService.signal(instance.getId());
	}

	public void endTask(ActionEvent actionEvent) {

		saveState();

		UIComponent component = (UIComponent) actionEvent.getSource();
		Transition transition = (Transition) ((UIParameter) component.getChildren().get(1)).getValue();

		this.workflowService.endTask(this.workflowTaskInstance.getId(), transition.getName());

		setupTaskPage(this.workflowTaskInstance.getId());
	}

	public List<WorkflowTaskInstance> getTaskInstances() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);

		String username = (String) session.getAttribute(Constants.AUTH_USERNAME);

		return this.workflowService.getTaskInstancesForUser(username);

	}

	public void assign(ActionEvent actionEvent) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String username = (String) session.getAttribute(Constants.AUTH_USERNAME);

		this.workflowService.assign(this.workflowTaskInstance.getId(), username);

		this.workflowTaskInstance = this.workflowService.getTaskInstanceByTaskId(this.workflowTaskInstance.getId(),
				FETCH_TYPE.FORUPDATE);
	}

	public List<WorkflowTaskInstance> getPooledTaskInstances() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String username = (String) session.getAttribute(Constants.AUTH_USERNAME);
		return this.workflowService.getPooledTaskInstancesForUser(username);
	}

	public List<WorkflowTaskInstance> getWorkflowHistory() {
		return this.taskHistory = this.workflowService.getWorkflowHistory(this.workflowInstance);
	}

	public String showTaskDetails(ActionEvent actionEvent) {

		this.selectedResourceFolder = null;
		this.showFolderSelector = false;
		this.directoryModel = null;

		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
				.getRequest();
		// TODO: TaskId should be obtained instead of this very strange logic
		WorkflowTaskInstance taskInstance = (WorkflowTaskInstance) request.getAttribute("taskInstance");

		this.setupTaskPage(taskInstance.getId());

		PageContentBean contentBean = new PageContentBean();
		contentBean.setTemplate("workflow/workflow-view");
		contentBean.setPageContent(true);

		this.navigationBean.setSelectedPanel(contentBean);

		return null;
	}

	public void setupTaskPage(String taskid) {
		this.taskController = null;
		this.workingWorkflowTaskinstance = this.workflowService.getTaskInstanceByTaskId(taskid, FETCH_TYPE.FORUPDATE);
		this.workflowTaskInstance = this.workflowService.getTaskInstanceByTaskId(taskid, FETCH_TYPE.INFO);
		this.workingWorkflowInstance = this.workflowService.getWorkflowInstanceByTaskInstance(taskid,
				FETCH_TYPE.FORUPDATE);
		this.workflowInstance = this.workflowService.getWorkflowInstanceByTaskInstance(taskid, FETCH_TYPE.INFO);

		this.taskController = null;
		this.newAssignment = null;

	}

	public WorkflowInstance getSelectedWorkflowInstance() {
		return workflowInstance;
	}

	public WorkflowTaskInstance getSelectedWorkflowTaskInstance() {
		return workflowTaskInstance;
	}

	public void turnBackToPool(ActionEvent actionEvent) {
		this.workflowTaskInstance.getProperties().put(WorkflowConstants.VAR_OWNER, null);
		this.workflowService.updateWorkflow(workflowTaskInstance);

		this.workflowTaskInstance = this.workflowService.getTaskInstanceByTaskId(this.workflowTaskInstance.getId(),
				FETCH_TYPE.FORUPDATE);

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

	@SuppressWarnings("unchecked")
	public List<DocumentRecord> getSelectedFolderItems() {
		List<DocumentRecord> records = new LinkedList<DocumentRecord>();

		if (this.selectedResourceFolder == null)
			return records;

		List<Document> documents = this.documentDAO.findByFolder(this.selectedResourceFolder.getMenuId());

		for (Document document : documents) {

			boolean documentAlreadyAppended = false;

			// do not append already added documents
			for (DocumentRecord wfDoc : (Set<DocumentRecord>) this.workflowInstance.getProperties().get(
					WorkflowConstants.VAR_DOCUMENTS)) {
				if (document.getId() == wfDoc.getDocId()) {
					documentAlreadyAppended = true;
					break;
				}
			}

			if (documentAlreadyAppended)
				continue;

			records.add(new DocumentRecord(document.getId(), DocumentsRecordsManager.CHILD_INDENT_STYLE_CLASS,
					DocumentsRecordsManager.CHILD_ROW_STYLE_CLASS));

		}

		return records;

	}

	@SuppressWarnings("unchecked")
	public void appendDocument(ActionEvent actionEvent) {
		UIComponent component = (UIComponent) actionEvent.getSource();
		DocumentRecord selectedDocumentRecord = (DocumentRecord) ((UIParameter) component.getChildren().get(0))
				.getValue();

		Set<Long> docIds = (Set<Long>) this.workingWorkflowInstance.getProperties()
				.get(WorkflowConstants.VAR_DOCUMENTS);
		docIds.add(selectedDocumentRecord.getDocId());

		Set<DocumentRecord> records = (Set<DocumentRecord>) this.workflowInstance.getProperties().get(
				WorkflowConstants.VAR_DOCUMENTS);

		records.add(selectedDocumentRecord);

	}

	public List<WorkflowTaskInstance> getSuspendedTaskInstances() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String username = (String) session.getAttribute(Constants.AUTH_USERNAME);
		return this.workflowService.getSuspendedTaskInstancesForUser(username);
	}

	public String saveState() {

		this.workflowService.updateWorkflow(this.workingWorkflowInstance);
		this.workflowService.updateWorkflow(this.workingWorkflowTaskinstance);

		this.setupTaskPage(workingWorkflowTaskinstance.getId());

		return null;
	}

	public void startTask() {
		this.workflowTaskInstance.getProperties().put(WorkflowConstants.VAR_TASKSTATE,
				WorkflowTaskInstance.STATE.STARTED.getVal());
		this.workflowService.updateWorkflow(this.workflowTaskInstance);

		this.setupTaskPage(workingWorkflowTaskinstance.getId());
	}

	public void resumeTask() {
		this.workflowTaskInstance.getProperties().put(WorkflowConstants.VAR_TASKSTATE,
				WorkflowTaskInstance.STATE.STARTED.getVal());
		this.workflowService.updateWorkflow(workflowTaskInstance);

		this.setupTaskPage(workingWorkflowTaskinstance.getId());
	}

	public void suspendTask() {
		this.workflowTaskInstance.getProperties().put(WorkflowConstants.VAR_TASKSTATE,
				WorkflowTaskInstance.STATE.SUSPENDED.getVal());
		this.workflowService.updateWorkflow(workflowTaskInstance);

		this.setupTaskPage(workingWorkflowTaskinstance.getId());
	}

	public WorkflowInstance getWorkingWorkflowInstance() {
		return workingWorkflowInstance;
	}

	public WorkflowTaskInstance getWorkingWorkflowTaskinstance() {
		return workingWorkflowTaskinstance;
	}

	public void openReassignmentDialog(ActionEvent actionEvent) {
		this.taskController = (TaskController) Context.getInstance().getBean("workflowTaskController");
	}

	public void closeReassignmentDialog(ActionEvent actionEvent) {
		this.taskController = null;
	}

	public void setupNewAssignment(ActionEvent actionEvent) {
		try {
			WorkflowTaskInstance taskInstance = this.workflowService.getTaskInstanceByTaskId(this.workflowTaskInstance
					.getId(), FETCH_TYPE.FORUPDATE);
			taskInstance.getProperties().put(WorkflowConstants.VAR_OWNER, this.newAssignment);
			this.workflowService.updateWorkflow(taskInstance);

			this.navigationBean.back();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public List<WorkflowTaskInstance> getAdminTasks() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);

		String username = (String) session.getAttribute(Constants.AUTH_USERNAME);
		User currentUser = this.userDAO.findByUserName(username);

		for (Group group : currentUser.getGroups()) {
			if (group.getName().equals("admin"))
				return this.workflowService.getAllActiveTaskInstances();
		}

		return null;
	}

	/**
	 * Opens the directory containing the selected search entry
	 */
	public String openInFolder() {
		Map<String, Object> map = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();

		Object entry = (Object) map.get("documentRecord");

		long docId = 0;

		docId = ((DocumentRecord) entry).getDocId();

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document document = docDao.findById(docId);
		Menu folder = document.getFolder();
		MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		if (folder != null && menuDao.isReadEnable(folder.getId(), SessionManagement.getUserId())) {
			DocumentNavigation documentNavigation = ((DocumentNavigation) FacesUtil.accessBeanFromFacesContext(
					"documentNavigation", FacesContext.getCurrentInstance(), log));
			documentNavigation.selectDirectory(folder.getId());
			documentNavigation.highlightDocument(docId);
			documentNavigation.setSelectedPanel(new PageContentBean(documentNavigation.getViewMode()));

			// Show the documents browsing panel
			NavigationBean navigation = ((NavigationBean) FacesUtil.accessBeanFromFacesContext("navigation",
					FacesContext.getCurrentInstance(), log));
			Menu documentsMenu = menuDao.findById(Menu.MENUID_DOCUMENTS);

			PageContentBean panel = new PageContentBean("m-" + documentsMenu.getId(), "document/browse");
			panel.setContentTitle(Messages.getMessage(documentsMenu.getText()));
			navigation.setSelectedPanel(panel);
		} else {
			log.warn("Menu "
					+ folder.getText().replace("menu.documents", Messages.getMessage("menu.documents", Locale.ENGLISH))
					+ " not readable");
		}

		return null;
	}

	public String abort() {
		this.navigationBean.back();
		return null;
	}
}
