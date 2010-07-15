package com.logicaldoc.workflow.editor;

import java.util.LinkedList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icesoft.faces.component.dragdrop.DndEvent;
import com.icesoft.faces.component.dragdrop.DragEvent;
import com.icesoft.faces.component.dragdrop.DropEvent;
import com.icesoft.faces.component.ext.HtmlCommandLink;
import com.icesoft.faces.component.ext.HtmlPanelGroup;
import com.icesoft.faces.component.selectinputtext.SelectInputText;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.document.DocumentNavigation;
import com.logicaldoc.web.document.DocumentsRecordsManager;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.navigation.PageContentBean;
import com.logicaldoc.web.util.FacesUtil;
import com.logicaldoc.workflow.WorkflowService;
import com.logicaldoc.workflow.debug.TRANSMITTER;
import com.logicaldoc.workflow.editor.controll.DragAndDropSupportController;
import com.logicaldoc.workflow.editor.controll.EditController;
import com.logicaldoc.workflow.editor.controll.DragAndDropSupportController.Container;
import com.logicaldoc.workflow.editor.message.DeployMessage;
import com.logicaldoc.workflow.editor.model.BaseWorkflowModel;
import com.logicaldoc.workflow.editor.model.WorkflowEditorException;
import com.logicaldoc.workflow.editor.model.WorkflowTask;
import com.logicaldoc.workflow.model.ModelConfiguration;
import com.logicaldoc.workflow.model.WorkflowDefinition;
import com.logicaldoc.workflow.model.WorkflowTemplate;
import com.logicaldoc.workflow.transform.WorkflowTransformService;
import com.logicaldoc.workflow.wizard.StartWorkflowWizard;
import com.thoughtworks.xstream.XStream;

public class WorkflowTemplateManager {

	private WorkflowTransformService workflowTransformService;

	private WorkflowService workflowService;

	private WorkflowPersistenceTemplate persistenceTemplate;

	private XStream xstream = new XStream();

	private ModelConfiguration modelConfiguration;

	protected static Log log = LogFactory.getLog(WorkflowTemplateManager.class);

	private BaseWorkflowModel component;

	private EditController controller;

	private WorkflowPersistenceTemplateDAO workflowTemplateDao;

	private WorkflowTemplate workflowTemplate;

	private Long workflowTemplateId;

	private List<DeployMessage> errorMessages;

	private boolean showWorkflowSettings = true;

	private UIInput nameInput = null;

	private UIInput descriptionInput = null;

	private UIInput assignmentSubjectInput = null;

	private UIInput assignmentBodyInput = null;

	private UIInput reminderSubjectInput = null;

	private UIInput reminderBodyInput = null;

	private List<String> possibleSupervisors = new LinkedList<String>();

	private UIInput supervisorInput = null;

	private boolean showSupervisorSettings = false;

	public String getXMLData() {
		this.workflowTransformService.fromObjectToWorkflowDefinition(this.workflowTemplate);

		return TRANSMITTER.XML_DATA;
	}

	public void setXMLData(String data) {
	}

	public WorkflowTemplateManager() {
		this.initializing();
	}

	public Long getWorkflowTemplateId() {
		return workflowTemplateId;
	}

	public void setWorkflowTemplateId(Long workflowTemplateId) {
		this.workflowTemplateId = workflowTemplateId;
	}

	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	public void setWorkflowTransformService(WorkflowTransformService workflowTransformService) {
		this.workflowTransformService = workflowTransformService;
	}

	public BaseWorkflowModel getSelectedComponent() {
		return this.component;
	}

	public EditController getController() {
		return controller;
	}

	public void setXStream(XStream xstream) {
		this.xstream = xstream;
	}

	public void initializing() {
		this.workflowTemplate = new WorkflowTemplate();
		this.persistenceTemplate = new WorkflowPersistenceTemplate();
		this.workflowService = (WorkflowService) Context.getInstance().getBean("workflowService");
		this.workflowTransformService = (WorkflowTransformService) Context.getInstance().getBean(
				"workflowTransformService");
		showWorkflowSettings = true;
	}

