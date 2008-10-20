package com.logicaldoc.core.security.dao;

import java.util.Collection;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.SecurityManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.util.io.CryptUtil;

/**
 * Test case for <code>HibernateUserDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
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
		User user = dao.findByUserName("author");
		assertEquals(1, user.getGroups().size());
		try {
			dao.delete(user.getId());
			fail("A referenced user was deleted ?!?");
		} catch (Throwable e) {
			// All ok, don't worry
		}
		user = dao.findByUserName("author");
		assertNotNull(user);

		// Try with a deletable user
		User testUser = dao.findByUserName("test");
		assertEquals(1, testUser.getGroups().size());
		manager.removeUserFromAllGroups(testUser);
		assertEquals(0, testUser.getGroups().size());
		assertTrue(dao.delete(testUser.getId()));
		user = dao.findByUserName("test");
		assertNull(user);

		Group group = groupDao.findByName("guest");
		assertFalse(group.getUsers().contains(testUser));
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

	public void testFindByUserName() {
		User user = dao.findByUserName("admin");
		assertNotNull(user);
		assertEquals("admin", user.getUserName());
		user.setDecodedPassword("admin");
		assertEquals(CryptUtil.cryptString("admin"), user.getPassword());
		assertEquals("admin@admin.net", user.getEmail());
		assertEquals(1, user.getGroups().size());

		// Try with unexisting username
		user = dao.findByUserName("xxxx");
		assertNull(user);
	}

	public void testFindByLikeUserName() {
		Collection<User> users = dao.findByLikeUserName("admin");
		assertNotNull(users);
		assertEquals(1, users.size());
		assertEquals("admin", users.iterator().next().getUserName());

		users = dao.findByLikeUserName("adm%");
		assertNotNull(users);
		assertEquals(1, users.size());
		assertEquals("admin", users.iterator().next().getUserName());

		users = dao.findByLikeUserName("xxx%");
		assertNotNull(users);
		assertTrue(users.isEmpty());
	}

	public void testFindById() {
		User user = dao.findById(1);
		assertNotNull(user);
		assertEquals("admin", user.getUserName());
		user.setDecodedPassword("admin");
		assertEquals(CryptUtil.cryptString("admin"), user.getPassword());
		assertEquals("admin@admin.net", user.getEmail());
		assertEquals(1, user.getGroups().size());

		// Try with unexisting id
		user = dao.findById(9999);
		assertNull(user);
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
		user.setName("claus");
		user.setFirstName("valca");
		user.setEmail("valca@acme.com");
		assertTrue(dao.store(user));
		manager.assignUserToGroups(user, new long[] { 1 });

		User storedUser = dao.findByUserName("xxx");
		assertNotNull(user);
		assertEquals(user, storedUser);
		assertEquals(1, storedUser.getGroups().size());
		assertEquals(CryptUtil.cryptString("xxxpwd"), storedUser.getDecodedPassword());

		user = dao.findById(1);
		user.setDecodedPassword("xxxpwd");
		dao.store(user);
		manager.assignUserToGroups(user, new long[] { 1, 2 });
		assertEquals(2, user.getGroups().size());
		user = dao.findById(1);
		assertNotNull(user);
		assertEquals(2, user.getGroups().size());
	}

	public void testValidateUser() {
		assertTrue(dao.validateUser("admin", "admin"));
		assertFalse(dao.validateUser("admin", "adminPWD"));
		assertFalse(dao.validateUser("xxxx", "admin"));
	}
}