package com.logicaldoc.core.document.dao;

import java.util.Collection;
import java.util.List;

import com.logicaldoc.core.ExtendedAttributeOption;
import com.logicaldoc.core.PersistentObjectDAO;

/**
 * DAO for <code>ExtendedAttributeOption</code> handling.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.1
 */
public interface ExtendedAttributeOptionDAO extends PersistentObjectDAO<ExtendedAttributeOption> {

	/**
	 * This method deletes options.
	 * 
	 * @param templateId ID of the template
	 * @param attribute Name of the attribute (optional)
	 */
	public boolean deleteByTemplateIdAndAttribute(long templateId, String attribute);
	
	/**
	 * This method deletes the orphaned options of a given template
	 * 
	 * @param templateId ID of the template
	 * @param currentAttributes Names of the attributes of the actual template
	 */
	public void deleteOrphaned(long templateId, Collection<String> currentAttributes);

	/**
	 * This finds all the options for a given attribute. The list is ordered by
	 * position asc.
	 * 
	 * @param templateId The template id
	 * @param attribute The attribute name (Optional)
	 * 
	 * @return The ordered list of options
	 */
	public List<ExtendedAttributeOption> findByTemplateAndAttribute(long templateId, String attribute);
}