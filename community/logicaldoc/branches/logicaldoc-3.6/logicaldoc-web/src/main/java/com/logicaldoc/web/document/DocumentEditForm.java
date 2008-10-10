package com.logicaldoc.web.document;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.i18n.DateBean;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.MenuGroup;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.SettingsConfig;

import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.navigation.PageContentBean;
import com.logicaldoc.web.upload.InputFileBean;
import com.logicaldoc.web.util.FacesUtil;

/**
 * Base form for document editing
 * 
 * @author Marco Meschieri
 * @version $Id: DocumentEditForm.java,v 1.9 2006/09/03 16:24:37 marco Exp $
 * @since 3.0
 */
public class DocumentEditForm {
	protected static Log log = LogFactory.getLog(DocumentEditForm.class);

	private String docName;

	private int menuParent;

	private int menuSort = 0;

	private String source;

	private String sourceAuthor;

	private Date sourceDate;

	private Date docDate;

	private String sourceType;

	private String coverage;

	private String language;

	private String keywords;

	private String versionDesc;

	private String filename;

	private Menu menu;

	private String[] menuGroup;

	private DocumentRecord record;

	private boolean readOnly = false;

	private DocumentNavigation documentNavigation;

	public DocumentEditForm() {
		reset();
	}

	public void reset() {
		docName = "";
		menuParent = -1;
		menuSort = 0;
		source = "";
		sourceAuthor = "";
		sourceDate = null;
		docDate = new Date();
		sourceType = "";
		coverage = "";
		language = "";
		keywords = "";
		versionDesc = "";
		filename = "";
		menu = null;
		menuGroup = null;
	}

	public void init(DocumentRecord record) {
		this.record = record;
		this.menu = record.getMenu();

		Document doc = record.getDocument();
		setDocName(doc.getDocName());

		setMenuParent(doc.getMenu().getMenuParent());
		setSource(doc.getSource());
		if (StringUtils.isEmpty(doc.getSource())) {
			SettingsConfig settings = (SettingsConfig) Context.getInstance().getBean(SettingsConfig.class);
			setSource(settings.getValue("defaultSource"));
		}
		setSourceAuthor(doc.getSourceAuthor());

		if (StringUtils.isNotEmpty(doc.getSourceDate())) {
			setSourceDate(DateBean.dateFromCompactString(doc.getSourceDate()));
		}

		if (StringUtils.isNotEmpty(doc.getDocDate())) {
			setDocDate(DateBean.dateFromCompactString(doc.getDocDate()));
		}

		setLanguage(doc.getLanguage());
		setKeywords(doc.getKeywordsString());
		setCoverage(doc.getCoverage());
		setSourceType(doc.getSourceType());

		String[] groupNames = record.getMenu().getMenuGroupNames();
		setMenuGroup(groupNames);
	}

	/**
	 * @return Returns the docName.
	 */
	public String getDocName() {
		return docName;
	}

	/**
	 * @return Returns the menuParent.
	 */
	public int getMenuParent() {
		return menuParent;
	}

	/**
	 * @return Returns the menuSort.
	 */
	public int getMenuSort() {
		return menuSort;
	}

	/**
	 * @return Returns the source.
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @return Returns the sourceAuthor.
	 */
	public String getSourceAuthor() {
		return sourceAuthor;
	}

	/**
	 * @return Returns the sourceDate.
	 */
	public Date getSourceDate() {
		return sourceDate;
	}

	/**
	 * @return Returns the sourceType.
	 */
	public String getSourceType() {
		return sourceType;
	}

	/**
	 * @return Returns the coverage.
	 */
	public String getCoverage() {
		return coverage;
	}

	/**
	 * @return Returns the language.
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @return Returns the keywords.
	 */
	public String getKeywords() {
		return keywords;
	}

	/**
	 * @return Returns the versionDesc.
	 */
	public String getVersionDesc() {
		return versionDesc;
	}

	/**
	 * @return Returns the menuGroup.
	 */
	public String[] getMenuGroup() {
		return menuGroup;
	}

	/**
	 * @param docName The docName to set.
	 */
	public void setDocName(String name) {
		docName = name;
	}

	/**
	 * @param menuParent The menuParent to set.
	 */
	public void setMenuParent(int parent) {
		menuParent = parent;
	}

	/**
	 * @param menuSort The menuSort to set.
	 */
	public void setMenuSort(int sort) {
		menuSort = sort;
	}

	/**
	 * @param source The source to set.
	 */
	public void setSource(String src) {
		source = src;
	}

	/**
	 * @param sourceAuthor The sourceAuthor to set.
	 */
	public void setSourceAuthor(String author) {
		sourceAuthor = author;
	}

	/**
	 * @param sourceDate The sourceDate to set.
	 */
	public void setSourceDate(Date date) {
		sourceDate = date;
	}

	/**
	 * @param sourceType The sourceType to set.
	 */
	public void setSourceType(String type) {
		sourceType = type;
	}

	/**
	 * @param coverage The coverage to set.
	 */
	public void setCoverage(String cover) {
		coverage = cover;
	}

	/**
	 * @param language The language to set.
	 */
	public void setLanguage(String lang) {
		language = lang;
	}

	/**
	 * @param keywords The keywords to set.
	 */
	public void setKeywords(String words) {
		keywords = words;
	}

	/**
	 * @return Returns the filename.
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename The filename to set.
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * @return Returns the menu.
	 */
	public Menu getMenu() {
		return menu;
	}

