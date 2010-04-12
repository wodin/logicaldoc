package com.logicaldoc.web.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.DocumentTemplate;
import com.logicaldoc.core.document.dao.DocumentTemplateDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.util.FacesUtil;

/**
 * <p>
 * The <code>TemplatesRecordsManager</code> class is responsible for
 * constructing the list of <code>Template</code> beans which will be bound to
 * a ice:dataTable JSF component. <p/>
 * <p>
 * Large data sets could be handle by adding a ice:dataPaginator. Alternatively
 * the dataTable could also be hidden and the dataTable could be added to
 * scrollable ice:panelGroup.
 * </p>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class TemplatesRecordsManager {

	protected static Log log = LogFactory.getLog(TemplatesRecordsManager.class);

	private List<DocumentTemplate> templates = new ArrayList<DocumentTemplate>();

	private String selectedPanel = "list";

	public TemplatesRecordsManager() {
	}

	public void reload() {
		templates.clear();

		try {
			DocumentTemplateDAO dao = (DocumentTemplateDAO) Context.getInstance().getBean(DocumentTemplateDAO.class);
			templates = dao.findAll();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			Messages.addLocalizedError("errors.error");
		}
	}

	public String getSelectedPanel() {
		return selectedPanel;
	}

	public void setSelectedPanel(String panel) {
		this.selectedPanel = panel;
	}

	public String addTemplate() {
		selectedPanel = "add";
		TemplateForm templateForm = ((TemplateForm) FacesUtil.accessBeanFromFacesContext("templateForm", FacesContext
				.getCurrentInstance(), log));
		templateForm.setTemplate(new DocumentTemplate());
		return null;
	}

	public String edit() {
		selectedPanel = "edit";
		TemplateForm templateForm = ((TemplateForm) FacesUtil.accessBeanFromFacesContext("templateForm", FacesContext
				.getCurrentInstance(), log));
		DocumentTemplate template = (DocumentTemplate) FacesContext.getCurrentInstance().getExternalContext()
				.getRequestMap().get("template");
		templateForm.setTemplate(template);
		return null;
	}

	public String list() {
		selectedPanel = "list";
		reload();
		return null;
	}

	/**
	 * Gets the list of template which will be used by the ice:dataTable
	 * component.
	 */
	public Collection<DocumentTemplate> getTemplates() {
		if (templates.size() == 0) {
			reload();
		}
		return templates;
	}

	public int getCount() {
		return getTemplates().size();
	}

	public String delete() {
		DocumentTemplate template = (DocumentTemplate) FacesContext.getCurrentInstance().getExternalContext()
				.getRequestMap().get("template");
		try {
			DocumentTemplateDAO dao = (DocumentTemplateDAO) Context.getInstance().getBean(DocumentTemplateDAO.class);
			dao.delete(template.getId());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			Messages.addLocalizedError("errors.error");
		}
		setSelectedPanel("list");
		reload();
		return null;
	}
}