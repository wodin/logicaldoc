package com.logicaldoc.workflow.editor.message;

import com.logicaldoc.workflow.editor.model.BaseWorkflowModel;

public class DeployMessage {
	
	private BaseWorkflowModel model;
	
	private String message;
	
	public DeployMessage(BaseWorkflowModel baseWorkflowModel , String message){
		this.message = message;
		this.model = baseWorkflowModel;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public void setModel(BaseWorkflowModel model) {
		this.model = model;
	}

	public String getMessage() {
		return message;
	}
	
	public BaseWorkflowModel getModel() {
		return model;
	}
}