	/**
	 * @param menu The menu to set.
	 */
	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	/**
	 * @param versionDesc The versionDesc to set.
	 */
	public void setVersionDesc(String desc) {
		versionDesc = desc;
	}

	/**
	 * @param menuGroups The menuGroup to set.
	 */
	public void setMenuGroup(String[] group) {
		menuGroup = group;
	}

	public Date getDocDate() {
		return docDate;
	}

	public void setDocDate(Date docDate) {
		this.docDate = docDate;
	}

	public String toString() {
		return (new ReflectionToStringBuilder(this) {
			protected boolean accept(java.lang.reflect.Field f) {
				return super.accept(f);
			}
		}).toString();
	}

	/**
	 * Saves data into a new Document. Saves the information provided in the
	 * document form. That also includes updating the search index for example.
	 * This method is invoked in the document's upload wizard
	 */
	public String save() {
		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		String username = SessionManagement.getUsername();
		Menu parent = documentNavigation.getSelectedDir().getMenu();
		if (SessionManagement.isValid() && mdao.isWriteEnable(parent.getMenuId(), username)) {
			try {
				InputFileBean inputFile = ((InputFileBean) FacesUtil.accessBeanFromFacesContext("inputFile",
						FacesContext.getCurrentInstance(), log));
				File file = inputFile.getFile();
				String filename = inputFile.getFileName();
				String name = getDocName();
				if (StringUtils.isEmpty(name)) {
					name = filename.substring(0, filename.lastIndexOf("."));
				}

				String[] docGroup = getMenuGroup();
				Set<MenuGroup> groups = new HashSet<MenuGroup>();
				for (int i = 0; i < docGroup.length; i++) {
					MenuGroup mg = new MenuGroup();
					mg.setGroupName(docGroup[i]);
					mg.setWriteEnable(1);
					groups.add(mg);
				}
				DocumentDAO ddao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
				Set<String> kwds = ddao.toKeywords(keywords);

				DocumentManager documentManager = (DocumentManager) Context.getInstance()
						.getBean(DocumentManager.class);
				Document doc = documentManager.create(file, parent, username, language, name, getSourceDate(), source,
						sourceAuthor, sourceType, coverage, versionDesc, kwds, groups);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				Messages.addMessage(FacesMessage.SEVERITY_ERROR, "errors.action.savedoc", "errors.action.savedoc");
			} finally {
				reset();
			}

			return null;
		} else {
			return "login";
		}
	}

	/**
	 * Updates data into a Document. Saves the information provided in the
	 * document form. That also includes updating the search index for example.
	 * This method is invoked for document's editing
	 */
	public String update() {
		DocumentNavigation navigation = ((DocumentNavigation) FacesUtil.accessBeanFromFacesContext(
				"documentNavigation", FacesContext.getCurrentInstance(), log));
		DocumentDAO ddao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);

		if (SessionManagement.isValid()) {
			try {
				Document doc = record.getDocument();
				String username = SessionManagement.getUsername();
				Set<String> keywords = ddao.toKeywords(getKeywords());
				documentManager.update(doc, username, docName, source, sourceAuthor, sourceDate, sourceType, coverage,
						language, keywords);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				Messages.addError(e.getMessage());
			} finally {
				reset();
			}
			navigation.setSelectedPanel(new PageContentBean("documents"));
			navigation.refresh();
			return null;
		} else {
			return "login";
		}
	}

	/**
	 * Executes a document's checkin creating a new version
	 */
	public String checkin() {
		Application application = FacesContext.getCurrentInstance().getApplication();
		InputFileBean fileForm = ((InputFileBean) application.createValueBinding("#{inputFile}").getValue(
				FacesContext.getCurrentInstance()));
		if (SessionManagement.isValid()) {
			String username = SessionManagement.getUsername();
			String versionDesc = fileForm.getDescription();
			Document document = record.getDocument();
			File file = fileForm.getFile();

			if (document.getDocStatus() == Document.DOC_CHECKED_OUT) {
				if (file != null) {
					// check that we have a valid file for storing as new
					// version
					String fileName = fileForm.getFileName();
					String type = fileForm.getVersionType();

					// determines the kind of version to create
					Version.VERSION_TYPE versionType;

					if (type.equals("release")) {
						versionType = Version.VERSION_TYPE.NEW_RELEASE;
					} else if (type.equals("subversion")) {
						versionType = Version.VERSION_TYPE.NEW_SUBVERSION;
					} else {
						versionType = Version.VERSION_TYPE.OLD_VERSION;
					}

					try {
						// checkin the document; throws an exception if
						// something goes wrong
						DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(
								DocumentManager.class);
						documentManager.checkin(document.getDocId(), new FileInputStream(file), fileName, username,
								versionType, versionDesc);

						/* create positive log message */
						Messages.addLocalizedInfo("msg.action.savedoc");
						fileForm.reset();
					} catch (Exception e) {
						log.error(e.getMessage(), e);
						Messages.addLocalizedError("errors.action.savedoc");
					}
				} else {
					Messages.addLocalizedError("errors.nofile");
				}
			}
			reset();
		} else {
			return "login";
		}

		DocumentNavigation documentNavigation = ((DocumentNavigation) application.createValueBinding(
				"#{documentNavigation}").getValue(FacesContext.getCurrentInstance()));
		documentNavigation.setSelectedPanel(new PageContentBean("documents"));
		documentNavigation.refresh();
		return null;
	}

	public DocumentRecord getRecord() {
		return record;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public void setDocumentNavigation(DocumentNavigation documentNavigation) {
		this.documentNavigation = documentNavigation;
	}
}