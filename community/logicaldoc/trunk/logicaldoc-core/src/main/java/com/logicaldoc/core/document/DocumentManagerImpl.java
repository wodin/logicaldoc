package com.logicaldoc.core.document;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.FileBean;
import com.logicaldoc.core.document.Version.VERSION_TYPE;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.HistoryDAO;
import com.logicaldoc.core.document.dao.TermDAO;
import com.logicaldoc.core.i18n.DateBean;
import com.logicaldoc.core.searchengine.Indexer;
import com.logicaldoc.core.searchengine.store.Storer;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.MenuGroup;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDocDAO;
import com.logicaldoc.core.text.parser.Parser;
import com.logicaldoc.core.text.parser.ParserFactory;
import com.logicaldoc.core.util.IconSelector;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.SettingsConfig;

/**
 * Basic Implementation of <code>DocumentManager</code>
 * 
 * @author Marco Meschieri
 * @version $Id:$
 * @since 3.5
 */
public class DocumentManagerImpl implements DocumentManager {
	protected static Log log = LogFactory.getLog(DocumentManagerImpl.class);

	private DocumentDAO documentDAO;

	private MenuDAO menuDAO;

	private HistoryDAO historyDAO;

	private UserDocDAO userDocDAO;

	private TermDAO termDAO;

	private SettingsConfig settings;

	private Storer storer;

	private Indexer indexer;

	public void setDocumentDAO(DocumentDAO documentDAO) {
		this.documentDAO = documentDAO;
	}

	public void setMenuDAO(MenuDAO menuDAO) {
		this.menuDAO = menuDAO;
	}

	public void setHistoryDAO(HistoryDAO historyDAO) {
		this.historyDAO = historyDAO;
	}

	public void setUserDocDAO(UserDocDAO userDocDAO) {
		this.userDocDAO = userDocDAO;
	}

	public void setTermDAO(TermDAO termDAO) {
		this.termDAO = termDAO;
	}

	public void setSettings(SettingsConfig settings) {
		this.settings = settings;
	}

	public void setStorer(Storer storer) {
		this.storer = storer;
	}

	public void setIndexer(Indexer indexer) {
		this.indexer = indexer;
	}

	@Override
	public void checkin(int docId, File file, String filename, String username,
			VERSION_TYPE versionType, String versionDesc) throws Exception {
		FileInputStream is = new FileInputStream(file);
		try {
			checkin(docId, is, filename, username, versionType, versionDesc);
		} finally {
			is.close();
		}
	}

	@Override
	public void checkin(int docId, InputStream fileInputStream,
			String filename, String username, VERSION_TYPE versionType,
			String versionDesc) throws Exception {
		// identify the document and menu
		Document document = documentDAO.findByPrimaryKey(docId);
		int menuId = document.getMenuId();
		Menu menu = menuDAO.findByPrimaryKey(menuId);

		// create some strings containing paths
		String menuPath = menu.getMenuPath() + "/" + String.valueOf(menuId);
		String completeDocPath = settings.getValue("docdir") + menuPath + "/";

		// rename the old current version file to the version name: "quelle.txt"
		// -> "2.0"
		if (!document.getDocType().equals("zip")
				|| !document.getDocType().equals("jar")) {
			FileBean.renameFile(completeDocPath + menu.getMenuRef(),
					completeDocPath + document.getDocVersion());
		}

		// extract file extension of the new file and select a file icon based
		// on the extension
		String extension = filename.substring(filename.lastIndexOf(".") + 1);
		menu.setMenuRef(filename);
		String icon = IconSelector.selectIcon(extension);
		menu.setMenuIcon(icon);

		// create new version
		Version version = createNewVersion(versionType, username, versionDesc,
				document.getDocVersion());
		String newVersion = version.getVersion();

		// set other properties of the document
		document.setDocDate(DateBean.toCompactString());
		document.setDocPublisher(username);
		document.setDocStatus(Document.DOC_CHECKED_IN);
		document.setDocType(extension);
		document.setCheckoutUser("");
		document.setMenu(menu);
		document.addVersion(version);
		document.setDocVersion(newVersion);
		if (documentDAO.store(document) == false)
			throw new Exception();

		// create history entry for this checkin event
		createHistoryEntry(docId, username, History.CHECKIN);

		// create search index entry
		createIndexEntry(document, menuId, filename, completeDocPath);

		// store the document in the repository (on the file system)
		store(document, fileInputStream, filename, newVersion);

		// Update file size
		menuDAO.store(menu);

		log.debug("Checked in document " + docId);
	}

