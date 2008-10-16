package com.logicaldoc.webservice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.i18n.Language;
import com.logicaldoc.core.i18n.LanguageManager;
import com.logicaldoc.core.searchengine.Search;
import com.logicaldoc.core.searchengine.SearchOptions;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.SnippetStripper;

/**
 * Web service implementation
 * 
 * @author Matteo Caruso
 * @version $Id:$
 * @since 3.6.0
 */
@WebService(endpointInterface = "com.logicaldoc.webservice.DmsService", serviceName = "DmsService")
public class DmsServiceImpl implements DmsService {

	protected static Log log = LogFactory.getLog(DmsServiceImpl.class);

	/**
	 * @see com.logicaldoc.webservice.DmsService#checkin(java.lang.String,
	 *      java.lang.String, long, java.lang.String, java.lang.String,
	 *      java.lang.String, javax.activation.DataHandler)
	 */
	public String checkin(String username, String password, long id, String filename, String description, String type,
			DataHandler content) throws Exception {
		DocumentDAO ddao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document document = ddao.findByPrimaryKey(id);
		Menu folder = document.getFolder();

		checkCredentials(username, password);
		checkWriteEnable(username, folder.getId());

		if (document.getStatus() == Document.DOC_CHECKED_OUT) {
			// determines the kind of version to create
			Version.VERSION_TYPE versionType;

			if ("release".equals(type)) {
				versionType = Version.VERSION_TYPE.NEW_RELEASE;
			} else if ("subversion".equals(type)) {
				versionType = Version.VERSION_TYPE.NEW_SUBVERSION;
			} else {
				versionType = Version.VERSION_TYPE.OLD_VERSION;
			}

			try {

				// Get file to upload inputStream
				InputStream stream = content.getInputStream();

				// checkin the document; throws an exception if
				// something goes wrong
				DocumentManager documentManager = (DocumentManager) Context.getInstance()
						.getBean(DocumentManager.class);
				documentManager.checkin(document.getId(), stream, filename, username, versionType, description);

				/* create positive log message */
				log.info("Document " + id + " checked in");
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		} else {
			return "document not checked out";
		}

		return "ok";
	}

	/**
	 * @see com.logicaldoc.webservice.DmsService#checkout(java.lang.String,
	 *      java.lang.String, long)
	 */
	public String checkout(String username, String password, long id) throws Exception {
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findByPrimaryKey(id);
		checkCredentials(username, password);
		checkWriteEnable(username, doc.getFolder().getId());
		DocumentManager DocumentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		try {
			DocumentManager.checkout(id, username);
			return "ok";
		} catch (Exception e11) {
			return "error";
		}
	}

	/**
	 * @see com.logicaldoc.webservice.DmsService#createDocument(java.lang.String,
	 *      java.lang.String, int, java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.String, javax.activation.DataHandler)
	 */
	public String createDocument(String username, String password, int parent, String docTitle, String source,
			String sourceDate, String author, String sourceType, String coverage, String language, String keywords,
			String versionDesc, String filename, DataHandler content) throws Exception {

		checkCredentials(username, password);

		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		Menu parentMenu = mdao.findByPrimaryKey(parent);

		if (parentMenu == null) {
			log.error("Menu " + parentMenu + " not found");
			return "error - parent not found";
		}

		checkWriteEnable(username, parent);

		DocumentDAO ddao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Set<String> kwds = ddao.toKeywords(keywords);

		Date date = null;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		if (StringUtils.isNotEmpty(sourceDate))
			date = df.parse(sourceDate);

		// Get file to upload inputStream
		InputStream stream = content.getInputStream();

		DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);

		try {
			Document doc = documentManager.create(stream, filename, parentMenu, username, language, null, date, source,
					author, sourceType, coverage, versionDesc, kwds);
			return String.valueOf(doc.getId());
		} catch (Exception e) {
			return "error";
		} finally {
			stream.close();
		}
	}

