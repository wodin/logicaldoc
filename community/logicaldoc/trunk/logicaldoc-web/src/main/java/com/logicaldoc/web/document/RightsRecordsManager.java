package com.logicaldoc.web.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.History;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.MenuGroup;
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.i18n.Messages;

/**
 * Control that allows the user to select access rights
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class RightsRecordsManager {
	protected static Log log = LogFactory.getLog(RightsRecordsManager.class);

	private List<GroupRule> rules = new ArrayList<GroupRule>();

	private List<Group> groups = new ArrayList<Group>();

	private Directory selectedDirectory;

	private boolean recursive = false;

	private DocumentNavigation documentNavigation;

	private Collection<SelectItem> availableGroups = new ArrayList<SelectItem>();

	private Collection<SelectItem> allowedGroups = new ArrayList<SelectItem>();

	private long[] selectedAvailableGroups = new long[0];

	private long[] selectedAllowedGroups = new long[0];

	private String availableGroupFilter = "";

	private String allowedGroupFilter = "";

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

	public boolean isRecursive() {
		return recursive;
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

	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}

	public Collection<SelectItem> getAvailableGroups() {
		return availableGroups;
	}

	public Collection<SelectItem> getAllowedGroups() {
		return allowedGroups;
	}

	public void setAvailableGroups(Collection<SelectItem> availableGroups) {
		this.availableGroups = availableGroups;
	}

	public void setAllowedGroups(Collection<SelectItem> allowedGroups) {
		this.allowedGroups = allowedGroups;
	}

	/**
	 * Changes the currently selected directory and updates the rules list.
	 * 
	 * @param dir
	 */
	public void selectDirectory(Directory dir) {
		recursive = false;
		selectedDirectory = dir;
		initRights(dir.getMenuId());
	}

	public void cleanSelection() {
		selectedDirectory = null;
		clean();
	}

	/**
	 * Initializes the collection of rights. Divides available and allowed
	 * entities into two column.
	 * 
	 * @param menuId The menu that must be evaluated
	 */
	private void initRights(long menuId) {
		clean();

		// initiate the list
		rules = new ArrayList<GroupRule>(10);

		try {
			GroupDAO gdao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
			groups = (List<Group>) gdao.findAll();
			Collections.sort(groups, new Comparator<Group>() {
				public int compare(Group arg0, Group arg1) {
					int sort = new Integer(arg0.getType()).compareTo(new Integer(arg1.getType()));
					if (sort == 0) {
						getEntityLabel(arg0).compareTo(getEntityLabel(arg1));
					}
					return sort;
				}
			});
			MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
			Menu menu = mdao.findById(menuId);
			mdao.initialize(menu);
			long userId = SessionManagement.getUserId();
			if (mdao.isPermissionEnabled(Permission.MANAGE_SECURITY, menuId, userId)) {
				Iterator<Group> iter = groups.iterator();
				while (iter.hasNext()) {
					Group g = (Group) iter.next();
					GroupRule gr = new GroupRule();
					if (g.getType() == Group.TYPE_DEFAULT
							|| ((g.getType() != Group.TYPE_DEFAULT) && (g.getUsers().isEmpty() || g.getUsers()
									.iterator().next().getType() == User.TYPE_DEFAULT))) {
						gr.setGroupName(g.getName());
						gr.setDisplayName(getEntityLabel(g));
						gr.setGroupId(g.getId());
						gr.setEnabled(true);

						MenuGroup mg = menu.getMenuGroup(g.getId());

						if ((mg == null) || mg.getGroupId() != g.getId()) {
							gr.setRead(false);
							gr.setWrite(false);
							gr.setAddChild(false);
							gr.setManageSecurity(false);
							gr.setManageImmutability(false);
							gr.setDelete(false);
							gr.setRename(false);
							gr.setBulkImport(false);
							gr.setBulkExport(false);
							availableGroups.add(new SelectItem(g.getId(), getEntityLabel(g)));
						} else {
							gr.setRead(true);
							gr.init(mg);
							allowedGroups.add(new SelectItem(g.getId(), getEntityLabel(g)));
							rules.add(gr);
						}

					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			Messages.addMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
		}
	}

	private void clean() {
		availableGroups.clear();
		allowedGroups.clear();
		groups.clear();
		selectedAvailableGroups = new long[0];
		selectedAllowedGroups = new long[0];
		availableGroupFilter = "";
		allowedGroupFilter = "";
		rules.clear();
	}

	/**
	 * Retrieves the label of a entity. There are two types: 'Group' and 'User'
	 * 
	 * @param g Group for which the label is recovered
	 * @return label
	 */
	private String getEntityLabel(Group g) {
		String label = "";
		if (g.getType() == Group.TYPE_DEFAULT) {
			label = Messages.getMessage("group") + ": " + g.getName();
		} else {
			Set<User> users = g.getUsers();
			if (users.isEmpty())
				label = Messages.getMessage("user") + ": " + g.getName();
			else {
				User user = users.iterator().next();
				label = Messages.getMessage("user") + ": " + user.getFullName() + " (" + user.getUserName() + ")";
			}
		}
		return label;
	}

	/**
	 * Moves the selected available groups to the allowed groups list associated
	 * to a menu
	 * 
	 */
	public void assignGroups() {
		if (selectedDirectory == null)
			return;
		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		Menu menu = mdao.findById(selectedDirectory.getMenu().getId());
		for (long grp : selectedAvailableGroups) {
			if (menu.getMenuGroup(grp) != null)
				continue;
			MenuGroup mg = new MenuGroup(grp);
			mg.setWrite(0);
			menu.getMenuGroups().add(mg);
		}

		mdao.store(menu);
		initRights(menu.getId());
	}

	/**
	 * Moves the selected allowed groups to the available groups list associated
	 * to a menu
	 * 
	 */
	public void unassignGroups() {
		if (selectedDirectory == null)
			return;

		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		Menu menu = mdao.findById(selectedDirectory.getMenu().getId());

		for (long grp : selectedAllowedGroups) {
			// Skip the admin group
			if (grp == 1)
				continue;
			MenuGroup mg = new MenuGroup(grp);
			menu.getMenuGroups().remove(mg);
		}

		mdao.store(menu);
		initRights(menu.getId());
	}

	/**
	 * Cleans up the resources used by this class. This method could be called
	 * when a session destroyed event is called.
	 */
	public void dispose() {
		rules.clear();
	}

	/**
	 * Gets the list of rules which will be used by the ice:dataTable component.
	 * 
	 * @return array list of rights
	 */
	public List<GroupRule> getRules() {
		return rules;
	}

	public String back() {
		documentNavigation.showDocuments();
		return null;
	}

	public String save() {
		if (selectedDirectory == null)
			return null;
		long id = selectedDirectory.getMenuId();
		long userId = SessionManagement.getUserId();
		try {
			saveRules(id, userId);
			Messages.addMessage(FacesMessage.SEVERITY_INFO, Messages.getMessage("msg.action.saverules"), Messages
					.getMessage("msg.action.saverules"));
		} catch (Exception e) {
			Messages.addMessage(FacesMessage.SEVERITY_ERROR, Messages.getMessage("errors.action.saverules"), Messages
					.getMessage("errors.action.saverules"));
		}
		documentNavigation.showDocuments();
		return null;
	}

	/**
	 * Saves the selected rights into the current element
	 * 
	 * @param id
	 * @param userId
	 * @throws Exception
	 */
	private void saveRules(long id, long userId) throws Exception {
		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);

		// Rules can be applied only if the user can manage the security
		if (!mdao.isPermissionEnabled(Permission.MANAGE_SECURITY, id, userId))
			return;

		Menu folder = mdao.findById(id);
		boolean sqlerrors = false;
		for (GroupRule rule : rules) {
			boolean read = rule.getRead();
			boolean isAdmin = rule.getGroupName().equals("admin");
			MenuGroup mg = folder.getMenuGroup(rule.getGroupId());
			if (read || isAdmin) {
				if ((mg == null) || mg.getGroupId() != rule.getGroupId()) {
					mg = new MenuGroup();
					mg.setGroupId(rule.getGroupId());
					folder.getMenuGroups().add(mg);
				}

				if (rule.isWrite() || isAdmin) {
					mg.setWrite(1);
				} else {
					mg.setWrite(0);
				}

				if (rule.isAddChild() || isAdmin) {
					mg.setAddChild(1);
				} else {
					mg.setAddChild(0);
				}

				if (rule.isManageSecurity() || isAdmin) {
					mg.setManageSecurity(1);
				} else {
					mg.setManageSecurity(0);
				}

				if (rule.isManageImmutability() || isAdmin) {
					mg.setManageImmutability(1);
				} else {
					mg.setManageImmutability(0);
				}

				if (rule.isDelete() || isAdmin) {
					mg.setDelete(1);
				} else {
					mg.setDelete(0);
				}

				if (rule.isRename() || isAdmin) {
					mg.setRename(1);
				} else {
					mg.setRename(0);
				}

				if (rule.isBulkImport() || isAdmin) {
					mg.setBulkImport(1);
				} else {
					mg.setBulkImport(0);
				}

				if (rule.isBulkExport() || isAdmin) {
					mg.setBulkExport(1);
				} else {
					mg.setBulkExport(0);
				}

				if (rule.isSign() || isAdmin) {
					mg.setSign(1);
				} else {
					mg.setSign(0);
				}

				if (rule.isArchive() || isAdmin) {
					mg.setArchive(1);
				} else {
					mg.setArchive(0);
				}

				if (rule.isWorkflow() || isAdmin) {
					mg.setWorkflow(1);
				} else {
					mg.setWorkflow(0);
				}

				boolean stored = mdao.store(folder);
				if (!stored) {
					sqlerrors = true;
				}
			} else {
				if (mg != null) {
					folder.getMenuGroups().remove(mg);

					boolean deleted = mdao.store(folder);

					if (!deleted) {
						sqlerrors = true;
					}
				}
			}
		}

		if (sqlerrors) {
			log.error("SQL errors saving permissions on folder " + folder.getText());
			throw new Exception("SQL errors saving permissions on folder " + folder.getText());
		} else {
			// Add a folder history entry
			History history = new History();
			history.setUser(SessionManagement.getUser());
			history.setEvent(History.EVENT_FOLDER_PERMISSION);
			history.setSessionId(SessionManagement.getCurrentUserSessionId());
			mdao.store(folder, history);
		}
		if (recursive) {
			// recursively apply permissions to all submenus where the user has
			// security management permission
			Collection<Menu> submenus = mdao.findByParentId(id);
			for (Menu submenu : submenus) {
				saveRules(submenu.getId(), userId);
			}
		}
	}

	public Directory getSelectedDirectory() {
		return selectedDirectory;
	}

	public void setDocumentNavigation(DocumentNavigation documentNavigation) {
		this.documentNavigation = documentNavigation;
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
			if (group.getType() == Group.TYPE_DEFAULT
					|| ((group.getType() != Group.TYPE_DEFAULT) && (group.getUsers().iterator().next().getType() == User.TYPE_DEFAULT))) {
				String username = "";
				String fullName = "";
				if (!group.getUsers().isEmpty()) {
					username = group.getUsers().iterator().next().getUserName();
					fullName = group.getUsers().iterator().next().getFullName();
				}

				if ((group.getType() == Group.TYPE_DEFAULT && group.getName().toLowerCase().contains(
						event.getNewValue().toString().toLowerCase()))
						|| (group.getType() == Group.TYPE_USER && (username.toLowerCase().contains(
								event.getNewValue().toString().toLowerCase()) || fullName.toLowerCase().contains(
								event.getNewValue().toString().toLowerCase())))) {
					// Check if the group is allowed
					boolean allowed = false;
					for (SelectItem item : allowedGroups) {
						if (((Long) item.getValue()).equals(group.getId())) {
							allowed = true;
							break;
						}
					}
					if (!allowed)
						availableGroups.add(new SelectItem(group.getId(), getEntityLabel(group)));
				}
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
			if (group.getType() == Group.TYPE_DEFAULT
					|| ((group.getType() != Group.TYPE_DEFAULT) && (group.getUsers().iterator().next().getType() == User.TYPE_DEFAULT))) {
				String username = "";
				String fullName = "";
				if (!group.getUsers().isEmpty()) {
					username = group.getUsers().iterator().next().getUserName();
					fullName = group.getUsers().iterator().next().getFullName();
				}

				if ((group.getType() == Group.TYPE_DEFAULT && group.getName().toLowerCase().contains(
						event.getNewValue().toString().toLowerCase()))
						|| (group.getType() == Group.TYPE_USER && (username.toLowerCase().contains(
								event.getNewValue().toString().toLowerCase()) || fullName.toLowerCase().contains(
								event.getNewValue().toString().toLowerCase())))) {
					// Check if the group is available
					boolean available = false;
					for (SelectItem item : availableGroups) {
						if (((Long) item.getValue()).equals(group.getId())) {
							available = true;
							break;
						}
					}
					if (!available)
						allowedGroups.add(new SelectItem(group.getId(), getEntityLabel(group)));
				}
			}
		}
	}
}