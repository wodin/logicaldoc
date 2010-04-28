package com.logicaldoc.webservice.auth;

import junit.framework.Assert;

import org.junit.Test;

import com.logicaldoc.core.document.dao.FolderDAO;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.MenuGroup;
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.webservice.AbstractWebServiceTestCase;

/**
 * Test case for <code>AuthServiceImpl</code>
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class AuthServiceImplTest extends AbstractWebServiceTestCase {
	private UserDAO userDao;

	private FolderDAO folderDao;

	private GroupDAO groupDao;

	// Instance under test
	private AuthServiceImpl authServiceImpl;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		userDao = (UserDAO) context.getBean("UserDAO");
		folderDao = (FolderDAO) context.getBean("FolderDAO");
		groupDao = (GroupDAO) context.getBean("GroupDAO");

		// Make sure that this is a AuthServiceImpl instance
		authServiceImpl = new AuthServiceImpl();
		authServiceImpl.setValidateSession(false);
	}

	@Test
	public void testGetUsers() throws Exception {
		long[] usersIds = new long[0];
		usersIds = authServiceImpl.getUsers("");
		Assert.assertEquals(5, usersIds.length);

		User user = userDao.findById(2);
		Assert.assertNotNull(user);
		userDao.initialize(user);
		user.setDeleted(1);
		userDao.store(user);

		usersIds = authServiceImpl.getUsers("");
		Assert.assertEquals(4, usersIds.length);
	}

	@Test
	public void testGetGroups() throws Exception {
		long[] groupIds = new long[0];
		groupIds = authServiceImpl.getGroups("");
		Assert.assertEquals(9, groupIds.length);

		Group group = groupDao.findById(10);
		Assert.assertNotNull(group);
		groupDao.initialize(group);
		group.setDeleted(1);
		groupDao.store(group);

		groupIds = authServiceImpl.getGroups("");
		Assert.assertEquals(8, groupIds.length);
	}

	@Test
	public void testGrantUser() throws Exception {
		User user = userDao.findById(3);

		Assert.assertTrue(folderDao.isPermissionEnabled(Permission.ADD_CHILD, 100, user.getId()));
		Assert.assertFalse(folderDao.isPermissionEnabled(Permission.MANAGE_IMMUTABILITY, 100, user.getId()));

		authServiceImpl.grantUser("", 100, user.getId(), 4091, false);

		Assert.assertTrue(folderDao.isPermissionEnabled(Permission.MANAGE_IMMUTABILITY, 100, user.getId()));
		Assert.assertFalse(folderDao.isPermissionEnabled(Permission.ADD_CHILD, 100, user.getId()));
	}

	@Test
	public void testGrantGroup() throws Exception {
		Group group = groupDao.findById(3);
		Assert.assertNotNull(group);
		Menu menu = folderDao.findById(99);
		Assert.assertNotNull(menu);
		Menu menu2 = folderDao.findById(100);
		Assert.assertNotNull(menu2);
		MenuGroup mg = menu.getMenuGroup(3);
		Assert.assertNull(mg);
		MenuGroup mg2 = menu2.getMenuGroup(3);
		Assert.assertNotNull(mg2);

		authServiceImpl.grantGroup("", 99, 3, 4095, false);

		menu = folderDao.findById(99);
		mg = menu.getMenuGroup(3);
		Assert.assertNotNull(mg);
	}

	@Test
	public void testGetGrantedUsers() {
		try {
			Right[] rights = new Right[0];
			rights = authServiceImpl.getGrantedUsers("", 100);
			Assert.assertEquals(1, rights.length);
			Assert.assertEquals(3, rights[0].getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetGrantedGroups() throws Exception {
		Right[] rights = new Right[0];
		rights = authServiceImpl.getGrantedGroups("", 100);
		Assert.assertEquals(2, rights.length);
	}
}
