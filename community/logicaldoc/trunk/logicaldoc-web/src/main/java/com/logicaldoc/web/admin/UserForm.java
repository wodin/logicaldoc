package com.logicaldoc.web.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.communication.EMail;
import com.logicaldoc.core.communication.EMailSender;
import com.logicaldoc.core.communication.Recipient;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.SecurityManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserHistory;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.PropertiesBean;
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

	private boolean passwordExpires = true;

	private UIInput firstNameControl;

	private UIInput passwordExpiresControl;

	private UIInput nameControl;

	private UIInput streetControl;

	private UIInput postalCodeControl;

	private UIInput cityControl;

	private UIInput countryControl;

	private UIInput languageControl;

	private UIInput emailControl;

	private UIInput stateControl;

	private UIInput phoneControl;

	private UIInput phone2Control;

	public UIInput getFirstNameControl() {
		return firstNameControl;
	}

	public UIInput getNameControl() {
		return nameControl;
	}

	public void setNameControl(UIInput nameControl) {
		this.nameControl = nameControl;
	}

	public UIInput getStreetControl() {
		return streetControl;
	}

	public void setStreetControl(UIInput streetControl) {
		this.streetControl = streetControl;
	}

	public UIInput getPostalCodeControl() {
		return postalCodeControl;
	}

	public void setPostalCodeControl(UIInput postalCodeControl) {
		this.postalCodeControl = postalCodeControl;
	}

	public UIInput getCityControl() {
		return cityControl;
	}

	public void setCityControl(UIInput cityControl) {
		this.cityControl = cityControl;
	}

	public UIInput getCountryControl() {
		return countryControl;
	}

	public void setCountryControl(UIInput countryControl) {
		this.countryControl = countryControl;
	}

	public UIInput getLanguageControl() {
		return languageControl;
	}

	public void setLanguageControl(UIInput languageControl) {
		this.languageControl = languageControl;
	}

	public UIInput getEmailControl() {
		return emailControl;
	}

	public void setEmailControl(UIInput emailControl) {
		this.emailControl = emailControl;
	}

	public UIInput getPhoneControl() {
		return phoneControl;
	}

	public void setPhoneControl(UIInput phoneControl) {
		this.phoneControl = phoneControl;
	}

	public void setCreateNew(boolean createNew) {
		this.createNew = createNew;
	}

	public void setFirstNameControl(UIInput firstNameControl) {
		this.firstNameControl = firstNameControl;
	}

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

	public boolean isCreateNew() {
		return createNew;
	}

	public UIInput getStateControl() {
		return stateControl;
	}

	public void setStateControl(UIInput stateControl) {
		this.stateControl = stateControl;
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
		passwordExpires = usr.getPasswordExpires() == 1;

		FacesUtil.forceRefresh(firstNameControl);
		FacesUtil.forceRefresh(passwordExpiresControl);
		FacesUtil.forceRefresh(nameControl);
		FacesUtil.forceRefresh(cityControl);
		FacesUtil.forceRefresh(countryControl);
		FacesUtil.forceRefresh(emailControl);
		FacesUtil.forceRefresh(languageControl);
		FacesUtil.forceRefresh(phoneControl);
		FacesUtil.forceRefresh(phone2Control);
		FacesUtil.forceRefresh(postalCodeControl);
		FacesUtil.forceRefresh(streetControl);
		FacesUtil.forceRefresh(stateControl);

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

	public PropertiesBean getConfig() {
		return (PropertiesBean) Context.getInstance().getBean("ContextProperties");
	}

	private String save(boolean withPassword) {
		String decodedPassword=getPassword();
		if (SessionManagement.isValid()) {
			try {
				UserDAO dao = (UserDAO) Context.getInstance().getBean(UserDAO.class);

				User tempuser = dao.findByUserName(user.getUserName());

				if ((tempuser != null) && !tempuser.equals(user)) {
					Messages.addLocalizedError("errors.action.userexists");

					return null;
				}

				user.setEmail(user.getEmail().toLowerCase());
				user.setPasswordExpires(passwordExpires ? 1 : 0);
				UserHistory history = null;

				if (withPassword) {
					if (!getPassword().equals(getRepass())) {
						Messages.addLocalizedError("msg.jsp.adduser.repass");
						return null;
					}

					if (StringUtils.isNotEmpty(getPassword()) && !user.getPassword().equals(getPassword())) {
						// The password was changed
						user.setDecodedPassword(getPassword());
						user.setPasswordChanged(new Date());

						history = new UserHistory();
						history.setUser(user);
						history.setEvent(UserHistory.EVENT_USER_PASSWORDCHANGED);
						history.setComment("");
						history.setSessionId(SessionManagement.getCurrentUserSessionId());

						// Notify the user by email
						notifyAccount(user, decodedPassword);
					}

					user.setRepass("");
				}

				SecurityManager manager = (SecurityManager) Context.getInstance().getBean(SecurityManager.class);
				manager.removeUserFromAllGroups(user);

				if (createNew) {
					// Generate an initial password
					decodedPassword = new PasswordGenerator().generate(getConfig().getInt("password.size"));
					user.setDecodedPassword(decodedPassword);
					dao.store(user);
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

				boolean stored = dao.store(user, history);

				if (!stored) {
					Messages.addLocalizedError("errors.action.saveuser.notstored");
				} else {
					Messages.addLocalizedInfo("msg.action.changeuser");

					// Notify the user by email
					if (createNew)
						notifyAccount(user, decodedPassword);
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

		email = new EMail();
		email.setAccountId(-1);
		email.setAuthor(user.getUserName());
		email.setAuthorAddress(SessionManagement.getUser().getEmail());

		Recipient recipient = new Recipient();
		recipient.setAddress(user.getEmail());
		email.addRecipient(recipient);
		email.setFolder("outbox");
		Locale locale = new Locale(user.getLanguage());
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
				.getRequest();

		String address = request.getScheme() + "://";
		address += (request.getServerName() + ":");
		address += request.getServerPort();
		address += request.getContextPath();
		String text = Messages.getMessage("email.notify.account", locale, new Object[] {
				user.getFirstName() + " " + user.getName(), user.getUserName(), password, address });
		email.setMessageText(text);
		email.setRead(1);
		email.setSentDate(new Date());
		email.setSubject(Messages.getMessage("email.notify.account.object", locale));
		email.setUserName(user.getUserName());

		try {
			sender.send(email);
			Messages.addLocalizedInfo("email.notify.account.sent");
		} catch (Exception ex) {
			log.warn(ex.getMessage(), ex);
			Messages.addWarn(Messages.getMessage("email.notify.account.error", user.getEmail()));
		}
	}

	public UIInput getPhone2Control() {
		return phone2Control;
	}

	public void setPhone2Control(UIInput phone2Control) {
		this.phone2Control = phone2Control;
	}

	public boolean isPasswordExpires() {
		return passwordExpires;
	}

	public void setPasswordExpires(boolean passwordExpires) {
		this.passwordExpires = passwordExpires;
	}

	public UIInput getPasswordExpiresControl() {
		return passwordExpiresControl;
	}

	public void setPasswordExpiresControl(UIInput passwordExpiresControl) {
		this.passwordExpiresControl = passwordExpiresControl;
	}
}