package com.logicaldoc.web.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.communication.EMail;
import com.logicaldoc.core.communication.EMailSender;
import com.logicaldoc.core.communication.Recipient;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.SecurityManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.security.PasswordGenerator;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.util.FacesUtil;

/**
 * Form for user editing
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class UserForm {
	protected static Log log = LogFactory.getLog(UserForm.class);

	private long[] group;

	private User user;

	private boolean createNew = false;

	private String password;

	private String repass;

	private long[] selectedAvailableGroups = new long[0];

	private long[] selectedAllowedGroups = new long[0];

	private String availableGroupFilter = "";

	private String allowedGroupFilter = "";

	private List<Group> groups = new ArrayList<Group>();

	private Collection<SelectItem> availableGroups = new ArrayList<SelectItem>();

	private Collection<SelectItem> allowedGroups = new ArrayList<SelectItem>();

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public long[] getSelectedAvailableGroups() {
		return selectedAvailableGroups;
	}

	public void setSelectedAvailableGroups(long[] selectedAvailableGroups) {
		this.selectedAvailableGroups = selectedAvailableGroups;
	}

	public long[] getSelectedAllowedGroups() {
		return selectedAllowedGroups;
	}

	public void setSelectedAllowedGroups(long[] selectedAllowedGroups) {
		this.selectedAllowedGroups = selectedAllowedGroups;
	}

	public String getAvailableGroupFilter() {
		return availableGroupFilter;
	}

	public void setAvailableGroupFilter(String availableGroupFilter) {
		this.availableGroupFilter = availableGroupFilter;
	}

	public String getAllowedGroupFilter() {
		return allowedGroupFilter;
	}

	public void setAllowedGroupFilter(String allowedGroupFilter) {
		this.allowedGroupFilter = allowedGroupFilter;
	}

	public Collection<SelectItem> getAvailableGroups() {
		return availableGroups;
	}

	public void setAvailableGroups(Collection<SelectItem> availableGroups) {
		this.availableGroups = availableGroups;
	}

	public Collection<SelectItem> getAllowedGroups() {
		return allowedGroups;
	}

	public void setAllowedGroups(Collection<SelectItem> allowedGroups) {
		this.allowedGroups = allowedGroups;
	}

	public User getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRepass() {
		return repass;
	}

	public void setRepass(String repass) {
		this.repass = repass;
	}

	public void setUser(User usr) {
		this.user = usr;
		createNew = StringUtils.isEmpty(this.user.getUserName());
		group = this.user.getGroupIds();

		availableGroups.clear();
		allowedGroups.clear();
		groups.clear();
		selectedAvailableGroups = new long[0];
		selectedAllowedGroups = new long[0];
		availableGroupFilter = "";
		allowedGroupFilter = "";

		GroupDAO gdao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
		groups = (List<Group>) gdao.findAll();
		Collections.sort(groups, new Comparator<Group>() {
			public int compare(Group arg0, Group arg1) {
				int sort = new Integer(arg0.getType()).compareTo(new Integer(arg1.getType()));
				if (sort == 0)
					sort = arg0.getName().toLowerCase().compareTo(arg1.getName().toLowerCase());
				return sort;
			}
		});

		for (int i = 0; i < group.length; i++) {
			Group grp = gdao.findById(group[i]);
			if (grp.getType() == Group.TYPE_DEFAULT)
				allowedGroups.add(new SelectItem(grp.getId(), grp.getName()));
		}

		for (Group group : groups) {
			if (group.getType() == Group.TYPE_DEFAULT) {
				boolean allowed = false;
				for (SelectItem item : allowedGroups) {
					if (((Long) item.getValue()).equals(group.getId())) {
						allowed = true;
						break;
					}
				}
				if (!allowed)
					availableGroups.add(new SelectItem(group.getId(), group.getName()));
			}
		}
	}

	public String save() {
		return save(false);
	}

	public String savePassword() {
		return save(true);
	}

	private String save(boolean withPassword) {
		if (SessionManagement.isValid()) {
			try {
				UserDAO dao = (UserDAO) Context.getInstance().getBean(UserDAO.class);

				User tempuser = dao.findByUserName(user.getUserName());

				if ((tempuser != null) && !tempuser.equals(user)) {
					Messages.addLocalizedError("errors.action.userexists");

					return null;
				}

				if (withPassword) {
					if (!getPassword().equals(getRepass())) {
						Messages.addLocalizedError("msg.jsp.adduser.repass");

						return null;
					}

					if (StringUtils.isNotEmpty(getPassword()) && !user.getPassword().equals(getPassword())) {
						// The password was changed
						user.setDecodedPassword(getPassword());
					}

					user.setRepass("");
				}

				SecurityManager manager = (SecurityManager) Context.getInstance().getBean(SecurityManager.class);
				manager.removeUserFromAllGroups(user);

				if (createNew) {
					// Generate an initial password
					String password = new PasswordGenerator().generate(8);
					user.setDecodedPassword(password);
					dao.store(user);

					// TODO Notify the user by email
					notifyAccount(user, password);
				}

				GroupDAO gdao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
				Group adminGroup = gdao.findByName("admin");
				ArrayList<Long> tmp = new ArrayList<Long>();
				for (SelectItem item : allowedGroups) {
					tmp.add(((Long) item.getValue()));
				}

				// The admin user must always member of admin group
				if ("admin".equals(user.getUserName()) && !tmp.contains(adminGroup.getId())) {
					tmp.add(adminGroup.getId());
				}

				long[] ids = new long[tmp.size()];
				for (int i = 0; i < tmp.size(); i++) {
					ids[i] = tmp.get(i).longValue();
				}

				manager.assignUserToGroups(user, ids);

				boolean stored = dao.store(user);

				if (!stored) {
					Messages.addLocalizedError("errors.action.saveuser.notstored");
				} else {
					Messages.addLocalizedInfo("msg.action.changeuser");
				}

				UsersRecordsManager recordsManager = ((UsersRecordsManager) FacesUtil.accessBeanFromFacesContext(
						"usersRecordsManager", FacesContext.getCurrentInstance(), log));
				recordsManager.reload();
				recordsManager.setSelectedPanel("list");
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				Messages.addLocalizedError("errors.action.saveuser.notstored");
			}

			return null;
		} else {
			return "login";
		}
	}

	public int getGroupCount() {
		return group.length;
	}

	public long[] getGroup() {
		return group;
	}

	public void setGroup(long[] group) {
		this.group = group;
	}

	/**
	 * Filters the available groups if group's name contains the string on
	 * "Filter" input text
	 * 
	 * @param event
	 */
	public void filterAvailableGroups(ValueChangeEvent event) {
		availableGroups.clear();
		for (Group group : groups) {
			if ((group.getType() == Group.TYPE_DEFAULT && group.getName().toLowerCase().contains(
					event.getNewValue().toString().toLowerCase()))) {
				// Check if the group is allowed
				boolean allowed = false;
				for (SelectItem item : allowedGroups) {
					if (((Long) item.getValue()).equals(group.getId())) {
						allowed = true;
						break;
					}
				}
				if (!allowed)
					availableGroups.add(new SelectItem(group.getId(), group.getName()));
			}
		}
	}

	/**
	 * Filters the allowed groups if group's name contains the string on
	 * "Filter" input text
	 * 
	 * @param event
	 */
	public void filterAllowedGroups(ValueChangeEvent event) {
		allowedGroups.clear();
		for (Group group : groups) {
			if ((group.getType() == Group.TYPE_DEFAULT && group.getName().toLowerCase().contains(
					event.getNewValue().toString().toLowerCase()))) {
				// Check if the group is available
				boolean available = false;
				for (SelectItem item : availableGroups) {
					if (((Long) item.getValue()).equals(group.getId())) {
						available = true;
						break;
					}
				}
				if (!available)
					allowedGroups.add(new SelectItem(group.getId(), group.getName()));
			}
		}
	}

	/**
	 * Moves the selected available groups to the allowed groups list associated
	 * to a menu
	 * 
	 */
	public void assignGroups() {
		GroupDAO gdao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
		// Add all selected groups to the assigned list
		for (long grp : selectedAvailableGroups) {
			SelectItem item = new SelectItem(grp, gdao.findById(grp).getName());
			allowedGroups.add(item);
		}
		availableGroups.clear();
		for (Group group : groups) {
			if ((group.getType() == Group.TYPE_DEFAULT && group.getName().toLowerCase().contains(
					availableGroupFilter.toString().toLowerCase()))) {
				// Check if the group is allowed
				boolean allowed = false;
				for (SelectItem item : allowedGroups) {
					if (((Long) item.getValue()).equals(group.getId())) {
						allowed = true;
						break;
					}
				}
				if (!allowed)
					availableGroups.add(new SelectItem(group.getId(), group.getName()));
			}
		}
	}

	/**
	 * Moves the selected allowed groups to the available groups list associated
	 * to a menu
	 * 
	 */
	public void unassignGroups() {
		GroupDAO gdao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
		// Add all selected groups to the assigned list
		for (long grp : selectedAllowedGroups) {
			SelectItem item = new SelectItem(grp, gdao.findById(grp).getName());
			availableGroups.add(item);
		}
		allowedGroups.clear();
		for (Group group : groups) {
			if ((group.getType() == Group.TYPE_DEFAULT && group.getName().toLowerCase().contains(
					allowedGroupFilter.toString().toLowerCase()))) {
				// Check if the group is allowed
				boolean available = false;
				for (SelectItem item : availableGroups) {
					if (((Long) item.getValue()).equals(group.getId())) {
						available = true;
						break;
					}
				}
				if (!available)
					allowedGroups.add(new SelectItem(group.getId(), group.getName()));
			}
		}
	}

	/**
	 * Notify the user with it's new account
	 * 
	 * @param user The created user
	 * @param password The decoded password
	 */
	private void notifyAccount(User user, String password) {
		EMail email;
		EMailSender sender = (EMailSender) Context.getInstance().getBean(EMailSender.class);
		try {
			email = new EMail();
			email.setAccountId(-1);
			email.setAuthor(user.getUserName());
			email.setAuthorAddress(sender.getSender());
			
			System.out.println("***"+user.getEmail());
			
			Recipient recipient = new Recipient();
			recipient.setAddress(user.getEmail());
			email.addRecipient(recipient);
			email.setFolder("outbox");
			email.setMessageText("Sei stato registrato su LogicalDOC con la password:" + password);
			email.setRead(1);
			email.setSentDate(String.valueOf(new Date().getTime()));
			email.setSubject("LogicalDOC - Registrazione account");
			email.setUserName(user.getUserName());

			try {
				sender.send(email);
				Messages.addLocalizedInfo("Password inviata per email all'indirizzo "+user.getEmail());
			} catch (Exception ex) {
				log.error(ex.getMessage(), ex);
				Messages.addLocalizedInfo("Impossibile inviare l'email all'indirizzo "+user.getEmail());
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			Messages.addLocalizedError("email.error");
		}
	}
}
