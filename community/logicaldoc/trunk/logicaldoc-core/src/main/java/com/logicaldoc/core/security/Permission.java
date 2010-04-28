package com.logicaldoc.core.security;

/**
 * Models a permission, that is the ability to do something
 * <p>
 * <ul>
 * <li>READ: ability to read the folder and its documents</li>
 * <li>WRITE: ability to insert and delete folder's documents</li>
 * <li>ADD_CHILD: ability to add child elements</li>
 * <li>MANAGE_SECURITY: ability to change security rules</li>
 * <li>MANAGE_IMMUTABILITY: ability to mark a document as immutable</li>
 * <li>DELETE: ability to delete the entity</li>
 * <li>RENAME: ability to rename the entity</li>
 * <li>BULK_IMPORT: ability to import documents</li>
 * <li>BULK_EXPORT: ability to export documents</li>
 * <li>SIGN: ability to digitally sign documents</li>
 * <li>ARCHIVE: ability to archive documents</li>
 * <li>WORKFLOW: ability to handle workflow</li>
 * </ul>
 * 
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public enum Permission {
	READ("read"), WRITE("write"), ADD_CHILD("addChild"), MANAGE_SECURITY("manageSecurity"), MANAGE_IMMUTABILITY(
			"manageImmutability"), DELETE("delete"), RENAME("rename"), BULK_IMPORT("bulkImport"), BULK_EXPORT(
			"bulkExport"), SIGN("sign"), ARCHIVE("archive"), WORKFLOW("workflow");

	private final String name;

	private int mask;

	private Permission(String name) {
		this.name = name;
		if ("write".equals(name))
			mask = Integer.parseInt("000000000010", 2);
		if ("addChild".equals(name))
			mask = Integer.parseInt("000000000100", 2);
		if ("manageSecurity".equals(name))
			mask = Integer.parseInt("000000001000", 2);
		if ("manageImmutability".equals(name))
			mask = Integer.parseInt("000000010000", 2);
		if ("delete".equals(name))
			mask = Integer.parseInt("000000100000", 2);
		if ("rename".equals(name))
			mask = Integer.parseInt("000001000000", 2);
		if ("bulkImport".equals(name))
			mask = Integer.parseInt("000010000000", 2);
		if ("bulkExport".equals(name))
			mask = Integer.parseInt("000100000000", 2);
		if ("sign".equals(name))
			mask = Integer.parseInt("001000000000", 2);
		if ("archive".equals(name))
			mask = Integer.parseInt("010000000000", 2);
		if ("workflow".equals(name))
			mask = Integer.parseInt("100000000000", 2);
	}

	public String getName() {
		return name;
	}

	public int getMask() {
		return mask;
	}

	public boolean match(int permission) {
		return (permission & mask) != 0;
	}
}