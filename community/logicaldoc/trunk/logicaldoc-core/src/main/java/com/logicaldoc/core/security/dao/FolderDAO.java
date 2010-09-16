package com.logicaldoc.core.security.dao;

import java.util.List;
import java.util.Set;

import com.logicaldoc.core.PersistentObjectDAO;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.Permission;

/**
 * Instances of this class is a DAO-service for folder objects.
 * 
 * @author Marco Meschieri - Logical Objects
 * @version 6.0
 */
public interface FolderDAO extends PersistentObjectDAO<Folder> {

	/**
	 * Finds all folders by folder name
	 * 
	 * @param name
	 * @return List of folders with given folder name.
	 */
	public List<Folder> findByName(String name);

	/**
	 * Finds all folders by folder text, contained in the parent folder and of
	 * the specified type
	 * 
	 * @param parent The parent folder(optional)
	 * @param name The folder name to search for
	 * @param caseSensitive
	 * @return List of folders with given name
	 */
	public List<Folder> findByName(Folder parent, String name, boolean caseSensitive);

	/**
	 * Finds authorized folders for a user
	 * 
	 * @param userId ID of the user
	 * @return List of found folders
	 */
	public List<Folder> findByUserId(long userId);

	/**
	 * Finds all folders ids with a specific permission enabled on the specifies
	 * user
	 * 
	 * @param userId The user identifier
	 * @param permission The permission to check
	 * @return
	 */
	public List<Long> findFolderIdByUserIdAndPermission(long userId, Permission permission);

	/**
	 * Finds direct children of a folder.
	 * 
	 * @param parentId ID of the folder which children are wanted
	 * @return List of found folders sorted by text
	 */
	public List<Folder> findByUserId(long userId, long parentId);

	/**
	 * Finds all children(direct and indirect) by parentId
	 * 
	 * @param parentId
	 * @return
	 */
	public List<Folder> findByParentId(long parentId);

	/**
	 * Finds direct children of a folder
	 * 
	 * @param parentId Folder ID of the folder which children are wanted
	 * @param max Optional, maximum number of children
	 * @return List of found folders
	 */
	public List<Folder> findChildren(long parentId, Integer max);

	/**
	 * Finds direct children of a folder accessible by the given user
	 * 
	 * @param parentId Folder ID of the folder which children are wanted
	 * @param userId Identifier of the user that must have read access
	 * @param max Optional, maximum number of children
	 * 
	 * @return List of found folders
	 */
	public List<Folder> findChildren(long parentId, long userId, Integer max);

	/**
	 * This method is looking up for writing rights for a folder and an user.
	 * 
	 * @param id ID of the folder
	 * @param userId ID of the user
	 */
	public boolean isWriteEnable(long id, long userId);

	public boolean isReadEnable(long id, long userId);

	/**
	 * This method checks if the given permission is enabled for a folder and an
	 * user.
	 * 
	 * @param permission the permission to check
	 * @param id ID of the folder
	 * @param userId ID of the user
	 */
	public boolean isPermissionEnabled(Permission permission, long id, long userId);

	/**
	 * Finds all permissions of a user enabled on the specified folder
	 * 
	 * @param id ID of the folder
	 * @param userId ID of the user
	 * @return Collection of enabled permissions
	 */
	public Set<Permission> getEnabledPermissions(long id, long userId);

	/**
	 * This method selects only the folder ID from the folders for which a user
	 * is authorized.
	 * 
	 * @param userId ID of the user.
	 * @return List of selected folder ID's.
	 */
	public List<Long> findFolderIdByUserId(long userId);

	/**
	 * This method selects only the folder ID from the folders for which a user
	 * is authorized. Only folders direct child of the specified parent are
	 * returned.
	 * 
	 * @param userId ID of the user
	 * @param parentId Parent folder
	 * @return List of selected folder ID's.
	 */
	public List<Long> findIdByUserId(long userId, long parentId);

	/**
	 * Checks that the user has access to the folder and all its sub-items
	 */
	public boolean hasWriteAccess(Folder folder, long userId);

	/**
	 * Finds all folders accessible by the passed group
	 * 
	 * @param groupId The group id
	 * @return The List of folders
	 */
	public List<Folder> findByGroupId(long groupId);

	/**
	 * Returns a List of folders being a parent of the given folder. The list is
	 * ordered starting from the root of folders
	 * 
	 * @param id
	 */
	public List<Folder> findParents(long id);

	/**
	 * Restores a previously deleted folder
	 * 
	 * @param id The folder identifier
	 * @param parents true if parents must be restored also
	 */
	public void restore(long id, boolean parents);

	/**
	 * Finds that folder that lies under a specific parent (given by the id) an
	 * with a given name(like operator is used)
	 * 
	 * @param text
	 * @param parentId
	 * @return
	 */
	public List<Folder> findByNameAndParentId(String text, long parentId);

	/**
	 * Same as store(Folder, boolean, History)
	 */
	public boolean store(Folder folder, History transaction);

	/**
	 * For each folder, save the folder delete history entry for each folder and
	 * delete the folder
	 * 
	 * @param folder List of folder to be delete
	 * @param transaction entry to log the event on each folder
	 */
	public void deleteAll(List<Folder> folders, History transaction);

	/**
	 * This method deletes the folder object and insert a new folder history
	 * entry.
	 * 
	 * @param id The id of the folder to delete
	 * @param transaction entry to log the event
	 * @return True if successfully deleted from the database.
	 */
	public boolean delete(long id, History transaction);

	/**
	 * Creates a new folder in the parent Folder
	 * 
	 * @param parent The parent folder
	 * @param name The folder name
	 * @transaction optional transaction entry to log the event
	 * @return The newly created folder
	 */
	public Folder create(Folder parent, String name, History transaction);

	/**
	 * Creates the folder for the specified path. All unexisting nodes specified
	 * in the path will be created.
	 * 
	 * @param parent The parent folder
	 * @param path The folder path(for example /dog/cat/mouse)
	 * @transaction optional transaction entry to log the event
	 * 
	 * @return The created folder
	 */
	public Folder createPath(Folder parent, String path, History transaction);

	/**
	 * Dynamically computes the path extended for the specified folder. The path
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
	public Folder find(String name, String pathExtended);

	/**
	 * Move a folder into another folder
	 * 
	 * @param source The folder to move
	 * @param target The target folder
	 * @param transaction entry to log the event (set the user)
	 * @throws Exception
	 */
	public void move(Folder source, Folder target, History transaction) throws Exception;

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
	public List<Folder> deleteTree(Folder folder, History transaction) throws Exception;

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
	public List<Folder> deleteTree(long folderId, History transaction) throws Exception;

	public void setUniqueName(Folder folder);

	/**
	 * Useful method that allows to find all folders that contains the given
	 * name into their text.
	 * 
	 * @param name The name to be found
	 * @return List of folders that contains the given name into their text.
	 */
	public List<Folder> find(String name);

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
	
	/**
	 * Propagates the security policies of a node to the whole subree 
	 */
	public boolean applyRithtToTree(long id, History transaction);
	
	/**
	 * Counts the number of folders
	 */
	public int count(boolean computeDeleted);
}