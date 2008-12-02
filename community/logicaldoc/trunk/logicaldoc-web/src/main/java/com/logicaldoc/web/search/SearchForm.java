package com.logicaldoc.web.search;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.DocumentTemplate;
import com.logicaldoc.core.document.dao.DocumentTemplateDAO;
import com.logicaldoc.core.i18n.LanguageManager;
import com.logicaldoc.core.searchengine.LuceneDocument;
import com.logicaldoc.core.searchengine.Result;
import com.logicaldoc.core.searchengine.Search;
import com.logicaldoc.core.searchengine.SearchOptions;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.text.analyzer.AnalyzerManager;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.StyleBean;
import com.logicaldoc.web.document.Directory;
import com.logicaldoc.web.document.DirectoryTreeModel;
import com.logicaldoc.web.document.DocumentRecord;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.navigation.NavigationBean;
import com.logicaldoc.web.navigation.PageContentBean;

/**
 * A simple search form bean.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 2.7
 */
public class SearchForm {
	protected static Log logger = LogFactory.getLog(SearchForm.class);

	private int hitsPerPage = 10;

	private DirectoryTreeModel directoryModel;

	private int hitsPerBlock = hitsPerPage * 4;

	private int maxHits = hitsPerBlock;

	private String language = "all";

	private String query = "";

	private String phrase = "";

	private String any = "";

	private String nots = "";

	private String format = "all";

	private String path = null;

	private Integer sizeMin = null;

	private Integer sizeMax = null;

	private Date publishingDateFrom;

	private Date publishingDateTo;

	private Date sourceDateFrom;

	private Date sourceDateTo;

	private boolean fuzzy = false;

	private boolean content = true;

	private boolean keywords = true;

	private boolean source = false;

	private boolean sourceAuthor = false;

	private boolean sourceType = false;

	private boolean coverage = false;

	private boolean customId = false;

	private boolean title = true;

	private boolean searchInSubPath = false;

	private Collection<DocumentResult> documentResult = new ArrayList<DocumentResult>();

	private String[] extendedAttributes = new String[0];

	private Collection<SelectItem> extendedAttributesItems = new ArrayList<SelectItem>();

	private NavigationBean navigation;

	private Search lastSearch = null;

	private boolean showFolderSelector = false;

	private String parentPathDescr;

	private Long excludeFromResult = null;

	private UIInput uiSizeMin = null;

	private UIInput uiSizeMax = null;

	private UIInput uiSourceDateFrom = null;

	private UIInput uiSourceDateTo = null;

	private UIInput uiPublishingDateFrom = null;

	private UIInput uiPublishingDateTo = null;

	private Long template = null;

	public SearchForm() {
		setQuery(Messages.getMessage("search") + "...");
		setLanguage(SessionManagement.getLanguage());
	}

	public Long getTemplate() {
		return template;
	}

	public void setTemplate(Long template) {
		this.template = template;
	}

	public String getLanguage() {
		return language;
	}

	public String[] getExtendedAttributes() {
		return extendedAttributes;
	}

	public void setExtendedAttributes(String[] extendedAttributes) {
		this.extendedAttributes = extendedAttributes;
	}

	public String getAny() {
		return any;
	}

	public void setAny(String any) {
		this.any = any;
	}

	public boolean isContent() {
		return content;
	}

	public void setContent(boolean content) {
		this.content = content;
	}

	public boolean isCoverage() {
		return coverage;
	}

	public boolean isTitle() {
		return title;
	}

	public void setCoverage(boolean coverage) {
		this.coverage = coverage;
	}