	public List<SelectItem> getTimeUnits() {
		List<SelectItem> timeValues = new LinkedList<SelectItem>();
		timeValues.add(new SelectItem(Messages.getMessage("minute")));
		timeValues.add(new SelectItem(Messages.getMessage("hour")));
		timeValues.add(new SelectItem(Messages.getMessage("hour.business")));
		timeValues.add(new SelectItem(Messages.getMessage("day")));
		timeValues.add(new SelectItem(Messages.getMessage("day.business")));
		timeValues.add(new SelectItem(Messages.getMessage("week")));
		timeValues.add(new SelectItem(Messages.getMessage("week.business")));

		return timeValues;
	}

	public String addWorkflowComponent(ActionEvent ae) {
		UIParameter param = (UIParameter) ((UIComponent) ae.getSource()).getChildren().get(0);

		EditController editController = modelConfiguration.getControllers().get(param.getValue());

		if (editController == null) {
			Messages.addLocalizedError("feature.enterprise");
		} else {
			removeSelection();
			BaseWorkflowModel workflowModel = editController.instantiateNew();
			workflowModel.setSelected(true);
			this.workflowTemplate.getWorkflowComponents().add(workflowModel);
			selectComponent(workflowModel);
		}

		return null;
	}

	public String removeWorkflowComponent() {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
				.getRequest();
		BaseWorkflowModel baseWorkflowModel = (BaseWorkflowModel) request.getAttribute("component");
		this.workflowTemplate.getWorkflowComponents().remove(baseWorkflowModel);
		return null;
	}

	public List<BaseWorkflowModel> getWorkflowComponents() {
		return this.workflowTemplate.getWorkflowComponents();
	}

	public Integer getWorkflowComponentSize() {
		return this.workflowTemplate.getWorkflowComponents().size();
	}

	public String setupWorkflow() {
		DocumentsRecordsManager documentsRecordsManager = (DocumentsRecordsManager) FacesUtil
				.accessBeanFromFacesContext("documentsRecordsManager", FacesContext.getCurrentInstance(), log);
		if (documentsRecordsManager.getSelection().isEmpty()) {
			Messages.addLocalizedWarn("noselection");
			return null;
		}

		DocumentNavigation documentNavigation = ((DocumentNavigation) FacesUtil.accessBeanFromFacesContext(
				"documentNavigation", FacesContext.getCurrentInstance(), log));
		documentNavigation.setSelectedPanel(new PageContentBean("workflow/wizard"));
		StartWorkflowWizard startWorkflowWizard = (StartWorkflowWizard) Context.getInstance().getBean(
				"StartWorkflowWizard");
		startWorkflowWizard.init();

		return null;
	}

	public String removeAllWorkflowComponents() {
		this.workflowTemplate.getWorkflowComponents().clear();

		if (this.controller != null)
			this.controller.invalidate();

		this.controller = null;

		this.component = null;

		return null;
	}

	public void selectComponent(ActionEvent actionEvent) {

		HtmlCommandLink commandLink = (HtmlCommandLink) actionEvent.getSource();

		removeSelection();

		BaseWorkflowModel baseWorkflowModel = null;

		for (UIComponent component : commandLink.getChildren()) {

			if ((component instanceof UIParameter) == false)
				continue;

			UIParameter param = (UIParameter) commandLink.getChildren().get(0);

			if (param.getName().equals("component") == false)
				continue;

			if ((param.getValue() instanceof BaseWorkflowModel) == false)
				throw new WorkflowEditorException("Given Parameter must be a base of "
						+ BaseWorkflowModel.class.getSimpleName());

			baseWorkflowModel = (BaseWorkflowModel) param.getValue();

			break;
		}

		if (baseWorkflowModel == null)
			throw new WorkflowEditorException("No BaseWorkflowModel has been passed");

		// if we have an existing workflowmodel we have to call invalidate

		selectComponent(baseWorkflowModel);
	}

	private void selectComponent(BaseWorkflowModel baseWorkflowModel) {
		if (this.controller != null)
			this.controller.invalidate();

		this.component = baseWorkflowModel;
		this.component.setSelected(true);

		this.controller = baseWorkflowModel.getController();

		// initializing the controller
		if (this.controller != null)
			this.controller.initialize(baseWorkflowModel);

		showWorkflowSettings = false;
	}

