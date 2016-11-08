package com.logicaldoc.core;

/**
 * Represent a status of the application
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.5
 */
public enum RunLevel {
	DEFAULT("default"), BULKLOAD("bulkload"), UPDATED("updated");

	private String level;

	RunLevel(String level) {
		this.level = level;
	}

	public String toString() {
		return this.level;
	}

	public static RunLevel fromString(String event) {
		if (event != null) {
			for (RunLevel b : RunLevel.values()) {
				if (event.equalsIgnoreCase(b.level)) {
					return b;
				}
			}
		}
		return null;
	}
}