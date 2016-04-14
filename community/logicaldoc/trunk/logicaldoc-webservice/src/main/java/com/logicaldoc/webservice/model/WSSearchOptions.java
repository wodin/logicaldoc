package com.logicaldoc.webservice.model;

import java.util.Locale;

import com.logicaldoc.core.searchengine.FulltextSearchOptions;
import com.logicaldoc.core.searchengine.SearchOptions;
import com.logicaldoc.webservice.AbstractService;

/**
 * Search options through Web Services.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.7
 */
public class WSSearchOptions implements Comparable<WSSearchOptions> {

	protected int maxHits = 40;

	private int type = SearchOptions.TYPE_FULLTEXT;

	protected String expression = "";

	protected String name = "";

	protected String description = "";

	private String topOperator;

	private int caseSensitive = 1;

	private int retrieveAliases = 0;

	/**
	 * Optional set of document ids. If specified only documents inside this set
	 * will be returned.
	 */
	private Long[] filterIds = null;

	private Long folderId = null;

	private int searchInSubPath = 1;

	private String expressionLanguage = Locale.ENGLISH.getLanguage();

	// Min size in bytes
	private Long sizeMin = null;

	// Max size in bytes
	private Long sizeMax = null;

	private String format = "";

	private String[] fields = null;

	private String language = null;

	private String dateFrom = null;

	private String dateTo = null;

	private String sourceDateFrom = null;

	private String sourceDateTo = null;

	private String creationFrom = null;

	private String creationTo = null;

	private Long template = null;

	/** Necessary constructor for the Search Web Service */
	public WSSearchOptions() {
	}

