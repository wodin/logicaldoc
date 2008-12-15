package com.logicaldoc.web.document;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.faces.application.Application;
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
import com.logicaldoc.util.CharsetDetector;
import com.logicaldoc.util.Context;
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

	private String sourceId;

	private String object;

	private DocumentRecord record;

	private boolean readOnly = false;

	private DocumentNavigation documentNavigation;

	private Long template = null;

	private boolean immediateIndexing = false;

	private boolean majorUpdate = false;

	private boolean checkOriginalFilename = true;

	private Collection<Attribute> extendedAttributes = new ArrayList<Attribute>();

	public DocumentEditForm() {
		reset();
	}

	public boolean isImmediateIndexing() {
		return immediateIndexing;
	}

	public void setImmediateIndexing(boolean immediateIndexing) {
		this.immediateIndexing = immediateIndexing;
	}

	public boolean isMajorUpdate() {
		return majorUpdate;
	}

	public void setMajorUpdate(boolean majorUpdate) {
		this.majorUpdate = majorUpdate;
	}

	public boolean isCheckOriginalFilename() {
		return checkOriginalFilename;
	}

	public void setCheckOriginalFilename(boolean checkOriginalFilename) {
		this.checkOriginalFilename = checkOriginalFilename;
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
		immediateIndexing = false;
		this.majorUpdate = false;
		this.checkOriginalFilename = true;
		extendedAttributes.clear();
	}

	public void init(DocumentRecord record) {
		this.record = record;
		record.initCollections();
		Document doc = record.getDocument();
		setTitle(doc.getTitle());
		setSource(doc.getSource());
		setSourceAuthor(doc.getSourceAuthor());
		setSourceDate(doc.getSourceDate());
		setDocDate(doc.getDate());
		setLanguage(doc.getLanguage());
		setKeywords(doc.getKeywordsString());
		setCoverage(doc.getCoverage());
		setSourceType(doc.getSourceType());
		setFilename(doc.getFileName());
		setObject(doc.getObject());
		setSourceId(doc.getSourceId());
		initTemplate();
	}

	private void initTemplate() {
		extendedAttributes.clear();
		DocumentTemplate templt = null;
		if (record != null) {
			Document doc = record.getDocument();
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

				Document doc = documentManager.create(file, folder, SessionManagement.getUser(), language, title,
						getSourceDate(), source, sourceAuthor, sourceType, coverage, versionDesc, kwds, template,
						attrs, sourceId, object, immediateIndexing);
				if (StringUtils.isNotEmpty(doc.getCustomId()))
					Messages.addInfo(Messages.getMessage("document.inserted", doc.getCustomId()));
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				Messages.addLocalizedError("errors.action.savedoc");
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
						language, keywords, sourceId, object);
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

	@SuppressWarnings("deprecation")
	public String uncheckout() {

		log.info("uncheckout()");

		Application application = FacesContext.getCurrentInstance().getApplication();
		InputFileBean fileForm = ((InputFileBean) application.createValueBinding("#{inputFile}").getValue(
				FacesContext.getCurrentInstance()));
		if (SessionManagement.isValid()) {
			Document document = record.getDocument();

			if (document.getStatus() == Document.DOC_CHECKED_OUT) {

				try {
					// Unchekout the document; throws an exception if something
					// goes wrong
					DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(
							DocumentManager.class);
					documentManager.uncheckout(document.getId(), SessionManagement.getUser());

					/* create positive log message */
					Messages.addLocalizedInfo("msg.action.changedoc");
					fileForm.reset();
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					Messages.addLocalizedError("errors.action.savedoc");
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

					// if checkOriginalFileName is selected verify that the
					// uploaded file has correct fileName
					if (isCheckOriginalFilename()) {

						if (!CharsetDetector.convert(fileName).equals(document.getFileName())) {
							log.info("Filename of the checked-in document(" + fileName
									+ ") is different from the original filename (" + document.getFileName() + ")");

							String localizedMessage = Messages.getMessage("checkin.originalfilename", document
									.getFileName());
							Messages.addError(localizedMessage, "iFile");
							return null;
						}
					}

					// determines the kind of version to create
					Version.VERSION_TYPE versionType = Version.VERSION_TYPE.NEW_SUBVERSION;
					if (isMajorUpdate()) {
						versionType = Version.VERSION_TYPE.NEW_RELEASE;
					}

					try {
						// checkin the document; throws an exception if
						// something goes wrong
						DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(
								DocumentManager.class);
						documentManager.checkin(document.getId(), new FileInputStream(file), fileName,
								SessionManagement.getUser(), versionType, versionDesc, immediateIndexing);

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

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}
}