package com.logicaldoc.web.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.component.UIInput;
import javax.faces.component.UISelectOne;
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

	private List<SelectItem> templateAttributes = new ArrayList<SelectItem>();;

	private String selectedAttribute = "";

	private String newAttribute = null;

	private boolean mandatory;

	private int type = ExtendedAttribute.TYPE_STRING;

	private UISelectOne selectListInput = null;

	private UIInput newAttributeInput = null;

	private UIInput typeInput = null;

	private UIInput mandatoryInput = null;

	private UIInput nameInput = null;

	private UIInput descriptionInput = null;

	// Utility map to restore the old positions of the template extended
	// attributes
	private Map<String, Integer> attributesPositions = new HashMap<String, Integer>();

	private boolean attributeNameValid = false;

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
		for (String name : template.getAttributeNames()) {
			attributesPositions.put(name, template.getAttributes().get(name).getPosition());
		}
	}

	private void init() {
		templateAttributes.clear();
		attributeNameValid = false;
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (String key : template.getAttributes().keySet()) {
			ExtendedAttribute attribute = template.getAttributes().get(key);
			items.add(new SelectItem(attribute.getPosition(), key));
			Collections.sort(items, new Comparator<SelectItem>() {
				public int compare(SelectItem arg0, SelectItem arg1) {
					int sort = ((Integer) arg0.getValue()).compareTo((Integer) (arg1.getValue()));
					return sort;
				}
			});
		}
		for (SelectItem selectItem : items) {
			templateAttributes.add(new SelectItem(selectItem.getLabel(), selectItem.getLabel()));
		}
	}

	public Collection<SelectItem> getTemplateAttributes() {
		return templateAttributes;
	}

	public void setTemplateAttributes(List<SelectItem> templateAttributes) {
		this.templateAttributes = templateAttributes;
	}

	public int getTemplateAttributesCount() {
		return templateAttributes.size();
	}

	public String getSelectedAttribute() {
		return selectedAttribute;
	}

	public void setSelectedAttribute(String selectedAttribute) {
		this.selectedAttribute = selectedAttribute;
	}

	public String addAttribute() {
		newAttribute = newAttribute.trim();
		// New Attribute Name Validation
		Pattern pattern = Pattern.compile("(\\w+\\p{Space}?+)+?");
		Matcher m = pattern.matcher(newAttribute);
		boolean matchFound = m.matches();
		if (!matchFound) {
			Messages.addLocalizedWarn("template.attribute.notvalid");
			attributeNameValid = false;
			return null;
		}
		// Now the attribute name is certainly valid
		attributeNameValid = true;

		if (StringUtils.isNotEmpty(newAttribute)) {
			ExtendedAttribute attribute = new ExtendedAttribute();
			attribute.setMandatory(mandatory ? 1 : 0);
			if (getTemplateAttributesCount() > 0) {
				attribute.setPosition(getMajorPosition(template) + 1);
			} else {
				attribute.setPosition(0);
			}
			attribute.setType(getType());
			DocumentTemplateDAO dao = (DocumentTemplateDAO) Context.getInstance().getBean(DocumentTemplateDAO.class);
			dao.initialize(template);
			template.getAttributes().put(newAttribute, attribute);
		}
		init();
		return null;
	}

	/**
	 * Remove the selected attribute. The user can remove an attribute only
	 * during the template creation.
	 */
	public String removeAttribute() {
		DocumentTemplateDAO dao = (DocumentTemplateDAO) Context.getInstance().getBean(DocumentTemplateDAO.class);
		dao.initialize(template);
		template.getAttributes().remove(selectedAttribute);
		newAttribute = null;
		mandatory = false;
		type = ExtendedAttribute.TYPE_STRING;
		init();
		return null;
	}

	public String cleanAttributes() {
		newAttribute = null;
		mandatory = false;
		selectedAttribute = null;
		type = ExtendedAttribute.TYPE_STRING;
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
			attributesPositions.clear();

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

		// Restore the old positions of the template extended attributes
		DocumentTemplateDAO dao = (DocumentTemplateDAO) Context.getInstance().getBean(DocumentTemplateDAO.class);
		dao.initialize(template);
		if (template.getAttributeNames().size() > 0) {
			if (!attributesPositions.isEmpty()) {
				// The template already exists, so we have to restore the old
				// attributes positions
				for (String name : template.getAttributeNames()) {
					ExtendedAttribute extAttribute = template.getAttributes().get(name);
					if (attributesPositions.get(name) != null)
						extAttribute.setPosition(attributesPositions.get(name));
					else
						template.getAttributes().remove(name);
				}
				dao.store(template);
			} else {
				// The user had started to create a template adding one or more
				// attributes, but then changed his mind, so the template must
				// be removed.
				dao.delete(template.getId());
			}
		}
		init();
		attributesPositions.clear();

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
		if (event.getNewValue() == null)
			return;
		FacesUtil.forceRefresh(mandatoryInput);
		FacesUtil.forceRefresh(typeInput);
		FacesUtil.forceRefresh(newAttributeInput);
		try {
			newAttribute = event.getNewValue().toString();
			ExtendedAttribute attribute = template.getAttributes().get(newAttribute);
			mandatory = attribute.getMandatory() == 1 ? true : false;
			type = attribute.getType();
			selectedAttribute = event.getNewValue().toString();
		} catch (Exception e) {
		}
	}

	/**
	 * Moves the selected attribute up of one position
	 */
	public String attributeUp() {
		if (selectedAttribute == null || selectedAttribute.trim().isEmpty())
			return null;
		DocumentTemplateDAO dao = (DocumentTemplateDAO) Context.getInstance().getBean(DocumentTemplateDAO.class);
		dao.initialize(template);
		ExtendedAttribute attribute = template.getAttributes().get(selectedAttribute);
		int oldPosition = attribute.getPosition();
		ExtendedAttribute otherAttribute = template.getAttributeAtPosition(oldPosition - 1);
		if (otherAttribute != null) {
			attribute.setPosition(oldPosition - 1);
			otherAttribute.setPosition(oldPosition);
			dao.store(template);
			reloadTemplateAttributesList();
		}

		return null;
	}

	/**
	 * Reload the template attributes list to visualise to the user the correct
	 * order of the template attributes
	 */
	private void reloadTemplateAttributesList() {
		DocumentTemplateDAO dao = (DocumentTemplateDAO) Context.getInstance().getBean(DocumentTemplateDAO.class);
		String query = "select ld_name from ld_template_ext where ld_templateid = '"
				+ template.getId() + "' order by ld_position";
		
		List<String> tnames = (List<String>) dao.queryForList(query, String.class);
		templateAttributes.clear();
		for (String tname : tnames) {
			templateAttributes.add(new SelectItem(tname, tname));
		}

	}

	/**
	 * Moves the selected attribute down of one position
	 */
	public String attributeDown() {
		if (selectedAttribute == null || selectedAttribute.trim().isEmpty())
			return null;
		DocumentTemplateDAO dao = (DocumentTemplateDAO) Context.getInstance().getBean(DocumentTemplateDAO.class);
		dao.initialize(template);
		ExtendedAttribute attribute = template.getAttributes().get(selectedAttribute);
		int oldPosition = attribute.getPosition();
		ExtendedAttribute otherAttribute = template.getAttributeAtPosition(oldPosition + 1);
		if (otherAttribute != null) {
			attribute.setPosition(oldPosition + 1);
			otherAttribute.setPosition(oldPosition);
			dao.store(template);
			reloadTemplateAttributesList();
		}

		return null;
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

	public UISelectOne getSelectListInput() {
		return selectListInput;
	}

	public void setSelectListInput(UISelectOne selectListInput) {
		this.selectListInput = selectListInput;
	}

	public int getMajorPosition(DocumentTemplate template) {
		int majorPosition = 0;
		for (String attrName : template.getAttributeNames()) {
			ExtendedAttribute extAttribute = template.getAttributes().get(attrName);
			if (extAttribute.getPosition() > majorPosition)
				majorPosition = extAttribute.getPosition();
		}

		return majorPosition;
	}

	public Map<String, Integer> getAttributesPositions() {
		return attributesPositions;
	}

	public boolean isAttributeNameValid() {
		return attributeNameValid;
	}
}