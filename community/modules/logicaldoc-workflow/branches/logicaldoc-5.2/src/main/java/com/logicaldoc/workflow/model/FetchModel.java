package com.logicaldoc.workflow.model;

public interface FetchModel {
	
	public static enum FETCH_TYPE {FORUPDATE, INFO};
	
	public boolean isUpdateable();
}
