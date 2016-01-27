package com.logicaldoc.core.document.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.logicaldoc.core.ExtendedAttributeOption;
import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.PersistentObject;
import com.logicaldoc.util.sql.SqlUtil;

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
				del(option, PersistentObject.DELETED_CODE_DEFAULT);
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
	public boolean delete(long id, int code) {
		ExtendedAttributeOption option = findById(id);
		del(option, code);

		return true;
	}

	private void del(ExtendedAttributeOption option, int code) {
		if (option != null) {
			option.setDeleted(code);
			option.setValue(option.getValue() + "." + option.getId());
		}
	}

	@Override
	public void deleteOrphaned(long templateId, Collection<String> currentAttributes) {
		try {
			if (currentAttributes == null || currentAttributes.isEmpty())
				return;
			StringBuffer buf = new StringBuffer(" ('");
			for (String name : currentAttributes) {
				if (buf.length() == 0)
					buf.append("('");
				else
					buf.append(",'");
				buf.append(SqlUtil.doubleQuotes(name));
				buf.append("'");
			}
			buf.append(") ");

			List<ExtendedAttributeOption> options = findByQuery(
					"from ExtendedAttributeOption _opt where _opt.templateId = ?1 and _opt.attribute not in "
							+ buf.toString(), new Object[] { templateId }, null);

			for (ExtendedAttributeOption option : options)
				del(option, PersistentObject.DELETED_CODE_DEFAULT);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
	}
}
