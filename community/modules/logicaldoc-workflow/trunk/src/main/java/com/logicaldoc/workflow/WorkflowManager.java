package com.logicaldoc.workflow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icesoft.faces.component.ext.RowSelectorEvent;
import com.icesoft.faces.context.effects.JavascriptContext;
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
import com.logicaldoc.workflow.editor.WorkflowHistoryDAO;
import com.logicaldoc.workflow.editor.WorkflowPersistenceTemplate;
import com.logicaldoc.workflow.editor.WorkflowPersistenceTemplateDAO;
import com.logicaldoc.workflow.editor.controll.TaskController;
import com.logicaldoc.workflow.model.Transition;
import com.logicaldoc.workflow.model.WorkflowDefinition;
import com.logicaldoc.workflow.model.WorkflowInstance;
import com.logicaldoc.workflow.model.WorkflowTaskInstance;
import com.logicaldoc.workflow.model.WorkflowTemplate;
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

	private TaskController taskController;

	private String newAssignment = null;

	private UserDAO userDAO;

	private Long selectedWorkflowTemplateId = 0L;

	private String selectedWorkflowInstanceId;

	private List<SelectItem> workflowTemplates = new LinkedList<SelectItem>();

	private ArrayList<WorkflowInstance> workflowInstances;

	private ArrayList<WorkflowHistory> histories;

	private int displayedRows = 10;

	private boolean multipleSelection = true;

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

		// Create the workflow history event
		WorkflowHistoryDAO workflowHistoryDao = (WorkflowHistoryDAO) Context.getInstance().getBean(
				WorkflowHistoryDAO.class);
		WorkflowPersistenceTemplateDAO workflowTemplateDao = (WorkflowPersistenceTemplateDAO) Context.getInstance()
				.getBean(WorkflowPersistenceTemplateDAO.class);
		WorkflowHistory transaction = new WorkflowHistory();
		WorkflowInstance instance = this.workflowService.getWorkflowInstanceByTaskInstance(this.workflowTaskInstance
				.getId(), FETCH_TYPE.INFO);
		WorkflowPersistenceTemplate template = workflowTemplateDao.findByName(instance.getName());

		transaction.setTemplateId(template.getId());
		transaction.setInstanceId(instance.getId());
		transaction.setDate(new Date());
		transaction.setSessionId(SessionManagement.getCurrentUserSessionId());
		transaction.setEvent(WorkflowHistory.EVENT_WORKFLOW_TASK_END);
		transaction.setComment("");
		transaction.setUser(SessionManagement.getUser());

		workflowHistoryDao.store(transaction);

		// Check if it is the last task of the workflow instance, so check if
		// the workflow instance is ended
		if (instance.getEndDate() != null) {
			WorkflowHistory instanceEnded = new WorkflowHistory();
			instanceEnded.setTemplateId(template.getId());
			instanceEnded.setInstanceId(instance.getId());
			instanceEnded.setDate(new Date());
			instanceEnded.setSessionId(SessionManagement.getCurrentUserSessionId());
			instanceEnded.setEvent(WorkflowHistory.EVENT_WORKFLOW_END);
			instanceEnded.setComment("");
			instanceEnded.setUser(SessionManagement.getUser());

			workflowHistoryDao.store(instanceEnded);
		}
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

		List<Document> documents = this.documentDAO.findByFolder(this.selectedResourceFolder.getMenuId(), null);

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

		// Create the workflow history event
		WorkflowHistoryDAO workflowHistoryDao = (WorkflowHistoryDAO) Context.getInstance().getBean(
				WorkflowHistoryDAO.class);
		WorkflowPersistenceTemplateDAO workflowTemplateDao = (WorkflowPersistenceTemplateDAO) Context.getInstance()
				.getBean(WorkflowPersistenceTemplateDAO.class);
		WorkflowHistory transaction = new WorkflowHistory();
		WorkflowInstance instance = this.workflowService.getWorkflowInstanceByTaskInstance(this.workflowTaskInstance
				.getId(), FETCH_TYPE.INFO);
		WorkflowPersistenceTemplate template = workflowTemplateDao.findByName(instance.getName());

		transaction.setTemplateId(template.getId());
		transaction.setInstanceId(instance.getId());
		transaction.setDate(new Date());
		transaction.setSessionId(SessionManagement.getCurrentUserSessionId());
		transaction.setEvent(WorkflowHistory.EVENT_WORKFLOW_DOCAPPENDED);
		transaction.setDocId(selectedDocumentRecord.getDocId());
		transaction.setComment("");
		transaction.setUser(SessionManagement.getUser());

		workflowHistoryDao.store(transaction);
	}

	public List<WorkflowTaskInstance> getSuspendedTaskInstances() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String username = (String) session.getAttribute(Constants.AUTH_USERNAME);

		// If the current user is an 'admin' user, he must see all the suspended
		// task instances
		User currentUser = this.userDAO.findByUserName(username);
		for (Group group : currentUser.getGroups()) {
			if (group.getName().equals("admin"))
				return this.workflowService.getSuspendedTaskInstances();
		}

		// Checks if the current user is the workflow supervisor, so he must see
		// the suspended tasks for the workflow and his suspended tasks of other
		// workflows
		List<WorkflowTaskInstance> suspendedTaskInstances = new ArrayList<WorkflowTaskInstance>();
		List<WorkflowTaskInstance> supervisorTaskInstances = getSupervisorWorkflowTasks();
		if (supervisorTaskInstances != null) {
			// The current user is a workflow supervisor
			for (WorkflowTaskInstance workflowTaskInstance : this.workflowService.getSuspendedTaskInstances()) {
				if (supervisorTaskInstances.contains(workflowTaskInstance))
					suspendedTaskInstances.add(workflowTaskInstance);
			}
			// Maybe the supervisor user has some suspended tasks of other
			// workflows
			for (WorkflowTaskInstance workflowTaskInstance : this.workflowService
					.getSuspendedTaskInstancesForUser(username)) {
				if (!suspendedTaskInstances.contains(workflowTaskInstance))
					suspendedTaskInstances.add(workflowTaskInstance);
			}

			return suspendedTaskInstances;
		}

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

		// Create the workflow history event
		WorkflowHistoryDAO workflowHistoryDao = (WorkflowHistoryDAO) Context.getInstance().getBean(
				WorkflowHistoryDAO.class);
		WorkflowPersistenceTemplateDAO workflowTemplateDao = (WorkflowPersistenceTemplateDAO) Context.getInstance()
				.getBean(WorkflowPersistenceTemplateDAO.class);
		WorkflowHistory transaction = new WorkflowHistory();
		WorkflowInstance instance = this.workflowService.getWorkflowInstanceByTaskInstance(this.workflowTaskInstance
				.getId(), FETCH_TYPE.INFO);
		WorkflowPersistenceTemplate template = workflowTemplateDao.findByName(instance.getName());

		transaction.setTemplateId(template.getId());
		transaction.setInstanceId(instance.getId());
		transaction.setDate(new Date());
		transaction.setSessionId(SessionManagement.getCurrentUserSessionId());
		transaction.setEvent(WorkflowHistory.EVENT_WORKFLOW_TASK_START);
		transaction.setComment("");
		transaction.setUser(SessionManagement.getUser());

		workflowHistoryDao.store(transaction);
	}

	public void resumeTask() {
		this.workflowTaskInstance.getProperties().put(WorkflowConstants.VAR_TASKSTATE,
				WorkflowTaskInstance.STATE.STARTED.getVal());
		this.workflowService.updateWorkflow(workflowTaskInstance);

		this.setupTaskPage(workingWorkflowTaskinstance.getId());

		// Create the workflow history event
		WorkflowHistoryDAO workflowHistoryDao = (WorkflowHistoryDAO) Context.getInstance().getBean(
				WorkflowHistoryDAO.class);
		WorkflowPersistenceTemplateDAO workflowTemplateDao = (WorkflowPersistenceTemplateDAO) Context.getInstance()
				.getBean(WorkflowPersistenceTemplateDAO.class);
		WorkflowHistory transaction = new WorkflowHistory();
		WorkflowInstance instance = this.workflowService.getWorkflowInstanceByTaskInstance(this.workflowTaskInstance
				.getId(), FETCH_TYPE.INFO);
		WorkflowPersistenceTemplate template = workflowTemplateDao.findByName(instance.getName());

		transaction.setTemplateId(template.getId());
		transaction.setInstanceId(instance.getId());
		transaction.setDate(new Date());
		transaction.setSessionId(SessionManagement.getCurrentUserSessionId());
		transaction.setEvent(WorkflowHistory.EVENT_WORKFLOW_TASK_RESUMED);
		transaction.setComment("");
		transaction.setUser(SessionManagement.getUser());

		workflowHistoryDao.store(transaction);
	}

	public void suspendTask() {
		this.workflowTaskInstance.getProperties().put(WorkflowConstants.VAR_TASKSTATE,
				WorkflowTaskInstance.STATE.SUSPENDED.getVal());
		this.workflowService.updateWorkflow(workflowTaskInstance);

		this.setupTaskPage(workingWorkflowTaskinstance.getId());

		// Create the workflow history event
		WorkflowHistoryDAO workflowHistoryDao = (WorkflowHistoryDAO) Context.getInstance().getBean(
				WorkflowHistoryDAO.class);
		WorkflowPersistenceTemplateDAO workflowTemplateDao = (WorkflowPersistenceTemplateDAO) Context.getInstance()
				.getBean(WorkflowPersistenceTemplateDAO.class);
		WorkflowHistory transaction = new WorkflowHistory();
		WorkflowInstance instance = this.workflowService.getWorkflowInstanceByTaskInstance(this.workflowTaskInstance
				.getId(), FETCH_TYPE.INFO);
		WorkflowPersistenceTemplate template = workflowTemplateDao.findByName(instance.getName());

		transaction.setTemplateId(template.getId());
		transaction.setInstanceId(instance.getId());
		transaction.setDate(new Date());
		transaction.setSessionId(SessionManagement.getCurrentUserSessionId());
		transaction.setEvent(WorkflowHistory.EVENT_WORKFLOW_TASK_SUSPENDED);
		transaction.setComment("");
		transaction.setUser(SessionManagement.getUser());

		workflowHistoryDao.store(transaction);
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

			// Create the workflow history event
			WorkflowHistoryDAO workflowHistoryDao = (WorkflowHistoryDAO) Context.getInstance().getBean(
					WorkflowHistoryDAO.class);
			WorkflowPersistenceTemplateDAO workflowTemplateDao = (WorkflowPersistenceTemplateDAO) Context.getInstance()
					.getBean(WorkflowPersistenceTemplateDAO.class);
			WorkflowHistory transaction = new WorkflowHistory();
			WorkflowInstance instance = this.workflowService.getWorkflowInstanceByTaskInstance(
					this.workflowTaskInstance.getId(), FETCH_TYPE.INFO);
			WorkflowPersistenceTemplate template = workflowTemplateDao.findByName(instance.getName());

			transaction.setTemplateId(template.getId());
			transaction.setInstanceId(instance.getId());
			transaction.setDate(new Date());
			transaction.setSessionId(SessionManagement.getCurrentUserSessionId());
			transaction.setEvent(WorkflowHistory.EVENT_WORKFLOW_TASK_REASSIGNED);
			transaction.setComment("Workflow Task " + this.workflowTaskInstance.getName() + " reassigned to "
					+ this.newAssignment);
			transaction.setUser(SessionManagement.getUser());

			workflowHistoryDao.store(transaction);

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
				return this.workflowService.getAllTaskInstances();
		}

		return null;
	}

	/**
	 * Retrieves all the tasks of the workflow for which the current user (not
	 * administator) is the supervisor.
	 */
	public List<WorkflowTaskInstance> getSupervisorWorkflowTasks() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);

		String username = (String) session.getAttribute(Constants.AUTH_USERNAME);
		User currentUser = this.userDAO.findByUserName(username);
		List<String> groupUserNames = new ArrayList<String>();
		for (Group group : currentUser.getGroups()) {
			groupUserNames.add(group.getName());
		}

		WorkflowTransformService workflowTransformService = (WorkflowTransformService) Context.getInstance().getBean(
				"workflowTransformService");
		List<WorkflowInstance> allWorkflowInstances = this.workflowService.getAllWorkflows();
		// A single workflow can have more than one instances, so a supervisor
		// can
		// supervise more than one workflow instances.
		List<WorkflowInstance> supervisorWorkflowInstance = new ArrayList<WorkflowInstance>();

		for (WorkflowInstance workflowInstance : allWorkflowInstances) {
			Object workflowTemplateXML = workflowInstance.getProperties().get(WorkflowConstants.VAR_TEMPLATE);
			WorkflowTemplate workflowTemplate = workflowTransformService
					.retrieveWorkflowModels((Serializable) workflowTemplateXML);
			if (workflowTemplate.getSupervisor() != null && !workflowTemplate.getSupervisor().trim().isEmpty()) {
				if (username.equals(workflowTemplate.getSupervisor())
						|| groupUserNames.contains(workflowTemplate.getSupervisor())) {
					supervisorWorkflowInstance.add(workflowInstance);
				}
			}
		}

		List<WorkflowTaskInstance> supervisorWorkflowTasks = new ArrayList<WorkflowTaskInstance>();
		if (supervisorWorkflowInstance != null && !supervisorWorkflowInstance.isEmpty()) {
			for (WorkflowInstance workflowInstance : supervisorWorkflowInstance) {
				supervisorWorkflowTasks.addAll(this.workflowService
						.getTaskInstancesByWorkflowInstanceId(workflowInstance.getId()));
			}
			return supervisorWorkflowTasks;
		} else
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

			PageContentBean panel = new PageContentBean(documentsMenu.getId(), "document/browse");
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
		this.selectedWorkflowTemplateId = 0L;
		this.selectedWorkflowInstanceId = "0";
		if (workflowTemplates != null)
			workflowTemplates.clear();
		if (workflowInstances != null)
			workflowInstances.clear();
		if (histories != null)
			histories.clear();

		this.navigationBean.back();
		return null;
	}

	public String showHistory() {
		PageContentBean contentBean = new PageContentBean();
		contentBean.setTemplate("workflow/workflow-history");
		contentBean.setPageContent(true);

		this.navigationBean.setSelectedPanel(contentBean);

		return null;
	}

	public List<SelectItem> getWorkflowTemplates() {
		WorkflowPersistenceTemplateDAO workflowTemplateDao = (WorkflowPersistenceTemplateDAO) Context.getInstance()
				.getBean(WorkflowPersistenceTemplateDAO.class);

		WorkflowHistoryDAO workflowHistoryDao = (WorkflowHistoryDAO) Context.getInstance().getBean(
				WorkflowHistoryDAO.class);

		List<Long> historyTemplates = workflowHistoryDao.findTemplateIds();

		if (workflowTemplates.isEmpty()) {
			for (Long templateId : historyTemplates) {
				WorkflowPersistenceTemplate template = workflowTemplateDao.findById(templateId);
				if (template != null && isUserAuthorized(template))
					workflowTemplates.add(new SelectItem(template.getId(), template.getName()));
			}

			if (workflowTemplates.size() > 0)
				workflowTemplates.add(0, new SelectItem(0, Messages.getMessage("workflow.history.choosetemplate")));
		}

		return workflowTemplates;
	}

	public void templateSelected(ValueChangeEvent event) {
		if (event != null && event.getNewValue() != null) {
			this.selectedWorkflowTemplateId = Long.parseLong(event.getNewValue().toString());
			if (workflowInstances != null)
				workflowInstances.clear();
			if (histories != null)
				histories.clear();
			reloadInstances();
		}
	}

	public List<WorkflowInstance> getWorkflowInstances() {
		reloadInstances();
		return workflowInstances;
	}

	public void search() {
		reload();
	}

	public void reset() {
		this.selectedWorkflowTemplateId = 0L;
		this.selectedWorkflowInstanceId = "0";
		workflowTemplates.clear();
		workflowInstances.clear();
		histories.clear();
		JavascriptContext.addJavascriptCall(FacesContext.getCurrentInstance(), "window.location.reload(false);");
	}

	public void reloadInstances() {
		// initiate the list
		if (workflowInstances != null) {
			workflowInstances.clear();
		} else {
			workflowInstances = new ArrayList<WorkflowInstance>();
		}

		WorkflowHistoryDAO workflowHistoryDao = (WorkflowHistoryDAO) Context.getInstance().getBean(
				WorkflowHistoryDAO.class);
		if (this.selectedWorkflowTemplateId > 0L && workflowInstances.isEmpty()) {
			List<String> historyInstances = workflowHistoryDao.findInstanceIds();
			WorkflowPersistenceTemplateDAO workflowTemplateDao = (WorkflowPersistenceTemplateDAO) Context.getInstance()
					.getBean(WorkflowPersistenceTemplateDAO.class);
			for (String workflowInstanceId : historyInstances) {
				WorkflowInstance instance = this.workflowService.getWorkflowInstanceById(workflowInstanceId,
						FETCH_TYPE.INFO);
				WorkflowTemplate workflowTemplate = (WorkflowTemplate) instance.getProperties().get(
						WorkflowConstants.VAR_TEMPLATE);

				String templateName = workflowTemplate.getName();
				WorkflowPersistenceTemplate template = workflowTemplateDao.findByName(templateName);
				if (template != null && template.getId() == this.selectedWorkflowTemplateId) {
					if (this.selectedWorkflowInstanceId != null && !this.selectedWorkflowInstanceId.isEmpty()
							&& this.selectedWorkflowInstanceId.equals(instance.getId()))
						instance.setSelected(true);
					workflowInstances.add(instance);
				}
			}
		}
		if (workflowInstances != null && workflowInstances.size() > 1) {
			Collections.sort(workflowInstances, new Comparator<WorkflowInstance>() {
				@Override
				public int compare(WorkflowInstance arg0, WorkflowInstance arg1) {
					return new Integer(arg1.getId()).compareTo(new Integer(arg0.getId()));
				}
			});
		}

	}

	public void reload() {
		// initiate the list
		if (histories != null) {
			histories.clear();
		} else {
			histories = new ArrayList<WorkflowHistory>();
		}

		try {
			WorkflowHistoryDAO workflowHistoryDao = (WorkflowHistoryDAO) Context.getInstance().getBean(
					WorkflowHistoryDAO.class);
			Collection<WorkflowHistory> tmphistories = workflowHistoryDao.findByTemplateIdAndInstanceId(
					this.selectedWorkflowTemplateId, this.selectedWorkflowInstanceId);

			for (WorkflowHistory history : tmphistories) {
				histories.add(history);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			Messages.addLocalizedError("errors.error");
		}
	}

	/**
	 * Gets the list of workflow histories which will be used by the
	 * ice:dataTable component.
	 */
	public List<WorkflowHistory> getHistories() {
		reload();
		return histories;
	}

	public int getHistoriesCount() {
		if (histories == null || histories.isEmpty()) {
			return 0;
		} else {
			return histories.size();
		}
	}

	public int getWorkflowInstancesCount() {
		if (workflowInstances == null || workflowInstances.isEmpty()) {
			return 0;
		} else {
			return workflowInstances.size();
		}
	}

	public int getDisplayedRows() {
		return displayedRows;
	}

	public void setDisplayedRows(int displayedRows) {
		if (displayedRows != this.displayedRows)
			JavascriptContext.addJavascriptCall(FacesContext.getCurrentInstance(), "window.location.reload(false);");
		this.displayedRows = displayedRows;
	}

	public void selectRow(RowSelectorEvent e) {
		WorkflowInstance instance = workflowInstances.get(e.getRow());
		this.selectedWorkflowInstanceId = instance.getId();

		reloadInstances();
		reload();
	}

	/**
	 * Checks if the current user is a supervisor of one or more workflow.
	 * 
	 * @return true if exists at least a workflow instance (active or ended) of
	 *         the workflow for which the current user is a supervisor.
	 */
	public boolean isUserSupervisor() {
		boolean isSupervisor = false;

		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);

		String username = (String) session.getAttribute(Constants.AUTH_USERNAME);
		User currentUser = this.userDAO.findByUserName(username);
		List<String> groupUserNames = new ArrayList<String>();
		for (Group group : currentUser.getGroups()) {
			groupUserNames.add(group.getName());
		}

		WorkflowTransformService workflowTransformService = (WorkflowTransformService) Context.getInstance().getBean(
				"workflowTransformService");
		List<WorkflowInstance> allWorkflowInstances = this.workflowService.getAllWorkflows();

		for (WorkflowInstance workflowInstance : allWorkflowInstances) {
			Object workflowTemplateXML = workflowInstance.getProperties().get(WorkflowConstants.VAR_TEMPLATE);
			WorkflowTemplate workflowTemplate = workflowTransformService
					.retrieveWorkflowModels((Serializable) workflowTemplateXML);
			if (workflowTemplate.getSupervisor() != null && !workflowTemplate.getSupervisor().trim().isEmpty()) {
				if (username.equals(workflowTemplate.getSupervisor())
						|| groupUserNames.contains(workflowTemplate.getSupervisor())) {
					isSupervisor = true;
					break;
				}
			}
		}

		return isSupervisor;
	}

	/**
	 * Checks if the current user is authorized to see of the given workflow.
	 * The users authorized are the admins and the sueprvisors.
	 * 
	 * @return true if the current user is authorized to see of the given
	 *         workflow.
	 */
	public boolean isUserAuthorized(WorkflowPersistenceTemplate template) {
		boolean isSupervisor = false;

		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);

		String username = (String) session.getAttribute(Constants.AUTH_USERNAME);
		User currentUser = this.userDAO.findByUserName(username);
		for (Group group : currentUser.getGroups()) {
			if (group.getName().equals("admin"))
				return true;
		}
		List<String> groupUserNames = new ArrayList<String>();
		for (Group group : currentUser.getGroups()) {
			groupUserNames.add(group.getName());
		}

		WorkflowTransformService workflowTransformService = (WorkflowTransformService) Context.getInstance().getBean(
				"workflowTransformService");
		List<WorkflowInstance> allWorkflowInstances = this.workflowService.getAllWorkflows();

		for (WorkflowInstance workflowInstance : allWorkflowInstances) {
			Object workflowTemplateXML = workflowInstance.getProperties().get(WorkflowConstants.VAR_TEMPLATE);
			WorkflowTemplate workflowTemplate = workflowTransformService
					.retrieveWorkflowModels((Serializable) workflowTemplateXML);
			if (workflowTemplate.getSupervisor() != null && !workflowTemplate.getSupervisor().trim().isEmpty()
					&& workflowTemplate.getName().equals(template.getName())) {
				if (username.equals(workflowTemplate.getSupervisor())
						|| groupUserNames.contains(workflowTemplate.getSupervisor())) {
					isSupervisor = true;
					break;
				}
			}
		}

		return isSupervisor;
	}

	public Long getSelectedWorkflowTemplateId() {
		return selectedWorkflowTemplateId;
	}

	public void setSelectedWorkflowTemplateId(Long selectedWorkflowTemplateId) {
		this.selectedWorkflowTemplateId = selectedWorkflowTemplateId;
	}

	public String getSelectedWorkflowInstanceId() {
		return selectedWorkflowInstanceId;
	}

	public void setSelectedWorkflowInstanceId(String selectedWorkflowInstanceId) {
		this.selectedWorkflowInstanceId = selectedWorkflowInstanceId;
	}

	public boolean isMultipleSelection() {
		return multipleSelection;
	}

	public void setMultipleSelection(boolean multipleSelection) {
		this.multipleSelection = multipleSelection;
	}
}