	public String saveComponent() {
		return null;
	}

	private BaseWorkflowModel draggedObject;

	public void dragObject(DragEvent event) {
		if (event.getEventType() == DndEvent.DRAGGING) {
			if (draggedObject == null)
				draggedObject = (BaseWorkflowModel) ((HtmlPanelGroup) event.getComponent()).getDragValue();
		} else if ((event.getEventType() == DndEvent.DRAG_CANCEL)) {
			draggedObject = null;
		}

		else if ((event.getEventType() == DndEvent.DROPPED)) {

		}
	}

	public void dropObject(DropEvent event) {
		if (event.getEventType() == DndEvent.DROPPED) {
			BaseWorkflowModel droppedZone = (BaseWorkflowModel) ((HtmlPanelGroup) event.getComponent()).getDropValue();
			BaseWorkflowModel draggedObject = this.draggedObject;
			this.draggedObject = null;

			Container cnt = new Container();
			cnt.draggedObject = draggedObject;
			cnt.droppingZone = droppedZone;

			try {
				EditController dndController = modelConfiguration.getControllers().get(
						cnt.droppingZone.getClass().getSimpleName());
				if (dndController == null)
					Messages.addLocalizedError("feature.enterprise");
				else if (dndController instanceof DragAndDropSupportController)
					((DragAndDropSupportController) dndController).droppedObject(cnt);
			} catch (Exception e) {
				throw new WorkflowEditorException(e);
			}
		}
	}

	public void moveObject(DropEvent event) {
		if (event.getEventType() == DndEvent.DROPPED && this.draggedObject != null) {
			BaseWorkflowModel droppedZone = (BaseWorkflowModel) ((HtmlPanelGroup) event.getComponent()).getDropValue();
			BaseWorkflowModel draggedObject = this.draggedObject;

			this.workflowTemplate.getWorkflowComponents().remove(draggedObject);
			int index = this.workflowTemplate.getWorkflowComponents().indexOf(droppedZone);
			this.workflowTemplate.getWorkflowComponents().add(index, draggedObject);
			this.draggedObject = null;
		}
	}

	public List<SelectItem> getAvailableWorkflowTemplates() {
		List<SelectItem> workflowTemplates = new LinkedList<SelectItem>();

		for (WorkflowPersistenceTemplate template : this.workflowTemplateDao.findAll())
			workflowTemplates.add(new SelectItem(template.getId(), template.getName()));

		return workflowTemplates;
	}

	public void deleteWorkflowComponent(ActionEvent actionEvent) {
		UIComponent component = (UIComponent) actionEvent.getSource();
		BaseWorkflowModel model = (BaseWorkflowModel) ((UIParameter) component.getChildren().get(0)).getValue();

		this.workflowTemplate.getWorkflowComponents().remove(model);
		this.component = null;
		showWorkflowSettings = true;
	}

	public String saveCurrentWorkflowTemplate() {

		this.workflowTemplate.setName(this.persistenceTemplate.getName());

		String xmlData = xstream.toXML(this.workflowTemplate);
		this.persistenceTemplate.setXmldata(xmlData);
		this.workflowTemplateDao.save(this.persistenceTemplate, WorkflowPersistenceTemplateDAO.WORKFLOW_STAGE.SAVED);
		this.persistenceTemplate.setDescription(this.workflowTemplate.getDescription());
		this.persistenceTemplate.setStartState(this.workflowTemplate.getStartState());

		return null;
	}

	private void setAllItemsToNull() {
		this.persistenceTemplate = null;
		this.workflowTemplate = null;
		this.workflowTemplateId = null;
	}

	public String createNewWorkflowTemplate() {
		initializing();
		reset();

		return null;
	}

	public WorkflowPersistenceTemplate getPersistenceTemplate() {
		return persistenceTemplate;
	}

	public String closeCurrentWorkflowTemplate() {
		setAllItemsToNull();

		return null;
	}

	public String deleteWorkflowTemplate() {

		WorkflowPersistenceTemplate workflowTemplate = this.workflowTemplateDao.load(this.workflowTemplateId,
				WorkflowPersistenceTemplateDAO.WORKFLOW_STAGE.SAVED);

		this.workflowTemplateDao.delete(workflowTemplate);

		if (this.persistenceTemplate.getId() == workflowTemplate.getId())
			initializing();

		initializing();

		return null;
	}

