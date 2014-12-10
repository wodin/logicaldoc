package com.logicaldoc.core.document.dao;

import java.util.List;

import org.slf4j.LoggerFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.document.DocumentTemplate;
import com.logicaldoc.util.sql.SqlUtil;

/**
 * Hibernate implementation of <code>DocumentTemplateDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
@SuppressWarnings("unchecked")
public class HibernateDocumentTemplateDAO extends HibernatePersistentObjectDAO<DocumentTemplate> implements
		DocumentTemplateDAO {

	private ExtendedAttributeOptionDAO optionsDao;

	public HibernateDocumentTemplateDAO() {
		super(DocumentTemplate.class);
		super.log = LoggerFactory.getLogger(HibernateDocumentTemplateDAO.class);
	}

	@Override
	public List<DocumentTemplate> findAll() {
		return findByWhere(" 1=1", "order by _entity.name", null);
	}

	@Override
	public List<DocumentTemplate> findAll(long tenantId) {
		return findByWhere(" _entity.tenantId=" + tenantId, "order by _entity.name", null);
	}

	@Override
	public DocumentTemplate findByName(String name, long tenantId) {
		DocumentTemplate template = null;
		List<DocumentTemplate> coll = findByWhere("_entity.name = '" + SqlUtil.doubleQuotes(name)
				+ "' and _entity.tenantId=" + tenantId, null, null);
		if (coll.size() > 0)
			template = coll.iterator().next();
		if (template != null && template.getDeleted() == 1)
			template = null;
		return template;
	}

	@Override
	public boolean delete(long id, int code) {
		boolean result = true;

		try {
			DocumentTemplate template = (DocumentTemplate) findById(id);
			if (template != null) {
				template.setDeleted(code);
				template.setName(template.getName() + "." + template.getId());
				saveOrUpdate(template);
			}

			optionsDao.deleteByTemplateIdAndAttribute(id, null);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	@Override
	public int countDocs(long id) {
		return queryForInt("select count(*) from ld_document where ld_deleted=0 and ld_templateid=" + id);
	}

	@Override
	public List<DocumentTemplate> findByType(int type, long tenantId) {
		return findByWhere("_entity.type =" + type + " and _entity.tenantId=" + tenantId,
				"order by _entity.category asc", null);
	}

	public void setOptionsDao(ExtendedAttributeOptionDAO optionsDao) {
		this.optionsDao = optionsDao;
	}

}