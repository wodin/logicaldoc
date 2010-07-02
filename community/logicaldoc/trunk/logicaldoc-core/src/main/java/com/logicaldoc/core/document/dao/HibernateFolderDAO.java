package com.logicaldoc.core.document.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.core.security.dao.HibernateMenuDAO;

public class HibernateFolderDAO extends HibernateMenuDAO implements FolderDAO {

	private DocumentDAO documentDAO;

	protected HibernateFolderDAO() {
		super();
		super.log = LogFactory.getLog(HibernateFolderDAO.class);
	}

	@Override
	public Menu create(Menu parent, String name, History transaction) {
		Menu menu = new Menu();
		menu.setText(name);
		menu.setParentId(parent.getId());
		menu.setSort(0);
		menu.setIcon("folder.png");
		menu.setType(Menu.MENUTYPE_DIRECTORY);

		if (parent.getSecurityRef() != null)
			menu.setSecurityRef(parent.getSecurityRef());
		else
			menu.setSecurityRef(parent.getId());

		setUniqueName(menu);
		if (transaction != null)
			transaction.setEvent(History.EVENT_FOLDER_CREATED);
		if (store(menu, transaction) == false)
			return null;
		return menu;
	}

	@Override
	public Menu createPath(Menu parent, String path, History transaction) {
		StringTokenizer st = new StringTokenizer(path, "/", false);

		Menu menu = parent;
		while (st.hasMoreTokens()) {
			String name = st.nextToken();
			List<Menu> childs = findByText(menu, name, Menu.MENUTYPE_DIRECTORY, true);
			System.out.println("childs 0" + childs.get(0));
			Menu dir;
			if (childs.isEmpty())
				dir = create(menu, name, transaction);
			else {
				dir = childs.iterator().next();
			}
			menu = dir;
		}
		return menu;
	}

	@Override
	public Menu find(String name, String pathExtended) {
		StringTokenizer st = new StringTokenizer(pathExtended, "/", false);
		Menu parent = findById(Menu.MENUID_DOCUMENTS);
		while (st.hasMoreTokens()) {
			List<Menu> list = findByText(parent, st.nextToken(), Menu.MENUTYPE_DIRECTORY, true);
			if (list.isEmpty())
				return null;
			parent = list.get(0);

		}

		List<Menu> specified_menu = findByText(parent, name, Menu.MENUTYPE_DIRECTORY, true);
		if (specified_menu != null && specified_menu.size() > 0)
			return specified_menu.iterator().next();
		return null;
	}

	@Override
	public void setUniqueName(Menu folder) {
		int counter = 1;
		String folderName = folder.getText();
		while (findByTextAndParentId(folder.getText(), folder.getParentId()).size() > 0) {
			folder.setText(folderName + "(" + (counter++) + ")");
		}
	}

	@Override
	public void move(Menu source, Menu target, History transaction) throws Exception {
		assert (source != null);
		assert (target != null);
		assert (transaction != null);
		assert (transaction.getUser() != null);

		if (isInPath(source.getId(), target.getId()))
			throw new IllegalArgumentException("Cannot move a dolder inside the same path");

		// Change the parent folder
		source.setParentId(target.getId());

		// Ensure unique folder name in a folder
		setUniqueName(source);

		// Modify folder history entry
		transaction.setEvent(History.EVENT_FOLDER_MOVED);

		store(source, transaction);
	}

	@Override
	public List<Menu> deleteTree(long folderId, History transaction) throws Exception {
		return deleteTree(findById(folderId), transaction);
	}

	@Override
	public List<Menu> deleteTree(Menu folder, History transaction) throws Exception {
		assert (folder != null);
		assert (transaction != null);
		assert (transaction.getUser() != null);

		List<Menu> deletableFolders = new ArrayList<Menu>();
		List<Menu> notDeletableFolders = new ArrayList<Menu>();

		List<Long> deletableIds = findMenuIdByUserIdAndPermission(transaction.getUserId(), Permission.DELETE,
				Menu.MENUTYPE_DIRECTORY);

		if (deletableIds.contains(folder.getId())) {
			deletableFolders.add(folder);
		} else {
			notDeletableFolders.add(folder);
			return notDeletableFolders;
		}

		try {
			// Retrieve all the sub-folders
			List<Menu> subfolders = findByParentId(folder.getId());

			for (Menu subfolder : subfolders) {
				if (deletableIds.contains(subfolder.getId())) {
					deletableFolders.add(subfolder);
				} else {
					notDeletableFolders.add(subfolder);
				}
			}

			for (Menu deletableFolder : deletableFolders) {
				boolean foundDocImmutable = false;
				boolean foundDocLocked = false;
				List<Document> docs = documentDAO.findByFolder(deletableFolder.getId(), null);

				for (Document doc : docs) {
					if (doc.getImmutable() == 1 && !transaction.getUser().isInGroup("admin")) {
						// If it he isn't an administrator he cannot delete a
						// folder containing immutable documents
						foundDocImmutable = true;
						continue;
					}
				}
				if (foundDocImmutable || foundDocLocked) {
					notDeletableFolders.add(deletableFolder);
				}
			}

			// Avoid deletion of the entire path of an undeletable folder
			for (Menu notDeletable : notDeletableFolders) {
				Menu parent = notDeletable;
				while (true) {
					if (deletableFolders.contains(parent))
						deletableFolders.remove(parent);
					if (parent.equals(folder))
						break;
					parent = findById(parent.getParentId());
				}
			}

			// Modify document history entry
			deleteAll(deletableFolders, transaction);
			return notDeletableFolders;
		} catch (Throwable e) {
			log.error(e);
			return notDeletableFolders;
		}
	}

	public void setDocumentDAO(DocumentDAO documentDAO) {
		this.documentDAO = documentDAO;
	}

	@Override
	public List<Menu> find(String name) {
		return findByText(null, "%" + name + "%", Menu.MENUTYPE_DIRECTORY, false);
	}

	public boolean isInPath(long folderId, long targetId) {
		for (Menu menu : findParents(targetId)) {
			if (menu.getId() == folderId)
				return true;
		}
		return false;
	}
}