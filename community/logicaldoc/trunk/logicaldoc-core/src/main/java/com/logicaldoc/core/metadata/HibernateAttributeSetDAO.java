package com.logicaldoc.core.metadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.util.sql.SqlUtil;

/**
 * Hibernate implementation of <code>TemplateDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
@SuppressWarnings("unchecked")
public class HibernateAttributeSetDAO extends HibernatePersistentObjectDAO<AttributeSet> implements AttributeSetDAO {

	private AttributeOptionDAO optionsDao;

	private TemplateDAO templateDao;

	public HibernateAttributeSetDAO() {
		super(AttributeSet.class);
		super.log = LoggerFactory.getLogger(HibernateAttributeSetDAO.class);
	}

	@Override
	public List<AttributeSet> findAll() {
		return findByWhere(" 1=1", "order by _entity.name", null);
	}

	@Override
	public List<AttributeSet> findAll(long tenantId) {
		return findByWhere(" _entity.tenantId=" + tenantId, "order by _entity.name", null);
	}

	@Override
	public AttributeSet findByName(String name, long tenantId) {
		AttributeSet template = null;
		List<AttributeSet> coll = findByWhere("_entity.name = '" + SqlUtil.doubleQuotes(name)
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
			AttributeSet set = (AttributeSet) findById(id);
			if (set == null)
				return true;

			set.setDeleted(code);
			set.setName(set.getName() + "." + set.getId());
			saveOrUpdate(set);

			optionsDao.deleteBySetIdAndAttribute(id, null);

			List<Template> templates = templateDao.findAll(set.getTenantId());
			for (Template template : templates)
				templateDao.store(template);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	@Override
	public List<AttributeSet> findByType(int type, long tenantId) {
		return findByWhere("_entity.type =" + type + " and _entity.tenantId=" + tenantId, "order by _entity.name asc",
				null);
	}

	public void setOptionsDao(AttributeOptionDAO optionsDao) {
		this.optionsDao = optionsDao;
	}

	@Override
	public boolean store(AttributeSet set) {
		// Enforce the set specifications in all the attributes
		for (Attribute att : set.getAttributes().values()) {
			att.setSetId(set.getId());
		}
		boolean result = super.store(set);
		for (Attribute att : set.getAttributes().values())
			att.setSetId(set.getId());
		super.store(set);
		optionsDao.deleteOrphaned(set.getId(), set.getAttributeNames());

		List<Template> templates = templateDao.findAll(set.getTenantId());
		for (Template template : templates) {
			templateDao.initialize(template);
			List<String> names = template.getAttributeNames(set.getId());
			for (String name : names) {
				if (set.getAttribute(name) != null) {
					// the attribute exists both in template and set so update
					// it
					try {
						template.getAttributes().put(name, (Attribute) set.getAttribute(name).clone());
					} catch (CloneNotSupportedException e) {

					}
				} else {
					// the attribute exists in template but not in the set so
					// remove it
					template.removeAttribute(name);
				}
			}
			templateDao.store(template);
		}

		return result;
	}

	public void setTemplateDao(TemplateDAO templateDao) {
		this.templateDao = templateDao;
	}

	@Override
	public Map<Long, AttributeSet> load(long tenantId) {
		Map<Long, AttributeSet> map = new HashMap<Long, AttributeSet>();
		List<AttributeSet> all = findAll(tenantId);
		for (AttributeSet set : all)
			map.put(set.getId(), set);
		return map;
	}
}