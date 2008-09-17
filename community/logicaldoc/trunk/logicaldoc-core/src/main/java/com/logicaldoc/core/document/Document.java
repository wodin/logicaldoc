package com.logicaldoc.core.document;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.logicaldoc.core.security.Menu;

/**
 * This class represents documents.
 * 
 * @author Michael Scholz
 * @author Marco Meschieri
 * @version 1.0
 */
public class Document {
	public static final int DOC_CHECKED_IN = 0;

	public static final int DOC_CHECKED_OUT = 1;

	private int docId = 0;

	private String docName = "";

	private String docVersion = "";

	private String docDate = "";

	private String docPublisher = "";

	/**
	 * Whether document is checked in or out;
	 * 
	 * @see Document#DOC_CHECKED_IN
	 * @see Document#DOC_CHECKED_OUT
	 */
	private int docStatus = DOC_CHECKED_IN;

	private String docType = "";

	private String checkoutUser = "";

	private String source = "";

	private String sourceAuthor = "";

	private String sourceDate = "";

	private String sourceType = "";

	private String coverage = "";

	private String language = "";

	private Set<String> keywords = new HashSet<String>();

	private Set<Version> versions = new HashSet<Version>();

	private Menu menu;

	public Document() {
	}

	public int getDocId() {
		return docId;
	}

	public String getDocName() {
		return docName;
	}

	public String getDocVersion() {
		return docVersion;
	}

	public String getDocDate() {
		return docDate;
	}

	public String getDocPublisher() {
		return docPublisher;
	}

	public int getDocStatus() {
		return docStatus;
	}

	public int getMenuId() {
		return getMenu().getMenuId();
	}

	public String getDocType() {
		return docType;
	}

	public String getCheckoutUser() {
		return checkoutUser;
	}

	public String getSource() {
		return source;
	}

	public String getSourceAuthor() {
		return sourceAuthor;
	}

	public String getSourceDate() {
		return sourceDate;
	}

	public String getSourceType() {
		return sourceType;
	}

	public String getCoverage() {
		return coverage;
	}

	public String getLanguage() {
		return language;
	}

	public Set<String> getKeywords() {
		return keywords;
	}

	public String getKeywordsString() {
		StringBuffer sb = new StringBuffer();
		Iterator<String> iter = keywords.iterator();
		boolean start = true;

		while (iter.hasNext()) {
			String words = iter.next();

			if (!start) {
				sb.append(", ");
			} else {
				start = false;
			}

			sb.append(words);
		}

		return sb.toString();
	}

	public Set<Version> getVersions() {
		return versions;
	}

	public Menu getMenu() {
		return menu;
	}

	public void setDocId(int id) {
		docId = id;
	}

	public void setDocName(String name) {
		docName = name;
	}

	public void setDocVersion(String version) {
		docVersion = version;
	}

	public void setDocDate(String date) {
		docDate = date;
	}

	public void setDocPublisher(String publisher) {
		docPublisher = publisher;
	}

	public void setDocStatus(int status) {
		docStatus = status;
	}

	public void setDocType(String type) {
		docType = type;
	}

	public void setCheckoutUser(String user) {
		checkoutUser = user;
	}

	public void setSource(String src) {
		source = src;
	}

	public void setSourceAuthor(String author) {
		sourceAuthor = author;
	}

	public void setSourceDate(String date) {
		sourceDate = date;
	}

	public void setSourceType(String type) {
		sourceType = type;
	}

	public void setCoverage(String cover) {
		coverage = cover;
	}

	public void setLanguage(String lang) {
		language = lang;
	}

	public void clearKeywords() {
		keywords.clear();
	}

	public void setKeywords(Set<String> words) {
		keywords = words;
	}

	public void setVersions(Set<Version> vers) {
		versions = vers;
	}

	public void setMenu(Menu m) {
		menu = m;
	}

	public void addKeyword(String word) {
		keywords.add(word);
	}

	public void addVersion(Version vers) {
		versions.add(vers);
	}

	/**
	 * Iterates over the versions searching for the specified id
	 * 
	 * @param id The version id
	 * @return The found version
	 */
	public Version getVersion(String id) {
		for (Version version : versions) {
			if (version.getVersion().equals(id))
				return version;
		}
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Document))
			return false;

		Document other = (Document) obj;
		return other.getDocId() == this.getDocId();
	}

	@Override
	public int hashCode() {
		return new Integer(docId).hashCode();
	}

	@Override
	public String toString() {
		return Integer.toString(docId);
	}
}