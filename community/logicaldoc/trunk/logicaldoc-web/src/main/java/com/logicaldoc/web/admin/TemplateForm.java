package com.logicaldoc.web.admin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.DocumentTemplate;

/**
 * Form for template editing
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class TemplateForm {
	protected static Log log = LogFactory.getLog(TemplateForm.class);

	private DocumentTemplate template;

	public DocumentTemplate getTemplate() {
		return template;
	}

	public void setTemplate(DocumentTemplate template) {
		this.template = template;
	}
}