	public void setExpression(String expr) {
		this.expression = expr;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getMaxHits() {
		return maxHits;
	}

	public void setMaxHits(int maxHits) {
		this.maxHits = maxHits;
	}

	public String getExpression() {
		return expression;
	}

	public String getTopOperator() {
		return topOperator;
	}

	public void setTopOperator(String topOperator) {
		this.topOperator = topOperator;
	}

	public Long[] getFilterIds() {
		return filterIds;
	}

	public void setFilterIds(Long[] filterIds) {
		this.filterIds = filterIds;
	}

	public int getCaseSensitive() {
		return caseSensitive;
	}

	public void setCaseSensitive(int caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	public int getRetrieveAliases() {
		return retrieveAliases;
	}

	public void setRetrieveAliases(int retrieveAliases) {
		this.retrieveAliases = retrieveAliases;
	}

	public Long getFolderId() {
		return folderId;
	}

	public void setFolderId(Long folderId) {
		this.folderId = folderId;
	}

	public int isSearchInSubPath() {
		return searchInSubPath;
	}

	public void setSearchInSubPath(int searchInSubPath) {
		this.searchInSubPath = searchInSubPath;
	}

	@Override
	public int compareTo(WSSearchOptions o) {
		return this.getName().compareTo(o.getName());
	}

	public String getExpressionLanguage() {
		return expressionLanguage;
	}

	public void setExpressionLanguage(String expressionLanguage) {
		this.expressionLanguage = expressionLanguage;
	}

	public Long getSizeMin() {
		return sizeMin;
	}

	public void setSizeMin(Long sizeMin) {
		this.sizeMin = sizeMin;
	}

	public Long getSizeMax() {
		return sizeMax;
	}

	public void setSizeMax(Long sizeMax) {
		this.sizeMax = sizeMax;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String[] getFields() {
		return fields;
	}

	public void setFields(String[] fields) {
		this.fields = fields;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Long getTemplate() {
		return template;
	}

	public void setTemplate(Long template) {
		this.template = template;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public String getSourceDateFrom() {
		return sourceDateFrom;
	}

	public void setSourceDateFrom(String sourceDateFrom) {
		this.sourceDateFrom = sourceDateFrom;
	}

	public String getSourceDateTo() {
		return sourceDateTo;
	}

	public void setSourceDateTo(String sourceDateTo) {
		this.sourceDateTo = sourceDateTo;
	}

	public String getCreationFrom() {
		return creationFrom;
	}

	public void setCreationFrom(String creationFrom) {
		this.creationFrom = creationFrom;
	}

	public String getCreationTo() {
		return creationTo;
	}

	public void setCreationTo(String creationTo) {
		this.creationTo = creationTo;
	}
	
	public static WSSearchOptions fromSearchOptions(SearchOptions opt) {
		WSSearchOptions wopt = new WSSearchOptions();

		wopt.setType(opt.getType());
		wopt.setName(opt.getName());
		wopt.setDescription(opt.getDescription());
		wopt.setExpression(opt.getExpression());
		wopt.setFolderId(opt.getFolderId());
		wopt.setSearchInSubPath(opt.isSearchInSubPath() ? 1 : 0);
		wopt.setMaxHits(opt.getMaxHits());
		wopt.setRetrieveAliases(opt.getRetrieveAliases());
		wopt.setTemplate(opt.getTemplate());
		wopt.setCaseSensitive(opt.isCaseSensitive() ? 1 : 0);
		if (opt.getFilterIds() != null && opt.getFilterIds() != null)
			wopt.setFilterIds(opt.getFilterIds().toArray(new Long[0]));

		switch (opt.getType()) {
		case SearchOptions.TYPE_FULLTEXT:
			wopt.setCreationFrom(AbstractService.convertDateToString(((FulltextSearchOptions) opt).getCreationFrom()));
			wopt.setCreationTo(AbstractService.convertDateToString(((FulltextSearchOptions) opt).getCreationTo()));
			wopt.setDateFrom(AbstractService.convertDateToString(((FulltextSearchOptions) opt).getDateFrom()));
			wopt.setDateTo(AbstractService.convertDateToString(((FulltextSearchOptions) opt).getDateTo()));
			wopt.setSourceDateFrom(AbstractService.convertDateToString(((FulltextSearchOptions) opt)
					.getSourceDateFrom()));
			wopt.setSourceDateTo(AbstractService.convertDateToString(((FulltextSearchOptions) opt).getSourceDateTo()));
			wopt.setExpressionLanguage(((FulltextSearchOptions) opt).getExpressionLanguage());
			wopt.setLanguage(((FulltextSearchOptions) opt).getLanguage());
			wopt.setFields(((FulltextSearchOptions) opt).getFields());
			wopt.setFormat(((FulltextSearchOptions) opt).getFormat());
			wopt.setSizeMax(((FulltextSearchOptions) opt).getSizeMax());
			wopt.setSizeMin(((FulltextSearchOptions) opt).getSizeMin());

			break;
		default:
		}

		return wopt;
	}

	public SearchOptions toSearchOptions() {
		SearchOptions so = null;
		switch (type) {
		case SearchOptions.TYPE_FULLTEXT:
			so = new FulltextSearchOptions();
			so.setCaseSensitive(caseSensitive == 1);
			so.setDescription(description);
			so.setExpression(expression);

			if (filterIds != null)
				for (Long id : filterIds) {
					so.getFilterIds().add(id);
				}

			so.setFolderId(folderId);
			so.setMaxHits(maxHits);
			so.setName(name);
			so.setRetrieveAliases(retrieveAliases);
			so.setSearchInSubPath(searchInSubPath == 1);
			so.setTopOperator(topOperator);
			so.setTemplate(getTemplate());

			((FulltextSearchOptions) so).setCreationFrom(AbstractService.convertStringToDate(creationFrom));
			((FulltextSearchOptions) so).setCreationTo(AbstractService.convertStringToDate(creationTo));
			((FulltextSearchOptions) so).setDateFrom(AbstractService.convertStringToDate(dateFrom));
			((FulltextSearchOptions) so).setDateTo(AbstractService.convertStringToDate(dateTo));
			((FulltextSearchOptions) so).setExpressionLanguage(expressionLanguage);
			((FulltextSearchOptions) so).setFields(fields);
			((FulltextSearchOptions) so).setFormat(format);
			((FulltextSearchOptions) so).setLanguage(language);
			((FulltextSearchOptions) so).setSizeMax(sizeMax);
			((FulltextSearchOptions) so).setSizeMin(sizeMin);
			((FulltextSearchOptions) so).setSourceDateFrom(AbstractService.convertStringToDate(sourceDateFrom));
			((FulltextSearchOptions) so).setSourceDateTo(AbstractService.convertStringToDate(sourceDateTo));
			break;
		default:
		}
		return so;
	}
}