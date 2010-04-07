package com.logicaldoc.core.security.dao;

import java.util.List;
import java.util.Set;

import com.logicaldoc.core.PersistentObjectDAO;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.Permission;

/**
 * Instances of this class is a DAO-service for menu objects.
 * 
 * @author Michael Scholz
 * @version 1.0
 */
public interface MenuDAO extends PersistentObjectDAO<Menu> {

	/**
	 * Finds all menus by menu text.
	 * 
	 * @param text
	 * @return List of menus with given menu text.
	 */
	public List<Menu> findByText(String text);

	/**
	 * Finds all menus by menu text, contained in the parent menu and of the
	 * specified type
	 * 
	 * @param parent The parent menu(optional)
	 * @param text The menutext to search for
	 * @param type The menu type(optional)
	 * @return List of menus with given menu text.
	 */
	public List<Menu> findByText(Menu parent, String text, Integer type);

	/**
	 * Finds authorized menus for a user.
	 * 
	 * @param userId ID of the user.
	 * @return List of found menus.
	 */
	public List<Menu> findByUserId(long userId);

	/**
	 * Finds all menues with a specific permission enabled on the specifies user
	 * 
	 * @param userId The user identifier
	 * @param permission The permission to check
	 * @param type The menu type (optional)
	 * @return
	 */
	public Set<Long> findMenuIdByUserIdAndPermission(long userId, Permission permission, Integer type);

	/**
	 * Finds direct children of a menu.
	 * 
	 * @param parentId MenuId of the menu which children are wanted.
	 * @return List of found menus sorted by type, sort, text
	 */
	public List<Menu> findByUserId(long userId, long parentId);

	/**
	 * Finds direct children of a menu.
	 * 
	 * @param parentId MenuId of the menu which children are wanted.
	 * @param type The wanted menu type, can be null
	 * @return List of found menus sorted by type, sort, text
	 */
	public List<Menu> findByUserId(long userId, long parentId, Integer type);

	/**
	 * Counts direct children of a menu.
	 * 
	 * @param parentId MenuId of the menu which children are wanted.
	 * @param type The wanted menu type, can be null
	 * @return The total number of elements
	 */
	public long countByUserId(long userId, long parentId, Integer type);

	/**
	 * Finds all children(direct and indirect) by parentId
	 * 
	 * @param parentId
	 * @return
	 */
	public List<Menu> findByParentId(long parentId);

	/**
	 * Finds direct children of a menu.
	 * 
	 * @param parentId MenuId of the menu which children are wanted.
	 * @return List of found menus.
	 */
	public List<Menu> findChildren(long parentId);

	/**
	 * This method is looking up for writing rights for a menu and an user.
	 * 
	 * @param menuId ID of the menu.
	 * @param userId ID of the user.
	 */
	public boolean isWriteEnable(long menuId, long userId);

	public boolean isReadEnable(long menuId, long userId);

	/**
	 * This method checks if the given permission is enabled for a menu and an
	 * user.
	 * 
	 * @param permission the permission to check
	 * @param menuId ID of the menu
	 * @param userId ID of the user
	 */
	public boolean isPermissionEnabled(Permission permission, long menuId, long userId);

	/**
	 * Finds all permissions of a user enabled on the specified menu.
	 * 
	 * @param menuId ID of the menu
	 * @param userId ID of the user
	 * @return Collection of enabled permissions
	 */
	public Set<Permission> getEnabledPermissions(long menuId, long userId);

	/**
	 * This method selects only the menu text from a menu.
	 * 
	 * @param menuId Id of the menu.
	 * @return Selected menu text.
	 */
	public String findTextByMenuId(long menuId);

	/**
	 * This method selects only the menuId from the menus for which a user is
	 * authorized.
	 * 
	 * @param userId ID of the user.
	 * @return List of selected menuId's.
	 */
	public Set<Long> findMenuIdByUserId(long userId);

	/**
	 * This method selects only the menuId from the menus for which a user is
	 * authorized. Only menus direct child of the specified parent are returned.
	 * 
	 * @param userId ID of the user.
	 * @param parentId Parent menu
	 * @param type The menu type, can be null
	 * @return List of selected menuId's.
	 */
	public Set<Long> findMenuIdByUserId(long userId, long parentId, Integer type);

	/**
	 * returns if a menu is writeable for a user
	 * 
	 * @param menuId check this menu
	 * @param userId privileges for this should be checked
	 * @return a 0 if false, a 1 if true
	 */
	public int isMenuWriteable(long menuId, long userId);

	/**
	 * checks that the user has access to the menu and all its sub-items
	 */
	public boolean hasWriteAccess(Menu menu, long userId);

	/**
	 * Finds all menus associated to the passed group
	 * 
	 * @param groupId The group id
	 * @return The List of menus
	 */
	public List<Menu> findByGroupId(long groupId);

	/**
	 * Creates a new folder in the parent menu
	 * 
	 * @param parent The parent menu
	 * @param name The folder name
	 * @transaction optional transaction entry to log the event
	 * @return The newly created folder
	 */
	public Menu createFolder(Menu parent, String name, History transaction);

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
	public Menu createFolders(Menu parent, String path, History transaction);

	/**
	 * Returns a List of menus being a parent of the given menu. The list is
	 * ordered starting from the root of menus.
	 * 
	 * @param menuId
	 */
	public List<Menu> findParents(long menuId);

	/**
	 * Retrieval of all menus that exists under a specific pathextended
	 * 
	 * @param pathExtended
	 * @return
	 */
	public List<Menu> findFoldersByPathExtended(String pathExtended);

	/**
	 * Retrieval of a folder by the parent extended path
	 * 
	 * @param folderName
	 * @param pathExtended
	 * @return
	 */
	public Menu findFolder(String folderName, String pathExtended);

	/**
	 * Restores a previously deleted menu
	 * 
	 * @param menuId The menu identifier
	 * @param parents true if parents must be restored also
	 */
	public void restore(long menuId, boolean parents);

	/**
	 * Finds that folder that lies under a specific parent (given by the id) an
	 * with a given text(like operator is used)
	 * 
	 * @param text
	 * @param parentId
	 * @return
	 */
	public List<Menu> findByMenuTextAndParentId(String text, long parentId);

	/**
	 * This method persists the menu object and updates its path extended
	 * 
	 * @param menu menu to be stored.
	 * @param updatePathExtended true if you want to update menu path extended
	 * @param optional transaction entry to log the event
	 * @return True if successfully stored in a database.
	 */
	public boolean store(Menu menu, boolean updatePathExtended, History transaction);

	/**
	 * Same as store(Menu, boolean, History) with updatePathExtended=true
	 */
	public boolean store(Menu menu, History transaction);

	/**
	 * For each folder, save the folder delete history entry for each folder and
	 * delete the folder
	 * 
	 * @param menu List of menu to be delete
	 * @param transaction entry to log the event on each folder
	 */
	public void deleteAll(List<Menu> menus, History transaction);

	/**
	 * This method deletes the menu object and insert a new menu history entry.
	 * 
	 * @param menuId The id of the menu to delete
	 * @param transaction entry to log the event
	 * @return True if successfully deleted from the database.
	 */
	public boolean delete(long menuId, History transaction);

	public void setUniqueFolderName(Menu menu);
}