package com.logicaldoc.web.admin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.SecurityManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserDoc;
import com.logicaldoc.core.security.UserHistory;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.core.security.dao.UserDocDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.util.sql.SqlUtil;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.components.SortableList;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.util.FacesUtil;

/**
 * <p>
 * The <code>UsersRecordsManager</code> class is responsible for constructing
 * the list of <code>User</code> beans which will be bound to a ice:dataTable
 * JSF component.
 * <p/>
 * <p>
 * Large data sets could be handle by adding a ice:dataPaginator. Alternatively
 * the dataTable could also be hidden and the dataTable could be added to
 * scrollable ice:panelGroup.
 * </p>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class UsersRecordsManager extends SortableList {

	protected static Log log = LogFactory.getLog(UsersRecordsManager.class);

	private List<User> users = new ArrayList<User>();

	private String selectedPanel = "list";

	private String usersFilter = "";

	public String getUsersFilter() {
		return usersFilter;
	}

	public void setUsersFilter(String usersFilter) {
		this.usersFilter = usersFilter;
	}

	public UsersRecordsManager() {
		// We don't sort by default
		super("xxx");
	}

	public void reload() {
		users.clear();

		try {
			MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
			long userId = SessionManagement.getUserId();

			if (mdao.isReadEnable(6, userId)) {
				UserDAO dao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
				Collection<User> tmpusers = null;
				if (usersFilter.length() != 0) {
					String buf = SqlUtil.doubleQuotes(usersFilter.trim().toLowerCase());
					tmpusers = dao.findByWhere(" lower(_entity.userName) like '%" + buf
							+ "%' or lower(_entity.name) like '%" + buf + "%' or lower(_entity.firstName) like '%"
							+ buf + "%'", null, null);
				} else
					tmpusers = dao.findAll();

				for (User usr : tmpusers) {
					if (usr.getType() == User.TYPE_DEFAULT)
						users.add(usr);
				}

				for (User user : users) {
					user.getGroupIds();
				}
			} else {
				Messages.addLocalizedError("errors.noaccess");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			Messages.addLocalizedError("errors.error");
		}
	}

	public String getSelectedPanel() {
		return selectedPanel;
	}

	public void setSelectedPanel(String panel) {
		this.selectedPanel = panel;
	}

	public String addUser() {
		selectedPanel = "add";

		UserForm userForm = ((UserForm) FacesUtil.accessBeanFromFacesContext("userForm", FacesContext
				.getCurrentInstance(), log));

		User user = new User();
		user.setLanguage(SessionManagement.getLanguage());
		userForm.setUser(user);

		return null;
	}

	public String edit() {
		selectedPanel = "edit";

		UserForm userForm = ((UserForm) FacesUtil.accessBeanFromFacesContext("userForm", FacesContext
				.getCurrentInstance(), log));
		User user = (User) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("user");
		userForm.setUser(user);
		return null;
	}

	public String password() {
		selectedPanel = "passwd";

		UserForm userForm = ((UserForm) FacesUtil.accessBeanFromFacesContext("userForm", FacesContext
				.getCurrentInstance(), log));
		User user = (User) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("user");
		userForm.setUser(user);

		return null;
	}

	public String list() {
		selectedPanel = "list";
		reload();

		return null;
	}

	/**
	 * Gets the list of User which will be used by the ice:dataTable component.
	 */
	public Collection<User> getUsers() {
		if (users.size() == 0) {
			reload();
		}

		return users;
	}

	public int getCount() {
		return getUsers().size();
	}

	public String enable() {
		User user = (User) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("user");
		user.setEnabled(1);
		UserDAO dao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		dao.store(user);
		return "";
	}

	public String disable() {
		User user = (User) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("user");
		user.setEnabled(0);
		UserDAO dao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		dao.store(user);
		return "";
	}

	public String delete() {
		User user = (User) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("user");

		if (SessionManagement.isValid()) {
			try {
				UserDAO dao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
				GroupDAO gdao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
				SecurityManager manager = (SecurityManager) Context.getInstance().getBean(SecurityManager.class);

				Group adminGroup = gdao.findByName("admin");

				// get the user's groups and check if he is member of
				// "admin" group
				User toBeDeletedUser = dao.findByUserName(user.getUserName());
				boolean isAdmin = false;

				if (toBeDeletedUser != null) {
					toBeDeletedUser.getGroupIds();

					long[] userGroups = toBeDeletedUser.getGroupIds();

					if (userGroups != null) {
						for (int i = 0; i < userGroups.length; i++) {
							if (userGroups[i] == adminGroup.getId()) {
								isAdmin = true;
								break;
							}
						}
					}
				}

				// if the user is member of "admin", we have to check that
				// he is not the last user in that group;
				// here we count how many users still belong to group admin
				int adminsFound = 0;

				if (isAdmin) {
					Collection<User> allUsers = dao.findAll();
					Iterator<User> userIter = allUsers.iterator(); // get all
					// users
					while (userIter.hasNext()) {
						User currUser = userIter.next();
						long[] groups = currUser.getGroupIds();

						if (groups != null) {
							for (int i = 0; i < groups.length; i++) {
								if (groups[i] == adminGroup.getId()) {
									adminsFound++;

									break; // for performance reasons we
									// break if we found enough
									// users
								}
							}
						}

						// basically we are just interested that there are
						// at least 2 users,
						// so we can safely delete one
						if (adminsFound > 2) {
							break;
						}
					}
				}

				// now we can try to delete the user
				if (!isAdmin || (isAdmin && (adminsFound > 1))) {
					// delete user doc entries (recently accessed files)
					UserDocDAO userDocDao = (UserDocDAO) Context.getInstance().getBean(UserDocDAO.class);
					Collection<UserDoc> userDocColl = userDocDao.findByUserId(user.getId());
					Iterator<UserDoc> userDocIter = userDocColl.iterator();

					while (userDocIter.hasNext()) {
						UserDoc userDoc = userDocIter.next();
						userDocDao.delete(userDoc.getDocId(), user.getId());
					}

					manager.removeUserFromAllGroups(toBeDeletedUser);

					// Create the user history event
					UserHistory transaction = new UserHistory();
					transaction.setSessionId(SessionManagement.getCurrentUserSessionId());
					transaction.setEvent(UserHistory.EVENT_USER_DELETED);
					transaction.setComment("");
					transaction.setUser(SessionManagement.getUser());

					boolean deleted = dao.delete(user.getId(), transaction);

					if (!deleted) {
						Messages.addLocalizedError("errors.action.deleteuser");
					} else {
						Messages.addLocalizedInfo("msg.action.deleteuser");

						PropertiesBean conf = (PropertiesBean) Context.getInstance().getBean("ContextProperties");
						File userdir = new File(conf.getPropertyWithSubstitutions("conf.userdir") + "/" + user.getUserName());
						FileUtils.deleteDirectory(userdir);
					}
				} else if (isAdmin && (adminsFound < 2)) {
					Messages.addLocalizedInfo("msg.action.deleteuser.admingroup");
				}

			} catch (Exception e) {
				log.error(e.getMessage(), e);
				Messages.addLocalizedError("errors.action.deleteuser");
			}
		} else {
			return "login";
		}

		setSelectedPanel("list");
		reload();

		return null;
	}

	@Override
	protected boolean isDefaultAscending(String sortColumn) {
		return true;
	}

	/**
	 * Sorts the list of DocumentRecord data.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void sort(final String column, final boolean ascending) {

		Comparator comparator = new Comparator() {
			public int compare(Object o1, Object o2) {

				User c1 = (User) o1;
				User c2 = (User) o2;
				if (column == null) {
					return 0;
				}
				if (column.equals("name")) {
					return ascending ? c1.getFirstName().compareTo(c2.getFirstName()) : c2.getFirstName().compareTo(
							c1.getFirstName());
				} else if (column.equals("userName")) {
					return ascending ? c1.getUserName().compareTo(c2.getUserName()) : c2.getUserName().compareTo(
							c1.getUserName());
				} else
					return 0;
			}
		};

		Collections.sort(users, comparator);
	}

	/**
	 * Filters all users if group's name contains the string on "Username" input
	 * text
	 * 
	 * @param event
	 */
	public void filterUsers(ValueChangeEvent event) {
		usersFilter = event.getNewValue().toString();
		reload();
	}
}