package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;
import java.util.Date;

/**
 * Search options
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class GUISearchOptions implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final int TYPE_FULLTEXT = 0;

	public static final int TYPE_PARAMETRIC = 1;

	private int maxHits = 40;

	private int type = TYPE_FULLTEXT;

	private String expression = "";

	private String expressionLanguage = "en";

	// Min size in bytes
	private Long sizeMin = null;

	// Max size in bytes
	private Long sizeMax = null;

	private String format = "";

	private boolean searchInSubPath = false;

	private long userId = -1;

	private Long folderId = null;

	private String[] fields = null;

	private String language = null;

	// // Useful for parametric searches
	// private Serializable[] parameters = null;

	private Date dateFrom = null;

	private Date dateTo = null;

	private Date sourceDateFrom = null;

	private Date sourceDateTo = null;

	private Date creationFrom = null;

	private Date creationTo = null;

	private Long template = null;

	private String name = "";

	private String description = "";

	/** Creates a new instance of SearchOptions */
	public GUISearchOptions() {
	}

	public Long getTemplate() {
		return template;
	}

	public void setTemplate(Long template) {
		this.template = template;
	}

	public String getExpression() {
		return expression;
	}

	public String getFormat() {
		return format;
	}

	public long getUserId() {
		return userId;
	}

	public String[] getFields() {
		return fields;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public void setFormat(String form) {
		format = form;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public void setFields(String[] flds) {
		fields = flds;
	}

	public void addField(String s) {
		fields[fields.length] = s;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
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
		this.sourceDateTo = sourceDateTo;
	}

	public boolean isSearchInSubPath() {
		return searchInSubPath;
	}

	public void setSearchInSubPath(boolean searchInSubPath) {
		this.searchInSubPath = searchInSubPath;
	}

	public Date getCreationTo() {
		return creationTo;
	}

	public void setCreationTo(Date creationTo) {
		this.creationTo = creationTo;
	}

	public Date getCreationFrom() {
		return creationFrom;
	}

	public void setCreationFrom(Date creationFrom) {
		this.creationFrom = creationFrom;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Long getFolderId() {
		return folderId;
	}

	public void setFolderId(Long folderId) {
		this.folderId = folderId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getQueryLanguage() {
		return expressionLanguage;
	}

	public void setExpressionLanguage(String queryLanguage) {
		this.expressionLanguage = queryLanguage;
	}

	// public Serializable[] getParameters() {
	// return parameters;
	// }
	//
	// public void setParameters(Serializable[] parameters) {
	// this.parameters = parameters;
	// }

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

	public boolean isFulltext() {
		return getType() == TYPE_FULLTEXT;
	}
}
