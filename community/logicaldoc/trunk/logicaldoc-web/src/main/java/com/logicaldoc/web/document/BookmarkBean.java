package com.logicaldoc.web.document;

import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icesoft.faces.context.effects.JavascriptContext;
import com.logicaldoc.core.document.Bookmark;
import com.logicaldoc.core.document.dao.BookmarkDAO;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.components.SortableList;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.navigation.NavigationBean;
import com.logicaldoc.web.navigation.PageContentBean;
import com.logicaldoc.web.util.FacesUtil;

/**
 * <p>
 * The <code>BookmarkBean</code> class is responsible for constructing the list
 * of <code>Bookmark</code> beans which will be bound to a ice:dataTable JSF
 * component.
 * <p/>
 * <p>
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class BookmarkBean extends SortableList {

	protected static Log log = LogFactory.getLog(BookmarkBean.class);

	private ArrayList<Bookmark> bookmarks;

	private String selectedPanel = "list";

	private String bookmarksFilter = "";

	private int displayedRows = 10;

	private Bookmark selectedBookmark = null;

	private UIInput titleInput = null;

	private UIInput descriptionInput = null;

	public BookmarkBean() {
		super("title");
		reload();
	}

	@Override
	protected boolean isDefaultAscending(String sortColumn) {
		return true;
	}

	/**
	 * Sorts the list of subscriptions data.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void sort(final String column, final boolean ascending) {
		log.debug("invoked BookmarkBean.sort()");
		log.debug("sort on column: " + column);
		log.debug("sort ascending: " + ascending);

		Comparator comparator = new Comparator() {
			public int compare(Object o1, Object o2) {

				Bookmark c1 = (Bookmark) o1;
				Bookmark c2 = (Bookmark) o2;
				if (column == null) {
					return 0;
				}
				if (column.equals("title")) {
					return ascending ? c1.getTitle().compareTo(c2.getTitle()) : c2.getTitle().compareTo(c1.getTitle());
				} else
					return 0;
			}
		};

		Collections.sort(bookmarks, comparator);
	}

	public void reload() {
		// initiate the list
		if (bookmarks != null) {
			bookmarks.clear();
		} else {
			bookmarks = new ArrayList<Bookmark>();
		}

		BookmarkDAO bookmarkDao = (BookmarkDAO) Context.getInstance().getBean(BookmarkDAO.class);
		try {
			Collection<Bookmark> tmpbookmarks = null;
			if (bookmarksFilter.length() != 0) {
				tmpbookmarks = bookmarkDao.findByWhere(" lower(_entity.title) like '%" + bookmarksFilter.toLowerCase()
						+ "%' and _entity.userId = " + SessionManagement.getUserId(), null, null);
			} else {
				tmpbookmarks = bookmarkDao.findByUserId(SessionManagement.getUserId());
			}

			for (Bookmark bookmark : tmpbookmarks) {
				bookmarks.add(bookmark);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			Messages.addLocalizedError("errors.error");
		}

		sort("title", false);
		if (getSortColumn() != null)
			sort(getSortColumn(), isAscending());
	}

	public String edit() {
		FacesUtil.forceRefresh(titleInput);
		FacesUtil.forceRefresh(descriptionInput);
		selectedBookmark = null;
		selectedPanel = "edit";

		BookmarkDAO bookmarkDao = (BookmarkDAO) Context.getInstance().getBean(BookmarkDAO.class);
		long bookmarkId = Long.parseLong((String) FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("bookmarkId"));
		selectedBookmark = bookmarkDao.findById(bookmarkId);

		return null;
	}

	/**
	 * Deletes a bookmark.
	 */
	public String delete() {
		BookmarkDAO bookmarkDao = (BookmarkDAO) Context.getInstance().getBean(BookmarkDAO.class);
		long bookmarkId = Long.parseLong((String) FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("bookmarkId"));

		if (SessionManagement.isValid()) {
			try {
				boolean deleted = bookmarkDao.delete(bookmarkId);
				if (deleted) {
					Messages.addLocalizedInfo("msg.action.deleteitem");
					DocumentNavigation documentNavigation = ((DocumentNavigation) FacesUtil.accessBeanFromFacesContext(
							"documentNavigation", FacesContext.getCurrentInstance(), log));
					documentNavigation.refresh();
				} else {
					Messages.addLocalizedError("errors.action.deleteitem");
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				Messages.addLocalizedError("errors.error");
			}

			setSelectedPanel("list");
			reload();
		} else {
			return "login";
		}

		return null;
	}

	/**
	 * Gets the list of Subscriptions which will be used by the ice:dataTable
	 * component.
	 */
	public List<Bookmark> getBookmarks() {
		reload();
		return bookmarks;
	}

	public int getCount() {
		if (getBookmarks() == null) {
			return 0;
		} else {
			return getBookmarks().size();
		}
	}

	public String saveBookmarks() {
		DocumentsRecordsManager manager = ((DocumentsRecordsManager) FacesUtil.accessBeanFromFacesContext(
				"documentsRecordsManager", FacesContext.getCurrentInstance(), log));
		Set<com.logicaldoc.web.document.DocumentRecord> selectedDocuments = manager.getSelection();
		BookmarkDAO bookmarkDao = (BookmarkDAO) Context.getInstance().getBean(BookmarkDAO.class);
		int added = 0;
		int alreadyAdded = 0;
		if (!selectedDocuments.isEmpty()) {
			for (DocumentRecord record : selectedDocuments) {
				try {
					Bookmark bookmark = null;
					if (bookmarkDao.findByUserIdAndDocId(SessionManagement.getUserId(), record.getDocId()).size() > 0) {
						// The bookmark already exists
						alreadyAdded++;
					} else {
						bookmark = new Bookmark();
						bookmark.setTitle(record.getTitle());
						bookmark.setUserId(SessionManagement.getUserId());
						bookmark.setDocId(record.getDocId());
						bookmark.setFileType(record.getDocument().getType());
						bookmarkDao.store(bookmark);
						added++;
					}
				} catch (AccessControlException e) {
					Messages.addLocalizedWarn("document.write.nopermission");
					return null;
				} catch (Exception e) {
					Messages.addLocalizedError("bookmarks.save.error");
					return null;
				}
			}

			if (alreadyAdded != 0)
				Messages.addLocalizedWarn("bookmarks.save.already");
			if (added != 0)
				Messages.addLocalizedInfo("bookmarks.save");

			manager.refresh();
		} else {
			Messages.addLocalizedWarn("noselection");
		}

		return null;
	}

	/**
	 * Filters all bookmarks if bookmark's name contains the string on "Filter"
	 * input text
	 * 
	 * @param event
	 */
	public void filterBookmarks(ValueChangeEvent event) {
		bookmarksFilter = event.getNewValue().toString();
		reload();
	}

	public String save() {
		if (SessionManagement.isValid()) {
			try {
				BookmarkDAO dao = (BookmarkDAO) Context.getInstance().getBean(BookmarkDAO.class);
				boolean stored = dao.store(selectedBookmark);
				if (stored) {
					Messages.addLocalizedInfo("bookmark.update");
				} else {
					Messages.addLocalizedError("bookmark.update.error");
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				Messages.addLocalizedError("bookmark.update.error");
			}

			abort();

			return null;
		} else {
			return "login";
		}
	}

	public String abort() {
		selectedPanel = "list";
		return null;
	}

	public int getDisplayedRows() {
		return displayedRows;
	}

	public void setDisplayedRows(int displayedRows) {
		if (displayedRows != this.displayedRows)
			JavascriptContext.addJavascriptCall(FacesContext.getCurrentInstance(), "window.location.reload(false);");
		this.displayedRows = displayedRows;
	}

	public String getSelectedPanel() {
		return selectedPanel;
	}

	public void setSelectedPanel(String selectedPanel) {
		this.selectedPanel = selectedPanel;
	}

	public String getBookmarksFilter() {
		return bookmarksFilter;
	}

	public void setBookmarksFilter(String bookmarksFilter) {
		this.bookmarksFilter = bookmarksFilter;
	}

	public String openInFolder() {
		long bookmarkId = Long.parseLong((String) FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("bookmarkId"));
		BookmarkDAO bookmarkDao = (BookmarkDAO) Context.getInstance().getBean(BookmarkDAO.class);

		Bookmark bookmark = bookmarkDao.findById(bookmarkId);

		MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		DocumentNavigation documentNavigation = ((DocumentNavigation) FacesUtil.accessBeanFromFacesContext(
				"documentNavigation", FacesContext.getCurrentInstance(), log));
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		documentNavigation.selectDirectory(docDao.findById(bookmark.getDocId()).getFolder().getId());
		documentNavigation.highlightDocument(bookmark.getDocId());
		documentNavigation.setSelectedPanel(new PageContentBean(documentNavigation.getViewMode()));

		// Show the documents browsing panel
		NavigationBean navigation = ((NavigationBean) FacesUtil.accessBeanFromFacesContext("navigation", FacesContext
				.getCurrentInstance(), log));
		Menu documentsMenu = menuDao.findById(Menu.MENUID_DOCUMENTS);

		PageContentBean panel = new PageContentBean(documentsMenu.getId(), "document/browse");
		panel.setContentTitle(Messages.getMessage(documentsMenu.getText()));
		navigation.setSelectedPanel(panel);

		return null;
	}

	public Bookmark getSelectedBookmark() {
		return selectedBookmark;
	}

	public void setSelectedBookmark(Bookmark selectedBookmark) {
		this.selectedBookmark = selectedBookmark;
	}

	public UIInput getTitleInput() {
		return titleInput;
	}

	public void setTitleInput(UIInput titleInput) {
		this.titleInput = titleInput;
	}

	public UIInput getDescriptionInput() {
		return descriptionInput;
	}

	public void setDescriptionInput(UIInput descriptionInput) {
		this.descriptionInput = descriptionInput;
	}
}
