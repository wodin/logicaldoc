package com.logicaldoc.core.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.PersistentObject;
import com.logicaldoc.util.sql.SqlUtil;

/**
 * Hibernate implementation of <code>AttributeOptionDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.1
 */
@SuppressWarnings("unchecked")
public class HibernateAttributeOptionDAO extends HibernatePersistentObjectDAO<AttributeOption> implements
		AttributeOptionDAO {

	public HibernateAttributeOptionDAO() {
		super(AttributeOption.class);
		super.log = LoggerFactory.getLogger(HibernatePersistentObjectDAO.class);
	}

	@Override
	public boolean deleteBySetIdAndAttribute(long setId, String attribute) {
		boolean result = true;
		try {
			List<AttributeOption> options = findBySetIdAndAttribute(setId, attribute);
			for (AttributeOption option : options)
				del(option, PersistentObject.DELETED_CODE_DEFAULT);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			result = false;
		}
		return result;
	}

	@Override
	public List<AttributeOption> findBySetIdAndAttribute(long setId, String attribute) {
		List<AttributeOption> coll = new ArrayList<AttributeOption>();
		try {
			if (StringUtils.isEmpty(attribute))
				coll = (List<AttributeOption>) findByQuery(
						"from AttributeOption _opt where _opt.deleted=0 and _opt.setId = ?1 order by _opt.position asc",
						new Object[] { new Long(setId) }, null);
			else
				coll = (List<AttributeOption>) findByQuery(
						"from AttributeOption _opt where _opt.deleted=0 and _opt.setId = ?1 and _opt.attribute = ?2 order by _opt.position asc",
						new Object[] { new Long(setId), attribute }, null);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
		return coll;
	}

	@Override
	public boolean delete(long id, int code) {
		AttributeOption option = findById(id);
		del(option, code);

		return true;
	}

	private void del(AttributeOption option, int code) {
		if (option != null) {
			option.setDeleted(code);
			option.setValue(option.getValue() + "." + option.getId());
		}
	}

	@Override
	public void deleteOrphaned(long setId, Collection<String> currentAttributes) {
		try {
			if (currentAttributes == null || currentAttributes.isEmpty())
				return;
			StringBuffer buf = new StringBuffer();
			for (String name : currentAttributes) {
				if (buf.length() == 0)
					buf.append("('");
				else
					buf.append(",'");
				buf.append(SqlUtil.doubleQuotes(name));
				buf.append("'");
			}
			buf.append(")");

			List<AttributeOption> options = findByQuery(
					"from AttributeOption _opt where _opt.setId = ?1 and _opt.attribute not in " + buf.toString(),
					new Object[] { setId }, null);

			for (AttributeOption option : options)
				del(option, PersistentObject.DELETED_CODE_DEFAULT);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
	}
}
