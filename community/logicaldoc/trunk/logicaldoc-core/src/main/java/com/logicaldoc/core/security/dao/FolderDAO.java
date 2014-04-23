package com.logicaldoc.core.security.dao;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.logicaldoc.core.PersistentObjectDAO;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.FolderHistory;
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
	 * @param name name of the folder
	 * @param tenantId Specification of the owning tenant (optional).
	 * 
	 * @return List of folders with given folder name.
	 */
	public List<Folder> findByName(String name, Long tenantId);

	/**
	 * Finds all folders by folder text, contained in the parent folder.
	 * 
	 * @param parent The parent folder(optional)
	 * @param name The folder name to search for
	 * @param tenantId Specification of the owning tenant (optional). If not
	 *        specified the tenant of parent is used instead
	 * @param caseSensitive
	 * @return List of folders with given name
	 */
	public List<Folder> findByName(Folder parent, String name, Long tenantId, boolean caseSensitive);

	/**
	 * Retrieves the root folder of the given tenant
	 */
	public Folder findRoot(long tenantId);

	/**
	 * Retrieves the Default workspace of the given tenant
	 */
	public Folder findDefaultWorkspace(long tenantId);

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
	 * @param parentId The id of the parent folder to inspect (optional)
	 * @param tree If true, the parentId will be interpreted as the root of a
	 *        tree
	 * @return List of selected folder ID's.
	 */
	public Collection<Long> findFolderIdByUserIdAndPermission(long userId, Permission permission, Long parentId,
			boolean tree);

	/**
	 * This method selects only the folder ID from the folders for which a user
	 * is authorized.
	 * 
	 * @param userId ID of the user.
	 * @param parentId The id of the parent folder to inspect (optional)
	 * @param tree If true, the parentId will be interpreted as the root of a
	 *        tree
	 * @return List of selected folder ID's.
	 */
	public Collection<Long> findFolderIdByUserId(long userId, Long parentId, boolean tree);

	/**
	 * Retrieve all the ids of the folder in a given tree
	 * 
	 * @param rootId Root of the folder
	 * @param includeDeleted True if the deleted records need to be loaded
	 * @return
	 */
	public Collection<Long> findFolderIdInTree(long rootId, boolean includeDeleted);

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
	 * 
	 * @return List of found folders
	 */
	public List<Folder> findChildren(long parentId, long userId);

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
	 * ordered starting from the root of folders. The list doesn't include the
	 * given folder.
	 * 
	 * @param id
	 */
	public List<Folder> findParents(long id);

	/**
	 * Restores a previously deleted folder
	 * 
	 * @param folderId The folder identifier
	 * @param parentId The parent folder to restore in
	 * @param transaction Current session informations
	 */
	public void restore(long folderId, long parentId, FolderHistory transaction);

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
	 * Same as store(Folder, boolean, FolderHistory)
	 */
	public boolean store(Folder folder, FolderHistory transaction);

	/**
	 * For each folder, save the folder delete history entry for each folder and
	 * delete the folder
	 * 
	 * @param folder List of folder to be delete
	 * @param transaction entry to log the event on each folder
	 */
	public void deleteAll(List<Folder> folders, FolderHistory transaction);

	/**
	 * This method deletes the folder object and insert a new folder history
	 * entry.
	 * 
	 * @param id The id of the folder to delete
	 * @param transaction entry to log the event
	 * @return True if successfully deleted from the database.
	 */
	public boolean delete(long id, FolderHistory transaction);

	/**
	 * Creates a new folder in the parent Folder
	 * 
	 * @param parent The parent folder
	 * @param folderVO The folder's metadata
	 * @param inheritSecurity If true the new folder will 'point' to the parent
	 *        for the security policies.
	 * @transaction optional transaction entry to log the event
	 * @return The newly created folder
	 */
	public Folder create(Folder parent, Folder folderVO, boolean inheritSecurity, FolderHistory transaction);

	/**
	 * Creates the folder for the specified path. All unexisting nodes specified
	 * in the path will be created.
	 * 
	 * @param parent The parent folder
	 * @param path The folder path(for example /dog/cat/mouse)
	 * @param inheritSecurity If true the new folders will 'point' to the parent
	 *        for the security policies.
	 * @transaction optional transaction entry to log the event
	 * 
	 * @return The created folder
	 */
	public Folder createPath(Folder parent, String path, boolean inheritSecurity, FolderHistory transaction);

	/**
	 * Dynamically computes the path extended for the specified folder. The path
	 * extended is a human readable path in the form: /folder1/folder2/folder3
	 * 
	 * @param id
	 * @return
	 */
	public String computePathExtended(long id);

	/**
	 * Retrieval of a folder by the extended path
	 * 
	 * @param pathExtended
	 * @return
	 */
	public Folder findByPath(String pathExtended);

	/**
	 * Move a folder into another folder
	 * 
	 * @param source The folder to move
	 * @param target The target folder
	 * @param transaction entry to log the event (set the user)
	 * @throws Exception
	 */
	public void move(Folder source, Folder target, FolderHistory transaction) throws Exception;

	/**
	 * Delete a folder and all its sub-folders that a user can delete. After
	 * recovering of all sub-folders inside the folder, will be canceled all
	 * folders for which the user has the delete permission or there isn't an
	 * immutable document inside it.
	 * <p>
	 * 
	 * <b>Important:</b> Remember to delete orphaned documents.
	 * 
	 * @param folder Folder to delete
	 * @param transaction entry to log the event (set the user)
	 * @return List of folders that the user cannot delete(permissions, o
	 *         immutable documents presents)
	 * @throws Exception
	 */
	public List<Folder> deleteTree(Folder folder, FolderHistory transaction) throws Exception;

	/**
	 * Delete a folder and all its sub-folders that a user can delete. After
	 * recovering of all sub-folders inside the folder, will be canceled all
	 * folders for which the user has the delete permission or there isn't an
	 * immutable document inside it.
	 * <p>
	 * 
	 * <b>Important:</b> All the contained documents will be deleted
	 * 
	 * @param folderId Folder to delete
	 * @param transaction entry to log the event (set the user)
	 * @return List of folders that the user cannot delete(permissions, o
	 *         immutable documents presents)
	 * @throws Exception
	 */
	public List<Folder> deleteTree(long folderId, FolderHistory transaction) throws Exception;

	public void setUniqueName(Folder folder);

	/**
	 * Useful method that allows to find all folders that contains the given
	 * name into their text.
	 * 
	 * @param name The name to be found
	 * @param tenantId The tenant to search in
	 * 
	 * @return List of folders that contains the given name into their text.
	 */
	public List<Folder> find(String name, Long tenantId);

	/**
	 * Finds all deleted folders of a specific user.
	 * 
	 * @param userId The user that performed the deletion
	 * @param maxHits Optional defines the max number of returned hits
	 * @return The folders list ordered by descending lastModified
	 */
	public List<Folder> findDeleted(long userId, Integer maxHits);

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
	public boolean applyRithtToTree(long rootId, FolderHistory transaction);

	/**
	 * Propagates the template metadata a node to the whole subree
	 */
	public boolean applyMetadataToTree(long id, FolderHistory transaction);

	/**
	 * Counts the number of folders
	 */
	public int count(boolean computeDeleted);

	/**
	 * Retrieves all the workspaces in the system, that are the first-level
	 * folders of type 1.
	 */
	public List<Folder> findWorkspaces(long tanantId);

	/**
	 * Initializes lazy loaded collections
	 * 
	 * @param folder The folder to be initialized
	 */
	public void initialize(Folder folder);

	/**
	 * Utility method that logs into the DB the transaction that involved the
	 * passed folder. The transaction must be provided with userId and userName.
	 */
	public void saveFolderHistory(Folder folder, FolderHistory transaction);
}