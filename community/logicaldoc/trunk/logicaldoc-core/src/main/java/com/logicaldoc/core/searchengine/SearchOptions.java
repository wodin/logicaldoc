package com.logicaldoc.core.searchengine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import com.logicaldoc.core.text.StringParser;

/**
 * Search options
 * 
 * @author Michael Scholz
 */
public class SearchOptions implements Serializable, Comparable<SearchOptions> {

	private static final long serialVersionUID = 1L;

	public static final int TYPE_FULLTEXT = 0;

	public static final int TYPE_PARAMETRIC = 1;

	private int maxHits = 40;

	private int type = TYPE_FULLTEXT;

	private String queryStr = "";

	private String queryLanguage = Locale.ENGLISH.getLanguage();

	// Min size in bytes
	private Long sizeMin = null;

	// Max size in bytes
	private Long sizeMax = null;

	private String format = "";

	private boolean searchInSubPath = false;

	private long userId = -1;

	private Long folderId = null;

	private String[] fields = null;

	private String[] languages = null;

	// Useful for parametric searches
	private Object[] parameters = null;

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
		return queryLanguage;
	}

	public void setQueryLanguage(String queryLanguage) {
		this.queryLanguage = queryLanguage;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

	public static SearchOptions read(File file) throws FileNotFoundException, IOException, ClassNotFoundException {
		SearchOptions searchOptions = null;
		// Deserialize from a file
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
		try {
			// Deserialize the object
			searchOptions = (SearchOptions) in.readObject();
		} finally {
			in.close();
		}
		return searchOptions;
	}

	public void write(File file) throws FileNotFoundException, IOException {
		// Serialize to a file
		ObjectOutput out = new ObjectOutputStream(new FileOutputStream(file));
		try {
			out.writeObject(this);
		} finally {
			out.flush();
			out.close();
		}
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

	@Override
	public int compareTo(SearchOptions o) {
		return this.getName().compareTo(o.getName());
	}

	public int getMaxHits() {
		return maxHits;
	}

	public void setMaxHits(int maxHits) {
		this.maxHits = maxHits;
	}
}