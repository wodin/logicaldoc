package com.logicaldoc.core.security.dao;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.logicaldoc.core.security.Menu;

/**
 * Instances of this class is a DAO-service for menu objects.
 * 
 * @author Michael Scholz
 * @version 1.0
 */
public interface MenuDAO {

	/**
	 * This method persists the menu object.
	 * 
	 * @param menu Menu to be stored.
	 * @return True if successful stored in a database.
	 */
	public boolean store(Menu menu);

	/**
	 * This method deletes a menu in database.
	 * 
	 * @param menuId Menu to be deleted.
	 * @return True if successful deleted.
	 */
	public boolean delete(long menuId);

	/**
	 * Finds a menu by ID.
	 * 
	 * @param menuId ID of wanted menu.
	 * @return Wanted menu or null.
	 */
	public Menu findById(long menuId);

	/**
	 * Finds all menus by menu text.
	 * 
	 * @param text
	 * @return Collection of menus with given menu text.
	 */
	public Collection<Menu> findByText(String text);

	/**
	 * Finds all menus by menu text, contained in the parent menu and of the
	 * specified type
	 * 
	 * @param parent The parent menu(optional)
	 * @param text The menutext to search for
	 * @param type The menu type(optional)
	 * @return Collection of menus with given menu text.
	 */
	public Collection<Menu> findByText(Menu parent, String text, Integer type);

	/**
	 * Finds authorized menus for a user.
	 * 
	 * @param userId ID of the user.
	 * @return Collection of found menus.
	 */
	public Collection<Menu> findByUserId(long userId);

	/**
	 * Finds direct children of a menu.
	 * 
	 * @param parentId MenuId of the menu which children are wanted.
	 * @return Collection of found menus sorted by type, sort, text
	 */
	public List<Menu> findByUserId(long userId, long parentId);

	/**
	 * Finds direct children of a menu.
	 * 
	 * @param parentId MenuId of the menu which children are wanted.
	 * @param type The wanted menu type, can be null
	 * @return Collection of found menus sorted by type, sort, text
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
	public Collection<Menu> findByParentId(long parentId);

	/**
	 * Finds direct children of a menu.
	 * 
	 * @param parentId MenuId of the menu which children are wanted.
	 * @return Collection of found menus.
	 */
	public Collection<Menu> findChildren(long parentId);

	/**
	 * This method is looking up for writing rights for a menu and an user.
	 * 
	 * @param menuId ID of the menu.
	 * @param userId ID of the user.
	 */
	public boolean isWriteEnable(long menuId, long userId);

	public boolean isReadEnable(long menuId, long userId);

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
	 * @return Collection of selected menuId's.
	 */
	public Set<Long> findMenuIdByUserId(long userId);

	/**
	 * This method selects only the menuId from the menus for which a user is
	 * authorized. Only menus direct child of the specified parent are returned.
	 * 
	 * @param userId ID of the user.
	 * @param parentId Parent menu
	 * @param type The menu type, can be null
	 * @return Collection of selected menuId's.
	 */
	public Set<Long> findMenuIdByUserId(long userId, long parentId, Integer type);

	/**
	 * returns if a menu is writeable for a user
	 * 
	 * @param menuId check this menu
	 * @param userId privileges for this should be checked
	 * @return a 0 if false, a 1 if true
	 */
	public Integer isMenuWriteable(long menuId, long userId);

	/**
	 * checks that the user has access to the menu and all its sub-items
	 */
	public boolean hasWriteAccess(Menu menu, long userId);

	/**
	 * Finds all menus associated to the passed group
	 * 
	 * @param groupId The group id
	 * @return The collection of menus
	 */
	public Collection<Menu> findByGroupId(long groupId);

	/**
	 * Creates a new folder in the parent menu
	 * 
	 * @param parent The parent menu
	 * @param name The folder name
	 * @return The newly created folder
	 */
	public Menu createFolder(Menu parent, String name);

	/**
	 * Creates the folder for the specified path. All unexisting nodes specified
	 * in the path will be created.
	 * 
	 * @param parent The parent menu
	 * @param path The folder path(for example /dog/cat/mouse)
	 * 
	 * @return The created folder
	 */
	public Menu createFolders(Menu parent, String path);

	/**
	 * Returns a Collection of menus being a parent of the given menu. The list
	 * is ordered starting from the root of menus.
	 * 
	 * @param menuId
	 */
	public List<Menu> findParents(long menuId);
}