package com.logicaldoc.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import com.logicaldoc.core.i18n.Language;
import com.logicaldoc.core.i18n.LanguageManager;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.i18n.Messages;

/**
 * <p>
 * The SelectionTagsBean Class is the backing bean for selection lists. It is
 * used to store the options of the various selection components.
 * <p>
 */
public class SelectionTagsBean {

	private static SelectItem[] languages;

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
	public SelectItem[] getGroups() {
		// gets available groups
		GroupDAO dao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
		Collection<Group> coll = dao.findAll();

		SelectItem[] items = new SelectItem[coll.size()];

		int i = 0;
		for (Group group : coll) {
			if (group.getType() == Group.TYPE_DEFAULT)
				items[i++] = new SelectItem(group.getId(), group.getName());
		}

		return items;
	}

	/**
	 * Gets the option items for relations
	 */
	public SelectItem[] getRelations() {
		return new SelectItem[] { new SelectItem("gt", Messages.getMessage("msg.jsp.greater")),
				new SelectItem("eq", Messages.getMessage("msg.jsp.equals")),
				new SelectItem("lt", Messages.getMessage("msg.jsp.less")) };
	}

	/**
	 * Gets the option items for relations regarding dates
	 */
	public SelectItem[] getDateRelations() {
		return new SelectItem[] { new SelectItem("gt", Messages.getMessage("msg.jsp.after")),
				new SelectItem("lt", Messages.getMessage("msg.jsp.before")) };
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
		return new SelectItem[] { new SelectItem(0, Messages.getMessage("msg.jsp.low")),
				new SelectItem(1, Messages.getMessage("msg.jsp.normal")),
				new SelectItem(2, Messages.getMessage("msg.jsp.high")) };
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