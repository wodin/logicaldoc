package com.logicaldoc.web.admin;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.SecurityManager;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.util.FacesUtil;

/**
 * <p>
 * The <code>GroupsManager</code> class is responsible for constructing the
 * list of <code>Group</code> beans which will be bound to a ice:dataTable JSF
 * component. <p/>
 * <p>
 * Large data sets could be handle by adding a ice:dataPaginator. Alternatively
 * the dataTable could also be hidden and the dataTable could be added to
 * scrollable ice:panelGroup.
 * </p>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class GroupsRecordsManager {
	protected static Log log = LogFactory.getLog(GroupsRecordsManager.class);

	private Collection<Group> groups = new ArrayList<Group>();

	private String selectedPanel = "list";

	private String groupFilter = "";

	public String getGroupFilter() {
		return groupFilter;
	}

	public void setGroupFilter(String groupFilter) {
		this.groupFilter = groupFilter;
	}

	public void reload() {
		groups.clear();
		try {
			GroupDAO dao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
			Collection<Group> tmpgroups = null;
			if (groupFilter.length() != 0) {
				tmpgroups = dao.findByLikeName("%" + groupFilter + "%");
			} else
				tmpgroups = dao.findAll();
			for (Group group : tmpgroups) {
				if (group.getType() == Group.TYPE_DEFAULT)
					groups.add(group);
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

	public String list() {
		selectedPanel = "list";
		FacesUtil.clearAllMessages();
		reload();

		return null;
	}

	public String edit() {
		selectedPanel = "edit";

		GroupForm groupForm = ((GroupForm) FacesUtil.accessBeanFromFacesContext("groupForm", FacesContext
				.getCurrentInstance(), log));
		Group group = (Group) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("group");
		groupForm.setGroup(group);

		return null;
	}

	public String addGroup() {
		long userId = SessionManagement.getUserId();
		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);

		GroupForm groupForm = ((GroupForm) FacesUtil.accessBeanFromFacesContext("groupForm", FacesContext
				.getCurrentInstance(), log));

		if (mdao.isReadEnable(7, userId)) {
			Group group = new Group();
			groupForm.setGroup(group);
			groupForm.setParentGroup(null);
			FacesUtil.clearAllMessages();
		} else {
			Messages.addLocalizedError("errors.noaccess");
		}

		selectedPanel = "create";
		return null;
	}

	public String delete() {
		long groupId = Long.parseLong((String) FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("groupId"));
		GroupDAO gdao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
		Group group = gdao.findById(groupId);

		if (SessionManagement.isValid()) {
			try {
				long userId = SessionManagement.getUserId();
				MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);

				if (mdao.isReadEnable(7, userId)) {
					// we do not allow to delete the initial "admin" group
					if (group.getName().equals("admin")) {
						Messages.addLocalizedError("errors.action.groupdeleted.admin");
					} else {
						//First of all remove users from this group
						SecurityManager manager = (SecurityManager) Context.getInstance().getBean(SecurityManager.class);				
						manager.removeAllUsersFromGroup(group);
						
						//Then delete the group itself
						boolean deleted = gdao.delete(groupId);
						if (!deleted) {
							Messages.addLocalizedError("errors.action.groupdeleted");
						} else {
							Messages.addLocalizedInfo("msg.action.groupdeleted");
						}
					}
				} else {
					Messages.addLocalizedError("errors.noaccess");
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				Messages.addLocalizedError("errors.action.groupdeleted");
			}

			reload();
		} else {
			return "login";
		}

		return null;
	}

	/**
	 * Gets the list of Group which will be used by the ice:dataTable component.
	 */
	public Collection<Group> getGroups() {
		if (groups.size() == 0) {
			reload();
		}

		return groups;
	}

	public int getCount() {
		return getGroups().size();
	}

	/**
	 * Filters all group if group's name contains the string on "GroupName"
	 * input text
	 * 
	 * @param event
	 */
	public void filterGroupsByName(ValueChangeEvent event) {
		groupFilter = event.getNewValue().toString();
		reload();
	}
}
