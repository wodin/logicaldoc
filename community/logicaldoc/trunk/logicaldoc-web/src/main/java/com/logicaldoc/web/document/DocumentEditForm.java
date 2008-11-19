package com.logicaldoc.web.document;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icesoft.faces.context.effects.JavascriptContext;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.DocumentTemplate;
import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.DocumentTemplateDAO;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.SettingsConfig;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.navigation.PageContentBean;
import com.logicaldoc.web.upload.InputFileBean;
import com.logicaldoc.web.util.Attribute;
import com.logicaldoc.web.util.FacesUtil;

/**
 * Base form for document editing
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class DocumentEditForm {
	protected static Log log = LogFactory.getLog(DocumentEditForm.class);

	private String title;

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

	private DocumentRecord record;

	private boolean readOnly = false;

	private DocumentNavigation documentNavigation;

	private Long template = null;

	private Collection<Attribute> extendedAttributes = new ArrayList<Attribute>();

	public DocumentEditForm() {
		reset();
	}

	public void reset() {
		title = "";
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
		template = null;
		extendedAttributes.clear();
	}

	public void init(DocumentRecord record) {
		this.record = record;
		record.initCollections();
		Document doc = record.getDocument();
		setTitle(doc.getTitle());
		setSource(doc.getSource());
		if (StringUtils.isEmpty(doc.getSource())) {
			SettingsConfig settings = (SettingsConfig) Context.getInstance().getBean(SettingsConfig.class);
			setSource(settings.getValue("defaultSource"));
		}
		setSourceAuthor(doc.getSourceAuthor());
		setSourceDate(doc.getSourceDate());
		setDocDate(doc.getDate());
		setLanguage(doc.getLanguage());
		setKeywords(doc.getKeywordsString());
		setCoverage(doc.getCoverage());
		setSourceType(doc.getSourceType());
		setFilename(doc.getFileName());
		initTemplate();
	}

	private void initTemplate() {
		extendedAttributes.clear();
		DocumentTemplate templt = null;
		if (record != null) {
			Document doc = record.getDocument();
			DocumentDAO dao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			dao.initialize(doc);
			templt = doc.getTemplate();
			if (templt != null) {
				template = templt.getId();
				if (templt != null)
					for (String attrName : templt.getAttributes()) {
						extendedAttributes.add(new Attribute(attrName, doc.getValue(attrName)));
					}
			}
		} else {
			if (template != null) {
				DocumentTemplateDAO dao = (DocumentTemplateDAO) Context.getInstance()
						.getBean(DocumentTemplateDAO.class);
				templt = dao.findById(template.longValue());
				for (String attrName : templt.getAttributes()) {
					extendedAttributes.add(new Attribute(attrName, ""));
				}
			}
		}

	}

	public int getExtendedAttributesCount() {
		if (record == null) {
			if (template != null) {
				DocumentTemplateDAO tdao = (DocumentTemplateDAO) Context.getInstance().getBean(
						DocumentTemplateDAO.class);
				DocumentTemplate buf = tdao.findById(template.longValue());
				return buf.getAttributes().size();
			} else
				return 0;
		} else {
			Document doc = record.getDocument();
			if (doc.getTemplate() != null) {
				return doc.getTemplate().getAttributes().size();
			} else {
				return 0;
			}
		}
	}

	public void changeTemplate(ValueChangeEvent event) {
		Long item = (Long) event.getNewValue();
		if (item != null) {
			DocumentTemplateDAO tdao = (DocumentTemplateDAO) Context.getInstance().getBean(DocumentTemplateDAO.class);
			template = item;
			DocumentTemplate buf = tdao.findById(template.longValue());
			if (record != null)
				record.getDocument().setTemplate(buf);
		} else {
			if (record != null)
				record.getDocument().setTemplate(null);
		}
		initTemplate();
		JavascriptContext.addJavascriptCall(FacesContext.getCurrentInstance(), "cleanAttributes();");
	}

	public Collection<Attribute> getExtendedAttributes() {
		return extendedAttributes;
	}

	public Long getTemplate() {
		return template;
	}

	public void setTemplate(Long template) {
		this.template = template;
	}

	/**
	 * @return Returns the title.
	 */
	public String getTitle() {
		return title;
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
	 * @param title The title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
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
	 * @param versionDesc The versionDesc to set.
	 */
	public void setVersionDesc(String desc) {
		versionDesc = desc;
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
		long userId = SessionManagement.getUserId();
		Menu folder = documentNavigation.getSelectedDir().getMenu();
		if (SessionManagement.isValid() && mdao.isWriteEnable(folder.getId(), userId)) {
			try {
				InputFileBean inputFile = ((InputFileBean) FacesUtil.accessBeanFromFacesContext("inputFile",
						FacesContext.getCurrentInstance(), log));
				File file = inputFile.getFile();
				String filename = inputFile.getFileName();
				String title = getTitle();
				if (StringUtils.isEmpty(title)) {
					title = filename.substring(0, filename.lastIndexOf("."));
				}

				DocumentDAO ddao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
				Set<String> kwds = ddao.toKeywords(keywords);

				DocumentManager documentManager = (DocumentManager) Context.getInstance()
						.getBean(DocumentManager.class);

				Map<String, String> attrs = new HashMap<String, String>();
				for (Attribute att : extendedAttributes) {
					if (StringUtils.isNotEmpty(att.getValue()))
						attrs.put(att.getName(), att.getValue());
				}

				documentManager.create(file, folder, SessionManagement.getUser(), language, title, getSourceDate(),
						source, sourceAuthor, sourceType, coverage, versionDesc, kwds, template, attrs);
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
		DocumentTemplateDAO tdao = (DocumentTemplateDAO) Context.getInstance().getBean(DocumentTemplateDAO.class);

		if (SessionManagement.isValid()) {
			try {
				Document doc = record.getDocument();

				doc.getAttributes().clear();
				if (template != null) {
					doc.setTemplate(tdao.findById(template));
					for (Attribute attribute : extendedAttributes) {
						if (StringUtils.isNotEmpty(attribute.getValue()))
							doc.setValue(attribute.getName(), attribute.getValue());
					}
				} else {
					doc.setTemplate(null);
				}

				User user = SessionManagement.getUser();
				Set<String> keywords = ddao.toKeywords(getKeywords());

				documentManager.update(doc, user, title, source, sourceAuthor, sourceDate, sourceType, coverage,
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
	@SuppressWarnings("deprecation")
	public String checkin() {
		Application application = FacesContext.getCurrentInstance().getApplication();
		InputFileBean fileForm = ((InputFileBean) application.createValueBinding("#{inputFile}").getValue(
				FacesContext.getCurrentInstance()));
		if (SessionManagement.isValid()) {
			String versionDesc = fileForm.getDescription();
			Document document = record.getDocument();
			File file = fileForm.getFile();

			if (document.getStatus() == Document.DOC_CHECKED_OUT) {
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
						documentManager.checkin(document.getId(), new FileInputStream(file), fileName,
								SessionManagement.getUser(), versionType, versionDesc);

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