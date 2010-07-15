package com.logicaldoc.web.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.ExtendedAttribute;
import com.logicaldoc.core.document.DocumentTemplate;
import com.logicaldoc.core.document.dao.DocumentTemplateDAO;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIExtendedAttribute;
import com.logicaldoc.gui.common.client.beans.GUITemplate;
import com.logicaldoc.gui.frontend.client.services.TemplateService;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.SessionUtil;

/**
 * Implementation of the TemplateService
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class TemplateServiceImpl extends RemoteServiceServlet implements TemplateService {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(TemplateServiceImpl.class);

	@Override
	public void delete(String sid, long templateId) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		DocumentTemplateDAO dao = (DocumentTemplateDAO) Context.getInstance().getBean(DocumentTemplateDAO.class);
		dao.delete(templateId);
	}

	@Override
	public GUITemplate save(String sid, GUITemplate template) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		DocumentTemplateDAO dao = (DocumentTemplateDAO) Context.getInstance().getBean(DocumentTemplateDAO.class);
		try {
			DocumentTemplate templ;
			if (template.getId() != 0) {
				templ = dao.findById(template.getId());
				dao.initialize(templ);
			} else {
				templ = new DocumentTemplate();
			}

			templ.setName(template.getName());
			templ.setDescription(template.getDescription());
			Map<String, ExtendedAttribute> attrs = new HashMap<String, ExtendedAttribute>();
			if (template.getAttributes() != null && template.getAttributes().length > 0) {
				templ.getAttributes().clear();
				for (GUIExtendedAttribute attribute : template.getAttributes()) {
					if (attribute != null) {
						ExtendedAttribute att = new ExtendedAttribute();
						att.setPosition(attribute.getPosition());
						att.setMandatory(attribute.isMandatory() ? 1 : 0);
						att.setType(attribute.getType());
						if (attribute.getValue() instanceof String)
							att.setStringValue(attribute.getStringValue());
						else if (attribute.getValue() instanceof Long)
							att.setIntValue(attribute.getIntValue());
						else if (attribute.getValue() instanceof Double)
							att.setDoubleValue(attribute.getDoubleValue());
						else if (attribute.getValue() instanceof Date)
							att.setDateValue(attribute.getDateValue());
						attrs.put(attribute.getName(), att);
					}
				}
			}
			if (attrs.size() > 0)
				templ.setAttributes(attrs);

			dao.store(templ);

			template.setId(templ.getId());
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}

		// TODO Rinumerare gli attributi in base all'ordine nell'array
		return template;
	}

	@Override
	public GUITemplate getTemplate(String sid, long templateId) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		DocumentTemplateDAO dao = (DocumentTemplateDAO) Context.getInstance().getBean(DocumentTemplateDAO.class);
		try {
			DocumentTemplate template = dao.findById(templateId);
			if (template == null)
				return null;

			GUITemplate templ = new GUITemplate();
			templ.setId(templateId);
			templ.setName(template.getName());
			templ.setDescription(template.getDescription());

			GUIExtendedAttribute[] attributes = new GUIExtendedAttribute[template.getAttributeNames().size()];
			int i = 0;
			for (String attrName : template.getAttributeNames()) {
				ExtendedAttribute extAttr = template.getAttributes().get(attrName);
				GUIExtendedAttribute att = new GUIExtendedAttribute();
				att.setName(attrName);
				att.setPosition(extAttr.getPosition());
				att.setMandatory(extAttr.getMandatory() == 1 ? true : false);
				att.setType(extAttr.getType());
				if (extAttr.getValue() instanceof String)
					att.setStringValue(extAttr.getStringValue());
				else if (extAttr.getValue() instanceof Long)
					att.setIntValue(extAttr.getIntValue());
				else if (extAttr.getValue() instanceof Double)
					att.setDoubleValue(extAttr.getDoubleValue());
				else if (extAttr.getValue() instanceof Date)
					att.setDateValue(extAttr.getDateValue());

				attributes[i] = att;
				i++;
			}
			if (attributes.length > 0)
				templ.setAttributes(attributes);

			return templ;
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}
}