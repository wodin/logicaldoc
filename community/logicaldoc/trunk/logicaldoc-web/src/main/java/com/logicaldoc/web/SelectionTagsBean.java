package com.logicaldoc.web;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
import com.logicaldoc.core.text.parser.ParserFactory;
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

	private SelectItem[] extensions;

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
	 * Gets the option items for and/or flags
	 */
	public SelectItem[] getAndOr() {
		return new SelectItem[] { new SelectItem("and", Messages.getMessage("and")),
				new SelectItem("or", Messages.getMessage("or")) };
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

	private void initExtensions() {
		List<SelectItem> sil = new ArrayList<SelectItem>();
		// Acquire all supported extensions
		for (String extension : ParserFactory.getExtensions()) {
			SelectItem si = new SelectItem(extension, extension);
			sil.add(si);
		}

		Collections.sort(sil, new Comparator<SelectItem>() {
			@Override
			public int compare(SelectItem o1, SelectItem o2) {
				return ((Comparable) o1.getValue()).compareTo(o2.getValue());
			}
		});

		extensions = (SelectItem[]) sil.toArray(new SelectItem[0]);
	}

	/**
	 * Gets the option items for file formats
	 */
	public SelectItem[] getFormats() {
		if (extensions == null)
			initExtensions();
		return extensions;
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

		// Eight-bit Unicode (or UCS) Transformation Format
		sil.add(new SelectItem("UTF8", "UTF-8 Unicode"));
		// Default plaform encoding for file names
		sil.add(new SelectItem(platformEncoding, ce.name() + " Default"));

		// PC Greek
		sil.add(new SelectItem("Cp737", "PC Greek"));
		// ISO-8859-1, Latin Alphabet No. 1
		sil.add(new SelectItem("ISO8859_1", "Latin Alphabet No. 1"));
		// Latin/Cyrillic Alphabet
		sil.add(new SelectItem("ISO8859_5", "Latin/Cyrillic"));
		// Latin/Arabic Alphabet
		sil.add(new SelectItem("ISO8859_6", "Latin/Arabic"));
		// Latin/Greek Alphabet (ISO-8859-7:2003)
		sil.add(new SelectItem("ISO8859_7", "Latin/Greek"));
		// Simplified Chinese, PRC standard
		sil.add(new SelectItem("GB18030", "GB18030 Simplified Chinese"));
		// GB2312, EUC encoding, Simplified Chinese
		sil.add(new SelectItem("EUC_CN", "GB2312 Simplified Chinese"));
		// JISX 0201, 0208 and 0212, EUC encoding Japanese
		sil.add(new SelectItem("EUC_JP", "EUC-JP Japanese"));
		// Shift-JIS, Japanese
		sil.add(new SelectItem("SJIS", "Shift_JIS Japanese"));
		// KS C 5601, EUC encoding, Korean
		sil.add(new SelectItem("EUC_KR", "EUC-KR Korean"));
		// Windows Eastern European
		sil.add(new SelectItem("Cp1250", "windows-1250 Eastern European"));
		// Windows Latin-1
		sil.add(new SelectItem("Cp1252", "windows-1252 Latin-1"));
		// Windows Greek
		sil.add(new SelectItem("Cp1253", "windows-1253 Greek"));
		// Windows Arabic
		sil.add(new SelectItem("Cp1256", "windows-1256 Arabic"));

		// clean the list from the unsupported charset
		// in practice some charset could not be installed and to prevent errors
		// we don't show them to the user
		List<SelectItem> silpurged = new ArrayList<SelectItem>();
		for (Iterator iter = sil.iterator(); iter.hasNext();) {
			SelectItem si = (SelectItem) iter.next();
			if (Charset.isSupported((String) si.getValue()))
				silpurged.add(si);
		}

		encodings = (SelectItem[]) silpurged.toArray(new SelectItem[0]);
	}

	public SelectItem[] getDocumentFields() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		items.add(new SelectItem("id", Messages.getMessage("document.id")));
		items.add(new SelectItem("customId", Messages.getMessage("document.customid")));
		items.add(new SelectItem("title", Messages.getMessage("document.title")));
		items.add(new SelectItem("sourceId", Messages.getMessage("document.sourceid")));
		items.add(new SelectItem("language", Messages.getMessage("language")));
		items.add(new SelectItem("folder", Messages.getMessage("folder")));
		items.add(new SelectItem("date", Messages.getMessage("msg.jsp.publishedon")));
		items.add(new SelectItem("creation", Messages.getMessage("document.createdon")));
		items.add(new SelectItem("creator", Messages.getMessage("document.createdfrom")));
		items.add(new SelectItem("publisher", Messages.getMessage("document.publishedfrom")));
		items.add(new SelectItem("sourceDate", Messages.getMessage("date")));
		items.add(new SelectItem("source", Messages.getMessage("document.source")));
		items.add(new SelectItem("sourceAuthor", Messages.getMessage("document.author")));
		items.add(new SelectItem("coverage", Messages.getMessage("document.coverage")));
		items.add(new SelectItem("sourceType", Messages.getMessage("document.type")));
		items.add(new SelectItem("recipient", Messages.getMessage("document.recipient")));
		items.add(new SelectItem("object", Messages.getMessage("document.object")));
		items.add(new SelectItem("template", Messages.getMessage("template")));
		items.add(new SelectItem("fileName", Messages.getMessage("file")));
		items.add(new SelectItem("fileSize", Messages.getMessage("size")));
		items.add(new SelectItem("type", Messages.getMessage("type")));

		Collections.sort(items, new Comparator<SelectItem>() {
			@Override
			public int compare(SelectItem o1, SelectItem o2) {
				return o1.getLabel().toLowerCase().compareTo(o2.getLabel().toLowerCase());
			}
		});

		return items.toArray(new SelectItem[0]);
	}

	/**
	 * Gets the option items for SMTP connection security
	 */
	public SelectItem[] getConnectionSecurity() {
		return new SelectItem[] { new SelectItem(0, Messages.getMessage("smtp.connectionSecurity.0")),
				new SelectItem(1, Messages.getMessage("smtp.connectionSecurity.1")),
				new SelectItem(2, Messages.getMessage("smtp.connectionSecurity.2")),
				new SelectItem(3, Messages.getMessage("smtp.connectionSecurity.3")) };
	}
}