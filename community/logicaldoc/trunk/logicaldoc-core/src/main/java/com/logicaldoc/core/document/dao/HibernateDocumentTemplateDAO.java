package com.logicaldoc.core.document.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.logicaldoc.core.document.DocumentTemplate;

/**
 * Hibernate implementation of <code>DocumentTemplateDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class HibernateDocumentTemplateDAO extends HibernateDaoSupport implements DocumentTemplateDAO {
	protected static Log log = LogFactory.getLog(HibernateDocumentTemplateDAO.class);

	@Override
	public boolean delete(long templateId) {
		try {
			DocumentTemplate template = (DocumentTemplate) getHibernateTemplate().get(DocumentTemplate.class,
					templateId);
			if (template != null) {
				template.setDeleted(1);
				getHibernateTemplate().saveOrUpdate(template);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			return false;
		}
	}

	@Override
	public List<DocumentTemplate> findAll() {
		List<DocumentTemplate> coll = new ArrayList<DocumentTemplate>();

		try {
			coll = (List<DocumentTemplate>) getHibernateTemplate().find("from DocumentTemplate order by name");
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return coll;
	}

	@Override
	public DocumentTemplate findById(long templateId) {
		DocumentTemplate template = null;

		try {
			template = (DocumentTemplate) getHibernateTemplate().get(DocumentTemplate.class, templateId);
			if (template != null && template.getDeleted() == 1)
				template = null;
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return template;
	}

	@Override
	public DocumentTemplate findByName(String name) {
		DocumentTemplate template = null;

		try {
			Collection<DocumentTemplate> coll = (Collection<DocumentTemplate>) getHibernateTemplate().find(
					"from DocumentTemplate _template where _template.name like '" + name + "'");
			if (coll.size() > 0) {
				template = coll.iterator().next();
			}
			if (template != null && template.getDeleted() == 1)
				template = null;
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return template;
	}

	@Override
	public boolean store(DocumentTemplate template) {
		boolean result = true;
		try {
			getHibernateTemplate().saveOrUpdate(template);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}
}