package com.logicaldoc.core.security.dao;

import java.util.Collection;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.CryptBean;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.SecurityManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.UserDAO;

/**
 * Test case for <code>HibernateUserDAO</code>
 * 
 * @author Marco Meschieri
 * @version $Id:$
 * @since 3.0
 */
public class HibernateUserDAOTest extends AbstractCoreTestCase {

	// Instance under test
	private UserDAO dao;

	private GroupDAO groupDao;

	private SecurityManager manager;

	public HibernateUserDAOTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateUserDAO
		dao = (UserDAO) context.getBean("UserDAO");

		groupDao = (GroupDAO) context.getBean("GroupDAO");

		manager = (SecurityManager) context.getBean("SecurityManager");
	}

	public void testDelete() {
		// User with history, not deletable
		User user = dao.findByPrimaryKey("author");
		assertEquals(1, user.getGroups().size());
		try {
			dao.delete("author");
			fail("A referenced user was deleted ?!?");
		} catch (Throwable e) {
			// All ok, don't worry
		}
		user = dao.findByPrimaryKey("author");
		assertNotNull(user);

		// Try with a deletable user
		User testUser = dao.findByPrimaryKey("test");
		assertEquals(1, testUser.getGroups().size());
		manager.removeUserFromAllGroups(testUser);
		assertEquals(0, testUser.getGroups().size());
		assertTrue(dao.delete("test"));
		user = dao.findByPrimaryKey("test");
		assertNull(user);

		Group group = groupDao.findByPrimaryKey("guest");
		assertFalse(group.getUsers().contains(testUser));
	}

	public void testExistsUser() {
		assertTrue(dao.existsUser("sebastian"));
		assertFalse(dao.existsUser("xxxx"));
	}

	public void testFindAll() {
		Collection<User> users = dao.findAll();
		assertNotNull(users);
		assertEquals(5, users.size());
	}

	public void testFindByName() {
		Collection<User> users = dao.findByName("Seba%");
		assertNotNull(users);
		assertEquals(1, users.size());

		users = dao.findByName("%i%");
		assertNotNull(users);
		assertEquals(3, users.size());

		users = dao.findByName("%xxx%");
		assertNotNull(users);
		assertEquals(0, users.size());
	}

	public void testFindByPrimaryKey() {
		User user = dao.findByPrimaryKey("admin");
		assertNotNull(user);
		assertEquals("admin", user.getUserName());
		user.setDecodedPassword("admin");
		assertEquals(CryptBean.cryptString("admin"), user.getPassword());
		assertEquals("admin@admin.net", user.getEmail());
		assertEquals(1, user.getGroups().size());

		// Try with unexisting username
		user = dao.findByPrimaryKey("xxxx");
		assertNull(user);
	}

	public void testFindByUserName() {
		Collection<User> users = dao.findByUserName("admin");
		assertNotNull(users);
		assertEquals(1, users.size());
		assertEquals("admin", users.iterator().next().getUserName());

		users = dao.findByUserName("adm%");
		assertNotNull(users);
		assertEquals(1, users.size());
		assertEquals("admin", users.iterator().next().getUserName());

		users = dao.findByUserName("xxx%");
		assertNotNull(users);
		assertTrue(users.isEmpty());
	}

	public void testFindByUserNameAndName() {
		Collection<User> users = dao.findByUserNameAndName("boss", "Meschieri");
		assertNotNull(users);
		assertEquals(1, users.size());
		assertEquals("boss", users.iterator().next().getUserName());

		users = dao.findByUserNameAndName("b%", "Mes%");
		assertNotNull(users);
		assertEquals(1, users.size());
		assertEquals("boss", users.iterator().next().getUserName());

		users = dao.findByUserNameAndName("a%", "xxxx%");
		assertNotNull(users);
		assertTrue(users.isEmpty());
	}

	public void testStore() {
		User user = new User();
		user.setUserName("xxx");
		user.setDecodedPassword("xxxpwd");
		user.setName("calus");
		user.setFirstName("valca");
		user.setEmail("valca@acme.com");
		assertTrue(dao.store(user));
		manager.assignUserToGroups(user, new String[] { "admin" });

		User storedUser = dao.findByPrimaryKey("xxx");
		assertNotNull(user);
		assertEquals(user, storedUser);
		assertEquals(1, storedUser.getGroups().size());
		assertEquals(CryptBean.cryptString("xxxpwd"), storedUser.getDecodedPassword());

		user = new User();
		user.setUserName("admin");
		user.setDecodedPassword("xxxpwd");
		dao.store(user);
		manager.assignUserToGroups(user, new String[] { "admin", "author" });
		assertEquals(2, user.getGroups().size());
		user = dao.findByPrimaryKey("admin");
		assertNotNull(user);
		assertEquals(2, user.getGroups().size());
	}

	public void testValidateUser() {
		assertTrue(dao.validateUser("admin", "admin"));
		assertFalse(dao.validateUser("admin", "adminPWD"));
		assertFalse(dao.validateUser("xxxx", "admin"));
	}
}