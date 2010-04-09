package com.logicaldoc.workflow.editor.controll;

import com.logicaldoc.workflow.editor.model.BaseWorkflowModel;
import com.logicaldoc.workflow.editor.model.EndState;


public class EndStateController implements EditController{

	@Override
	public void initialize(BaseWorkflowModel baseWorkflowModel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BaseWorkflowModel instantiateNew() {
		return new EndState();
	}

	@Override
	public void invalidate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void persist() {
		// TODO Auto-generated method stub
		
	}

}
