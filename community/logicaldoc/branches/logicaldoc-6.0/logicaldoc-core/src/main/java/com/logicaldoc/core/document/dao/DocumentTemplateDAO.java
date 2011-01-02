package com.logicaldoc.core.document.dao;

import com.logicaldoc.core.PersistentObjectDAO;
import com.logicaldoc.core.document.DocumentTemplate;

/**
 * This class is a DAO-service for document templates.
 * 
 * @author Marco Meschieri - Logical Objects
 * @version 1.0
 */
public interface DocumentTemplateDAO extends PersistentObjectDAO<DocumentTemplate> {
	/**
	 * This method finds a template by name.
	 * 
	 * @param name Name of the template.
	 * @return DocumentTemplate with given name.
	 */
	public DocumentTemplate findByName(String name);
}
