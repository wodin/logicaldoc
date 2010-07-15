package com.logicaldoc.web.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.util.FacesUtil;

/**
 * Form for group editing
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 4.0
 */
public class GroupForm {

	protected static Log log = LogFactory.getLog(GroupForm.class);

	private List<Preference> preferences = new ArrayList<Preference>();

	private Group group = null;

	private UIInput groupName = null;

	private UIInput groupDesc = null;

	private Long parentGroup = null;

	private String groupFilter = "";

	private Collection<SelectItem> items = new ArrayList<SelectItem>();

	public Collection<SelectItem> getItems() {
		return items;
	}

	public void setItems(Collection<SelectItem> items) {
		this.items = items;
	}

	public Group getGroup() {
		return group;
	}

	public Long getParentGroup() {
		return parentGroup;
	}

	public void setParentGroup(Long parentGroup) {
		this.parentGroup = parentGroup;
	}

	public void setGroup(Group group) {
		this.group = group;
		init();
	}

	public String getGroupFilter() {
		return groupFilter;
	}

	public void setGroupFilter(String groupFilter) {
		this.groupFilter = groupFilter;
	}

	public void init() {
		FacesUtil.forceRefresh(groupName);
		FacesUtil.forceRefresh(groupDesc);
		items.clear();
		initPreferences();
		try {
			GroupDAO dao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
			Collection<Group> tmpgroups = null;
			if (groupFilter.length() != 0) {
				tmpgroups = dao.findByLikeName("%" + groupFilter + "%");
			} else
				tmpgroups = dao.findAll();
			for (Group group : tmpgroups) {
				if (group.getType() == Group.TYPE_DEFAULT)
					items.add(new SelectItem(group.getId(), group.getName()));
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			Messages.addLocalizedError("errors.error");
		}
	}

	/**
	 * Filters all groups if group's name contains the string on "Filter" input
	 * text
	 * 
	 * @param event
	 */
	public void filterGroups(ValueChangeEvent event) {
		items.clear();
		GroupDAO dao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
		List<Group> groups = (List<Group>) dao.findAll();
		for (Group group : groups) {
			if (group.getName().toLowerCase().contains(event.getNewValue().toString().toLowerCase())
					&& group.getType() == Group.TYPE_DEFAULT) {
				SelectItem item = new SelectItem(group.getId(), group.getName());
				items.add(item);
			}
		}
	}

	/**
	 * Sets the list of preferences which will be used by the ice:dataTable
	 * component.
	 */
	public void initPreferences() {
		preferences.clear();

		Preference pref = new Preference();
		pref.setName("coverage");
		pref.setLabel(Messages.getMessage("document.coverage"));
		preferences.add(pref);
		if (group.getValue("preference.field.coverage") != null)
			pref.decodePreferences(group.getValue("preference.field.coverage").toString());

		pref = new Preference();
		pref.setName("customId");
		pref.setLabel(Messages.getMessage("document.customid"));
		preferences.add(pref);
		if (group.getValue("preference.field.customId") != null)
			pref.decodePreferences(group.getValue("preference.field.customId").toString());

		pref = new Preference();
		pref.setName("tags");
		pref.setLabel(Messages.getMessage("tags"));
		preferences.add(pref);
		if (group.getValue("preference.field.tags") != null)
			pref.decodePreferences(group.getValue("preference.field.tags").toString());

		pref = new Preference();
		pref.setName("object");
		pref.setLabel(Messages.getMessage("document.object"));
		preferences.add(pref);
		if (group.getValue("preference.field.object") != null)
			pref.decodePreferences(group.getValue("preference.field.object").toString());

		pref = new Preference();
		pref.setName("recipient");
		pref.setLabel(Messages.getMessage("document.recipient"));
		preferences.add(pref);
		if (group.getValue("preference.field.recipient") != null)
			pref.decodePreferences(group.getValue("preference.field.recipient").toString());

		pref = new Preference();
		pref.setName("source");
		pref.setLabel(Messages.getMessage("document.source"));
		preferences.add(pref);
		if (group.getValue("preference.field.source") != null)
			pref.decodePreferences(group.getValue("preference.field.source").toString());

		pref = new Preference();
		pref.setName("sourceAuthor");
		pref.setLabel(Messages.getMessage("document.author"));
		preferences.add(pref);
		if (group.getValue("preference.field.sourceAuthor") != null)
			pref.decodePreferences(group.getValue("preference.field.sourceAuthor").toString());

		pref = new Preference();
		pref.setName("sourceDate");
		pref.setLabel(Messages.getMessage("msg.jsp.sourcedate"));
		preferences.add(pref);
		if (group.getValue("preference.field.sourceDate") != null)
			pref.decodePreferences(group.getValue("preference.field.sourceDate").toString());

		pref = new Preference();
		pref.setName("sourceId");
		pref.setLabel(Messages.getMessage("document.sourceid"));
		preferences.add(pref);
		if (group.getValue("preference.field.sourceId") != null)
			pref.decodePreferences(group.getValue("preference.field.sourceId").toString());

		pref = new Preference();
		pref.setName("sourceType");
		pref.setLabel(Messages.getMessage("document.type"));
		preferences.add(pref);
		if (group.getValue("preference.field.sourceType") != null)
			pref.decodePreferences(group.getValue("preference.field.sourceType").toString());

		pref = new Preference();
		pref.setName("template");
		pref.setLabel(Messages.getMessage("template"));
		preferences.add(pref);
		if (group.getValue("preference.field.template") != null)
			pref.decodePreferences(group.getValue("preference.field.template").toString());

		Collections.sort(preferences, new Comparator<Preference>() {
			@Override
			public int compare(Preference pref1, Preference pref2) {
				return pref1.getLabel().compareTo(pref2.getLabel());
			}
		});
	}

	public String save() {
		GroupDAO dao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
		GroupsRecordsManager recordsManager = ((GroupsRecordsManager) FacesUtil.accessBeanFromFacesContext(
				"groupsRecordsManager", FacesContext.getCurrentInstance(), log));

		try {
			if (recordsManager.getSelectedPanel().equals("create") && dao.findById(group.getId()) != null) {
				Messages.addLocalizedError("errors.action.groupexists");
			} else {
				boolean stored = false;

				if (recordsManager.getSelectedPanel().equals("create")) {
					if (parentGroup == null || parentGroup.longValue() <= 0) {
						stored = dao.store(group);
					} else {
						stored = dao.insert(group, parentGroup.longValue());
					}
				} else {
					for (Preference preference : preferences) {
						preference.updateGroup(group);
					}
					stored = dao.store(group);

					if (parentGroup != null && parentGroup.longValue() > 0)
						dao.inheritACLs(group.getId(), parentGroup.longValue());
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

		recordsManager.reload();
		recordsManager.setSelectedPanel("list");
		return null;
	}

	/**
	 * Inner class to define the Preference Bean user only by the Group
	 */
	public class Preference {

		private String name = "";

		private String label = "";

		private boolean insertHidden = false;

		private boolean insertReadOnly = false;

		private boolean insertMandatory = false;

		private boolean editHidden = false;

		private boolean editReadOnly = false;

		private boolean editMandatory = false;

		public Preference() {
		}

		public boolean isInsertHidden() {
			return insertHidden;
		}

		public void setInsertHidden(boolean insertHidden) {
			this.insertHidden = insertHidden;
		}

		public boolean isInsertReadOnly() {
			return insertReadOnly;
		}

		public void setInsertReadOnly(boolean insertReadOnly) {
			this.insertReadOnly = insertReadOnly;
		}

		public boolean isInsertMandatory() {
			return insertMandatory;
		}

		public void setInsertMandatory(boolean insertMandatory) {
			this.insertMandatory = insertMandatory;
		}

		public boolean isEditHidden() {
			return editHidden;
		}

		public void setEditHidden(boolean editHidden) {
			this.editHidden = editHidden;
		}

		public boolean isEditReadOnly() {
			return editReadOnly;
		}

		public void setEditReadOnly(boolean editReadOnly) {
			this.editReadOnly = editReadOnly;
		}

		public boolean isEditMandatory() {
			return editMandatory;
		}

		public void setEditMandatory(boolean editMandatory) {
			this.editMandatory = editMandatory;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		/**
		 * Decode the preferences definitions from a string of ones and zeros as
		 * described in <code>FieldPreferences</code>
		 */
		public void decodePreferences(String str) {
			setInsertHidden(str.charAt(0) == '0' ? false : true);
			setInsertReadOnly(str.charAt(1) == '0' ? false : true);
			setInsertMandatory(str.charAt(2) == '0' ? false : true);
			setEditHidden(str.charAt(3) == '0' ? false : true);
			setEditReadOnly(str.charAt(4) == '0' ? false : true);
			setEditMandatory(str.charAt(5) == '0' ? false : true);
		}

		/**
		 * Sets the proper extended attribute on the given group
		 * 
		 * @param group
		 */
		public void updateGroup(Group group) {
			String prefValue = "";
			prefValue += isInsertHidden() ? "1" : "0";
			prefValue += isInsertReadOnly() ? "1" : "0";
			prefValue += isInsertMandatory() ? "1" : "0";
			prefValue += isEditHidden() ? "1" : "0";
			prefValue += isEditReadOnly() ? "1" : "0";
			prefValue += isEditMandatory() ? "1" : "0";
			String prefName = "preference.field." + getName();
			if (prefValue.equals("000000") && group.getAttributeNames().contains(prefName))
				group.getAttributes().remove(prefName);
			else
				group.setValue(prefName, prefValue);
		}
	}

	public List<Preference> getPreferences() {
		return preferences;
	}

	public UIInput getGroupName() {
		return groupName;
	}

	public void setGroupName(UIInput groupName) {
		this.groupName = groupName;
	}

	public UIInput getGroupDesc() {
		return groupDesc;
	}

	public void setGroupDesc(UIInput groupDesc) {
		this.groupDesc = groupDesc;
	}
}
