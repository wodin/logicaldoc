package com.logicaldoc.web;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.faces.model.SelectItem;

import org.java.plugin.registry.Extension;

import com.logicaldoc.core.ExtendedAttribute;
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
	
  private static SelectItem[] encodings;

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
			SelectItem si = new SelectItem(language.getLocale(), language.getDisplayLanguage());
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
	 * Gets the option items for extended attribute types
	 */
	public SelectItem[] getExtendedTypes() {
		return new SelectItem[] { new SelectItem(ExtendedAttribute.TYPE_STRING, Messages.getMessage("string")),
				new SelectItem(ExtendedAttribute.TYPE_INT, Messages.getMessage("integer")),
				new SelectItem(ExtendedAttribute.TYPE_DOUBLE, Messages.getMessage("decimal")),
				new SelectItem(ExtendedAttribute.TYPE_DATE, Messages.getMessage("date")) };
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
	
	/**
	 * Gets the option items for Zip encoding
	 */
	public SelectItem[] getEncodings() {
		if (encodings == null)
			initEncodings();
		return encodings;
	}
	
	private void initEncodings() {

		String platformEncoding = System.getProperty("file.encoding");
		Charset ce = Charset.forName(platformEncoding);

		List<SelectItem> sil = new ArrayList<SelectItem>();
		sil.add(new SelectItem("UTF8", "UTF-8")); // Eight-bit Unicode (or UCS) Transformation Format 
		sil.add(new SelectItem(platformEncoding, ce.name())); // Default plaform encoding for file names
		sil.add(new SelectItem("ISO8859_1", "ISO-8859-1")); // ISO-8859-1, Latin Alphabet No. 1 
		sil.add(new SelectItem("ISO8859_5", "ISO-8859-5")); // Latin/Cyrillic Alphabet
		sil.add(new SelectItem("ISO8859_6", "ISO-8859-6")); // Latin/Arabic Alphabet
		sil.add(new SelectItem("ISO8859_7", "ISO-8859-7")); // Latin/Greek Alphabet (ISO-8859-7:2003) 
    sil.add(new SelectItem("GB18030", "GB18030")); // Simplified Chinese, PRC standard 
		sil.add(new SelectItem("EUC_CN", "GB2312")); // GB2312, EUC encoding, Simplified Chinese 
		sil.add(new SelectItem("EUC_JP", "EUC-JP")); // JISX 0201, 0208 and 0212, EUC encoding Japanese 
		sil.add(new SelectItem("SJIS", "Shift_JIS")); // Shift-JIS, Japanese 
		sil.add(new SelectItem("EUC_KR", "EUC-KR")); // KS C 5601, EUC encoding, Korean
		sil.add(new SelectItem("Cp1250", "windows-1250")); // Windows Eastern European 
		sil.add(new SelectItem("Cp1252", "windows-1252")); // Windows Latin-1 
		sil.add(new SelectItem("Cp1253", "windows-1253")); // Windows Greek 
		sil.add(new SelectItem("Cp1256", "windows-1256")); // Windows Arabic
		
		// clean the list from the unsupported charset
		// in practice some charset could not be installed and to prevent errors we don't show them to the user
		List<SelectItem> silpurged = new ArrayList<SelectItem>();
		for (Iterator iter = sil.iterator(); iter.hasNext();) {
			SelectItem si = (SelectItem) iter.next();
			if (Charset.isSupported((String)si.getValue()))
				silpurged.add(si);
		}
		
		encodings = (SelectItem[]) silpurged.toArray(new SelectItem[0]);
	}
	
}