	public void setTitle(boolean title) {
		this.title = title;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public boolean isFuzzy() {
		return fuzzy;
	}

	public void setFuzzy(boolean fuzzy) {
		this.fuzzy = fuzzy;
	}

	public boolean isKeywords() {
		return keywords;
	}

	public void setKeywords(boolean keywords) {
		this.keywords = keywords;
	}

	public String getNots() {
		return nots;
	}

	public void setNots(String not) {
		this.nots = not;
	}

	public String getPhrase() {
		return phrase;
	}

	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public boolean isSource() {
		return source;
	}

	public void setSource(boolean source) {
		this.source = source;
	}

	public boolean isSourceAuthor() {
		return sourceAuthor;
	}

	public void setSourceAuthor(boolean sourceAuthor) {
		this.sourceAuthor = sourceAuthor;
	}

	public boolean isSourceType() {
		return sourceType;
	}

	public void setSourceType(boolean sourceType) {
		this.sourceType = sourceType;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setDocumentResult(Collection<DocumentResult> result) {
		this.documentResult = result;
	}

	public int getHitsPerPage() {
		return hitsPerPage;
	}

	public void setHitsPerPage(int hitsPerPage) {
		this.hitsPerPage = hitsPerPage;
	}

	public List<DocumentResult> getDocumentResult() {
		return new ArrayList<DocumentResult>(documentResult);
	}

	public void validateSizes(FacesContext context, UIComponent component, Object value) throws ValidatorException {

		if (uiSizeMin == null) {
			return;
		}

		int sizeMinLocal = 0;
		int sizeMaxLocal = 0;

		Object entry = uiSizeMin.getLocalValue();

		if (entry != null) {
			if (entry instanceof Long) {
				Long entryLong = (Long) entry;
				sizeMinLocal = entryLong.intValue();
			} else {
				Integer entryInt = (Integer) entry;
				sizeMinLocal = entryInt.intValue();
			}
		}

		if (value != null) {
			if (value instanceof Long) {
				Long entryLong = (Long) value;
				sizeMaxLocal = entryLong.intValue();
			} else {
				Integer entryInt = (Integer) value;
				sizeMaxLocal = entryInt.intValue();
			}
		}

		if (sizeMinLocal > sizeMaxLocal) {
			FacesMessage message = new FacesMessage();
			message.setDetail(Messages.getMessage("errors.val.twonumbers"));
			message.setSummary(Messages.getMessage("errors.val.twonumbers"));
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(message);
		}
	}

	public void validateSourceDates(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {

		if (getSourceDateFrom() == null) {
			return;
		}

		Date lowerDate = getSourceDateFrom();
		Date upperDate = (Date) value;

		if ((lowerDate == null) || (upperDate == null)) {
			return;
		}

		if (lowerDate.compareTo(upperDate) > 0) {
			FacesMessage message = new FacesMessage();
			message.setDetail(Messages.getMessage("errors.val.twodates"));
			message.setSummary(Messages.getMessage("errors.val.twodates"));
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(message);
		}
	}

	public void validatePublishingDates(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		if (getPublishingDateFrom() == null) {
			return;
		}

		Date lowerDate = getPublishingDateFrom();
		Date upperDate = (Date) value;

		if ((lowerDate == null) || (upperDate == null)) {
			return;
		}

		if (lowerDate.compareTo(upperDate) > 0) {
			FacesMessage message = new FacesMessage();
			message.setDetail(Messages.getMessage("errors.val.twodates"));
			message.setSummary(Messages.getMessage("errors.val.twodates"));
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(message);
		}
	}

	/**
	 * Returns the results as a map, the key is docId.
	 * 
	 * @return
	 */
	public Map<String, Result> getResultMap() {
		Map<String, Result> map = new HashMap<String, Result>();
		for (Result result : lastSearch.getResults()) {
			map.put(Long.toString(result.getDocId()), result);
		}
		return map;
	}

	public String quickSearch() {
		phrase = "";
		any = "";
		nots = "";
		format = "all";
		path = null;
		parentPathDescr = null;
		fuzzy = false;
		content = true;
		keywords = true;
		source = false;
		sourceAuthor = false;
		sourceType = false;
		coverage = false;
		title = true;
		publishingDateFrom = null;
		publishingDateTo = null;
		sourceDateFrom = null;
		sourceDateTo = null;
		sizeMin = null;
		sizeMax = null;
		searchInSubPath = false;
		customId = false;
		excludeFromResult = null;
		template = null;
		maxHits = hitsPerBlock;
		return searchHits();
	}

	/**
	 * Launches the search.
	 */
	public String search() {
		maxHits = hitsPerBlock;
		return searchHits();
	}

	/**
	 * Execute the search.
	 * 
	 * <p>
	 * <b>Note:</b> only the first maxHits will be returned
	 * </p>
	 */
	public String searchHits() {

		if (SessionManagement.isValid()) {

			try {
				long userId = SessionManagement.getUserId();

				SearchOptions opt = new SearchOptions();
				ArrayList<String> fields = new ArrayList<String>();

				if (isContent()) {
					fields.add(LuceneDocument.FIELD_CONTENT);
				}

				if (isKeywords()) {
					fields.add(LuceneDocument.FIELD_KEYWORDS);
				}

				if (isSource()) {
					fields.add(LuceneDocument.FIELD_SOURCE);
				}

				if (isSourceAuthor()) {
					fields.add(LuceneDocument.FIELD_SOURCE_AUTHOR);
				}

				if (isSourceType()) {
					fields.add(LuceneDocument.FIELD_SOURCE_TYPE);
				}

				if (isCoverage()) {
					fields.add(LuceneDocument.FIELD_COVERAGE);
				}

				if (isTitle()) {
					fields.add(LuceneDocument.FIELD_TITLE);
				}

				if (isCustomId()) {
					fields.add(LuceneDocument.FIELD_CUSTOM_ID);
				}

				if (template != null && template.longValue() > 0) {
					opt.setTemplate(template);
					for (String attribute : extendedAttributes) {
						fields.add("ext_" + attribute);
					}
				} else {
					opt.setTemplate(null);
				}

				String[] flds = (String[]) fields.toArray(new String[fields.size()]);
				opt.setFields(flds);

				ArrayList<String> languages = new ArrayList<String>();

				if ("all".equals(language)) {
					List<String> iso639_2Languages = LanguageManager.getInstance().getISO639_2Languages();
					languages.addAll(iso639_2Languages);
				} else {
					languages.add(language);
				}

				String[] langs = (String[]) languages.toArray(new String[languages.size()]);
				opt.setLanguages(langs);

				opt.setFuzzy(isFuzzy());
				opt.setQueryStr(getQuery(), getPhrase(), getAny(), getNots());
				opt.setFormat(getFormat());

				if ((getPublishingDateFrom() != null) && (getPublishingDateTo() != null)) {
					opt.setDateFrom(getPublishingDateFrom());
					opt.setDateTo(getPublishingDateTo());
				}
				if ((getSourceDateFrom() != null) && (getSourceDateTo() != null)) {
					opt.setSourceDateFrom(getSourceDateFrom());
					opt.setSourceDateTo(getSourceDateTo());
				}

				opt.setLengthMin(getSizeMin());
				if (getSizeMax() != null && getSizeMax().intValue() > 0)
					opt.setLengthMax(getSizeMax());
				opt.setUserId(userId);

				if (StringUtils.isNotEmpty(getPath())) {
					opt.setPath(getPath());
					{
						if (isSearchInSubPath())
							opt.setSearchInSubPath(true);
					}
				}

				String searchLanguage = "all".equals(language) ? SessionManagement.getLanguage() : language;
				lastSearch = new Search(opt, searchLanguage);
				lastSearch.setMaxHits(maxHits);

				List<Result> result = lastSearch.search();

				List<DocumentResult> docResult = new ArrayList<DocumentResult>();

				for (Result myResult : result) {
					if (excludeFromResult != null && excludeFromResult.longValue() == myResult.getDocId())
						continue;
					DocumentResult dr = new DocumentResult(myResult);
					docResult.add(dr);
				}

				setDocumentResult(docResult);

				PageContentBean page = new PageContentBean("result", "search/result");
				StyleBean style=(StyleBean)Context.getInstance().getBean(StyleBean.class);
				page.setIcon(style.getImagePath("search.png"));
				page.setContentTitle(Messages.getMessage("msg.jsp.searchresult"));
				navigation.setSelectedPanel(page);
			} catch (Throwable e) {
				logger.error(e.getMessage(), e);
				Messages.addMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
			}
		} else {
			return "login";
		}

		return null;
	}

	public boolean isCustomId() {
		return customId;
	}

	public void setCustomId(boolean customId) {
		this.customId = customId;
	}

	/**
	 * Re-launches the search including one more block of hits.
	 */
	public String searchMore() {
		maxHits += hitsPerBlock;
		return searchHits();
	}

	/**
	 * Search for similar documents.
	 */
	@SuppressWarnings("unchecked")
	public String searchSimilar() {
		if (SessionManagement.isValid()) {
			Map map = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
			long docId;

			if (map.containsKey("entry")) {
				Result entry = (Result) map.get("entry");
				docId = entry.getDocId();
			} else {
				DocumentRecord entry = (DocumentRecord) map.get("documentRecord");
				docId = entry.getDocId();
			}

			try {
				DocumentManager manager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
				String text = manager.getDocumentContent(docId);
				// Extracts the most used 10 words
				AnalyzerManager analyzer = (AnalyzerManager) Context.getInstance().getBean(AnalyzerManager.class);
				String terms = analyzer.getTermsAsString(10, text, SessionManagement.getLanguage());
				terms = terms.replaceAll(",", " ");

				// Prepare a query and launch the search
				phrase = "";
				any = "";
				nots = "";
				format = "all";
				path = null;
				parentPathDescr = null;
				fuzzy = false;
				content = true;
				keywords = true;
				source = false;
				sourceAuthor = false;
				sourceType = false;
				coverage = false;
				title = true;
				publishingDateFrom = null;
				publishingDateTo = null;
				sourceDateFrom = null;
				sourceDateTo = null;
				sizeMin = null;
				sizeMax = null;
				template = null;
				searchInSubPath = false;
				maxHits = hitsPerBlock;
				setQuery(terms);
				setExcludeFromResult(docId);
				search();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				Messages.addMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
			}
		} else {
			return "login";
		}

		return null;
	}

	/**
	 * Search for similar documents.
	 */
	public String showDocumentPath() {

		if (SessionManagement.isValid()) {
			Map<String, Object> map = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();

			if (map.containsKey("entry")) {
				DocumentResult entry = (DocumentResult) map.get("entry");
				entry.showDocumentPath();
			}

		} else {
			return "login";
		}

		return null;
	}

	/**
	 * Cleans up the resources used by this class. This method could be called
	 * when a session destroyed event is called.
	 */
	public void dispose() {
		documentResult.clear();
	}

	public void reset() {
		setQuery(Messages.getMessage("search") + "...");
		phrase = "";
		any = "";
		nots = "";
		format = "all";
		path = null;
		parentPathDescr = null;
		publishingDateFrom = null;
		publishingDateTo = null;
		sourceDateFrom = null;
		sourceDateTo = null;
		sizeMin = null;
		sizeMax = null;
		fuzzy = false;
		content = false;
		keywords = false;
		source = false;
		sourceAuthor = false;
		sourceType = false;
		coverage = false;
		title = true;
		template = null;
		searchInSubPath = false;
		extendedAttributes = new String[0];
	}

	/**
	 * Shows the advanced search form.
	 */
	public String advanced() {

		if ((Messages.getMessage("search") + "...").equals(query)) {
			setQuery("");
		}

		PageContentBean page = new PageContentBean("advancedSearch", "search/advancedSearch");
		page.setContentTitle(Messages.getMessage("search.advanced"));
		StyleBean style=(StyleBean)Context.getInstance().getBean(StyleBean.class); 
		page.setIcon(style.getImagePath("extsearch.gif"));
		navigation.setSelectedPanel(page);

		return null;
	}

	public void setNavigation(NavigationBean navigation) {
		this.navigation = navigation;
	}

	public boolean isMoreHitsPresent() {
		return lastSearch.isMoreHitsPresent();
	}

	public int getHitsPerBlock() {
		return hitsPerBlock;
	}

	public int getEstimatedHitsNumber() {
		return lastSearch.getEstimatedHitsNumber();
	}

	public long getExecTime() {
		return lastSearch.getExecTime();
	}

	public Date getPublishingDateFrom() {
		return publishingDateFrom;
	}

	public void setPublishingDateFrom(Date publishingDateFrom) {
		this.publishingDateFrom = publishingDateFrom;
	}

	public Date getPublishingDateTo() {
		return publishingDateTo;
	}

	public Integer getSizeMin() {
		return sizeMin;
	}

	public void setSizeMin(Integer sizeMin) {
		this.sizeMin = sizeMin;
	}

	public Integer getSizeMax() {
		return sizeMax;
	}

	public void setSizeMax(Integer sizeMax) {
		this.sizeMax = sizeMax;
	}

	public Date getSourceDateFrom() {
		return sourceDateFrom;
	}

	public void setSourceDateFrom(Date sourceDateFrom) {
		this.sourceDateFrom = sourceDateFrom;
	}

	public Date getSourceDateTo() {
		return sourceDateTo;
	}

	public void setSourceDateTo(Date sourceDateTo) {

		if (sourceDateTo != null) {

			// Include all the specified day
			Calendar cal = Calendar.getInstance();
			cal.setTime(sourceDateTo);
			cal.set(Calendar.MILLISECOND, 0);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.HOUR, 23);
			this.sourceDateTo = cal.getTime();
		} else {
			this.sourceDateTo = sourceDateTo;
		}
	}

	public void setPublishingDateTo(Date publishingDateTo) {

		if (publishingDateTo != null) {

			// Include all the specified day
			Calendar cal = Calendar.getInstance();
			cal.setTime(publishingDateTo);
			cal.set(Calendar.MILLISECOND, 0);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.HOUR, 23);
			this.publishingDateTo = cal.getTime();
		} else {
			this.publishingDateTo = publishingDateTo;
		}
	}

	public UIInput getUiSizeMin() {
		return uiSizeMin;
	}

	public void setUiSizeMin(UIInput uiSizeMin) {
		this.uiSizeMin = uiSizeMin;
	}

	public UIInput getUiSizeMax() {
		return uiSizeMax;
	}

	public void setUiSizeMax(UIInput uiSizeMax) {
		this.uiSizeMax = uiSizeMax;
	}

	public UIInput getUiSourceDateFrom() {
		return uiSourceDateFrom;
	}

	public void setUiSourceDateFrom(UIInput uiSourceDateFrom) {
		this.uiSourceDateFrom = uiSourceDateFrom;
	}

	public UIInput getUiSourceDateTo() {
		return uiSourceDateTo;
	}

	public void setUiSourceDateTo(UIInput uiSourceDateTo) {
		this.uiSourceDateTo = uiSourceDateTo;
	}

	public UIInput getUiPublishingDateFrom() {
		return uiPublishingDateFrom;
	}

	public void setUiPublishingDateFrom(UIInput uiPublishingDateFrom) {
		this.uiPublishingDateFrom = uiPublishingDateFrom;
	}

	public UIInput getUiPublishingDateTo() {
		return uiPublishingDateTo;
	}

	public void setUiPublishingDateTo(UIInput uiPublishingDateTo) {
		this.uiPublishingDateTo = uiPublishingDateTo;
	}

	public void openFolderSelector(ActionEvent e) {
		showFolderSelector = true;
	}

	public void closeFolderSelector(ActionEvent e) {
		showFolderSelector = false;
		path = null;
		parentPathDescr = null;
	}
	
	public void cancelFolderSelector(ActionEvent e) {
		directoryModel.cancelSelection();
		showFolderSelector = false;
	}

	public void folderSelected(ActionEvent e) {
		showFolderSelector = false;

		Directory dir = directoryModel.getSelectedDir();
		if (dir.getMenuId() == Menu.MENUID_DOCUMENTS) {
			parentPathDescr = null;
			setPath(null);
			return;
		}

		Menu menu = dir.getMenu();
		String dirPath = menu.getPath() + "/" + menu.getId();
		setPath(dirPath);
		parentPathDescr = dir.getMenu().getText();
	}

	public String getParentPathDescr() {
		return parentPathDescr;
	}

	public void setParentPathDescr(String parentPathDescr) {
		this.parentPathDescr = parentPathDescr;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String parentPath) {
		this.path = parentPath;
	}

	public Collection<SelectItem> getExtendedAttributesItems() {
		return extendedAttributesItems;
	}

	public void setExtendedAttributesItems(Collection<SelectItem> extendedAttributesItems) {
		this.extendedAttributesItems = extendedAttributesItems;
	}

	public boolean isSearchInSubPath() {
		return searchInSubPath;
	}

	public void setSearchInSubPath(boolean searchInSubPath) {
		this.searchInSubPath = searchInSubPath;
	}

	public boolean isShowFolderSelector() {
		return showFolderSelector;
	}

	public void setShowFolderSelector(boolean showFolderSelector) {
		this.showFolderSelector = showFolderSelector;
	}

	public DirectoryTreeModel getDirectoryModel() {
		if (directoryModel == null) {
			loadTree();
		}
		return directoryModel;
	}

	void loadTree() {
		directoryModel = new DirectoryTreeModel();
	}

	public Long getExcludeFromResult() {
		return excludeFromResult;
	}

	public void setExcludeFromResult(Long excludeFromResult) {
		this.excludeFromResult = excludeFromResult;
	}

	private void initTemplate() {
		extendedAttributes = new String[0];
		extendedAttributesItems.clear();
		if (template != null) {
			DocumentTemplateDAO dao = (DocumentTemplateDAO) Context.getInstance().getBean(DocumentTemplateDAO.class);
			DocumentTemplate docTemplate = dao.findById(template);
			for (String attrName : docTemplate.getAttributes()) {
				extendedAttributesItems.add(new SelectItem(attrName));
			}
		}
	}

	public int getExtendedAttributesCount() {
		if (template != null) {
			DocumentTemplateDAO dao = (DocumentTemplateDAO) Context.getInstance().getBean(DocumentTemplateDAO.class);
			DocumentTemplate docTemplate = dao.findById(template);
			return docTemplate.getAttributes().size();
		} else {
			return 0;
		}
	}

	public void changeTemplate(ValueChangeEvent event) {
		Long item = (Long) event.getNewValue();
		this.template = item;
		initTemplate();
	}
}