	/**
	 * @see com.logicaldoc.webservice.DmsService#createFolder(java.lang.String,
	 *      java.lang.String, java.lang.String, int)
	 */
	public String createFolder(String username, String password, String name, int parent) throws Exception {
		checkCredentials(username, password);

		MenuDAO dao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		Menu parentMenu = dao.findByPrimaryKey(parent);

		checkWriteEnable(username, parent);

		Menu menu = new Menu();
		menu.setText(name);
		menu.setParentId(parent);
		menu.setSort(1);
		menu.setIcon("folder.png");
		menu.setType(Menu.MENUTYPE_DIRECTORY);
		menu.setRef("");
		menu.setMenuGroup(parentMenu.getMenuGroupNames());

		boolean stored = dao.store(menu);
		menu.setPath(parentMenu.getPath() + "/" + menu.getId());
		stored = dao.store(menu);

		if (!stored) {
			log.error("Folder " + name + " not created");
			return "error";
		} else {
			log.info("Created folder " + name);
		}

		return Long.toString(menu.getId());
	}

	/**
	 * @see com.logicaldoc.webservice.DmsService#deleteDocument(java.lang.String,
	 *      java.lang.String, long)
	 */
	public String deleteDocument(String username, String password, long id) throws Exception {
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findByPrimaryKey(id);
		checkCredentials(username, password);
		checkWriteEnable(username, doc.getFolder().getId());
		docDao.delete(id);
		return "ok";
	}

	/**
	 * @see com.logicaldoc.webservice.DmsService#deleteFolder(java.lang.String,
	 *      java.lang.String, int)
	 */
	public String deleteFolder(String username, String password, int folder) throws Exception {
		checkCredentials(username, password);
		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		checkWriteEnable(username, folder);
		try {
			mdao.delete(folder);
			return "ok";
		} catch (Exception e) {
			log.error("Some elements were not deleted");
			return "error";
		}
	}

	/**
	 * @see com.logicaldoc.webservice.DmsService#downloadDocument(java.lang.String,
	 *      java.lang.String, java.lang.String, long, java.lang.String)
	 */
	public DataHandler downloadDocument(String username, String password, long id, String version) throws Exception {
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findByPrimaryKey(id);
		checkCredentials(username, password);
		checkReadEnable(username, doc.getFolder().getId());

		DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		File file = documentManager.getDocumentFile(doc);

		if (!file.exists()) {
			throw new FileNotFoundException(file.getPath());
		}

		log.debug("Attach file " + file.getPath());

		// Now we can append the 'document' attachment to the response
		DataHandler content = new DataHandler(new FileDataSource(file));

		return content;
	}

	/**
	 * @see com.logicaldoc.webservice.DmsService#downloadDocumentInfo(java.lang.String,
	 *      java.lang.String, long)
	 */
	public DocumentInfo downloadDocumentInfo(String username, String password, long id) throws Exception {
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findByPrimaryKey(id);
		checkCredentials(username, password);
		checkReadEnable(username, doc.getFolder().getId());

		// Retrieve the document
		MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);

		// Populate document's metadata
		DocumentInfo info = new DocumentInfo();
		info.setId(doc.getId());
		info.setTitle(doc.getTitle());
		info.setAuthor(doc.getSourceAuthor());
		info.setSourceDate(convertDateToXML(doc.getSourceDate()));
		info.setLanguage(doc.getLanguage());
		info.setFolderId(doc.getFolder().getId());
		info.setFolderName(doc.getFolder().getText());
		info.setSource(doc.getSource());
		info.setType(doc.getType());
		info.setUploadDate(convertDateToXML(doc.getDate()));
		info.setPublisher(doc.getPublisher());
		info.setCoverage(doc.getCoverage());
		info.setFilename(doc.getFileName());

		Set<Version> versions = doc.getVersions();
		for (Version version : versions) {
			VersionInfo vInfo = new VersionInfo();
			vInfo.setDate(convertDateToXML(version.getDate()));
			vInfo.setComment(version.getComment());
			vInfo.setVersion(version.getVersion());
			info.addVersion(vInfo);
		}

