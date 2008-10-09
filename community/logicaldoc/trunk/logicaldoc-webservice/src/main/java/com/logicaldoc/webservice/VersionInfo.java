package com.logicaldoc.webservice;

/**
 * Version informations
 * 
 * @author Marco Meschieri
 * @version $Id:$
 * @since 3.0
 */
public class VersionInfo {
	private String id;

	private String date;

	private String description;

	private String uploadUser;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUploadUser() {
		return uploadUser;
	}

	public void setUploadUser(String uploadUser) {
		this.uploadUser = uploadUser;
	}
}