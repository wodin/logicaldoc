package com.logicaldoc.workflow.editor.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.logicaldoc.workflow.editor.controll.EditController;
import com.logicaldoc.workflow.editor.message.DeployMessage;

public abstract class BaseWorkflowModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;

	private String name;
	
	public BaseWorkflowModel() {
		id = UUID.randomUUID().toString();
	}

	public BaseWorkflowModel(BaseWorkflowModel workflowModel){
		this.copy(workflowModel);
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public abstract String getType();
	
	public abstract String getImage();

	public abstract String getTemplate();
	
	public abstract EditController getController();
	
	public abstract boolean isPossibleStartState();
	
	public abstract void checkForDeploy(List<DeployMessage> failures);
	
	public BaseWorkflowModel copy(BaseWorkflowModel baseWorkflowModel) {
		this.id = baseWorkflowModel.id;
		this.name = baseWorkflowModel.name;
		
		return this;
	}

}
