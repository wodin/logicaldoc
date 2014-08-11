package com.logicaldoc.core.document.dao;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.logicaldoc.core.ExtendedAttributeOption;
import com.logicaldoc.core.HibernatePersistentObjectDAO;

/**
 * Hibernate implementation of <code>ExtendedAttributeOptionDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.1
 */
@SuppressWarnings("unchecked")
public class HibernateExtendedAttributeOptionDAO extends HibernatePersistentObjectDAO<ExtendedAttributeOption>
		implements ExtendedAttributeOptionDAO {

	public HibernateExtendedAttributeOptionDAO() {
		super(ExtendedAttributeOption.class);
		super.log = LoggerFactory.getLogger(HibernatePersistentObjectDAO.class);
	}

	@Override
	public boolean deleteByTemplateIdAndAttribute(long templateId, String attribute) {
		boolean result = true;
		try {
			List<ExtendedAttributeOption> options = findByTemplateAndAttribute(templateId, attribute);
			for (ExtendedAttributeOption option : options)
				del(option);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			result = false;
		}
		return result;
	}

	@Override
	public List<ExtendedAttributeOption> findByTemplateAndAttribute(long templateId, String attribute) {
		List<ExtendedAttributeOption> coll = new ArrayList<ExtendedAttributeOption>();
		try {
			if (StringUtils.isEmpty(attribute))
				coll = (List<ExtendedAttributeOption>) findByQuery(
						"from ExtendedAttributeOption _opt where _opt.templateId = ?1 order by _opt.position asc",
						new Object[] { new Long(templateId) }, null);
			else
				coll = (List<ExtendedAttributeOption>) findByQuery(
						"from ExtendedAttributeOption _opt where _opt.templateId = ?1 and _opt.attribute = ?2 order by _opt.position asc",
						new Object[] { new Long(templateId), attribute }, null);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
		return coll;
	}

	@Override
	public boolean delete(long id) {
		ExtendedAttributeOption option = findById(id);
		del(option);

		return true;
	}

	private void del(ExtendedAttributeOption option) {
		if (option != null) {
			option.setDeleted(1);
			option.setValue(option.getValue() + "." + option.getId());
		}
	}

}
