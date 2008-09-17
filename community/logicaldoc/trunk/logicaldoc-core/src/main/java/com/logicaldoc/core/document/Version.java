package com.logicaldoc.core.document;

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

	private String versionUser = "";

	private String versionDate = "";

	private String versionComment = "";

	public Version() {
	}

	public String getVersion() {
		return version;
	}

	public String getVersionUser() {
		return versionUser;
	}

	public String getVersionDate() {
		return versionDate;
	}

	public String getVersionComment() {
		return versionComment;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setVersionUser(String vuser) {
		versionUser = vuser;
	}

	public void setVersionDate(String date) {
		versionDate = date;
	}

	public void setVersionComment(String comment) {
		versionComment = comment;
	}

	/**
	 * returns a new version name
	 * 
	 * @param oldVersionName name of the old version, e.g. 1.5
	 * @param versionType what kind of version, so if it should be 1.6, 2.0 or
	 *        remain 1.5
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
		return version.toString();
	}
}