package com.logicaldoc.workflow.transform;

import java.util.Iterator;
import java.util.List;

import com.logicaldoc.workflow.editor.model.BaseWorkflowModel;

public abstract class TransformContext {
	
	private List<BaseWorkflowModel> workflowComponents;
	
	private Iterator<BaseWorkflowModel> iteratorModel;
	
	private BaseWorkflowModel currentModel;
	
	public TransformContext(List<BaseWorkflowModel> list){
		this.workflowComponents = list;
	}
	
	public boolean hasNext(){
		if(this.iteratorModel == null)
			this.iteratorModel = workflowComponents.iterator();
		
		return this.iteratorModel.hasNext();
	}
	
	public BaseWorkflowModel next(){
		
		if(this.iteratorModel == null)
			this.iteratorModel = workflowComponents.iterator();
		
		this.currentModel = this.iteratorModel.next();
		
		return this.currentModel;
	}
	
	public BaseWorkflowModel getCurrentBaseModel(){
		return this.currentModel;
	}
	
}
