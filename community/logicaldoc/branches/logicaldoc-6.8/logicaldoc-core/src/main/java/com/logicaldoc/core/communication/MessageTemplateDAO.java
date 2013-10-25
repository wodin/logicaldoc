package com.logicaldoc.core.communication;

import java.util.List;

import com.logicaldoc.core.PersistentObjectDAO;

/**
 * This is a DAO service for MessageTemplate.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.5
 */
public interface MessageTemplateDAO extends PersistentObjectDAO<MessageTemplate> {

	/**
	 * Finds the templates by given type and language.
	 */
	public List<MessageTemplate> findByLanguage(String language);

	/**
	 * Finds the template by the alternate key. If the template is not found for
	 * the specified language, the 'en' will be used instead.
	 * 
	 * @param name Name of the template
	 * @param language The language, if null 'en' will be used instead
	 * 
	 * @return The found template or the 'en' if none was found.
	 */
	public MessageTemplate findByNameAndLanguage(String name, String language);
}