package com.logicaldoc.core.document.dao;

import java.util.List;

import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.document.DocumentTemplate;
import com.logicaldoc.util.sql.SqlUtil;

/**
 * Hibernate implementation of <code>DocumentTemplateDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class HibernateDocumentTemplateDAO extends HibernatePersistentObjectDAO<DocumentTemplate> implements
		DocumentTemplateDAO {
	public HibernateDocumentTemplateDAO() {
		super(DocumentTemplate.class);
		super.log = LogFactory.getLog(HibernateDocumentTemplateDAO.class);
	}

	@Override
	public List<DocumentTemplate> findAll() {
		return findByWhere(" 1=1", "order by _entity.name");
	}

	@Override
	public DocumentTemplate findByName(String name) {
		DocumentTemplate template = null;
		List<DocumentTemplate> coll = findByWhere("_entity.name = '" + SqlUtil.doubleQuotes(name) + "'", null);
		if (coll.size() > 0) {
			template = coll.iterator().next();
		}
		if (template != null && template.getDeleted() == 1)
			template = null;
		return template;
	}

	@Override
	public boolean delete(long id) {
		boolean result = true;

		try {
			DocumentTemplate template = (DocumentTemplate) getHibernateTemplate().get(DocumentTemplate.class, id);
			if (template != null) {
				template.setDeleted(1);
				template.setName(template.getName() + "." + template.getId());
				getHibernateTemplate().saveOrUpdate(template);
			}
		} catch (Throwable e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}
}