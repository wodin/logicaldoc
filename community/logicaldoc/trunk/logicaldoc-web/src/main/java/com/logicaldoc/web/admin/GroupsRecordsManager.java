package com.logicaldoc.web.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.component.ext.HtmlInputTextarea;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.SelectionTagsBean;
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
 * @author Marco Meschieri
 * @version $Id: DocumentsRecordsManager.java,v 1.1 2007/06/29 06:28:29 marco
 *          Exp $
 * @since 3.0
 */
public class GroupsRecordsManager {
	protected static Log log = LogFactory.getLog(GroupsRecordsManager.class);

	private Collection<Group> groups = new ArrayList<Group>();

	private String selectedPanel = "list";

	private long parentGroup;

	private Group selectedGroup = null;

	private HtmlInputText groupName = null;

	private HtmlInputTextarea groupDesc = null;
	
	private Collection<SelectItem> items = new ArrayList<SelectItem>();
	
	private String groupFilter = "";

	private SelectionTagsBean selectionTags = null;

	public Collection<SelectItem> getItems() {
		return items;
	}

	public void setItems(Collection<SelectItem> items) {
		this.items = items;
	}

	public String getGroupFilter() {
		return groupFilter;
	}

	public void setGroupFilter(String groupFilter) {
		this.groupFilter = groupFilter;
	}

	public SelectionTagsBean getSelectionTags() {
		return selectionTags;
	}

	public void setSelectionTags(SelectionTagsBean selectionTags) {
		this.selectionTags = selectionTags;
	}

	public GroupsRecordsManager() {
	}

	private void clear() {

		if (groupName != null) {
			groupName.resetValue();
		}

		if (groupDesc != null) {
			groupDesc.resetValue();
		}
	}

	private void setInputData() {

		if (groupName != null) {
			groupName.setSubmittedValue(selectedGroup.getName());
		}

		if (groupDesc != null) {
			groupDesc.setSubmittedValue(selectedGroup.getDescription());
		}
	}

	private void reload() {
		groups.clear();
		items = selectionTags.getGroups();
		try {
			long userId = SessionManagement.getUserId();
			MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);

			if (mdao.isReadEnable(7, userId)) {
				GroupDAO dao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
				Collection<Group> tmpgroups= null;
				if (groupFilter.length()!=0){
					tmpgroups = dao.findByLikeName(groupFilter+"%");
				}
				else
					tmpgroups = dao.findAll();
				for (Group group : tmpgroups) {
					if (group.getType() == Group.TYPE_DEFAULT)
						groups.add(group);
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

	public String list() {
		selectedPanel = "list";
		FacesUtil.clearAllMessages();
		reload();

		return null;
	}

	public String edit() {
		selectedGroup = (Group) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("group");

		long userId = SessionManagement.getUserId();
		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);

		if (mdao.isReadEnable(7, userId)) {
			setInputData();
		} else {
			Messages.addLocalizedError("errors.noaccess");
		}

		selectedPanel = "edit";

		return null;
	}

	public String addGroup() {
		parentGroup = -1;

		long userId = SessionManagement.getUserId();
		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);

		if (mdao.isReadEnable(7, userId)) {
			selectedGroup = new Group();
			clear();
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

	public String save() {
		if (SessionManagement.isValid()) {
			GroupDAO dao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);

			try {

				if ("create".equals(selectedPanel) && dao.findById(selectedGroup.getId()) != null) {
					Messages.addLocalizedError("errors.action.groupexists");
				} else {
					boolean stored = false;

					if ("create".equals(selectedPanel)) {
						stored = dao.insert(selectedGroup, parentGroup);
					} else {
						stored = dao.store(selectedGroup);
					}

					if (!stored) {
						Messages.addLocalizedError("errors.action.savegroup.notstored");
					} else {
						Messages.addLocalizedInfo("msg.action.savegroup");
					}
				}
			} catch (Exception e) {
				Messages.addLocalizedError("errors.action.savegroup.notstored");
			}

			selectedPanel = "list";
			reload();

			return null;
		} else {
			return "login";
		}
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

	public Group getSelectedGroup() {
		return selectedGroup;
	}

	public void setSelectedGroup(Group selectedGroup) {
		this.selectedGroup = selectedGroup;
	}

	public long getParentGroup() {
		return parentGroup;
	}

	public void setParentGroup(long group) {
		this.parentGroup = group;
	}

	public HtmlInputText getGroupName() {
		return groupName;
	}

	public void setGroupName(HtmlInputText groupName) {
		this.groupName = groupName;
	}

	public HtmlInputTextarea getGroupDesc() {
		return groupDesc;
	}

	public void setGroupDesc(HtmlInputTextarea groupDesc) {
		this.groupDesc = groupDesc;
	}

	/**
	 * Filters all groups if group's name contains the string on
	 * "Filter" input text
	 * 
	 * @param event
	 */
	public void filterGroups(ValueChangeEvent event) {
		items.clear();
		GroupDAO dao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
		List<Group> groups = (List<Group>) dao.findAll();
		for (Group group : groups) {
			if (group.getName().toLowerCase().contains(event.getNewValue().toString().toLowerCase()) && group.getType()==Group.TYPE_DEFAULT) {
				SelectItem item = new SelectItem(group.getId(), group.getName());
				items.add(item);
			}
		}
	}
	
	/**
	 * Filters all group if group's name contains the string on
	 * "GroupName" input text
	 * 
	 * @param event
	 */
	public void filterGroupsByName(ValueChangeEvent event) {
		groupFilter = event.getNewValue().toString();
		reload();
	}
}
