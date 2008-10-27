package com.logicaldoc.testbench;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Generates database records browsing an existing filesystem in LogicalDOC's
 * format.
 * <p>
 * <b>NOTE:</b> The filesystem must be compliant with the one used by
 * LogicalDOC to store document archive files, so folders must be named with
 * internal menu id.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class GenerateRecords {
	private File root;

	private Connection con;

	private PreparedStatement insertMenu;

	private PreparedStatement insertMenuGroup;

	private PreparedStatement insertDoc;

	private long lastFolderId = 10000;

	private long[] groupIds = new long[] { 1 };

	/**
	 * @param con The database connection to use
	 * @param root The root directory to be inspected searching for folders and
	 *        files
	 * @throws SQLException
	 */
	public GenerateRecords(Connection con, File root) throws SQLException {
		super();
		this.root = root;
		this.con = con;
		insertMenu = con
				.prepareStatement("INSERT INTO LD_MENU (LD_ID,LD_LASTMODIFIED,LD_TEXT,LD_PARENTID,LD_SORT,LD_ICON,LD_PATH,LD_PATHEXTENDED,LD_TYPE,LD_REF,LD_SIZE) VALUES (?,?,?,?,?,?,?,?,?,?,?);");
		insertMenuGroup = con
				.prepareStatement("INSERT INTO LD_MENUGROUP (LD_MENUID,LD_GROUPID,LD_WRITEENABLE) VALUES (?,?,?);");
		insertDoc = con
				.prepareStatement("INSERT INTO LD_DOCUMENT (LD_ID,LD_LASTMODIFIED,LD_TITLE,LD_VERSION,LD_DATE,LD_PUBLISHER,LD_STATUS,LD_TYPE,LD_CHECKOUTUSER,LD_SOURCE,LD_SOURCEAUTHOR,LD_SOURCEDATE,LD_SOURCETYPE,LD_COVERAGE,LD_LANGUAGE,LD_FILENAME,LD_FILESIZE,LD_FOLDERID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
	}

	public void generate() throws SQLException {
		addDocuments(root, "/");
		
		
		try{
		 con.createStatement().execute("SHUTDOWN COMPACT");
		 con.commit();
		}catch(Throwable e){
			
		}
	}

	
	/**
	 * Adds all documents inside the specified folder
	 * 
	 * @param folder The folder to browse
	 * @param path Path for LD_PATH and LS_PATHEXT fields
	 * @throws SQLException
	 */
	private void addDocuments(File folder, String path) {
		long parentFolderId = Long.parseLong(folder.getName());

		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory() && !files[i].getName().startsWith("doc_")) {
				try {
					insertFolder(files[i], path + "/" + parentFolderId);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}else if (files[i].isDirectory() && files[i].getName().startsWith("doc_")) {
				
			}
		}
	}

	private void insertDocument(File dir){
		
	}
	
	/**
	 * Inserts a folder into the database.
	 * 
	 * @param dir The filesystem counterpart of the folder (it's name is used as
	 *        LD_ID)
	 * @param path Path to be used as LD_PATH and LD_PATHEXT
	 * @throws SQLException
	 */
	private void insertFolder(File dir, String path) throws SQLException {
		long id = Long.parseLong(dir.getName());
		long parentId = Long.parseLong(dir.getParentFile().getName());

		// LD_ID
		insertMenu.setLong(1, id);
		// LD_LASTMODIFIED
		insertMenu.setDate(2, new Date(new java.util.Date().getTime()));
		// LD_TEXT
		insertMenu.setString(3, dir.getName());
		// LD_PARENTID
		insertMenu.setLong(4, parentId);
		// LD_SORT
		insertMenu.setInt(5, 0);
		// LD_ICON
		insertMenu.setString(6, "administration.png");
		// LD_PATH
		insertMenu.setString(7, path);
		// LD_PATHEXTENDED
		insertMenu.setString(8, path);
		// LD_TYPE
		insertMenu.setInt(9, 3);
		// LD_REF
		insertMenu.setString(10, "");
		// LD_SIZE
		insertMenu.setLong(11, 0);
		insertMenu.execute();

		for (int j = 0; j < groupIds.length; j++) {
			// LD_MENUID
			insertMenuGroup.setLong(1, lastFolderId);
			// LD_GROUPID
			insertMenuGroup.setLong(2, groupIds[j]);
			// LD_WRITEENABLE
			insertMenuGroup.setInt(3, 1);
			insertMenuGroup.addBatch();
		}
		insertMenuGroup.executeBatch();
		con.commit();
		insertMenuGroup.clearBatch();
	}
}