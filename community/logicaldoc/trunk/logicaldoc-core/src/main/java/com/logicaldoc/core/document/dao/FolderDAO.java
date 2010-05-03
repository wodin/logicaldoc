package com.logicaldoc.core.document.dao;

import java.util.List;

import com.logicaldoc.core.document.History;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.dao.MenuDAO;

/**
 * Instances of this class is a DAO-service for menu objects.
 * 
 * @author Marco Meschieri - Logical Objects
 * @version 1.0
 */
public interface FolderDAO extends MenuDAO {

	/**
	 * Creates a new folder in the parent menu
	 * 
	 * @param parent The parent menu
	 * @param name The folder name
	 * @transaction optional transaction entry to log the event
	 * @return The newly created folder
	 */
	public Menu create(Menu parent, String name, History transaction);

	/**
	 * Creates the folder for the specified path. All unexisting nodes specified
	 * in the path will be created.
	 * 
	 * @param parent The parent menu
	 * @param path The folder path(for example /dog/cat/mouse)
	 * @transaction optional transaction entry to log the event
	 * 
	 * @return The created folder
	 */
	public Menu createPath(Menu parent, String path, History transaction);

	/**
	 * Dynamically computes the path extended for the specified menu. The path
	 * extended is a human readable path in the form: /folder1/folder2/folder3
	 * 
	 * @param id
	 * @return
	 */
	public String computePathExtended(long id);

	/**
	 * Retrieval of a folder by the parent extended path
	 * 
	 * @param name
	 * @param pathExtended
	 * @return
	 */
	public Menu find(String name, String pathExtended);

	/**
	 * Move a folder into another folder
	 * 
	 * @param source The folder to move
	 * @param target The target folder
	 * @param transaction entry to log the event (set the user)
	 * @throws Exception
	 */
	public void move(Menu source, Menu target, History transaction) throws Exception;

	/**
	 * Delete a folder and all its sub-folders that a user can delete. After
	 * recovering of all sub-folders inside the folder, will be canceled all
	 * folders for which the user has the delete permission or there isn't an
	 * immutable document inside it.
	 * 
	 * @param folder Folder to delete
	 * @param transaction entry to log the event (set the user)
	 * @return List of folders that the user cannot delete(permissions, o
	 *         immutable documents presents)
	 * @throws Exception
	 */
	public List<Menu> deleteTree(Menu folder, History transaction) throws Exception;

	/**
	 * Delete a folder and all its sub-folders that a user can delete. After
	 * recovering of all sub-folders inside the folder, will be canceled all
	 * folders for which the user has the delete permission or there isn't an
	 * immutable document inside it.
	 * 
	 * @param folderId Folder to delete
	 * @param transaction entry to log the event (set the user)
	 * @return List of folders that the user cannot delete(permissions, o
	 *         immutable documents presents)
	 * @throws Exception
	 */
	public List<Menu> deleteTree(long folderId, History transaction) throws Exception;

	public void setUniqueName(Menu folder);

	public List<Menu> find(String name);

	/**
	 * Checks if a folder with the given folderId is parent of the folder with
	 * the given targetId
	 * 
	 * @param folderId The folder to be checked
	 * @param targetId The target folder
	 * @return True if the folder with the given folderId is parent of the
	 *         folder with the given targetId
	 */
	public boolean isInPath(long folderId, long targetId);
}