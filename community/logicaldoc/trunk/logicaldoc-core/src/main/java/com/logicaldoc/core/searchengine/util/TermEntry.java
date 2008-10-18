package com.logicaldoc.core.searchengine.util;

import java.util.ArrayList;
import java.util.Collection;


public class TermEntry {
	private String name;

	private double value;

	private String originWord;

	private Collection<Edge> documents;

	/**
	 * 
	 */
	public TermEntry() {
		name = "";
		value = 0d;
		originWord = "";
		documents = new ArrayList<Edge>();
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the documents.
	 */
	public Collection<Edge> getDocuments() {
		return documents;
	}

	/**
	 * @param documents The documents to set.
	 */
	public void setDocuments(Collection<Edge> documents) {
		this.documents = documents;
	}

	/**
	 * Adds the id of an document to the document list.
	 * 
	 * @param id
	 */
	public void addDocument(Edge edge) {
		documents.add(edge);
	}

	/**
	 * @return Returns the originWord.
	 */
	public String getOriginWord() {
		return originWord;
	}

	/**
	 * @param originWord The originWord to set.
	 */
	public void setOriginWord(String originWord) {
		this.originWord = originWord;
	}

	/**
	 * @return Returns the value.
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @param value The value to set.
	 */
	public void setValue(double value) {
		this.value = value;
	}
}