package com.logicaldoc.webservice.model;

import javax.xml.bind.annotation.XmlType;

import com.logicaldoc.webservice.doc.WSDoc;

/**
 * Useful class to associate a user or a group to a permission integer
 * representation.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
@XmlType(name = "WSRight")
public class WSRight {
	@WSDoc(description = "unique identifier of a user or a group")
	private long id;

	@WSDoc(description = "permissions mask. "
			+ "<br/> this is an integer representation of a list of 13 bits. Each bit refers to a permission: <b>0</b> to deny, <b>1</b> to grant the permission."
			+ "<br/> This list represent the bit array starting from left to right:"
			+ "<ol><li>Password</li><li>Print</li><li>Subscription</li><li>Calendar</li><li>Download </li><li>Workflow</li><li>Archive</li><li>Sign</li><li>Export</li><li>Import</li>"
			+ "<li>Delete</li><li>Immutable</li><li>Security</li><li>Add</li><li>Write</li><li>Read</li></ol>"
			+ "In particular, 'Read' is represented by the last right bit while 'Download' is represented by the first left bit.<br/>"
			+ "Here are two examples:"
			+ "</p><p>A) if you want to assign to a user the permissions Read, Write, Immutable, Rename, Sign, Download, the 'permissions' value must be <b>4691</b>, in fact it is <b>01001001010011</b> in binary representation."
			+ "</p><p>B) if you want to assign to a group the permissions Read, Write, Add, Security, Import, Archive, Workflow, the 'permissions' value must be <b>3215</b>, in fact it is <b>00110010001111</b> in binary representation." 
			+ "</p><p><br></p>")
	private int permissions;

	public WSRight() {
	}

	public WSRight(long id, int permissions) {
		this.id = id;
		this.permissions = permissions;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getPermissions() {
		return permissions;
	}

	public void setPermissions(int permissions) {
		this.permissions = permissions;
	}
}
