package com.logicaldoc.web.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.faces.application.FacesMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.GroupRule;
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
 * @author Marco Meschieri
 * @version $Id: RightsRecordsManager.java,v 1.4 2006/09/03 16:24:37 marco Exp $
 * @since 3.0
 */
public class RightsRecordsManager {
	protected static Log log = LogFactory.getLog(RightsRecordsManager.class);

	private ArrayList<GroupRule> rules = new ArrayList<GroupRule>();

	private Document selectedDocument;

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
		selectedDocument = null;
		selectedDirectory = dir;

		initRights(dir.getMenuId());
	}

	/**
	 * Changes the currently selected document and updates the rules list.
	 * 
	 * @param doc
	 */
	public void selectDocument(Document doc) {
		recursive = false;
		selectedDocument = doc;
		selectedDirectory = null;

		initRights(doc.getMenuId());
	}

	/**
	 * Initializes the collection of rights
	 * 
	 * @param menuId The menu that must be evaluated
	 */
	private void initRights(int menuId) {
		// initiate the list
		if (rules != null) {
			rules.clear();
		} else {
			rules = new ArrayList<GroupRule>(10);
		}

		try {
			GroupDAO gdao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
			MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
			String username = SessionManagement.getUsername();

			if (mdao.isWriteEnable(menuId, username)) {
				Collection groups = gdao.findAll();
				Iterator iter = groups.iterator();

				while (iter.hasNext()) {
					Group g = (Group) iter.next();
					GroupRule gr = new GroupRule();
					gr.setGroupName(g.getGroupName());

					gr.setEnabled(true);

					Menu menu = mdao.findByPrimaryKey(menuId);
					MenuGroup mg = menu.getMenuGroup(g.getGroupName());

					if ((mg == null) || !mg.getGroupName().equals(g.getGroupName())) {
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
	public ArrayList getRules() {
		return rules;
	}

	public String back() {
		documentNavigation.setSelectedPanel(new PageContentBean("documents"));

		return null;
	}

	public String save() {
		int id = (selectedDocument != null) ? selectedDocument.getMenuId() : selectedDirectory.getMenuId();
		String username = SessionManagement.getUsername();
		saveRules(id, username);

		documentNavigation.setSelectedPanel(new PageContentBean("documents"));
		return null;
	}

	/**
	 * Saves the selected rights into the current element
	 * 
	 * @param id
	 * @param username
	 */
	private void saveRules(int id, String username) {
		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);

		if (!mdao.isWriteEnable(id, username)) {
			return;
		}

		Menu menu = mdao.findByPrimaryKey(id);

		boolean sqlerrors = false;

		for (GroupRule rule : rules) {
			boolean read = rule.getRead();
			boolean write = rule.getWrite();

			MenuGroup mg = menu.getMenuGroup(rule.getGroupName());

			if (read) {
				if ((mg == null) || !mg.getGroupName().equals(rule.getGroupName())) {
					mg = new MenuGroup();
					mg.setGroupName(rule.getGroupName());
					menu.getMenuGroups().add(mg);
				}

				if (write) {
					mg.setWriteEnable(1);
				} else {
					mg.setWriteEnable(0);
				}

				boolean stored = mdao.store(menu);

				if (!stored) {
					sqlerrors = true;
				}
			} else {
				if (mg != null) {
					menu.getMenuGroups().remove(mg);

					boolean deleted = mdao.store(menu);

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
			// recursively apply permissions to all submenues
			Collection<Menu> submenues = mdao.findByParentId(id);

			for (Menu submenu : submenues) {
				saveRules(submenu.getMenuId(), username);
			}
		}
	}

	public Document getSelectedDocument() {
		return selectedDocument;
	}

	public void setSelectedDocument(Document selectedDocument) {
		this.selectedDocument = selectedDocument;
	}

	public Directory getSelectedDirectory() {
		return selectedDirectory;
	}

	public void setDocumentNavigation(DocumentNavigation documentNavigation) {
		this.documentNavigation = documentNavigation;
	}
}
