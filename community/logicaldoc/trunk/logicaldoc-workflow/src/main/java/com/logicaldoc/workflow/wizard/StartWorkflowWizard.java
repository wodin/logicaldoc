package com.logicaldoc.workflow.wizard;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icesoft.faces.component.ext.RowSelectorEvent;
import com.icesoft.faces.component.paneltabset.PanelTab;
import com.icesoft.faces.component.paneltabset.PanelTabSet;
import com.icesoft.faces.component.paneltabset.TabChangeEvent;
import com.icesoft.faces.component.paneltabset.TabChangeListener;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.document.DocumentNavigation;
import com.logicaldoc.web.document.DocumentRecord;
import com.logicaldoc.web.document.DocumentsRecordsManager;
import com.logicaldoc.web.util.FacesUtil;
import com.logicaldoc.workflow.WorkflowConstants;
import com.logicaldoc.workflow.WorkflowService;
import com.logicaldoc.workflow.editor.WorkflowTemplateLoader;
import com.logicaldoc.workflow.editor.WorkflowTemplateLoader.WORKFLOW_STAGE;
import com.logicaldoc.workflow.editor.model.WorkflowTask;
import com.logicaldoc.workflow.model.WorkflowDefinition;
import com.logicaldoc.workflow.model.WorkflowInstance;
import com.logicaldoc.workflow.model.WorkflowTemplate;
import com.logicaldoc.workflow.persistence.WorkflowPersistenceTemplate;
import com.logicaldoc.workflow.transform.WorkflowTransformService;
import com.thoughtworks.xstream.XStream;

/**
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.0
 */
public class StartWorkflowWizard implements TabChangeListener {
	protected static Log log = LogFactory.getLog(StartWorkflowWizard.class);

	private WorkflowPersistenceTemplate persistenceTemplate;

	private WorkflowDefinition workflowDefinition;

	private WorkflowTemplateLoader workflowTemplateLoader;

	private WorkflowTransformService workflowTransformService;

	private WorkflowService workflowService;

	private String workingDir;

	private Integer priority;

	private WorkflowTask workflowTask;

	private WorkflowTemplate workflowTemplate;

	private DocumentNavigation documentNavigation;

	private XStream xStream;
	
	private DocumentsRecordsManager documentsRecordsManager;
	
	/**
	 * Binding used by example to listen
	 */
	private PanelTabSet tabSet;

	private boolean workflowStartSuccess;

	public StartWorkflowWizard() {
		this.documentsRecordsManager = (DocumentsRecordsManager)FacesUtil.accessBeanFromFacesContext("documentsRecordsManager", FacesContext.getCurrentInstance(), log);
		
		workflowTransformService = (WorkflowTransformService) Context
				.getInstance().getBean("workflowTransformService");
		workflowTemplateLoader = (WorkflowTemplateLoader) Context.getInstance()
				.getBean("WorkflowTemplateLoader");
		workflowService = (WorkflowService) Context.getInstance().getBean(
				"workflowService");
		this.documentNavigation = (DocumentNavigation) FacesUtil
				.accessBeanFromFacesContext("documentNavigation", FacesContext
						.getCurrentInstance(), log);

	}

	public void setXStream(XStream stream) {
		xStream = stream;
	}
	
	private void init() {

	}

	public List<SelectItem> getPriorities() {

		List<SelectItem> priorities = new LinkedList<SelectItem>();
		priorities.add(new SelectItem(0, "Low"));
		priorities.add(new SelectItem(1, "Medium"));
		priorities.add(new SelectItem(2, "High"));

		return priorities;

	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setWorkflowTemplateLoader(
			WorkflowTemplateLoader workflowTemplateLoader) {
		this.workflowTemplateLoader = workflowTemplateLoader;
	}

	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	public void setPersistenceTemplate(
			WorkflowPersistenceTemplate persistenceTemplate) {
		this.persistenceTemplate = persistenceTemplate;
	}

	public WorkflowPersistenceTemplate getPersistenceTemplate() {
		return persistenceTemplate;
	}

	public void rowSelectionListener(RowSelectorEvent event) {

		setupAllPanels();
		
		this.workflowDefinition = this.workflowService.getAllDefinitions().get(
				event.getRow());

		WorkflowPersistenceTemplate workflowPersistenceTemplate = this.workflowTemplateLoader
				.loadWorkflowTemplate(this.workflowDefinition.getName(),
						WORKFLOW_STAGE.DEPLOYED);

		this.workflowTemplate = this.workflowTransformService
				.fromWorkflowDefinitionToObject(workflowPersistenceTemplate);

	}

	public WorkflowDefinition getWorkflowDefinition() {
		return workflowDefinition;
	}

	public String cancel() {

		this.documentNavigation.showDocuments();

		return null;
	}

	public String startWorkflow() {

		Map<String, Serializable> properties = new HashMap<String, Serializable>();
		
		properties.put(WorkflowConstants.VAR_TEMPLATE, (Serializable)xStream.toXML(this.workflowTemplate));
	
		Set<Long> documents = new LinkedHashSet<Long>();
		
		for(DocumentRecord doc : this.documentsRecordsManager.getSelection())
			documents.add(doc.getDocId());
		
		properties.put(WorkflowConstants.VAR_DOCUMENTS, (Serializable)documents);
		
		WorkflowInstance instance = this.workflowService.startWorkflow(
				workflowDefinition, properties);
		this.workflowService.signal(instance.id);
		
		return null;
	}

	/*
	 * Steps
	 */
	public String prepareSelectedWorkflow() {

		init();

		return null;
	}

	public String editOverallWorkflowSettings() {

		

		return null;
	}

	@SuppressWarnings("unchecked")
	public void setupAllPanels() {
		int currentTab = tabSet.getSelectedIndex();
		currentTab++;

		if (currentTab < 6) {
			tabSet.setSelectedIndex(currentTab);

			
			List<UIComponent> panels = tabSet.getChildren();

			for (int i = 0; i < panels.size(); i++) {
				PanelTab panel = (PanelTab) panels.get(i);

				panel.setDisabled(false);
			}
			
			
		}
	}

	public void prev() {
		int currentTab = tabSet.getSelectedIndex();
		currentTab--;

		if (currentTab > 0) {
			tabSet.setSelectedIndex(currentTab);
		}
	}

	/**
	 * Called when the table binding's tab focus changes.
	 * 
	 * @param tabChangeEvent
	 *            used to set the tab focus.
	 * @throws AbortProcessingException
	 *             An exception that may be thrown by event listeners to
	 *             terminate the processing of the current event.
	 */
	public void processTabChange(TabChangeEvent tabChangeEvent)
			throws AbortProcessingException {
		log.info("processTabChange: sss = " + tabChangeEvent.getNewTabIndex());
	}

	/**
	 * Gets the tabbed pane object bound to this bean.
	 * 
	 * @return bound tabbed pane.
	 */
	public PanelTabSet getTabSet() {
		return tabSet;
	}

	/**
	 * Set a tabbed pane object which will be bound to this object
	 * 
	 * @param tabSet
	 *            new PanelTabSet object.
	 */
	public void setTabSet(PanelTabSet tabSet) {
		this.tabSet = tabSet;
	}

	public WorkflowTemplate getWorkflowTemplate() {
		return workflowTemplate;
	}

	/**
	 * 
	 */
	public void changeAssignments(ActionEvent actionEvent) {
		UIComponent cmp = (UIComponent) actionEvent.getSource();
		this.workflowTask = (WorkflowTask) ((UIParameter) cmp.getChildren()
				.get(0)).getValue();
	}

	public WorkflowTask getWorkflowTask() {
		return workflowTask;
	}
}