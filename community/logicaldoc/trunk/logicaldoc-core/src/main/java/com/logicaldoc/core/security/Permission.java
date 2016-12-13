package com.logicaldoc.core.security;

import java.util.HashSet;
import java.util.Set;

/**
 * Models a permission, that is the ability to do something
 * <p>
 * <ul>
 * <li>READ: ability to read the folder and its documents</li>
 * <li>WRITE: ability to insert and delete folder's documents</li>
 * <li>ADD: ability to add child elements</li>
 * <li>SECURITY: ability to change security rules</li>
 * <li>IMMUTABILE: ability to mark a document as immutable</li>
 * <li>DELETE: ability to delete the entity</li>
 * <li>RENAME: ability to rename the entity</li>
 * <li>IMPORT: ability to import documents</li>
 * <li>EXPORT: ability to export documents</li>
 * <li>SIGN: ability to digitally sign documents</li>
 * <li>ARCHIVE: ability to archive documents</li>
 * <li>WORKFLOW: ability to handle workflow</li>
 * <li>CALENDAR: ability to handle calendar events</li>
 * <li>SUBSCRIPTION: ability to handle events subscription</li>
 * <li>PRINT: ability to print</li>
 * <li>PASSWORD: ability to put a password in a document</li>
 * </ul>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public enum Permission {
	READ("read"), DOWNLOAD("download"), WRITE("write"), ADD("add"), SECURITY("security"), IMMUTABLE("immutable"), DELETE(
			"delete"), RENAME("rename"), IMPORT("import"), EXPORT("export"), SIGN("sign"), ARCHIVE("archive"), WORKFLOW(
			"workflow"), CALENDAR("calendar"), SUBSCRIPTION("subscription"), PRINT("print"), PASSWORD("password");

	private final String name;

	private int mask;

	private Permission(String name) {
		this.name = name;
		if ("read".equals(name))
			mask = Integer.parseInt("00000000000000001", 2);
		if ("write".equals(name))
			mask = Integer.parseInt("00000000000000010", 2);
		if ("add".equals(name))
			mask = Integer.parseInt("00000000000000100", 2);
		if ("security".equals(name))
			mask = Integer.parseInt("00000000000001000", 2);
		if ("immutable".equals(name))
			mask = Integer.parseInt("00000000000010000", 2);
		if ("delete".equals(name))
			mask = Integer.parseInt("00000000000100000", 2);
		if ("rename".equals(name))
			mask = Integer.parseInt("00000000001000000", 2);
		if ("import".equals(name))
			mask = Integer.parseInt("00000000010000000", 2);
		if ("export".equals(name))
			mask = Integer.parseInt("00000000100000000", 2);
		if ("sign".equals(name))
			mask = Integer.parseInt("00000001000000000", 2);
		if ("archive".equals(name))
			mask = Integer.parseInt("00000010000000000", 2);
		if ("workflow".equals(name))
			mask = Integer.parseInt("00000100000000000", 2);
		if ("download".equals(name))
			mask = Integer.parseInt("00001000000000000", 2);
		if ("calendar".equals(name))
			mask = Integer.parseInt("00010000000000000", 2);
		if ("subscription".equals(name))
			mask = Integer.parseInt("00100000000000000", 2);
		if ("print".equals(name))
			mask = Integer.parseInt("01000000000000000", 2);
		if ("password".equals(name))
			mask = Integer.parseInt("10000000000000000", 2);
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

	public static Permission valueOf(int mask) {
		for (Permission permission : all()) {
			if (permission.match(mask))
				return permission;
		}
		return null;
	}

	public static Set<Permission> all() {
		Set<Permission> permissions = new HashSet<Permission>();
		permissions.add(READ);
		permissions.add(WRITE);
		permissions.add(ADD);
		permissions.add(SECURITY);
		permissions.add(IMMUTABLE);
		permissions.add(DELETE);
		permissions.add(RENAME);
		permissions.add(EXPORT);
		permissions.add(IMPORT);
		permissions.add(SIGN);
		permissions.add(ARCHIVE);
		permissions.add(WORKFLOW);
		permissions.add(DOWNLOAD);
		permissions.add(CALENDAR);
		permissions.add(SUBSCRIPTION);
		permissions.add(PRINT);
		permissions.add(PASSWORD);
		return permissions;
	}

	@Override
	public String toString() {
		return name;
	}
}