	public String deployWorkflowTemplate() {

		this.saveCurrentWorkflowTemplate();

		this.errorMessages = new LinkedList<DeployMessage>();

		if (this.workflowTemplate.getWorkflowComponents().size() == 0)
			this.errorMessages.add(new DeployMessage(this.workflowTemplate, "No workflow-component have been added"));

		boolean workflowTaskExist = false;
		for (BaseWorkflowModel model : this.workflowTemplate.getWorkflowComponents()) {

			if (model instanceof WorkflowTask)
				workflowTaskExist = true;

			model.checkForDeploy(errorMessages);
		}

		if (workflowTaskExist == false)
			this.errorMessages.add(new DeployMessage(this.workflowTemplate,
					"There must at least exist one Workflow-Task"));

		if (this.errorMessages.size() > 0)
			return null;

		// at first we have to delete the current workflow instance
		// TODO:we should add API-improvements to handle more clearer this
		// List<WorkflowDefinition> definitions =
		// this.workflowService.getAllDefinitions();
		//
		// for (WorkflowDefinition definition : definitions) {
		// if (definition.getName().equals(workflowTemplate.getName()))
		// this.workflowService.undeployWorkflow(definition.getDefinitionId());
		// }

		this.persistenceTemplate.setXmldata(xstream.toXML(this.workflowTemplate));
		this.workflowTemplateDao.deploy(persistenceTemplate);
		this.workflowService.deployWorkflow(this.workflowTemplate);

		return null;
	}

	public List<SelectItem> getPossibleStartStates() {
		List<SelectItem> possibleStartStates = new LinkedList<SelectItem>();

		for (BaseWorkflowModel model : this.workflowTemplate.getWorkflowComponents()) {

			if (model.isPossibleStartState())
				possibleStartStates.add(new SelectItem(model.getId(), model.getName()));
		}

		return possibleStartStates;
	}

	public void undeployAllDefinitions() {
		List<WorkflowDefinition> definitions = this.workflowService.getAllDefinitions();
		for (WorkflowDefinition def : definitions) {
			this.workflowService.undeployWorkflow(def.getDefinitionId());
		}

	}

	public void deleteAllActiveWorkflows() {
		this.workflowService.deleteAllActiveWorkflows();
	}

