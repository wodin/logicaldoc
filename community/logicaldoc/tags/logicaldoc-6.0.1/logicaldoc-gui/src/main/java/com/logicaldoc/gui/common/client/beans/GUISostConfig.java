package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * This represent the Generic storing the configuration of a Storage document
 * template mapping.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class GUISostConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	private long templateId;

	private int documentType;

	private int signRequired = 0;

	private String label;

	private Date date1;

	private Date date2;

	private int deleted;

	private Double double1;

	private Double double2;

	private long id;

	private Integer integer1;

	private Integer integer2;

	private Date lastModified;

	private String string1;

	private String string2;

	private String type;

	private String subtype;

	private String[] attributeNames;

	private Map<String, GUIExtendedAttribute> attributes;

	public GUISostConfig() {
	}

	public long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}

	public int getDocumentType() {
		return documentType;
	}

	public void setDocumentType(int documentType) {
		this.documentType = documentType;
	}

	public int getSignRequired() {
		return signRequired;
	}

	public void setSignRequired(int signRequired) {
		this.signRequired = signRequired;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Date getDate1() {
		return date1;
	}

	public void setDate1(Date date1) {
		this.date1 = date1;
	}

	public Date getDate2() {
		return date2;
	}

	public void setDate2(Date date2) {
		this.date2 = date2;
	}

	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public Double getDouble1() {
		return double1;
	}

	public void setDouble1(Double double1) {
		this.double1 = double1;
	}

	public Double getDouble2() {
		return double2;
	}

	public void setDouble2(Double double2) {
		this.double2 = double2;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Integer getInteger1() {
		return integer1;
	}

	public void setInteger1(Integer integer1) {
		this.integer1 = integer1;
	}

	public Integer getInteger2() {
		return integer2;
	}

	public void setInteger2(Integer integer2) {
		this.integer2 = integer2;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getString1() {
		return string1;
	}

	public void setString1(String string1) {
		this.string1 = string1;
	}

	public String getString2() {
		return string2;
	}

	public void setString2(String string2) {
		this.string2 = string2;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubtype() {
		return subtype;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public String[] getAttributeNames() {
		return attributeNames;
	}

	public void setAttributeNames(String[] attributeNames) {
		this.attributeNames = attributeNames;
	}

	public Map<String, GUIExtendedAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, GUIExtendedAttribute> attributes) {
		this.attributes = attributes;
	}

	// public ExtendedAttribute getExtendedAttribute(String name) {
	// return generic.getExtendedAttribute(name);
	// }
	//
	// public Object getValue(String name) {
	// return generic.getValue(name);
	// }
	//
	// public void removeAttribute(String name) {
	// generic.removeAttribute(name);
	// }
}
