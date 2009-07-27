package com.logicaldoc.workflow;

import java.util.Map;

public interface TemplateService {
	
	public String transformToString(String text,
			Map<String, Object> modelProperties);
	
}
