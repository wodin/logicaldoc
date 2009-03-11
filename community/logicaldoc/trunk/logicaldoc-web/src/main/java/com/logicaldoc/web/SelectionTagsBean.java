package com.logicaldoc.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.java.plugin.registry.Extension;

import com.logicaldoc.core.document.DocumentTemplate;
import com.logicaldoc.core.document.dao.DocumentTemplateDAO;
import com.logicaldoc.core.i18n.Language;
import com.logicaldoc.core.i18n.LanguageManager;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.PluginRegistry;
import com.logicaldoc.web.i18n.Messages;

/**
 * <p>
 * The SelectionTagsBean Class is the backing bean for selection lists. It is
 * used to store the options of the various selection components.
 * <p>
 */
public class SelectionTagsBean {
	private static SelectItem[] languages;

	private SelectItem[] viewModesBrowsing;

	private SelectItem[] viewModesSearch;

	/**
	 * Gets the option items for languages
	 */
	public SelectItem[] getLanguages() {
		if (languages == null)
			initLanguages();
		return languages;
	}

	private void initLanguages() {
		List<SelectItem> sil = new ArrayList<SelectItem>();
		LanguageManager lm = LanguageManager.getInstance();
		Collection<Language> cLanguages = lm.getLanguages();
		for (Language language : cLanguages) {
			SelectItem si = new SelectItem(language.getLanguage(), language.getDisplayLanguage());
			sil.add(si);
		}
		languages = (SelectItem[]) sil.toArray(new SelectItem[0]);
	}

	private void initViewModesBrowsing() {
		List<SelectItem> sil = new ArrayList<SelectItem>();
		// Acquire the 'ViewModeBrowsing' extensions of the core plugin
		PluginRegistry registry = PluginRegistry.getInstance();
		Collection<Extension> exts = registry.getSortedExtensions("logicaldoc-core", "ViewModeBrowsing", null);

		for (Extension ext : exts) {
			String id = ext.getParameter("id").valueAsString();
			String label = ext.getParameter("label").valueAsString();
			SelectItem si = new SelectItem(id, Messages.getMessage(label));
			sil.add(si);
		}
		viewModesBrowsing = (SelectItem[]) sil.toArray(new SelectItem[0]);
	}

	private void initViewModesSearch() {
		List<SelectItem> sil = new ArrayList<SelectItem>();
		// Acquire the 'ViewModeBrowsing' extensions of the core plugin
		PluginRegistry registry = PluginRegistry.getInstance();
		Collection<Extension> exts = registry.getSortedExtensions("logicaldoc-core", "ViewModeSearch", null);

		for (Extension ext : exts) {
			String id = ext.getParameter("id").valueAsString();
			String label = ext.getParameter("label").valueAsString();
			SelectItem si = new SelectItem(id, Messages.getMessage(label));
			sil.add(si);
		}
		
		viewModesSearch = (SelectItem[]) sil.toArray(new SelectItem[0]);
	}

	public SelectItem[] getViewModesSearch() {
		if (viewModesSearch == null)
			initViewModesSearch();
		return viewModesSearch;
	}

	public SelectItem[] getViewModesBrowsing() {
		if (viewModesBrowsing == null)
			initViewModesBrowsing();
		return viewModesBrowsing;
	}

	/**
	 * Gets the option items for yes/no flags
	 */
	public SelectItem[] getYesNo() {
		return new SelectItem[] { new SelectItem(1, Messages.getMessage("yes")),
				new SelectItem(0, Messages.getMessage("no")) };
	}

	/**
	 * Gets the option items for version types
	 */
	public SelectItem[] getVersionTypes() {
		return new SelectItem[] { new SelectItem("release", Messages.getMessage("msg.jsp.newrelease")),
				new SelectItem("oldversion", Messages.getMessage("msg.jsp.oldversion")),
				new SelectItem("subversion", Messages.getMessage("msg.jsp.newsubversion")) };
	}

	/**
	 * Gets the option items for groups selection
	 */
	public List<SelectItem> getGroups() {
		// gets available groups
		GroupDAO dao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
		Collection<Group> coll = dao.findAll();

		ArrayList<SelectItem> items = new ArrayList<SelectItem>();

		for (Group group : coll) {
			if (group.getType() == Group.TYPE_DEFAULT)
				items.add(new SelectItem(group.getId(), group.getName()));
		}

		return items;
	}

	/**
	 * Gets the option items for templates selection
	 */
	public List<SelectItem> getTemplates() {
		// Gets available templates
		DocumentTemplateDAO dao = (DocumentTemplateDAO) Context.getInstance().getBean(DocumentTemplateDAO.class);
		Collection<DocumentTemplate> coll = dao.findAll();

		ArrayList<SelectItem> items = new ArrayList<SelectItem>();

		for (DocumentTemplate template : coll) {
			items.add(new SelectItem(template.getId(), template.getName()));
		}
		return items;
	}

	/**
	 * Gets the option items for relations
	 */
	public SelectItem[] getRelations() {
		return new SelectItem[] { new SelectItem("gt", Messages.getMessage("greater")),
				new SelectItem("eq", Messages.getMessage("equals")), new SelectItem("lt", Messages.getMessage("less")) };
	}

	/**
	 * Gets the option items for relations regarding dates
	 */
	public SelectItem[] getDateRelations() {
		return new SelectItem[] { new SelectItem("gt", Messages.getMessage("msg.jsp.after")),
				new SelectItem("lt", Messages.getMessage("before")) };
	}

	/**
	 * Gets the option items for file formats
	 */
	public SelectItem[] getFormats() {
		return new SelectItem[] { new SelectItem("html", Messages.getMessage("msg.jsp.internetpage") + " (.html)"),
				new SelectItem("xml", Messages.getMessage("msg.jsp.xmlfile") + " (.xml)"),
				new SelectItem("pdf", "Adobe Acrobat (.pdf)"), new SelectItem("ps", "Adobe Postscript (.ps)"),
				new SelectItem("doc", "Microsoft Word (.doc)"), new SelectItem("docx", "Microsoft Word 2007(.docx)"),
				new SelectItem("odt", "OpenOffice Text (.odt)"), new SelectItem("ods", "OpenOffice Calc (.ods)"),
				new SelectItem("wpd", "Word Perfect (.wpd)"), new SelectItem("rtf", "Rich Text Format (.rtf)"),
				new SelectItem("txt", Messages.getMessage("msg.jsp.textfile") + " (.txt)") };
	}

	/**
	 * Gets the option items for message priorities
	 */
	public SelectItem[] getMessagePriorities() {
		return new SelectItem[] { new SelectItem(0, Messages.getMessage("low")),
				new SelectItem(1, Messages.getMessage("normal")), new SelectItem(2, Messages.getMessage("high")) };
	}

	/**
	 * Gets the option items for task max length
	 */
	public SelectItem[] getTaskMaxLengths() {
		return new SelectItem[] { new SelectItem(-1, Messages.getMessage("task.scheduling.maxlength.nolimit")),
				new SelectItem(60 * 15, Messages.getMessage("task.scheduling.maxlength.fifteenminutes")),
				new SelectItem(60 * 60, Messages.getMessage("task.scheduling.maxlength.onehour")),
				new SelectItem(60 * 60 * 5, Messages.getMessage("task.scheduling.maxlength.fivehours")) };
	}
}