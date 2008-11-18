package com.logicaldoc.core.document.dao;

import java.util.List;

import com.logicaldoc.core.document.DocumentTemplate;

/**
 * This class is a DAO-service for document templates.
 * 
 * @author Marco Meschieri - Logical Objects
 * @version 1.0
 */
public interface DocumentTemplateDAO {
	/**
	 * This method persists a template object.
	 * 
	 * @param template DocumentTemplate to be stored.
	 * @return True if successfully stored in a database.
	 */
	public boolean store(DocumentTemplate template);

	/**
	 * This method deletes a template.
	 * 
	 * @param templateId ID of the template which should be delete.
	 */
	public boolean delete(long templateId);
	
	/**
	 * This method finds a template by ID.
	 * 
	 * @param templateId ID of the template.
	 * @return DocumentTemplate with given ID.
	 */
	public DocumentTemplate findById(long templateId);

	/**
	 * This method selects all templates ordered by name.
	 */
	public List<DocumentTemplate> findAll();
	
	/**
	 * This method finds a template by name.
	 * 
	 * @param name Name of the template.
	 * @return DocumentTemplate with given name.
	 */
	public DocumentTemplate findByName(String name);
}
