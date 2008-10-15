package com.logicaldoc.core.searchengine.util;

/**
 * Created on 15.11.2004
 */
public class Edge {
	private int thickness;

	private long id;

	/**
	 * 
	 */
	public Edge() {
	}

	public Edge(int thick, long i) {
		thickness = thick;
		id = i;
	}

	/**
	 * @return Returns the id.
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id The id to set.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return Returns the thickness.
	 */
	public int getThickness() {
		return thickness;
	}

	/**
	 * @param thickness The thickness to set.
	 */
	public void setThickness(int thickness) {
		this.thickness = thickness;
	}
}