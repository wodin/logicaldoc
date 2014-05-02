package com.logicaldoc.core.security;

import java.io.Serializable;

import com.logicaldoc.core.PersistentObject;

/**
 * This class represents a Tenant, that is a branch of the organization or an
 * organizational unit or whatever other class of organization.
 * 
 * @author Marco Meschieri
 * 
 * @version 6.9
 */
public class Tenant extends PersistentObject implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final long SYSTEM_ID = -1L;
	
	public static final long DEFAULT_ID = 1L;

	public static final String DEFAULT_NAME = "default";

	public static final int DEFAULT_TYPE = 0;

	private String name;

	private String displayName;

	private String street;

	private String postalCode;

	private String city;

	private String state;

	private String country;

	private String email;

	private String telephone;

	private int type = DEFAULT_TYPE;

	private Integer maxUsers;

	private Integer maxSessions;

	private Long maxRepoDocs;

	/**
	 * Maximum repository size expressed in MB
	 */
	private Long maxRepoSize;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	@Override
	public String toString() {
		return displayName != null ? displayName : name;
	}

	public Integer getMaxUsers() {
		return maxUsers;
	}

	public void setMaxUsers(Integer maxUsers) {
		this.maxUsers = maxUsers;
	}

	public Integer getMaxSessions() {
		return maxSessions;
	}

	public void setMaxSessions(Integer maxSessions) {
		this.maxSessions = maxSessions;
	}

	public Long getMaxRepoDocs() {
		return maxRepoDocs;
	}

	public void setMaxRepoDocs(Long maxRepoDocs) {
		this.maxRepoDocs = maxRepoDocs;
	}

	public Long getMaxRepoSize() {
		return maxRepoSize;
	}

	public void setMaxRepoSize(Long maxRepoSize) {
		this.maxRepoSize = maxRepoSize;
	}
}