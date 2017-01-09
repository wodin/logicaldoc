package com.logicaldoc.core.communication;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.util.sql.SqlUtil;

/**
 * Hibernate implementation of <code>MessageTemplateDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.5
 */
@SuppressWarnings("unchecked")
public class HibernateMessageTemplateDAO extends HibernatePersistentObjectDAO<MessageTemplate> implements
		MessageTemplateDAO {

	public HibernateMessageTemplateDAO() {
		super(MessageTemplate.class);
		super.log = LoggerFactory.getLogger(HibernateMessageTemplateDAO.class);
	}

	@Override
	public List<MessageTemplate> findByLanguage(String language, long tenantId) {
		return findByWhere(" _entity.language='" + language + "' and _entity.tenantId=" + tenantId,
				"order by _entity.name", null);
	}

	@Override
	public List<MessageTemplate> findByTypeAndLanguage(String type, String language, long tenantId) {
		StringBuffer query = new StringBuffer("_entity.language='" + language + "' ");
		query.append(" and _entity.tenantId=" + tenantId);
		if (StringUtils.isNotEmpty(type))
			query.append(" and _entity.type='" + type + "' ");

		return findByWhere(query.toString(), "order by _entity.name", null);
	}

	@Override
	public MessageTemplate findByNameAndLanguage(String name, String language, long tenantId) {
		String lang = language;
		if (StringUtils.isEmpty(lang))
			lang = "en";

		List<MessageTemplate> buf = findByWhere(" _entity.language='" + lang + "' and _entity.name='" + name
				+ "' and _entity.tenantId=" + tenantId, null, null);
		if (buf != null && !buf.isEmpty())
			return buf.get(0);

		buf = findByWhere(" _entity.language='en' and _entity.name='" + name + "' and _entity.tenantId=" + tenantId,
				null, null);
		if (buf != null && !buf.isEmpty())
			return buf.get(0);

		return null;
	}

	@Override
	public boolean delete(long id, int code) {
		assert (code != 0);
		MessageTemplate template = (MessageTemplate) findById(id);
		if (template != null) {
			template.setDeleted(code);
			template.setName(template.getName() + "." + template.getId());
			saveOrUpdate(template);
		}
		return true;
	}

	@Override
	public List<MessageTemplate> findByName(String name, long tenantId) {
		return findByWhere(" _entity.name='" + SqlUtil.doubleQuotes(name) + "' and _entity.tenantId=" + tenantId, null,
				null);
	}
}