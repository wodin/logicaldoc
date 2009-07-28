package com.logicaldoc.workflow.editor;

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
import com.logicaldoc.workflow.model.WorkflowTemplate;
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

	private WorkflowTemplateLoader workflowTemplateLoader;

	private WorkflowTemplate workflowTemplate;
		
	private Long workflowTemplateId;
	
	public WorkflowTemplateManager(){
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

	public void setWorkflowTemplateLoader(WorkflowTemplateLoader workflowTemplateLoader) {
		this.workflowTemplateLoader = workflowTemplateLoader;
	}

	private BaseWorkflowModel getWorkflowComponentById(String taskId) {
		BaseWorkflowModel baseWorkflowModel = null;
		for (BaseWorkflowModel model : this.workflowTemplate.getWorkflowComponents()) {
			if (model.getId().equals(taskId)) {
				return baseWorkflowModel;
			}
		}
		return null;
	}

	
	public void setBPMModelController(HashMap<String, EditController> bpmModelController) {
		this.instantiatedCmponents = bpmModelController;
	}
	
	public void setInstantiatedCmponents(
			HashMap<String, EditController> instantiatedCmponents) {
		this.instantiatedCmponents = instantiatedCmponents;
	}

	@SuppressWarnings("unchecked")
	public void initializing() {
		TRANSMITTER.XML_DATA = "";
		this.workflowTemplate = new WorkflowTemplate();
		this.persistenceTemplate = new WorkflowPersistenceTemplate();
		this.workflowService = (WorkflowService)Context.getInstance().getBean("workflowService");
		this.workflowTransformService = (WorkflowTransformService) Context.getInstance().getBean("workflowTransformService");
		this.instantiatedCmponents = (HashMap<String, EditController>) Context.getInstance().getBean("bpmModelController");
		

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

	public String addWorkflowComponent(ActionEvent ae) {
		UIParameter param = (UIParameter) ((UIComponent) ae.getSource()).getChildren().get(0);
		EditController editController = this.instantiatedCmponents.get(param.getValue());

		if (editController == null)
			throw new WorkflowEditorException("No match with an instantiated controller");

		BaseWorkflowModel workflowModel = editController.instantiateNew();
		
		this.workflowTemplate.getWorkflowComponents().add(workflowModel);

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
		this.workflowTemplate.getWorkflowComponents().clear();

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

	public List<SelectItem> getAvailableWorkflowTemplates() {
		List<SelectItem> workflowTemplates = new LinkedList<SelectItem>();
		
		for(WorkflowPersistenceTemplate template : this.workflowTemplateLoader.getAvailableWorkflowTemplates())
			workflowTemplates.add( new SelectItem( template.getId(),template.getName() ) );
		
		return workflowTemplates;
		
	}

	public void deleteWorkflowComponent(ActionEvent actionEvent) {
		UIComponent component = (UIComponent) actionEvent.getSource();
		BaseWorkflowModel model = (BaseWorkflowModel) ((UIParameter) component.getChildren().get(0)).getValue();

		this.workflowTemplate.getWorkflowComponents().remove(model);
	}

	public String saveCurrentWorkflowTemplate() {
		
		this.workflowTemplate.setName(this.persistenceTemplate.getName());
		
		String xmlData = xstream.toXML(this.workflowTemplate);
		this.persistenceTemplate.setXmldata(xmlData);
		Long id = this.workflowTemplateLoader.saveWorkflowTemplate(this.persistenceTemplate, WorkflowTemplateLoader.WORKFLOW_STAGE.SAVED);
		this.persistenceTemplate.setId( id );
		
		return null;
	}
	
	private void setAllItemsToNull(){
		this.persistenceTemplate = null;
		this.workflowTemplate = null;
		this.workflowTemplateId = null;
		
	}

	public String createNewWorkflowTemplate() {
		
		initializing();
		
		
		return null;
	}

	public WorkflowPersistenceTemplate getPersistenceTemplate() {
		return persistenceTemplate;
	}

	public String closeCurrentWorkflowTemplate(){
		
		setAllItemsToNull();
		
		return null;
	}
	
	public String deleteWorkflowTemplate() {
		
		WorkflowPersistenceTemplate workflowTemplate = this.workflowTemplateLoader
				.loadWorkflowTemplate(this.workflowTemplateId,
						WorkflowTemplateLoader.WORKFLOW_STAGE.SAVED);
		
		this.workflowTemplateLoader.deleteWorkflowTemplate(workflowTemplate);

		if(this.persistenceTemplate.getId() == workflowTemplate.getId())
			initializing();
		
		initializing();
		
		return null;
	}

	public String deployWorkflowTemplate() {

		WorkflowPersistenceTemplate workflowTemplate = this.workflowTemplateLoader
				.loadWorkflowTemplate(this.workflowTemplateId,
						WorkflowTemplateLoader.WORKFLOW_STAGE.SAVED);

		// at first we have to delete the current workflow instance
		// TODO:we should add API-improvements to handle more clearer this
		List<WorkflowDefinition> definitions = this.workflowService.getAllDefinitions();

		for (WorkflowDefinition definition : definitions) {
			if (definition.getName().equals(workflowTemplate.getName()))
				this.workflowService.undeployWorkflow(definition.getDefinitionId());
		}

		this.persistenceTemplate.setXmldata( xstream.toXML(this.workflowTemplate) );
		this.workflowTemplateLoader.deployWorkflowTemplate(persistenceTemplate);
		this.workflowService.deployWorkflow(this.workflowTemplate);
		
		return null;
	}

	public String getXMLData() {
		return TRANSMITTER.XML_DATA;
	}

	public void setXMLData(String s) {

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
		
		this.initializing();
		this.persistenceTemplate = this.workflowTemplateLoader.loadWorkflowTemplate(this.workflowTemplateId, WorkflowTemplateLoader.WORKFLOW_STAGE.SAVED);
		if(this.persistenceTemplate.getXmldata() != null && ((String)this.persistenceTemplate.getXmldata()).getBytes().length > 0){
			this.workflowTemplate = this.workflowTransformService.fromWorkflowDefinitionToObject(persistenceTemplate);
		}
		
		return null;
	}
	
	public WorkflowTemplate getWorkflowTemplate() {
		return workflowTemplate;
	}
	
	
}
