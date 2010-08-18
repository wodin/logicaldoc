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
	 * @param caseSensitive
	 * @return List of menus with given menu text.
	 */
	public List<Menu> findByText(Menu parent, String text, Integer type, boolean caseSensitive);

	/**
	 * Finds authorized menus for a user.
	 * 
	 * @param userId ID of the user.
	 * @return List of found menus.
	 */
	public List<Menu> findByUserId(long userId);
	
	/**
	 * Finds authorized menus for a user.
	 * 
	 * @param userId ID of the user.
	 * @param type The menu type
	 * @return List of found menus.
	 */
	public List<Menu> findByUserId(long userId, Integer type);

	/**
	 * Finds all menues ids with a specific permission enabled on the specifies user
	 * 
	 * @param userId The user identifier
	 * @param permission The permission to check
	 * @param type The menu type (optional)
	 * @return
	 */
	public List<Long> findMenuIdByUserIdAndPermission(long userId, Permission permission, Integer type);

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
	 * Finds all children(direct and indirect) by parentId
	 * 
	 * @param parentId
	 * @return
	 */
	public List<Menu> findByParentId(long parentId);

	/**
	 * Finds direct children of a menu.
	 * 
	 * @param parentId MenuId of the menu which children are wanted
	 * @param max Optional, maximum number of children
	 * @return List of found menus
	 */
	public List<Menu> findChildren(long parentId, Integer max);

	/**
	 * Finds direct children of a menu accessible by the given user.
	 * 
	 * @param parentId MenuId of the menu which children are wanted
	 * @param userId Identifier of the user that mush have read access
	 * @param max Optional, maximum number of children
	 * 
	 * @return List of found menus.
	 */

	public List<Menu> findChildren(long parentId, long userId, Integer max);

	/**
	 * This method is looking up for writing rights for a menu and an user.
	 * 
	 * @param id ID of the menu.
	 * @param userId ID of the user.
	 */
	public boolean isWriteEnable(long id, long userId);

	public boolean isReadEnable(long id, long userId);

	/**
	 * This method checks if the given permission is enabled for a menu and an
	 * user.
	 * 
	 * @param permission the permission to check
	 * @param id ID of the menu
	 * @param userId ID of the user
	 */
	public boolean isPermissionEnabled(Permission permission, long id, long userId);

	/**
	 * Finds all permissions of a user enabled on the specified menu.
	 * 
	 * @param id ID of the menu
	 * @param userId ID of the user
	 * @return Collection of enabled permissions
	 */
	public Set<Permission> getEnabledPermissions(long id, long userId);

	/**
	 * This method selects only the menu text from a menu.
	 * 
	 * @param id Id of the menu.
	 * @return Selected menu text.
	 */
	public String findTextById(long id);

	/**
	 * This method selects only the menuId from the menus for which a user is
	 * authorized.
	 * 
	 * @param userId ID of the user.
	 * @return List of selected menuId's.
	 */
	public List<Long> findMenuIdByUserId(long userId);

	/**
	 * This method selects only the menuId from the menus for which a user is
	 * authorized. Only menus direct child of the specified parent are returned.
	 * 
	 * @param userId ID of the user.
	 * @param parentId Parent menu
	 * @param type The menu type, can be null
	 * @return List of selected menuId's.
	 */
	public List<Long> findIdByUserId(long userId, long parentId, Integer type);

	/**
	 * returns if a menu is writeable for a user
	 * 
	 * @param id check this menu
	 * @param userId privileges for this should be checked
	 * @return a 0 if false, a 1 if true
	 */
	public int isMenuWriteable(long id, long userId);

	/**
	 * checks that the user has access to the menu and all its sub-items
	 */
	public boolean hasWriteAccess(Menu menu, long userId);

	/**
	 * Finds all menus accessible by the passed group
	 * 
	 * @param groupId The group id
	 * @return The List of menus
	 */
	public List<Menu> findByGroupId(long groupId);

	/**
	 * Returns a List of menus being a parent of the given menu. The list is
	 * ordered starting from the root of menus.
	 * 
	 * @param id
	 */
	public List<Menu> findParents(long id);

	/**
	 * Restores a previously deleted menu
	 * 
	 * @param id The menu identifier
	 * @param parents true if parents must be restored also
	 */
	public void restore(long id, boolean parents);

	/**
	 * Finds that folder that lies under a specific parent (given by the id) an
	 * with a given text(like operator is used)
	 * 
	 * @param text
	 * @param parentId
	 * @return
	 */
	public List<Menu> findByTextAndParentId(String text, long parentId);

	/**
	 * Same as store(Menu, boolean, History)
	 */
	public boolean store(Menu menu, History transaction);

	/**
	 * For each menu, save the folder delete history entry for each folder and
	 * delete the folder
	 * 
	 * @param menu List of menu to be delete
	 * @param transaction entry to log the event on each folder
	 */
	public void deleteAll(List<Menu> menus, History transaction);

	/**
	 * This method deletes the menu object and insert a new menu history entry.
	 * 
	 * @param id The id of the menu to delete
	 * @param transaction entry to log the event
	 * @return True if successfully deleted from the database.
	 */
	public boolean delete(long id, History transaction);

	/**
	 * Dynamically computes the path extended for the specified menu. The path
	 * extended is a human readable path in the form: /folder1/folder2/folder3
	 * 
	 * @param id
	 * @return
	 */
	public String computePathExtended(long id);
	
	/**
	 * Propagates the security policies of a node to the whole subree 
	 */
	public boolean applyRithtToTree(long id, History transaction);
}