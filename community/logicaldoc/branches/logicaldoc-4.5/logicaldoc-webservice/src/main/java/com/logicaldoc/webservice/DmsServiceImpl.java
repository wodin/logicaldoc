package com.logicaldoc.webservice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.DocumentTemplate;
import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.DocumentTemplateDAO;
import com.logicaldoc.core.document.dao.VersionDAO;
import com.logicaldoc.core.i18n.LanguageManager;
import com.logicaldoc.core.searchengine.LuceneDocument;
import com.logicaldoc.core.searchengine.Search;
import com.logicaldoc.core.searchengine.SearchOptions;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.LocaleUtil;
import com.logicaldoc.util.SnippetStripper;
import com.logicaldoc.util.TagUtil;

/**
 * Web service implementation
 * 
 * @author Matteo Caruso - Logical Objects
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
		UserDAO udao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		User user = udao.findByUserName(username);
		if (user == null) {
			log.error("User " + username + " not found");
			throw new Exception("error - user not found");
		}

		DocumentDAO ddao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document document = ddao.findById(id);
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
				documentManager.checkin(document.getId(), stream, filename, user, versionType, description, false);

				/* create positive log message */
				log.info("Document " + id + " checked in");
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw new Exception(e);
			}
		} else {
			throw new Exception("document not checked out");
		}

		return "ok";
	}

	/**
	 * @see com.logicaldoc.webservice.DmsService#checkout(java.lang.String,
	 *      java.lang.String, long)
	 */
	public String checkout(String username, String password, long id) throws Exception {
		UserDAO udao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		User user = udao.findByUserName(username);
		if (user == null) {
			log.error("User " + username + " not found");
			throw new Exception("error - user not found");
		}

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findById(id);
		checkCredentials(username, password);
		checkWriteEnable(username, doc.getFolder().getId());
		DocumentManager DocumentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		DocumentManager.checkout(id, user);
		return "ok";
	}

	/**
	 * @see com.logicaldoc.webservice.DmsService#createDocument(java.lang.String,
	 *      java.lang.String, long, java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.String, javax.activation.DataHandler,
	 *      java.lang.String, com.logicaldoc.webservice.ExtendedAttribute[],
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String)
	 */
	public String createDocument(String username, String password, long folderId, String docTitle, String source,
			String sourceDate, String author, String sourceType, String coverage, String language, String tags,
			String versionDesc, String filename, DataHandler content, String templateName,
			ExtendedAttribute[] extendedAttributes, String sourceId, String object, String recipient, String customId)
			throws Exception {

		checkCredentials(username, password);

		UserDAO udao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		User user = udao.findByUserName(username);
		if (user == null) {
			log.error("User " + username + " not found");
			throw new Exception("error - user not found");
		}

		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		Menu folder = mdao.findById(folderId);
		if (folder == null) {
			log.error("Menu " + folder + " not found");
			throw new Exception("error - folder not found");
		}

		checkWriteEnable(username, folderId);

		Set<String> tgs = TagUtil.extractTags(tags);

		Date date = null;
		if (StringUtils.isNotEmpty(sourceDate))
			date = convertXMLToDate(sourceDate);

		DocumentTemplate template = null;
		Map<String, String> attributes = null;
		if (StringUtils.isNotEmpty(templateName)) {
			DocumentTemplateDAO templDao = (DocumentTemplateDAO) Context.getInstance().getBean(
					DocumentTemplateDAO.class);
			template = templDao.findByName(templateName);
			if (template != null) {
				if (extendedAttributes != null && extendedAttributes.length > 0) {
					attributes = new HashMap<String, String>();
					for (int i = 0; i < extendedAttributes.length; i++) {
						attributes.put(extendedAttributes[i].getName(), extendedAttributes[i].getValue());
					}
				}
			}
		}

		// Get file to upload inputStream
		InputStream stream = content.getInputStream();

		DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);

		try {
			Document doc = documentManager.create(stream, filename, folder, user, LocaleUtil.toLocale(language), null,
					date, source, author, sourceType, coverage, versionDesc, tgs, template != null ? template.getId()
							: null, attributes, sourceId, object, recipient, customId, false);
			return String.valueOf(doc.getId());
		} finally {
			stream.close();
		}
	}

	/**
	 * @see com.logicaldoc.webservice.DmsService#createFolder(java.lang.String,
	 *      java.lang.String, java.lang.String, long)
	 */
	public String createFolder(String username, String password, String name, long parent) throws Exception {
		checkCredentials(username, password);

		MenuDAO dao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		Menu parentMenu = dao.findById(parent);

		checkWriteEnable(username, parent);

		Menu menu = new Menu();
		menu.setText(name);
		menu.setParentId(parent);
		menu.setSort(1);
		menu.setIcon("folder.png");
		menu.setType(Menu.MENUTYPE_DIRECTORY);
		menu.setRef("");
		menu.setMenuGroup(parentMenu.getMenuGroupIds());

		boolean stored = dao.store(menu);
		menu.setPath(parentMenu.getPath() + "/" + parentMenu.getId());
		stored = dao.store(menu);

		if (!stored) {
			log.error("Folder " + name + " not created");
			throw new Exception("error");
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
		Document doc = docDao.findById(id);
		checkCredentials(username, password);
		checkWriteEnable(username, doc.getFolder().getId());
		docDao.delete(id);
		return "ok";
	}

	/**
	 * @see com.logicaldoc.webservice.DmsService#deleteFolder(java.lang.String,
	 *      java.lang.String, long)
	 */
	public String deleteFolder(String username, String password, long folder) throws Exception {
		checkCredentials(username, password);
		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		checkWriteEnable(username, folder);
		try {
			mdao.delete(folder);
			return "ok";
		} catch (Exception e) {
			log.error("Some elements were not deleted");
			throw new Exception("error");
		}
	}

	/**
	 * @see com.logicaldoc.webservice.DmsService#downloadDocument(java.lang.String,
	 *      java.lang.String, java.lang.String, long, java.lang.String)
	 */
	public DataHandler downloadDocument(String username, String password, long id, String version) throws Exception {
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findById(id);
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
		Document doc = docDao.findById(id);
		docDao.initialize(doc);
		checkCredentials(username, password);
		checkReadEnable(username, doc.getFolder().getId());

		// Populate document's metadata
		DocumentInfo info = new DocumentInfo();
		try {
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
			info.setPublisher(doc.getCreator());
			info.setCoverage(doc.getCoverage());
			info.setFilename(doc.getFileName());
			info.setCustomId(doc.getCustomId());
			info.setSourceId(doc.getSourceId());
			info.setObject(doc.getObject());

			if (doc.getTags() != null) {
				Set<String> tmpset = doc.getTags();
				String[] mytags = (String[]) tmpset.toArray(new String[tmpset.size()]);
				info.setTags(mytags);
			}

			if (doc.getTemplate() != null) {
				// Insert template infos
				info.setTemplateName(doc.getTemplate().getName());
				info.setTemplateId(doc.getTemplate().getId());

				// Populate extended attributes
				ExtendedAttribute[] extendedAttributes = new ExtendedAttribute[doc.getAttributes().size()];
				int i = 0;
				for (String name : doc.getAttributeNames()) {
					extendedAttributes[i++] = new ExtendedAttribute(name, doc.getValue(name));
				}
				info.setExtendedAttribute(extendedAttributes);
			}

			VersionDAO vdao = (VersionDAO) Context.getInstance().getBean(VersionDAO.class);
			List<Version> versions = vdao.findByDocId(id);
			for (Version version : versions) {
				VersionInfo vInfo = new VersionInfo();
				vInfo.setDate(convertDateToXML(version.getDate()));
				vInfo.setComment(version.getComment());
				vInfo.setVersion(version.getVersion());
				info.addVersion(vInfo);
			}
		} catch (RuntimeException re) {
			log.error("RuntimeException: " + re.getMessage(), re);
			throw new Exception(re);
		} catch (Exception e) {
			log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}

		return info;
	}

	/**
	 * @see com.logicaldoc.webservice.DmsService#downloadFolderContent(java.lang.String,
	 *      java.lang.String, long)
	 */
	public FolderContent downloadFolderContent(String username, String password, long folder) throws Exception {

		FolderContent folderContent = new FolderContent();
		checkCredentials(username, password);
		checkReadEnable(username, folder);

		// Retrieve the referenced menu and it's parent populating the folder
		// content
		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		Menu folderMenu = mdao.findById(folder);
		folderContent.setId(folder);
		folderContent.setName(folderMenu.getText());
		folderContent.setParentId(folderMenu.getParentId());
		Menu parenMenu = mdao.findById(folderContent.getParentId());
		folderContent.setParentName(parenMenu.getText());

		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		User user = userDao.findByUserName(username);

		// Now search for sub-elements
		Collection<Menu> children = mdao.findByUserId(user.getId(), folder, Menu.MENUTYPE_DIRECTORY);
		for (Menu menu : children) {
			Content content = new Content();
			content.setId(menu.getId());
			content.setTitle(menu.getText());
			content.setWriteable(mdao.isWriteEnable(menu.getId(), user.getId()) ? 1 : 0);
			folderContent.addFolder(content);
		}

		boolean writeEnable = mdao.isReadEnable(folder, user.getId());
		DocumentDAO docdao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Collection<Document> docs = docdao.findByFolder(folder);
		for (Document document : docs) {
			Content content = new Content();
			content.setId(document.getId());
			content.setTitle(document.getTitle());
			content.setWriteable(writeEnable ? 1 : 0);
			folderContent.addDocument(content);
		}

		return folderContent;
	}

	/**
	 * 
	 * @see com.logicaldoc.webservice.DmsService#search(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String, int, java.lang.String, java.lang.String[])
	 */
	public SearchResult search(String username, String password, String query, String indexLanguage,
			String queryLanguage, int maxHits, String templateName, String[] templateFields) throws Exception {

		UserDAO udao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		User user = udao.findByUserName(username);
		if (user == null) {
			log.error("User " + username + " not found");
			return null;
		}

		checkCredentials(username, password);
		SearchResult searchResult = new SearchResult();

		SearchOptions opt = new SearchOptions();
		ArrayList<String> fields = new ArrayList<String>();
		fields.add(LuceneDocument.FIELD_CONTENT);
		fields.add(LuceneDocument.FIELD_TAGS);
		fields.add(LuceneDocument.FIELD_TITLE);

		if (StringUtils.isNotEmpty(templateName)) {
			DocumentTemplateDAO templDao = (DocumentTemplateDAO) Context.getInstance().getBean(
					DocumentTemplateDAO.class);
			DocumentTemplate template = templDao.findByName(templateName);
			if (template != null) {
				opt.setTemplate(template.getId());
				for (String attr : template.getAttributes()) {
					if (templateFields != null && templateFields.length > 0) {
						for (int i = 0; i < templateFields.length; i++) {
							if (attr.equals(templateFields[i])) {
								fields.add("ext_" + templateFields[i]);
								break;
							}
						}
					}
				}
			}
		}

		String[] flds = (String[]) fields.toArray(new String[fields.size()]);
		opt.setFields(flds);

		ArrayList<String> languages = new ArrayList<String>();
		if (StringUtils.isEmpty(indexLanguage)) {
			// Collection<Language> langs =
			// LanguageManager.getInstance().getLanguages();
			// for (Language language : langs) {
			// languages.add(language.getLanguage());
			// }
			List<String> langs = LanguageManager.getInstance().getLanguagesAsString();
			languages.addAll(langs);
		} else {
			languages.add(indexLanguage);
		}

		String[] langs = (String[]) languages.toArray(new String[languages.size()]);
		opt.setLanguages(langs);
		opt.setQueryStr(query);
		opt.setUserId(user.getId());
		opt.setFormat("all");

		// Execute the search
		Search lastSearch = new Search(opt, LocaleUtil.toLocale(queryLanguage));
		lastSearch.setMaxHits(maxHits);
		List<com.logicaldoc.core.searchengine.Result> tmp = lastSearch.search();

		// Prepares the result array
		ArrayList<Result> result = new ArrayList<Result>();
		for (com.logicaldoc.core.searchengine.Result res : tmp) {
			Result newRes = new Result();
			newRes.setId(res.getDocId());
			newRes.setDate(convertDateToXML(res.getDate()));
			newRes.setTitle(res.getTitle());
			newRes.setSummary(SnippetStripper.strip(res.getSummary()));
			newRes.setSize(res.getSize());
			newRes.setType(res.getType());
			newRes.setScore(res.getScore());
			newRes.setCustomId(res.getCustomId());
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

	private void checkWriteEnable(String username, long folderId) throws Exception {
		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		User user = userDao.findByUserName(username);
		if (user == null)
			throw new Exception("The provided user has no read permissions");

		MenuDAO dao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		if (!dao.isWriteEnable(folderId, user.getId())) {
			log.error("User " + username + " cannot write element " + folderId);
			throw new Exception("The provided user has no write permissions");
		}
	}

	private void checkReadEnable(String username, long folderId) throws Exception {
		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		User user = userDao.findByUserName(username);
		if (user == null)
			throw new Exception("The provided user has no read permissions");

		MenuDAO dao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		if (!dao.isReadEnable(folderId, user.getId())) {
			log.error("User " + username + " cannot read element " + folderId);
			throw new Exception("The provided user has no read permissions");
		}
	}

	/**
	 * Converts a date to a valid XML string
	 */
	protected String convertDateToXML(Date date) {
		if (date == null)
			return null;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(date);
	}

	protected Date convertXMLToDate(String date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return df.parse(date);
		} catch (ParseException e) {
			df = new SimpleDateFormat("yyyy-MM-dd");
			try {
				return df.parse(date);
			} catch (ParseException e1) {
			}
		}
		return null;
	}

	@Override
	public String renameFolder(String username, String password, long folder, String name) throws Exception {
		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		User user = userDao.findByUserName(username);
		if (user == null)
			throw new Exception("user " + username + "not found");

		MenuDAO dao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		if (!dao.isPermissionEnabled(Permission.RENAME, folder, user.getId())) {
			throw new Exception("user does't have rename permission");
		}

		if (folder == Menu.MENUID_DOCUMENTS)
			throw new Exception("cannot rename the root folder");

		Menu menu = dao.findById(folder);
		if (menu == null)
			throw new Exception("cannot find folder " + folder);

		if (dao.findByMenuTextAndParentId(name, menu.getParentId()).size() > 0) {
			throw new Exception("duplicate folder name " + name);
		} else {
			menu.setText(name);
			dao.store(menu);
			return String.valueOf(menu.getId());
		}
	}

	@Override
	public String update(String username, String password, long id, String title, String source, String sourceAuthor,
			String sourceDate, String sourceType, String coverage, String language, String[] tags, String sourceId,
			String object, String recipient, Long templateId, @WebParam(name = "extendedAttribute")
			ExtendedAttribute[] extendedAttribute) throws Exception {

		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		User user = userDao.findByUserName(username);
		if (user == null)
			throw new Exception("user " + username + "not found");

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findById(id);
		if (doc == null)
			throw new Exception("unexisting document " + id);
		if (doc.getImmutable() == 1)
			throw new Exception("the document is immutable");

		// Initialize the lazy loaded collections
		docDao.initialize(doc);

		Date sdate = null;
		if (StringUtils.isNotEmpty(sourceDate))
			sdate = convertXMLToDate(sourceDate);
		doc.setSourceDate(sdate);

		MenuDAO dao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		if (!dao.isWriteEnable(doc.getFolder().getId(), user.getId())) {
			throw new Exception("user does't have write permission");
		}

		Map<String, String> attributes = new HashMap<String, String>();
		for (int i = 0; extendedAttribute != null && i < extendedAttribute.length; i++) {
			attributes.put(extendedAttribute[i].getName(), extendedAttribute[i].getValue());
		}

		DocumentManager manager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		Set<String> setTags = new TreeSet<String>();
		if (tags != null) {
			for (int i = 0; i < tags.length; i++) {
				setTags.add(tags[i]);
			}
		}

		manager.update(doc, user, title, source, sourceAuthor, sdate, sourceType, coverage, LocaleUtil
				.toLocale(language), setTags, sourceId, object, recipient, templateId, attributes);
		return Long.toString(doc.getId());
	}
}