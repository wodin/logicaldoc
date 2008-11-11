package com.logicaldoc.web.admin;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

	private String[] selectedAttributes = new String[0];

	private String newAttribute = null;

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
		selectedAttributes = new String[0];
		templateAttributes.clear();
		for (String attr : template.getAttributes()) {
			templateAttributes.add(new SelectItem(attr, attr));
		}
	}

	public Collection<SelectItem> getTemplateAttributes() {
		return templateAttributes;
	}

	public void setTemplateAttributes(Collection<SelectItem> templateAttributes) {
		this.templateAttributes = templateAttributes;
	}

	public String[] getSelectedAttributes() {
		return selectedAttributes;
	}

	public void setSelectedAttributes(String[] selectedAttributes) {
		this.selectedAttributes = selectedAttributes;
	}

	public String addAttribute() {
		if (newAttribute != null) {
			newAttribute = newAttribute.trim().toLowerCase();
			if (StringUtils.isNotEmpty(newAttribute) && !template.getAttributes().contains(newAttribute))
				template.addAttribute(newAttribute);
		}
		newAttribute = null;
		init();
		return null;
	}

	public String removeAttributes() {
		for (String attr : selectedAttributes) {
			template.removeAttribute(attr);
		}
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

			TemplatesRecordsManager recordsManager = ((TemplatesRecordsManager) FacesUtil.accessBeanFromFacesContext(
					"templatesRecordsManager", FacesContext.getCurrentInstance(), log));
			recordsManager.reload();
			recordsManager.setSelectedPanel("list");

			return null;
		} else {
			return "login";
		}
	}
}