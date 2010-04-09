package com.logicaldoc.web.admin;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.ExtendedAttribute;
import com.logicaldoc.core.document.DocumentTemplate;
import com.logicaldoc.core.document.dao.DocumentTemplateDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.util.FacesUtil;

/**
 * Form for template editing
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class TemplateForm {
	protected static Log log = LogFactory.getLog(TemplateForm.class);

	private DocumentTemplate template;

	private Collection<SelectItem> templateAttributes = new ArrayList<SelectItem>();

	private String selectedAttribute = "";

	private String newAttribute = null;

	private boolean mandatory;

	private int type = ExtendedAttribute.TYPE_STRING;

	private UIInput newAttributeInput = null;

	private UIInput typeInput = null;

	private UIInput mandatoryInput = null;

	private UIInput nameInput = null;

	private UIInput descriptionInput = null;

	public DocumentTemplate getTemplate() {
		return template;
	}

	public String getNewAttribute() {
		return newAttribute;
	}

	public void setNewAttribute(String newAttribute) {
		this.newAttribute = newAttribute;
	}

	public void setTemplate(DocumentTemplate template) {
		this.template = template;
		init();
	}

	private void init() {
		templateAttributes.clear();
		for (String attr : template.getAttributeNames()) {
			templateAttributes.add(new SelectItem(attr, attr));
		}
	}

	public Collection<SelectItem> getTemplateAttributes() {
		return templateAttributes;
	}

	public void setTemplateAttributes(Collection<SelectItem> templateAttributes) {
		this.templateAttributes = templateAttributes;
	}

	public String getSelectedAttribute() {
		return selectedAttribute;
	}

	public void setSelectedAttribute(String selectedAttribute) {
		this.selectedAttribute = selectedAttribute;
	}

	public String addAttribute() {
		newAttribute = newAttribute.trim().toLowerCase();
		if (StringUtils.isNotEmpty(newAttribute)) {
			ExtendedAttribute attribute = new ExtendedAttribute();
			attribute.setMandatory(mandatory ? 1 : 0);
			attribute.setType(getType());
			DocumentTemplateDAO dao = (DocumentTemplateDAO) Context.getInstance().getBean(DocumentTemplateDAO.class);
			dao.initialize(template);
			template.getAttributes().put(newAttribute, attribute);
		}
		init();
		return null;
	}

	public String removeAttributes() {
		DocumentTemplateDAO dao = (DocumentTemplateDAO) Context.getInstance().getBean(DocumentTemplateDAO.class);
		dao.initialize(template);
		template.getAttributes().remove(selectedAttribute);
		newAttribute = null;
		mandatory = false;
		type = ExtendedAttribute.TYPE_STRING;
		init();
		return null;
	}

	public String save() {
		if (SessionManagement.isValid()) {
			try {
				DocumentTemplateDAO dao = (DocumentTemplateDAO) Context.getInstance()
						.getBean(DocumentTemplateDAO.class);
				dao.store(template);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				Messages.addLocalizedError("template.save.error");
			}

			selectedAttribute = null;
			newAttribute = null;
			mandatory = false;
			type = ExtendedAttribute.TYPE_STRING;
			FacesUtil.forceRefresh(nameInput);
			FacesUtil.forceRefresh(descriptionInput);
			FacesUtil.forceRefresh(mandatoryInput);
			FacesUtil.forceRefresh(typeInput);
			FacesUtil.forceRefresh(newAttributeInput);

			TemplatesRecordsManager recordsManager = ((TemplatesRecordsManager) FacesUtil.accessBeanFromFacesContext(
					"templatesRecordsManager", FacesContext.getCurrentInstance(), log));
			recordsManager.reload();
			recordsManager.setSelectedPanel("list");

			return null;
		} else {
			return "login";
		}
	}

	public void cancel() {
		selectedAttribute = null;
		newAttribute = null;
		mandatory = false;
		type = ExtendedAttribute.TYPE_STRING;
		FacesUtil.forceRefresh(nameInput);
		FacesUtil.forceRefresh(descriptionInput);
		FacesUtil.forceRefresh(mandatoryInput);
		FacesUtil.forceRefresh(typeInput);
		FacesUtil.forceRefresh(newAttributeInput);

		TemplatesRecordsManager recordsManager = ((TemplatesRecordsManager) FacesUtil.accessBeanFromFacesContext(
				"templatesRecordsManager", FacesContext.getCurrentInstance(), log));
		recordsManager.list();
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public UIInput getTypeInput() {
		return typeInput;
	}

	public void setTypeInput(UIInput typeInput) {
		this.typeInput = typeInput;
	}

	public UIInput getMandatoryInput() {
		return mandatoryInput;
	}

	public void setMandatoryInput(UIInput mandatoryInput) {
		this.mandatoryInput = mandatoryInput;
	}

	public void selectAttribute(ValueChangeEvent event) {
		FacesUtil.forceRefresh(mandatoryInput);
		FacesUtil.forceRefresh(typeInput);
		FacesUtil.forceRefresh(newAttributeInput);
		try {
			newAttribute = event.getNewValue().toString();
			ExtendedAttribute attribute = template.getAttributes().get(newAttribute);
			mandatory = attribute.getMandatory() == 1 ? true : false;
			type = attribute.getType();
		} catch (Exception e) {
		}
	}

	public UIInput getNewAttributeInput() {
		return newAttributeInput;
	}

	public void setNewAttributeInput(UIInput newAttributeInput) {
		this.newAttributeInput = newAttributeInput;
	}

	public UIInput getNameInput() {
		return nameInput;
	}

	public void setNameInput(UIInput nameInput) {
		this.nameInput = nameInput;
	}

	public UIInput getDescriptionInput() {
		return descriptionInput;
	}

	public void setDescriptionInput(UIInput descriptionInput) {
		this.descriptionInput = descriptionInput;
	}
}