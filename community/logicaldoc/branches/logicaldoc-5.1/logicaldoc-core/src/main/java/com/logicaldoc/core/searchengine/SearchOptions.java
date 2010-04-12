package com.logicaldoc.core.searchengine;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import com.logicaldoc.core.text.StringParser;

/**
 * Search options
 * 
 * @author Michael Scholz
 */
public class SearchOptions {

	private String queryStr = "";

	// Min size in bytes
	private Long sizeMin = null;

	// Max size in bytes
	private Long sizeMax = null;

	private String format = "";

	private boolean searchInSubPath = false;

	private long userId = -1;

	private String path = null;

	private String[] fields = null;

	private String[] languages = null;

	private Date dateFrom = null;

	private Date dateTo = null;

	private Date sourceDateFrom = null;

	private Date sourceDateTo = null;

	private Date creationFrom = null;

	private Date creationTo = null;

	private Long template = null;

	/** Creates a new instance of SearchOptions */
	public SearchOptions() {
	}

	public Long getTemplate() {
		return template;
	}

	public void setTemplate(Long template) {
		this.template = template;
	}

	public String getQueryStr() {
		return queryStr;
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

	public void setQueryStr(String query) {
		queryStr = query;
	}

	public void setQueryStr(String query, String phrase, String any, String not) {
		queryStr = query;

		if ((phrase != null) && !phrase.equals("")) {
			queryStr += " \"" + phrase + "\"";
		}

		if ((any != null) && !any.equals("")) {
			boolean first = true;
			StringParser sp = new StringParser(any);
			Collection<String> collany = sp.getWordTable();
			Iterator<String> iter = collany.iterator();

			while (iter.hasNext()) {
				String word = iter.next();

				if (!first) {
					queryStr += " OR";
				} else {
					first = false;
				}

				queryStr += " " + word;
			}
		}

		if ((not != null) && !not.equals("")) {
			queryStr += " NOT (" + not + ")";
		}
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

	public String[] getLanguages() {
		return languages;
	}

	public void setLanguages(String[] languages) {
		this.languages = languages;
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

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
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
}