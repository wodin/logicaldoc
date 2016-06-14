package com.logicaldoc.core.communication;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;

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
}