		return info;
	}

	/**
	 * @see com.logicaldoc.webservice.DmsService#downloadFolderContent(java.lang.String,
	 *      java.lang.String, int)
	 */
	public FolderContent downloadFolderContent(String username, String password, int folder) throws Exception {

		FolderContent folderContent = new FolderContent();

		checkCredentials(username, password);

		checkReadEnable(username, folder);

		// Retrieve the referenced menu and it's parent populating the folder
		// content
		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		Menu folderMenu = mdao.findByPrimaryKey(folder);
		folderContent.setId(folder);
		folderContent.setName(folderMenu.getText());
		folderContent.setParentId(folderMenu.getParentId());
		Menu parenMenu = mdao.findByPrimaryKey(folderContent.getParentId());
		folderContent.setParentName(parenMenu.getText());

		// Now search for sub-elements
		Collection<Menu> children = mdao.findChildren(folder);
		for (Menu menu : children) {
			Content content = new Content();
			content.setId(menu.getId());
			content.setTitle(menu.getText());
			content.setWriteable(mdao.isReadEnable(menu.getId(), username) ? 1 : 0);
			if (menu.getType() == Menu.MENUTYPE_DIRECTORY)
				folderContent.addFolder(content);
		}

		// TODO Search for documents also

		return folderContent;
	}

	/**
	 * @see com.logicaldoc.webservice.DmsService#search(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String, int)
	 */
	public SearchResult search(String username, String password, String query, String indexLanguage,
			String queryLanguage, int maxHits) throws Exception {

		checkCredentials(username, password);

		SearchResult searchResult = new SearchResult();

		SearchOptions opt = new SearchOptions();
		ArrayList<String> fields = new ArrayList<String>();
		fields.add("content");
		fields.add("keywords");
		fields.add("name");

		String[] flds = (String[]) fields.toArray(new String[fields.size()]);
		opt.setFields(flds);

		ArrayList<String> languages = new ArrayList<String>();
		if (StringUtils.isEmpty(indexLanguage)) {
			Collection<Language> langs = LanguageManager.getInstance().getLanguages();
			for (Language language : langs) {
				languages.add(language.getLanguage());
			}
		} else {
			languages.add(indexLanguage);
		}

		String[] langs = (String[]) languages.toArray(new String[languages.size()]);
		opt.setLanguages(langs);
		opt.setQueryStr(query);
		opt.setUsername(username);
		opt.setFormat("all");

		// Execute the search
		Search lastSearch = new Search(opt, queryLanguage);
		lastSearch.setMaxHits(maxHits);
		List<com.logicaldoc.core.searchengine.Result> tmp = lastSearch.search();

		// Prepares the result array
		ArrayList<Result> result = new ArrayList<Result>();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		for (com.logicaldoc.core.searchengine.Result res : tmp) {
			Result newRes = new Result();
			newRes.setId(res.getDocId());
			newRes.setDate(df.format(res.getDate()));
			newRes.setTitle(res.getTitle());
			newRes.setSummary(SnippetStripper.strip(res.getSummary()));
			newRes.setLength((int) res.getSize());
			newRes.setType(res.getType());
			newRes.setScore(res.getScore());
			result.add(newRes);
		}

		searchResult.setTotalHits(result.size());
		searchResult.setResult(result.toArray(new Result[] {}));
		searchResult.setEstimatedHitsNumber(lastSearch.getEstimatedHitsNumber());
		searchResult.setTime(lastSearch.getExecTime());
		searchResult.setMoreHits(lastSearch.isMoreHitsPresent() ? 1 : 0);

		log.info("User:" + username + " Query:" + query);
		log.info("Results number:" + result.size());

		return searchResult;
	}

	/**
	 * Check provided credentials
	 * 
	 * @param username The username
	 * @param password The password
	 * @throws Exception Raised if the user is not authenticated
	 */
	private void checkCredentials(String username, String password) throws Exception {
		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);

		if (!userDao.validateUser(username, password)) {
			log.error("Invalid credentials " + username + "/" + password);
			throw new Exception("Invalid credentials");
		}
	}

	private void checkWriteEnable(String username, long menuId) throws Exception {
		MenuDAO dao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		if (!dao.isWriteEnable(menuId, username)) {
			log.error("User " + username + " cannot write element " + menuId);
			throw new Exception("The provided user has no write permissions");
		}
	}

	private void checkReadEnable(String username, long menuId) throws Exception {
		MenuDAO dao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		if (!dao.isReadEnable(menuId, username)) {
			log.error("User " + username + " cannot read element " + menuId);
			throw new Exception("The provided user has no read permissions");
		}
	}

	/**
	 * Converts a dateO to a valid XML string
	 */
	protected String convertDateToXML(Date date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(date);
	}

	/**
	 * Parses a comma-separated list of group names checking the existence
	 * 
	 * @param groups The list of comma-separated group names
	 * @return The array of existing groups
	 */
	private String[] parseGroups(String groups) {
		ArrayList<String> array = new ArrayList<String>();
		GroupDAO gdao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);

		StringTokenizer st = new StringTokenizer(groups, ",", false);
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (gdao.findByPrimaryKey(token) != null)
				array.add(token);
		}
		return array.toArray(new String[] {});
	}
}