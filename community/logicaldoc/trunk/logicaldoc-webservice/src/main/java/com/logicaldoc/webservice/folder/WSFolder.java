package com.logicaldoc.webservice.folder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.ExtendedAttribute;
import com.logicaldoc.core.document.DocumentTemplate;
import com.logicaldoc.core.document.dao.DocumentTemplateDAO;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.util.Context;
import com.logicaldoc.webservice.AbstractService;
import com.logicaldoc.webservice.WSAttribute;

/**
 * Web Service Folder. Useful class to create repository Folders.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class WSFolder implements Serializable {

	private static final long serialVersionUID = 1L;

	protected static Logger log = LoggerFactory.getLogger(WSFolder.class);

	private long id = 0;

	private String name = "";

	private long parentId = 0;

	private String description = "";

	private String lastModified;

	private int type = Folder.TYPE_DEFAULT;

	private Long templateId;

	private int templateLocked = 0;

	private String creation;

	private String creator;

	private int position = 1;

	private int hidden = 0;

	private WSAttribute[] extendedAttributes = new WSAttribute[0];

	public void addExtendedAttribute(WSAttribute att) {
		List<WSAttribute> buf = (List<WSAttribute>) Arrays.asList(getExtendedAttributes());
		buf.add(att);
		setExtendedAttributes(buf.toArray(new WSAttribute[0]));
	}

	public Collection<String> listAttributeNames() {
		List<String> names = new ArrayList<String>();
		for (WSAttribute att : getExtendedAttributes()) {
			names.add(att.getName());
		}
		return names;
	}

	public WSAttribute attribute(String name) {
		for (WSAttribute att : getExtendedAttributes()) {
			if (att.getName().equals(name))
				return att;
		}
		return null;
	}

	public static WSFolder fromFolder(Folder folder) {
		WSFolder wsFolder = new WSFolder();
		wsFolder.setId(folder.getId());
		wsFolder.setName(folder.getName());
		wsFolder.setType(folder.getType());
		wsFolder.setDescription(folder.getDescription());
		wsFolder.setParentId(folder.getParentId());
		wsFolder.setLastModified(AbstractService.convertDateToString(folder.getLastModified()));
		wsFolder.setCreation(AbstractService.convertDateToString(folder.getCreation()));
		wsFolder.setCreator(folder.getCreator());
		wsFolder.setPosition(folder.getPosition());
		wsFolder.setTemplateLocked(folder.getTemplateLocked());
		wsFolder.setHidden(folder.getHidden());

		if (folder.getTemplate() != null)
			wsFolder.setTemplateId(folder.getTemplate().getId());

		// Populate extended attributes
		WSAttribute[] attributes = new WSAttribute[0];
		if (folder.getTemplate() != null && folder.getAttributes() != null && folder.getAttributes().size() > 0) {
			attributes = new WSAttribute[folder.getAttributeNames().size()];
			int i = 0;
			for (String name : folder.getAttributeNames()) {
				ExtendedAttribute attr = folder.getExtendedAttribute(name);
				WSAttribute attribute = new WSAttribute();
				attribute.setName(name);
				attribute.setMandatory(attr.getMandatory());
				attribute.setPosition(attr.getPosition());
				attribute.setValue(attr.getValue());

				if (attr.getType() == ExtendedAttribute.TYPE_USER) {
					attribute.setIntValue(attr.getIntValue());
					attribute.setStringValue(attr.getStringValue());
				}

				attribute.setType(attr.getType());
				attributes[i++] = attribute;
			}
		}
		wsFolder.setExtendedAttributes(attributes);

		return wsFolder;
	}

	public void updateExtendedAttributes(Folder folder) {
		DocumentTemplate template = null;
		if (templateId != null) {
			folder.getAttributes().clear();
			DocumentTemplateDAO templDao = (DocumentTemplateDAO) Context.getInstance().getBean(
					DocumentTemplateDAO.class);
			template = templDao.findById(templateId);
			if (template != null) {
				if (extendedAttributes != null && extendedAttributes.length > 0) {
					for (int i = 0; i < extendedAttributes.length; i++) {
						ExtendedAttribute extAttribute = new ExtendedAttribute();
						extAttribute.setMandatory(extendedAttributes[i].getMandatory());
						extAttribute.setPosition(extendedAttributes[i].getPosition());
						extAttribute.setIntValue(extendedAttributes[i].getIntValue());
						extAttribute.setStringValue(extendedAttributes[i].getStringValue());
						extAttribute.setDoubleValue(extendedAttributes[i].getDoubleValue());
						extAttribute.setDateValue(AbstractService.convertStringToDate(extendedAttributes[i]
								.getDateValue()));
						extAttribute.setType(extendedAttributes[i].getType());
						folder.getAttributes().put(extendedAttributes[i].getName(), extAttribute);
					}
				}
			}
		}

		folder.setTemplate(template);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLastModified() {
		return lastModified;
	}

	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public WSAttribute[] getExtendedAttributes() {
		return extendedAttributes;
	}

	public void setExtendedAttributes(WSAttribute[] extendedAttributes) {
		this.extendedAttributes = extendedAttributes;
	}

	public String getCreation() {
		return creation;
	}

	public void setCreation(String creation) {
		this.creation = creation;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getTemplateLocked() {
		return templateLocked;
	}

	public void setTemplateLocked(int templateLocked) {
		this.templateLocked = templateLocked;
	}

	public int getHidden() {
		return hidden;
	}

	public void setHidden(int hidden) {
		this.hidden = hidden;
	}
}