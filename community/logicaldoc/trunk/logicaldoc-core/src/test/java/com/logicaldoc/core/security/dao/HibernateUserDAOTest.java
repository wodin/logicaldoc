package com.logicaldoc.core.security.dao;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.SecurityManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserHistory;
import com.logicaldoc.util.io.CryptUtil;

/**
 * Test case for <code>HibernateUserDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
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
		assertEquals(2, user.getGroups().size());
		dao.delete(user.getId());
		user = dao.findByUserName("author");
		assertNull(user);

		// Try with a deletable user
		User testUser = dao.findByUserName("test");
		assertEquals(2, testUser.getGroups().size());
		manager.removeUserFromAllGroups(testUser);
		assertEquals(1, testUser.getGroups().size());
		String name = testUser.getUserGroupName();
		assertTrue(dao.delete(testUser.getId()));
		user = dao.findByUserName("test");
		assertNull(user);
		assertNull(groupDao.findByName(name));

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
		assertEquals(2, user.getGroups().size());

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
		assertEquals(2, user.getGroups().size());

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
		UserHistory transaction = new UserHistory();
		transaction.setEvent(UserHistory.EVENT_USER_LOGIN);
		transaction.setUserId(user.getId());
		transaction.setNotified(0);
		dao.store(user, transaction);
		assertTrue(dao.store(user));
		assertTrue(groupDao.findByName(user.getUserGroupName()) != null);
		manager.assignUserToGroups(user, new long[] { 1 });

		User storedUser = dao.findByUserName("xxx");
		assertNotNull(user);
		assertEquals(user, storedUser);
		assertEquals(2, storedUser.getGroups().size());
		assertNotNull(storedUser.getUserGroup());
		assertEquals(CryptUtil.cryptString("xxxpwd"), storedUser.getPassword());

		user = dao.findById(1);
		user.setDecodedPassword("xxxpwd");
		transaction = new UserHistory();
		transaction.setEvent(UserHistory.EVENT_USER_PASSWORDCHANGED);
		transaction.setUserId(user.getId());
		transaction.setNotified(0);
		dao.store(user, transaction);
		manager.assignUserToGroups(user, new long[] { 1, 2 });
		assertEquals(3, user.getGroups().size());
		user = dao.findById(1);
		assertNotNull(user);
		assertEquals(3, user.getGroups().size());
	}

	public void testValidateUser() {
		assertTrue(dao.validateUser("admin", "admin"));
		assertFalse(dao.validateUser("admin", "adminPWD"));
		assertFalse(dao.validateUser("xxxx", "admin"));
		assertFalse(dao.validateUser("test", "admin"));
	}

	public void testCount() {
		assertEquals(5, dao.count());
	}

	public void isPasswordExpired() {
		assertFalse(dao.isPasswordExpired("admin"));
		User user = dao.findByUserName("boss");
		Date lastChange = null;
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(lastChange);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR, 0);
		calendar.add(Calendar.DAY_OF_MONTH, -91);
		lastChange = calendar.getTime();
		user.setPasswordChanged(lastChange);
		dao.store(user);
		assertTrue(dao.isPasswordExpired("boss"));

		calendar.add(Calendar.DAY_OF_MONTH, +2);
		lastChange = calendar.getTime();
		user.setPasswordChanged(lastChange);
		dao.store(user);
		assertFalse(dao.isPasswordExpired("boss"));
	}
}