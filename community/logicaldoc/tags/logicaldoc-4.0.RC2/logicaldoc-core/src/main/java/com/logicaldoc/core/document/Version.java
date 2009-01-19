package com.logicaldoc.core.document;

import java.util.Date;

/**
 * This class represents versions.
 * 
 * @author Michael Scholz
 * @version 1.0
 */
public class Version implements Comparable<Version> {
	/**
	 * specifies different version types
	 */
	public enum VERSION_TYPE {
		NEW_RELEASE, NEW_SUBVERSION, OLD_VERSION;
	}

	private String version;

	private String username;

	private Date date;

	private String comment;

	private long userId;

	public Version() {
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	/**
	 * @see Version#getVersion()
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @see Version#getUsername()
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @see Version#getDate()
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @see Version#getComment()
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @see Version#setVersion(java.lang.String)
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @see Version#setUsername(java.lang.String)
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @see Version#setDate(java.lang.String)
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @see Version#setComment(java.lang.String)
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @see Version#getNewVersionName(java.lang.String,
	 *      VersionImpl.VERSION_TYPE)
	 */
	public String getNewVersionName(String oldVersionName, VERSION_TYPE versionType) {
		String release = oldVersionName.substring(0, oldVersionName.indexOf("."));
		String version = oldVersionName.substring(oldVersionName.lastIndexOf(".") + 1);

		int number;
		switch (versionType) {
		case NEW_RELEASE:
			number = Integer.parseInt(release);
			release = String.valueOf(number + 1);
			version = "0";
			break;
		case NEW_SUBVERSION:
			number = Integer.parseInt(version);
			version = String.valueOf(number + 1);
			break;
		case OLD_VERSION:
			return oldVersionName;
		}

		return release + "." + version;
	}

	/** for sorting a list of Version objects by the version number */
	public int compareTo(Version other) {
		return this.getVersion().toLowerCase().compareTo(other.getVersion().toLowerCase());
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Version))
			return false;
		Version other = (Version) obj;
		return this.getVersion().equals(other.getVersion());
	}

	@Override
	public int hashCode() {
		return version.hashCode();
	}

	@Override
	public String toString() {
		return version.toString() + "-" + comment;
	}
}