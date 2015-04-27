package com.logicaldoc.web.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.ExtendedAttribute;
import com.logicaldoc.core.ExtendedAttributeOption;
import com.logicaldoc.core.document.DocumentTemplate;
import com.logicaldoc.core.document.dao.DocumentTemplateDAO;
import com.logicaldoc.core.document.dao.ExtendedAttributeOptionDAO;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.gui.common.client.ServerException;
import com.logicaldoc.gui.common.client.beans.GUIExtendedAttribute;
import com.logicaldoc.gui.common.client.beans.GUITemplate;
import com.logicaldoc.gui.frontend.client.services.TemplateService;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.csv.CSVFileReader;
import com.logicaldoc.web.UploadServlet;
import com.logicaldoc.web.util.ServiceUtil;

/**
 * Implementation of the TemplateService
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class TemplateServiceImpl extends RemoteServiceServlet implements TemplateService {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(TemplateServiceImpl.class);

	@Override
	public void delete(String sid, long templateId) throws ServerException {
		ServiceUtil.validateSession(sid);

		DocumentTemplateDAO dao = (DocumentTemplateDAO) Context.getInstance().getBean(DocumentTemplateDAO.class);
		dao.delete(templateId);
	}

	@Override
	public void saveOptions(String sid, long templateId, String attribute, String[] values) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		ExtendedAttributeOptionDAO dao = (ExtendedAttributeOptionDAO) Context.getInstance().getBean(
				ExtendedAttributeOptionDAO.class);
		try {
			Map<String, ExtendedAttributeOption> optionsMap = new HashMap<String, ExtendedAttributeOption>();
			List<ExtendedAttributeOption> options = dao.findByTemplateAndAttribute(templateId, attribute);
			for (ExtendedAttributeOption option : options)
				optionsMap.put(option.getValue(), option);

			for (int i = 0; i < values.length; i++) {
				String value = values[i];
				ExtendedAttributeOption option = optionsMap.get(value);
				if (option == null) {
					option = new ExtendedAttributeOption(templateId, attribute, value);
				} else {
					if (value.equals(option.getValue()) && option.getPosition() == i)
						continue;
					option.setValue(value);
				}

				option.setPosition(i);
				dao.store(option);
			}
		} catch (Throwable t) {
			ServiceUtil.throwServerException(session, log, t);
		}
	}

	@Override
	public void deleteOptions(String sid, long templateId, String attribute, String[] values) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		ExtendedAttributeOptionDAO dao = (ExtendedAttributeOptionDAO) Context.getInstance().getBean(
				ExtendedAttributeOptionDAO.class);
		try {
			List<ExtendedAttributeOption> options = dao.findByTemplateAndAttribute(templateId, attribute);
			for (ExtendedAttributeOption option : options)
				for (String value : values)
					if (value.equals(option.getValue())) {
						dao.delete(option.getId());
						break;
					}
		} catch (Throwable t) {
			ServiceUtil.throwServerException(session, log, t);
		}
	}

	@Override
	public GUITemplate save(String sid, GUITemplate template) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		DocumentTemplateDAO dao = (DocumentTemplateDAO) Context.getInstance().getBean(DocumentTemplateDAO.class);
		try {
			DocumentTemplate templ;
			if (template.getId() != 0) {
				templ = dao.findById(template.getId());
				dao.initialize(templ);
			} else {
				templ = new DocumentTemplate();

				if (template.getType() == GUITemplate.TYPE_AOS) {
					// Retrieve the attributes of the template with the same
					// category. This template was already created at the
					// startup.
					// By default, hypothesize that the template belongs to
					// Generic category.
					long templateId = DocumentTemplate.GENERIC;
					if (template.getCategory() == DocumentTemplate.CATEGORY_ACTIVE_INVOICE)
						templateId = DocumentTemplate.ACTIVE_INVOICE;
					else if (template.getCategory() == DocumentTemplate.CATEGORY_PASSIVE_INVOICE)
						templateId = DocumentTemplate.PASSIVE_INVOICE;
					else if (template.getCategory() == DocumentTemplate.CATEGORY_DDT)
						templateId = DocumentTemplate.DDT;
					else if (template.getCategory() == DocumentTemplate.CATEGORY_CONTRACT)
						templateId = DocumentTemplate.CONTRACT;

					DocumentTemplate t = dao.findById(templateId);
					Map<String, ExtendedAttribute> attributes = t.getAttributes();
					GUIExtendedAttribute[] guiAttr = new GUIExtendedAttribute[attributes.size()];
					int i = 0;
					for (String attrName : attributes.keySet()) {
						ExtendedAttribute extAttr = attributes.get(attrName);
						GUIExtendedAttribute att = new GUIExtendedAttribute();
						att.setName(attrName);
						att.setPosition(extAttr.getPosition());
						att.setMandatory(extAttr.getMandatory() == 1 ? true : false);
						att.setType(extAttr.getType());
						if (StringUtils.isEmpty(extAttr.getLabel()))
							att.setLabel(attrName);
						else
							att.setLabel(extAttr.getLabel());
						att.setEditor(extAttr.getEditor());
						att.setStringValue(extAttr.getStringValue());

						guiAttr[i] = att;
						i++;
					}
					if (guiAttr.length > 0)
						template.setAttributes(guiAttr);
				}
			}

			templ.setTenantId(session.getTenantId());
			templ.setName(template.getName());
			templ.setDescription(template.getDescription());
			templ.setReadonly(template.isReadonly() ? 1 : 0);
			templ.setType(template.getType());
			templ.setCategory(template.getCategory());
			templ.setSignRequired(template.getSignRequired());

			Map<String, ExtendedAttribute> attrs = new HashMap<String, ExtendedAttribute>();
			if (template.getAttributes() != null && template.getAttributes().length > 0) {
				templ.getAttributes().clear();
				for (GUIExtendedAttribute attribute : template.getAttributes()) {
					if (attribute != null) {
						ExtendedAttribute att = new ExtendedAttribute();
						att.setPosition(attribute.getPosition());
						att.setMandatory(attribute.isMandatory() ? 1 : 0);
						att.setType(attribute.getType());
						att.setLabel(attribute.getLabel());
						att.setEditor(attribute.getEditor());
						att.setStringValue(attribute.getStringValue());
						if (StringUtils.isEmpty(attribute.getLabel()))
							att.setLabel(attribute.getName());
						if (attribute.getValue() instanceof String)
							att.setStringValue(attribute.getStringValue());
						else if (attribute.getValue() instanceof Long)
							att.setIntValue(attribute.getIntValue());
						else if (attribute.getValue() instanceof Double)
							att.setDoubleValue(attribute.getDoubleValue());
						else if (attribute.getValue() instanceof Date)
							att.setDateValue(attribute.getDateValue());
						else if (attribute.getValue() instanceof Boolean)
							att.setBooleanValue(attribute.getBooleanValue());
						attrs.put(attribute.getName(), att);
					}
				}
			}
			if (attrs.size() > 0)
				templ.setAttributes(attrs);

			dao.store(templ);

			template.setId(templ.getId());
		} catch (Throwable t) {
			return (GUITemplate) ServiceUtil.throwServerException(session, log, t);
		}

		return template;
	}

	@Override
	public GUITemplate getTemplate(String sid, long templateId) throws ServerException {
		ServiceUtil.validateSession(sid);

		DocumentTemplateDAO dao = (DocumentTemplateDAO) Context.getInstance().getBean(DocumentTemplateDAO.class);
		try {
			DocumentTemplate template = dao.findById(templateId);
			if (template == null)
				return null;

			GUITemplate templ = new GUITemplate();
			templ.setId(templateId);
			templ.setName(template.getName());
			templ.setDescription(template.getDescription());
			templ.setReadonly(template.getReadonly() == 1);
			templ.setType(template.getType());
			templ.setCategory(template.getCategory());
			templ.setSignRequired(template.getSignRequired());

			GUIExtendedAttribute[] attributes = new GUIExtendedAttribute[template.getAttributeNames().size()];
			int i = 0;
			for (String attrName : template.getAttributeNames()) {
				ExtendedAttribute extAttr = template.getAttributes().get(attrName);
				GUIExtendedAttribute att = new GUIExtendedAttribute();
				att.setName(attrName);
				att.setPosition(extAttr.getPosition());
				att.setMandatory(extAttr.getMandatory() == 1 ? true : false);
				att.setType(extAttr.getType());
				if (StringUtils.isEmpty(extAttr.getLabel()))
					att.setLabel(attrName);
				else
					att.setLabel(extAttr.getLabel());
				if (extAttr.getValue() instanceof String)
					att.setStringValue(extAttr.getStringValue());
				else if (extAttr.getValue() instanceof Long)
					att.setIntValue(extAttr.getIntValue());
				else if (extAttr.getValue() instanceof Double)
					att.setDoubleValue(extAttr.getDoubleValue());
				else if (extAttr.getValue() instanceof Date)
					att.setDateValue(extAttr.getDateValue());
				else if (extAttr.getValue() instanceof Boolean)
					att.setBooleanValue(extAttr.getBooleanValue());

				att.setEditor(extAttr.getEditor());
				if (extAttr.getType() == ExtendedAttribute.TYPE_USER
						|| extAttr.getEditor() == ExtendedAttribute.EDITOR_LISTBOX) {
					String buf = (String) extAttr.getStringValue();
					List<String> list = new ArrayList<String>();
					if (buf != null) {
						if (buf.contains(",")) {
							StringTokenizer st = new StringTokenizer(buf, ",");
							while (st.hasMoreElements()) {
								String val = (String) st.nextElement();
								if (!list.contains(val))
									list.add(val);
							}
						} else
							list.add(buf.trim());
						att.setStringValue(buf);
					}
					att.setOptions(list.toArray(new String[0]));
				}
				attributes[i] = att;
				i++;
			}
			if (attributes.length > 0)
				templ.setAttributes(attributes);

			return templ;
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
		}

		return null;
	}

	@Override
	public String[] parseOptions(String sid, long templateId, String attribute) throws ServerException {
		ServiceUtil.validateSession(sid);

		Map<String, File> uploadedFilesMap = UploadServlet.getReceivedFiles(getThreadLocalRequest(), sid);
		List<String> options = new ArrayList<String>();

		CSVFileReader reader = null;
		try {
			File file = null;
			for (String fileId : uploadedFilesMap.keySet())
				if (fileId.startsWith("LDOC_OPT")) {
					file = uploadedFilesMap.get(fileId);
					break;
				}

			if (file != null) {
				reader = new CSVFileReader(file.getPath());
				Vector<String> row = reader.readFields();
				if (row != null && "value".equals(row.get(0).toLowerCase()))
					row = reader.readFields();
				while (row != null && !row.isEmpty()) {
					options.add(row.get(0).trim());
					row = reader.readFields();
				}
			}
		} catch (Throwable e) {
			log.error("Unable to parse options in CSV file", e);
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
				}
		}

		String[] values = options.toArray(new String[0]);
		if (values.length > 0)
			saveOptions(sid, templateId, attribute, values);
		return values;
	}
}