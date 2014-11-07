package com.logicaldoc.core.security.dao;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.logicaldoc.core.AbstractCoreTCase;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.SecurityManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserHistory;
import com.logicaldoc.util.crypt.CryptUtil;

/**
 * Test case for <code>HibernateUserDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class HibernateUserDAOTest extends AbstractCoreTCase {

	// Instance under test
	private UserDAO dao;

	private GroupDAO groupDao;

	private SecurityManager manager;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateUserDAO
		dao = (UserDAO) context.getBean("UserDAO");

		groupDao = (GroupDAO) context.getBean("GroupDAO");

		manager = (SecurityManager) context.getBean("SecurityManager");
	}

	@Test
	public void testDelete() {
		// User with history, not deletable
		User user = dao.findByUserName("author");
		Assert.assertEquals(2, user.getGroups().size());
		dao.delete(user.getId());
		user = dao.findByUserName("author");
		Assert.assertNull(user);

		// Try with a deletable user
		User testUser = dao.findByUserName("test");
		Assert.assertEquals(2, testUser.getGroups().size());
		manager.removeUserFromAllGroups(testUser);
		Assert.assertEquals(1, testUser.getGroups().size());
		String name = testUser.getUserGroupName();
		Assert.assertTrue(dao.delete(testUser.getId()));
		user = dao.findByUserName("test");
		Assert.assertNull(user);
		Assert.assertNull(groupDao.findByName(name, 1));

		Group group = groupDao.findByName("guest", 1);
		groupDao.initialize(group);
		Assert.assertFalse(group.getUsers().contains(testUser));
	}

	@Test
	public void testFindAll() {
		Collection<User> users = dao.findAll();
		Assert.assertNotNull(users);
		Assert.assertEquals(5, users.size());
	}

	@Test
	public void testFindByName() {
		Collection<User> users = dao.findByName("Seba%");
		Assert.assertNotNull(users);
		Assert.assertEquals(1, users.size());

		users = dao.findByName("%i%");
		Assert.assertNotNull(users);
		Assert.assertEquals(3, users.size());

		users = dao.findByName("%xxx%");
		Assert.assertNotNull(users);
		Assert.assertEquals(0, users.size());
	}

	@Test
	public void testFindByUserName() {
		User user = dao.findByUserName("admin");
		Assert.assertNotNull(user);
		Assert.assertEquals("admin", user.getUserName());
		user.setDecodedPassword("admin");
		Assert.assertEquals(CryptUtil.cryptString("admin"), user.getPassword());
		Assert.assertEquals("admin@admin.net", user.getEmail());
		Assert.assertEquals(2, user.getGroups().size());

		// Try with unexisting username
		user = dao.findByUserName("xxxx");
		Assert.assertNull(user);

		user = dao.findByUserName("Admin");
		Assert.assertNull(user);
	}

	@Test
	public void testFindByUserNameIgnoreCase() {
		User user = dao.findByUserNameIgnoreCase("admin");
		Assert.assertNotNull(user);
		Assert.assertEquals("admin", user.getUserName());
		
		// Try with unexisting username
		user = dao.findByUserNameIgnoreCase("xxxx");
		Assert.assertNull(user);

		// Try with different case
		user = dao.findByUserName("AdMiN");
		Assert.assertNull(user);
		user = dao.findByUserNameIgnoreCase("AdMiN");
		Assert.assertNotNull(user);
		Assert.assertEquals("admin", user.getUserName());
	}
	
	@Test
	public void testFindByLikeUserName() {
		Collection<User> users = dao.findByLikeUserName("admin");
		Assert.assertNotNull(users);
		Assert.assertEquals(1, users.size());
		Assert.assertEquals("admin", users.iterator().next().getUserName());

		users = dao.findByLikeUserName("adm%");
		Assert.assertNotNull(users);
		Assert.assertEquals(1, users.size());
		Assert.assertEquals("admin", users.iterator().next().getUserName());

		users = dao.findByLikeUserName("xxx%");
		Assert.assertNotNull(users);
		Assert.assertTrue(users.isEmpty());
	}

	@Test
	public void testFindById() {
		User user = dao.findById(1);
		Assert.assertNotNull(user);
		Assert.assertEquals("admin", user.getUserName());
		user.setDecodedPassword("admin");
		Assert.assertEquals(CryptUtil.cryptString("admin"), user.getPassword());
		Assert.assertEquals("admin@admin.net", user.getEmail());
		Assert.assertEquals(2, user.getGroups().size());

		// Try with unexisting id
		user = dao.findById(9999);
		Assert.assertNull(user);
	}

	@Test
	public void testFindByUserNameAndName() {
		Collection<User> users = dao.findByUserNameAndName("boss", "Meschieri");
		Assert.assertNotNull(users);
		Assert.assertEquals(1, users.size());
		Assert.assertEquals("boss", users.iterator().next().getUserName());

		users = dao.findByUserNameAndName("b%", "Mes%");
		Assert.assertNotNull(users);
		Assert.assertEquals(1, users.size());
		Assert.assertEquals("boss", users.iterator().next().getUserName());

		users = dao.findByUserNameAndName("a%", "xxxx%");
		Assert.assertNotNull(users);
		Assert.assertTrue(users.isEmpty());
	}

	@Test
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
		Assert.assertTrue(dao.store(user));
		Assert.assertTrue(groupDao.findByName(user.getUserGroupName(), 1) != null);
		manager.assignUserToGroups(user, new long[] { 1 });

		User storedUser = dao.findByUserName("xxx");
		Assert.assertNotNull(user);
		Assert.assertEquals(user, storedUser);
		Assert.assertEquals(2, storedUser.getGroups().size());
		Assert.assertNotNull(storedUser.getUserGroup());
		Assert.assertEquals(CryptUtil.cryptString("xxxpwd"), storedUser.getPassword());

		user = dao.findById(1);
		user.setDecodedPassword("xxxpwd");
		transaction = new UserHistory();
		transaction.setEvent(UserHistory.EVENT_USER_PASSWORDCHANGED);
		transaction.setUserId(user.getId());
		transaction.setNotified(0);
		dao.store(user, transaction);
		manager.assignUserToGroups(user, new long[] { 1, 2 });
		Assert.assertEquals(3, user.getGroups().size());
		user = dao.findById(1);
		Assert.assertNotNull(user);
		Assert.assertEquals(3, user.getGroups().size());
	}

	@Test
	public void testValidateUser() {
		Assert.assertTrue(dao.validateUser("admin", "admin"));
		Assert.assertFalse(dao.validateUser("admin", "adminPWD"));
		Assert.assertFalse(dao.validateUser("xxxx", "admin"));
		Assert.assertFalse(dao.validateUser("test", "admin"));
	}

	@Test
	public void testCount() {
		Assert.assertEquals(5, dao.count(null));
	}

	public void isPasswordExpired() {
		Assert.assertFalse(dao.isPasswordExpired("admin"));
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
		Assert.assertTrue(dao.isPasswordExpired("boss"));

		calendar.add(Calendar.DAY_OF_MONTH, +2);
		lastChange = calendar.getTime();
		user.setPasswordChanged(lastChange);
		dao.store(user);
		Assert.assertFalse(dao.isPasswordExpired("boss"));
	}
}