	@Override
	public void checkout(int docId, String username) throws Exception {
		Document document = documentDAO.findByMenuId(docId);

		if (document.getDocStatus() == Document.DOC_CHECKED_IN) {
			document.setCheckoutUser(username);
			document.setDocStatus(Document.DOC_CHECKED_OUT);
			document.setMenu(document.getMenu());
			documentDAO.store(document);

			// create history entry for this checkout event
			createHistoryEntry(docId, username, History.CHECKOUT);

			log.debug("Checked out document " + docId);
		} else {
			throw new Exception("Document already checked out");
		}
	}

	@Override
	public Document create(File file, Menu parent, String username,
			String language) throws Exception {
		return create(file, parent, username, language, "", null, "", "", "",
				"", "", null, parent.getMenuGroups());
	}

	@Override
	public Document create(InputStream content, String filename, Menu parent,
			String username, String language, String name, Date sourceDate,
			String source, String sourceAuthor, String sourceType,
			String coverage, String versionDesc, Set<String> keywords,
			Set<MenuGroup> groups) throws Exception {
		try {
			Menu menu = new Menu();
			menu.setMenuParent(parent.getMenuId());

			// Makes menuPath
			String menupath = new StringBuilder(parent.getMenuPath()).append(
					"/").append(parent.getMenuId()).toString();
			int menuhier = parent.getMenuHier();
			menu.setMenuPath(menupath);
			menu.setMenuHier(menuhier++);

			Document doc = new Document();
			Version vers = new Version();
			String ext = filename.substring(filename.lastIndexOf(".") + 1);

			if (StringUtils.isNotEmpty(name)) {
				menu.setMenuText(name);
			} else {
				menu.setMenuText(filename.substring(0, filename
						.lastIndexOf(".")));
			}

			String icon = IconSelector.selectIcon(ext);
			menu.setMenuIcon(icon);
			menu.setMenuType(Menu.MENUTYPE_FILE);
			menu.setMenuRef(filename);
			for (MenuGroup mg : groups) {
				menu.getMenuGroups().add(mg);
			}
			menuDAO.store(menu);

			doc.setMenu(menu);
			if ((name != null) && !name.equals("")) {
				doc.setDocName(name);
			} else {
				doc
						.setDocName(filename.substring(0, filename
								.lastIndexOf(".")));
			}

			String now = DateBean.toCompactString();
			doc.setDocDate(now);
			if (sourceDate != null)
				doc.setSourceDate(DateBean.toCompactString(sourceDate));
			else
				doc.setSourceDate(now);
			doc.setDocPublisher(username);
			doc.setDocStatus(Document.DOC_CHECKED_IN);
			doc.setDocType(filename.substring(filename.lastIndexOf(".") + 1));
			doc.setDocVersion("1.0");
			doc.setSource(source);
			doc.setSourceAuthor(sourceAuthor);
			doc.setSourceType(sourceType);
			doc.setCoverage(coverage);
			doc.setLanguage(language);
			if (keywords != null)
				doc.setKeywords(keywords);

			/* insert initial version 1.0 */
			vers.setVersion("1.0");
			vers.setVersionComment(versionDesc);
			vers.setVersionDate(DateBean.toCompactString());
			vers.setVersionUser(username);

			doc.addVersion(vers);

			documentDAO.store(doc);

			String path = new StringBuilder(settings.getValue("docdir"))
					.append("/").append(menupath).append("/").append(
							String.valueOf(menu.getMenuId())).append("/")
					.toString();

			/* store the document */
			store(doc, content, filename, "1.0");

			// Update file size
			menuDAO.store(menu);

			createHistoryEntry(doc.getDocId(), username, History.STORED);

			/* create search index entry */
			String lang = doc.getLanguage();
			File file = new File(new StringBuilder(path).append("/").append(
					menu.getMenuRef()).toString());
			indexer.addFile(file, doc, getDocumentContent(doc), lang);
			return doc;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public Document create(File file, Menu parent, String username,
			String language, String name, Date sourceDate, String source,
			String sourceAuthor, String sourceType, String coverage,
			String versionDesc, Set<String> keywords, Set<MenuGroup> groups)
			throws Exception {
		Locale locale = new Locale(language);
		Parser parser = ParserFactory.getParser(file, locale);
		String filename = file.getName();
		String _name = name;
		String _author = sourceAuthor;
		Set<String> _kwds = keywords;
		if (parser != null) {
			if (StringUtils.isEmpty(name)) {
				if (parser.getTitle().length() == 0)
					_name = filename.substring(0, filename.lastIndexOf("."));
				else
					_name = parser.getTitle();
			}
			if (StringUtils.isEmpty(name))
				_author = parser.getAuthor();
			String keys = parser.getKeywords();
			if (keys != null && keys.length() > 0) {
				if (keywords == null || keywords.isEmpty())
					_kwds = new HashSet<String>();
				_kwds = documentDAO.toKeywords(keys);
			}
		} else {
			if (StringUtils.isEmpty(name))
				name = filename;
		}

		InputStream is = new FileInputStream(file);
		try {
			return create(is, file.getName(), parent, username, language,
					_name, sourceDate, source, _author, sourceType, coverage,
					versionDesc, _kwds, groups);
		} finally {
			is.close();
		}
	}

	private void store(Document doc, InputStream content, String filename,
			String version) throws IOException {
		Menu menu = doc.getMenu();

		// Makes menuPath
		String mpath = menu.getMenuPath() + "/"
				+ String.valueOf(menu.getMenuId());

		// Get file to upload inputStream
		Storer storer = (Storer) Context.getInstance().getBean(Storer.class);

		// stores it in folder
		storer.store(content, mpath, filename, version);
	}

	@Override
	public Menu createFolder(Menu parent, String name) {
		Menu menu = new Menu();
		menu.setMenuText(name);
		menu.setMenuParent(parent.getMenuId());
		menu.setMenuSort(0);
		menu.setMenuIcon("folder.gif");
		menu.setMenuPath(parent.getMenuPath() + "/" + parent.getMenuId());
		menu.setMenuType(Menu.MENUTYPE_DIRECTORY);
		menu.setMenuHier(parent.getMenuHier() + 1);
		menu.setMenuRef("");
		for (MenuGroup mg : parent.getMenuGroups()) {
			menu.getMenuGroups().add(mg);
		}

		if (menuDAO.store(menu) == false)
			return null;
		return menu;
	}

	@Override
	public void delete(int menuId, String username) throws Exception {
		Menu menu = menuDAO.findByPrimaryKey(menuId);
		int type = menu.getMenuType();
		try {
			if (type == Menu.MENUTYPE_FILE) {
				deleteFile(menu, username);
			} else if (type == Menu.MENUTYPE_DIRECTORY) {
				Collection<Menu> children = menuDAO.findByParentId(menuId);
				Iterator<Menu> childIter = children.iterator();
				while (childIter.hasNext()) {
					Menu m = (Menu) childIter.next();
					deleteFile(m, username);
				}
			}
			menuDAO.delete(menuId);
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage(), ex);
			throw ex;
		}
	}

	@Override
	public File getDocumentFile(Document doc) {
		return getDocumentFile(doc, null);
	}

	@Override
	public File getDocumentFile(Document doc, String version) {
		Menu menu = menuDAO.findByPrimaryKey(doc.getMenuId());
		String path = settings.getValue("docdir") + "/";
		path += (menu.getMenuPath() + "/" + menu.getMenuId());

		/*
		 * Older versions of a document are stored in the same directory as the
		 * current version, but the filename is the version number without
		 * extension, e.g. "menuid/2.1"
		 */
		String menuref;
		if (StringUtils.isEmpty(version))
			menuref = menu.getMenuRef();
		else
			menuref = version;
		return new File(path, menuref);
	}

	@Override
	public String getDocumentContent(Document doc) {
		String content = null;
		File file = getDocumentFile(doc);

		// Parses the file where it is already stored
		Locale locale = new Locale(doc.getLanguage());
		Parser parser = ParserFactory.getParser(file, locale);

		// and gets some fields
		if (parser != null) {
			content = parser.getContent();
		}
		if (content == null) {
			content = "";
		}
		return content;
	}

	@Override
	public void reindex(Document doc, String originalLanguage) throws Exception {
		/* get search index entry */
		String lang = doc.getLanguage();

		// Extract the content from the file
		String content = getDocumentContent(doc);

		// Remove the document from the index
		indexer.deleteFile(String.valueOf(doc.getMenuId()), originalLanguage);

		// Add the document to the index (lucene 2.x doesn't support the update
		// operation)
		File file = getDocumentFile(doc);
		indexer.addFile(file, doc, content, lang);
	}

	@Override
	public void update(Document doc, String username, String name,
			String source, String sourceAuthor, Date sourceDate,
			String sourceType, String coverage, String language,
			Set<String> keywords) throws Exception {
		try {
			Menu menu = menuDAO.findByPrimaryKey(doc.getMenuId());
			if (StringUtils.isNotEmpty(name)) {
				menu.setMenuText(name);
			}
			menuDAO.store(menu);

			doc.setMenu(menu);
			doc.setDocName(name);
			doc.setSource(source);
			doc.setSourceAuthor(sourceAuthor);
			if (sourceDate != null)
				doc.setSourceDate(DateBean.toCompactString(sourceDate));
			else
				doc.setSourceDate("");
			doc.setSourceType(sourceType);
			doc.setCoverage(coverage);

			// Intercept language changes
			String oldLang = doc.getLanguage();
			doc.setLanguage(language);

			doc.clearKeywords();
			documentDAO.store(doc);
			doc.setKeywords(keywords);
			documentDAO.store(doc);

			/* create history entry */
			History history = new History();
			history.setDocId(doc.getDocId());
			history.setDate(DateBean.toCompactString());
			history.setUsername(username);
			history.setEvent(History.CHANGED);
			historyDAO.store(history);

			// Launch document re-indexing
			reindex(doc, oldLang);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * Creates a new version object and fills in the provided attributes
	 * 
	 * @param versionType
	 *            either a new release, a new subversion or just the old version
	 * @param username
	 *            user creating the new version
	 * @param description
	 *            change description
	 * @param docId
	 *            version should belong to this document
	 * @param oldVersionName
	 *            the previous version name
	 */
	private Version createNewVersion(Version.VERSION_TYPE versionType,
			String username, String description, String oldVersionName) {
		Version version = new Version();
		String newVersionName = version.getNewVersionName(oldVersionName,
				versionType);

		version.setVersion(newVersionName);
		version.setVersionComment(description);
		version.setVersionDate(DateBean.toCompactString());
		version.setVersionUser(username);

		return version;
	}

	/** Creates a new search index entry for the given document */
	private void createIndexEntry(Document document, int menuId,
			String filename, String path) throws Exception {
		indexer.deleteFile(String.valueOf(menuId), document.getLanguage());
		indexer.addDirectory(new File(path + filename), document);
	}

	/** Creates history entry saying username has checked in document (id) */
	private void createHistoryEntry(int docId, String username, String eventType) {
		History history = new History();
		history.setDocId(docId);
		history.setDate(DateBean.toCompactString());
		history.setUsername(username);
		history.setEvent(eventType);
		historyDAO.store(history);
	}

	/**
	 * Deletes the given menu; does not perform an access check; only used
	 * internally
	 */
	private void deleteFile(Menu menu, String username) throws Exception {
		int id = menu.getMenuId();
		userDocDAO.delete(username, id);
		Document doc = documentDAO.findByMenuId(id);
		if (doc != null) {
			indexer.deleteFile(String.valueOf(id), doc.getLanguage());
		}
		boolean deleted1 = documentDAO.deleteByMenuId(id);
		boolean deleted2 = termDAO.delete(id);
		boolean deleted = menuDAO.delete(id);
		String menupath = menu.getMenuPath() + "/" + String.valueOf(id);
		if (!storer.delete(menupath) || !deleted || !deleted1 || !deleted2)
			throw new Exception();
	}

	@Override
	public Menu createFolders(Menu parent, String path) {
		StringTokenizer st = new StringTokenizer(path, "/", false);

		Menu menu = parent;
		while (st.hasMoreTokens()) {
			String name = st.nextToken();
			Collection<Menu> childs = menuDAO.findByMenuText(menu, name,
					Menu.MENUTYPE_DIRECTORY);
			Menu dir;
			if (childs.isEmpty())
				dir = createFolder(menu, name);
			else {
				dir = childs.iterator().next();
			}
			menu = dir;
		}
		return menu;
	}

	@Override
	public String getDocumentContent(int docId) {
		Document doc = documentDAO.findByPrimaryKey(docId);
		org.apache.lucene.document.Document luceneDoc = indexer.getDocument(Integer.toString(docId), doc
				.getLanguage());
		//First search the document using it's id
		if(luceneDoc==null)
			luceneDoc = indexer.getDocumentByMenuId(Integer.toString(doc.getMenuId()), doc
					.getLanguage());
		//If not found, search the document using it's menu id
		if(luceneDoc!=null)
			return luceneDoc.get("content");
		else
			return "";
	}
}