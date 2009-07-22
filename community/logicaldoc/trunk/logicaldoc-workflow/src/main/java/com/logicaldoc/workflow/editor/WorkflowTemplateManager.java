package com.logicaldoc.workflow.editor;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icesoft.faces.component.dragdrop.DndEvent;
import com.icesoft.faces.component.dragdrop.DragEvent;
import com.icesoft.faces.component.dragdrop.DropEvent;
import com.icesoft.faces.component.ext.HtmlCommandLink;
import com.icesoft.faces.component.ext.HtmlPanelGroup;
import com.icesoft.faces.component.ext.RowSelectorEvent;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.document.DocumentNavigation;
import com.logicaldoc.web.navigation.PageContentBean;
import com.logicaldoc.web.util.FacesUtil;
import com.logicaldoc.workflow.WorkflowService;
import com.logicaldoc.workflow.debug.TRANSMITTER;
import com.logicaldoc.workflow.editor.controll.DragAndDropSupportController;
import com.logicaldoc.workflow.editor.controll.EditController;
import com.logicaldoc.workflow.editor.controll.DragAndDropSupportController.Container;
import com.logicaldoc.workflow.editor.model.BaseWorkflowModel;
import com.logicaldoc.workflow.editor.model.Transition;
import com.logicaldoc.workflow.editor.model.WorkflowEditorException;
import com.logicaldoc.workflow.model.WorkflowDefinition;
import com.logicaldoc.workflow.persistence.WorkflowPersistenceTemplate;
import com.logicaldoc.workflow.transform.WorkflowTransformService;
import com.thoughtworks.xstream.XStream;

public class WorkflowTemplateManager {

	private WorkflowTransformService workflowTransformService;

	private WorkflowService workflowService;

	private WorkflowPersistenceTemplate persistenceTemplate;

	private XStream xstream = new XStream();

	private HashMap<String, EditController> instantiatedCmponents = new HashMap<String, EditController>();

	protected static Log log = LogFactory.getLog(WorkflowTemplateManager.class);

	private BaseWorkflowModel component;

	private EditController controller;

	private List<BaseWorkflowModel> workflowComponents;

	private WorkflowTemplateLoader workflowTemplateLoader;

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

	public void setWorkflowTemplateLoader(WorkflowTemplateLoader workflowTemplateLoader) {
		this.workflowTemplateLoader = workflowTemplateLoader;
	}

	private BaseWorkflowModel getWorkflowComponentById(String taskId) {
		BaseWorkflowModel baseWorkflowModel = null;
		for (BaseWorkflowModel model : this.workflowComponents) {
			if (model.getId().equals(taskId)) {
				return baseWorkflowModel;
			}
		}
		return null;
	}

	public WorkflowTemplateManager() {

	}

	public void setBPMModelController(HashMap<String, EditController> bpmModelController) {
		this.instantiatedCmponents = bpmModelController;
	}

	@SuppressWarnings("unchecked")
	public void initializing() {
		TRANSMITTER.XML_DATA = "";
		this.workflowComponents = new LinkedList<BaseWorkflowModel>();
		this.persistenceTemplate = new WorkflowPersistenceTemplate();
		this.instantiatedCmponents = (HashMap<String, EditController>) Context.getInstance().getBean(
				"bpmModelController");
		this.workflowTransformService = (WorkflowTransformService) Context.getInstance().getBean(
				"workflowTransformService");
		this.xstream = (XStream) Context.getInstance().getBean("xStream");
		this.workflowService = (WorkflowService) Context.getInstance().getBean("workflowService");
	}

	public List<SelectItem> getTimeUnits() {

		List<SelectItem> timeValues = new LinkedList<SelectItem>();
		timeValues.add(new SelectItem("Minute"));
		timeValues.add(new SelectItem("Hour"));
		timeValues.add(new SelectItem("Business Hour"));
		timeValues.add(new SelectItem("Day"));
		timeValues.add(new SelectItem("Business Day"));
		timeValues.add(new SelectItem("Week"));
		timeValues.add(new SelectItem("Business Week"));

		return timeValues;
	}

	public void setWorkflowTasks(List<BaseWorkflowModel> workflowComponents) {
		this.workflowComponents = workflowComponents;
	}

	public String addWorkflowComponent(ActionEvent ae) {
		UIParameter param = (UIParameter) ((UIComponent) ae.getSource()).getChildren().get(0);
		EditController editController = this.instantiatedCmponents.get(param.getValue());

		if (editController == null)
			throw new WorkflowEditorException("No match with an instantiated controller");

		BaseWorkflowModel workflowModel = editController.instantiateNew();

		/*
		 * WorkflowTask workflowTask = new WorkflowTask();
		 * workflowTask.setName("Task" + new Random().nextInt());
		 * 
		 * Transition approveTransition = new Transition();
		 * approveTransition.setName("Approve");
		 * 
		 * Transition rejectTransition = new Transition();
		 * rejectTransition.setName("reject");
		 * 
		 * workflowTask.addTransition(approveTransition);
		 * workflowTask.addTransition(rejectTransition);
		 */
		this.workflowComponents.add(workflowModel);

		return null;
	}

	public String removeWorkflowComponent() {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
				.getRequest();
		BaseWorkflowModel baseWorkflowModel = (BaseWorkflowModel) request.getAttribute("component");

		this.workflowComponents.remove(baseWorkflowModel);

		return null;

	}

