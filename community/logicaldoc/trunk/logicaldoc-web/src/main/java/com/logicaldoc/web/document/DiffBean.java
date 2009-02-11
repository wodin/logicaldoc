package com.logicaldoc.web.document;

import com.logicaldoc.core.document.Version;

/**
 * This bean handles diffs between versions 
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class DiffBean {

	private Version version1;
	
	private Version version2;

	public Version getVersion1() {
		return version1;
	}

	public void setVersion1(Version version1) {
		this.version1 = version1;
	}

	public Version getVersion2() {
		return version2;
	}

	public void setVersion2(Version version2) {
		this.version2 = version2;
	}
	
	
}
