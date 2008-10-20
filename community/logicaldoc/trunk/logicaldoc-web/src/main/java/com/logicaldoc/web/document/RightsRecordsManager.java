package com.logicaldoc.web.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.faces.application.FacesMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.MenuGroup;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.navigation.PageContentBean;

/**
 * Control that allows the user to select access rights
 * 
 * @author Marco Meschieri - Logical Objects
 * @version $Id: RightsRecordsManager.java,v 1.4 2006/09/03 16:24:37 marco Exp $
 * @since 3.0
 */
public class RightsRecordsManager {
	protected static Log log = LogFactory.getLog(RightsRecordsManager.class);

	private ArrayList<GroupRule> rules = new ArrayList<GroupRule>();

	private Directory selectedDirectory;

	private boolean recursive = false;

	private DocumentNavigation documentNavigation;

	public boolean isRecursive() {
		return recursive;
	}

	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
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

	/**
	 * Initializes the collection of rights
	 * 
	 * @param menuId The menu that must be evaluated
	 */
	private void initRights(long menuId) {
		// initiate the list
		if (rules != null) {
			rules.clear();
		} else {
			rules = new ArrayList<GroupRule>(10);
		}

		try {
			GroupDAO gdao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
			MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
			long userId = SessionManagement.getUserId();

			if (mdao.isWriteEnable(menuId, userId)) {
				Collection<Group> groups = gdao.findAll();
				Iterator<Group> iter = groups.iterator();
				while (iter.hasNext()) {
					Group g = (Group) iter.next();
					GroupRule gr = new GroupRule();
					gr.setGroupName(g.getName());
					gr.setGroupId(g.getId());
					gr.setEnabled(true);

					Menu menu = mdao.findById(menuId);
					MenuGroup mg = menu.getMenuGroup(g.getId());

					if ((mg == null) || mg.getGroupId() != g.getId()) {
						gr.setRead(false);
						gr.setWrite(false);
					} else {
						gr.setRead(true);

						if (mg.getWriteEnable() == 1) {
							gr.setWrite(true);
						} else {
							gr.setWrite(false);
						}
					}

					rules.add(gr);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			Messages.addMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
		}
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
	public ArrayList<GroupRule> getRules() {
		return rules;
	}

	public String back() {
		documentNavigation.setSelectedPanel(new PageContentBean("documents"));

		return null;
	}

	public String save() {
		long id = selectedDirectory.getMenuId();
		long userId = SessionManagement.getUserId();
		saveRules(id, userId);
		documentNavigation.setSelectedPanel(new PageContentBean("documents"));
		return null;
	}

	/**
	 * Saves the selected rights into the current element
	 * 
	 * @param id
	 * @param userId
	 */
	private void saveRules(long id, long userId) {
		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);

		if (!mdao.isWriteEnable(id, userId)) {
			return;
		}
		Menu folder = mdao.findById(id);
		boolean sqlerrors = false;
		for (GroupRule rule : rules) {
			boolean read = rule.getRead();
			boolean write = rule.getWrite();

			MenuGroup mg = folder.getMenuGroup(rule.getGroupId());
			if (write || read || rule.getGroupName().equals("admin")) {
				if ((mg == null) || mg.getGroupId() != rule.getGroupId()) {
					mg = new MenuGroup();
					mg.setGroupId(rule.getGroupId());
					folder.getMenuGroups().add(mg);
				}

				if (write || rule.getGroupName().equals("admin")) {
					mg.setWriteEnable(1);
				} else {
					mg.setWriteEnable(0);
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
			Messages.addMessage(FacesMessage.SEVERITY_ERROR, Messages.getMessage("errors.action.saverules"), Messages
					.getMessage("errors.action.saverules"));
		} else {
			Messages.addMessage(FacesMessage.SEVERITY_INFO, Messages.getMessage("msg.action.saverules"), Messages
					.getMessage("msg.action.saverules"));
		}

		if (recursive) {
			// recursively apply permissions to all submenus
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
}
