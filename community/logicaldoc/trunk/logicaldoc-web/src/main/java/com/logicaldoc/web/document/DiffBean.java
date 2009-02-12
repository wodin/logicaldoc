package com.logicaldoc.web.document;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.document.dao.VersionDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.FacesUtil;

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
		this.version2 = version1;
	}

	public Version getVersion2() {
		return version2;
	}

	public void setVersion2(Version version2) {
		this.version2 = version2;
	}

	public String getVersion2Id() {
		if (version2 != null)
			return version2.getVersion();
		else
			return version1.getVersion();
	}

	public void setVersion2Id(String version2Id) {
		VersionsRecordsManager manager = ((VersionsRecordsManager) FacesUtil.accessBeanFromFacesContext(
				"versionsRecordsManager", FacesContext.getCurrentInstance()));
		version2 = manager.getVersion(version2Id).getWrappedVersion();
		VersionDAO vdao = (VersionDAO) Context.getInstance().getBean(VersionDAO.class);
		vdao.initialize(version2);
	}

	public List<String> getTemplateAttributes() {
		List<String> attributes = new ArrayList<String>();
		if (version1 != null && version1.getTemplateId() != null)
			for (String name : version1.getAttributeNames()) {
				if (!attributes.contains(name))
					attributes.add(name);
			}
		if (version2 != null && version2.getTemplateId() != null)
			for (String name : version2.getAttributeNames()) {
				if (!attributes.contains(name))
					attributes.add(name);
			}
		return attributes;
	}
}