	public String loadWorkflowTemplate() {
		if (this.workflowTemplateId == 0)
			return null;

		try {
			this.initializing();
			this.persistenceTemplate = this.workflowTemplateDao.load(this.workflowTemplateId,
					WorkflowPersistenceTemplateDAO.WORKFLOW_STAGE.SAVED);
			if (this.persistenceTemplate.getXmldata() != null
					&& ((String) this.persistenceTemplate.getXmldata()).getBytes().length > 0) {
				this.workflowTemplate = this.workflowTransformService
						.fromWorkflowDefinitionToObject(persistenceTemplate);
			}

			reset();
			removeSelection();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return null;
	}

	private void reset() {
		this.component = null;
		FacesUtil.forceRefresh(nameInput);
		FacesUtil.forceRefresh(descriptionInput);
		FacesUtil.forceRefresh(assignmentSubjectInput);
		FacesUtil.forceRefresh(assignmentBodyInput);
		FacesUtil.forceRefresh(reminderSubjectInput);
		FacesUtil.forceRefresh(reminderBodyInput);
		FacesUtil.forceRefresh(supervisorInput);
	}

	public void removeSupervisor(ActionEvent actionEvent) {
		this.workflowTemplate.setSupervisor("");
		showSupervisorSettings = false;
	}

	public void addSupervisor(ActionEvent actionEvent) {
		showSupervisorSettings = true;
	}

	public void selectSupervisorChanged(ValueChangeEvent event) {
		if (event.getComponent() instanceof SelectInputText) {
			// get the number of displayable records from the component
			SelectInputText autoComplete = (SelectInputText) event.getComponent();
			if (autoComplete == null)
				return;
			String currentValue = autoComplete.getValue().toString();
			UserDAO userDAO = (UserDAO) Context.getInstance().getBean(UserDAO.class);
			GroupDAO groupDAO = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
			List<User> matchedUsers = userDAO
					.findByWhere(
							"_entity.type = 0 and (_entity.userName like concat(?,'%') OR _entity.firstName like concat(?,'%') OR _entity.name like concat(?,'%'))",
							new Object[] { currentValue, currentValue, currentValue }, null, null);

			List<Group> matchedGroups = groupDAO.findByWhere("_entity.type = 0 and _entity.name like concat(?,'%')",
					new Object[] { currentValue, }, null, null);

			possibleSupervisors = new LinkedList<String>();
			for (User user : matchedUsers) {
				possibleSupervisors.add(user.getUserName());
			}
			for (Group group : matchedGroups) {
				if (!possibleSupervisors.contains(group.getName()))
					possibleSupervisors.add(group.getName());
			}
		}
	}

	public List<SelectItem> getPossibleSupervisors() {
		List<SelectItem> items = new LinkedList<SelectItem>();
		for (String supervisor : possibleSupervisors) {
			items.add(new SelectItem(supervisor, supervisor));
		}
		return items;
	}

	public WorkflowTemplate getWorkflowTemplate() {
		return workflowTemplate;
	}

	public List<DeployMessage> getErrorMessages() {
		return this.errorMessages;
	}

	public void closeErrorMessageWindow() {
		this.errorMessages = null;
	}

	public void setModelConfiguration(ModelConfiguration modelConfiguration) {
		this.modelConfiguration = modelConfiguration;
	}

	public boolean isShowWorkflowSettings() {
		return showWorkflowSettings;
	}

	public void setShowWorkflowSettings(boolean showWorkflowSettings) {
		this.showWorkflowSettings = showWorkflowSettings;
	}

	public String load(ValueChangeEvent event) {
		showSupervisorSettings = false;
		if (event != null && event.getNewValue() != null && this.workflowTemplateId != null) {
			this.workflowTemplateId = Long.parseLong(event.getNewValue().toString());
			return loadWorkflowTemplate();
		} else
			return null;
	}

	public void removeSelection() {
		for (BaseWorkflowModel baseWorkflowModel : getWorkflowComponents()) {
			baseWorkflowModel.setSelected(false);
		}
	}

	public UIInput getReminderBodyInput() {
		return reminderBodyInput;
	}

	public void setReminderBodyInput(UIInput reminderBodyInput) {
		this.reminderBodyInput = reminderBodyInput;
	}

	public UIInput getNameInput() {
		return nameInput;
	}

	public void setNameInput(UIInput nameInput) {
		this.nameInput = nameInput;
	}

	public UIInput getDescriptionInput() {
		return descriptionInput;
	}

	public void setDescriptionInput(UIInput descriptionInput) {
		this.descriptionInput = descriptionInput;
	}

	public UIInput getReminderSubjectInput() {
		return reminderSubjectInput;
	}

	public void setReminderSubjectInput(UIInput reminderSubjectInput) {
		this.reminderSubjectInput = reminderSubjectInput;
	}

	public UIInput getAssignmentSubjectInput() {
		return assignmentSubjectInput;
	}

	public void setAssignmentSubjectInput(UIInput assignmentSubjectInput) {
		this.assignmentSubjectInput = assignmentSubjectInput;
	}

	public UIInput getAssignmentBodyInput() {
		return assignmentBodyInput;
	}

	public void setAssignmentBodyInput(UIInput assignmentBodyInput) {
		this.assignmentBodyInput = assignmentBodyInput;
	}

	public void setWorkflowTemplateDao(WorkflowPersistenceTemplateDAO workflowTemplateDao) {
		this.workflowTemplateDao = workflowTemplateDao;
	}

	public boolean isShowSupervisorSettings() {
		return showSupervisorSettings;
	}

	public void setShowSupervisorSettings(boolean showSupervisorSettings) {
		this.showSupervisorSettings = showSupervisorSettings;
	}

	public UIInput getSupervisorInput() {
		return supervisorInput;
	}

	public void setSupervisorInput(UIInput supervisorInput) {
		this.supervisorInput = supervisorInput;
	}
}