	public List<BaseWorkflowModel> getWorkflowComponents() {
		return this.workflowComponents;
	}

	public Integer getWorkflowComponentSize() {
		return this.workflowComponents.size();
	}

	public void addTransition() {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
				.getRequest();

		String componentId = request.getParameter("componentId");

		BaseWorkflowModel signedTask = getWorkflowComponentById(componentId);
		signedTask.getTransitions().add(new Transition());

	}

	public String setupWorkflow() {
		DocumentNavigation documentNavigation = ((DocumentNavigation) FacesUtil.accessBeanFromFacesContext(
				"documentNavigation", FacesContext.getCurrentInstance(), log));
		documentNavigation.setSelectedPanel(new PageContentBean("workflow/wizard"));

		return null;
	}

	public String removeAllWorkflowComponents() {
		this.workflowComponents.clear();

		if (this.controller != null)
			this.controller.invalidate();

		this.controller = null;

		this.component = null;

		return null;
	}

	public void selectComponent(ActionEvent actionEvent) {

		HtmlCommandLink commandLink = (HtmlCommandLink) actionEvent.getSource();

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

		if (this.controller != null)
			this.controller.invalidate();

		this.component = baseWorkflowModel;

		this.controller = baseWorkflowModel.getController();

		// initializing the controller
		this.controller.initialize(baseWorkflowModel);

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
				DragAndDropSupportController dndController = ((DragAndDropSupportController) this.instantiatedCmponents
						.get(cnt.droppingZone.getClass().getSimpleName()));

				dndController.droppedObject(cnt);
			} catch (Exception e) {
				throw new WorkflowEditorException(e);
			}
		}

	}


	public void rowSelectionListener(RowSelectorEvent event) {

		this.persistenceTemplate = this.getAvailableWorkflowTemplates().get(event.getRow());

		try {
			this.workflowComponents = (List<BaseWorkflowModel>) xstream.fromXML((String) this.persistenceTemplate
					.getXmldata());

			if (this.workflowComponents == null)
				this.workflowComponents = new LinkedList<BaseWorkflowModel>();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public List<WorkflowPersistenceTemplate> getAvailableWorkflowTemplates() {

		return this.workflowTemplateLoader.getAvailableWorkflowTemplates();
	}

	public Long saveWorkflowTemplate(WorkflowPersistenceTemplate persistenceTemplate) {

		return null;

	}

	public void deleteWorkflowComponent(ActionEvent actionEvent) {
		UIComponent component = (UIComponent) actionEvent.getSource();
		BaseWorkflowModel model = (BaseWorkflowModel) ((UIParameter) component.getChildren().get(0)).getValue();

		this.workflowComponents.remove(model);
	}

	public void saveCurrentWorkflowTemplate() {
		String xmlData = xstream.toXML(this.workflowComponents);
		this.persistenceTemplate.setXmldata(xmlData);
		this.workflowTemplateLoader.saveWorkflowTemplate(this.persistenceTemplate);
	}

	public String createNewWorkflowTemplate() {
		this.initializing();

		return null;
	}

	public WorkflowPersistenceTemplate getPersistenceTemplate() {
		return persistenceTemplate;
	}

	public String deleteWorkflowTemplate() {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
				.getRequest();
		String workflowTemplateId = request.getParameter("id");
		WorkflowPersistenceTemplate workflowTemplate = this.workflowTemplateLoader.loadWorkflowTemplate(Long
				.parseLong(workflowTemplateId));
		this.workflowTemplateLoader.deleteWorkflowTemplate(workflowTemplate);

		return null;
	}

	public String deployWorkflowTemplate() {
		TRANSMITTER.XML_DATA = "";

		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
				.getRequest();
		String workflowTemplateId = request.getParameter("id");

		WorkflowPersistenceTemplate workflowTemplate = this.workflowTemplateLoader.loadWorkflowTemplate(Long
				.parseLong(workflowTemplateId));

		// at first we have to delete the current workflow instance
		// TODO:we should add API-improvements to handle more clearer this
		List<WorkflowDefinition> definitions = this.workflowService.getAllDefinitions();

		for (WorkflowDefinition definition : definitions) {
			if (definition.getName().equals(workflowTemplate.getName()))
				this.workflowService.undeployWorkflow(definition.getDefinitionId());
		}

		Object fromObjectToWorkflowDefinition = this.workflowTransformService
				.fromObjectToWorkflowDefinition(workflowTemplate);
		this.workflowService.deployWorkflow(workflowTemplate, (Serializable) fromObjectToWorkflowDefinition);
		return null;
	}

	public String getXMLData() {
		return TRANSMITTER.XML_DATA;
	}

	public void setXMLData(String s) {

	}

	public List<SelectItem> getPossibleStartStates() {
		List<SelectItem> possibleStartStates = new LinkedList<SelectItem>();

		for (BaseWorkflowModel model : this.workflowComponents) {

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

	public void loadWorkflowTemplate() {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
				.getRequest();
		String workflowTemplateId = request.getParameter("id");
		this.initializing();
		this.persistenceTemplate = this.workflowTemplateLoader.loadWorkflowTemplate(Long.parseLong(workflowTemplateId));
		this.workflowComponents = this.workflowTransformService.fromWorkflowDefinitionToObject(persistenceTemplate);
	}
}
