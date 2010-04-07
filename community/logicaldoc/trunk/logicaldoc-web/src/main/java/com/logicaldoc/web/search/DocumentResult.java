package com.logicaldoc.web.search;

import java.util.Date;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.searchengine.Result;
import com.logicaldoc.core.searchengine.SearchOptions;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.SnippetStripper;
import com.logicaldoc.web.StyleBean;
import com.logicaldoc.web.document.DocumentRecord;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.navigation.NavigationBean;
import com.logicaldoc.web.navigation.PageContentBean;
import com.logicaldoc.web.util.FacesUtil;

/**
 * A result entry usable by the GUI.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class DocumentResult extends DocumentRecord implements Result {
	private static final long serialVersionUID = 24242424L;

	protected static Log log = LogFactory.getLog(DocumentResult.class);

	private Result result;

	private boolean showPath;

	public DocumentResult(Result result) {
		super();
		this.result = result;
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		super.document = docDao.findById(result.getDocId());
		if (document != null) {
			if (document.getDocRef() != null) {
				// This document is a shortcut, so load the referenced doc
				super.shortcut = document;
				super.document = docDao.findById(document.getDocRef());
			}
			setDocId(document.getId());
		} else {
			setDocId(result.getDocId());
		}
	}

	public Date getDate() {
		return result.getDate();
	}

	public int getDateCategory() {
		return result.getDateCategory();
	}

	public long getDocId() {
		return result.getDocId();
	}

	public int getDocType() {
		return result.getDocType();
	}

	public Integer getScore() {
		return result.getScore();
	}

	public String getIcon() {
		return result.getIcon();
	}

	public int getLengthCategory() {
		return result.getLengthCategory();
	}

	public String getTitle() {
		return result.getTitle();
	}

	public String getPath() {
		return result.getPath();
	}

	public String getSource() {
		return result.getSource();
	}

	public Integer getRed() {
		return result.getRed();
	}

	public long getSize() {
		return (long) result.getSize();
	}

	public Date getSourceDate() {
		return result.getSourceDate();
	}

	/**
	 * Teturns the the summary properly escaped for the results page. The Lucene
	 * Hilights are preserved.
	 * 
	 * @see com.logicaldoc.core.searchengine.Result#getSummary()
	 */
	public String getSummary() {
		String summary = result.getSummary();
		return SnippetStripper.strip(summary);
	}

	public String getType() {
		return result.getType();
	}

	public long getDocRef() {
		return result.getDocRef();
	}

	/**
	 * Creates the context menu associated with this record
	 * 
	 * @see com.logicaldoc.web.navigation.MenuBarBean#createMenuItems()
	 */
	protected void createMenuItems() {
		model.clear();

		if (document != null) {
			try {
				Menu folder = document.getFolder();
				StyleBean style = (StyleBean) Context.getInstance().getBean(StyleBean.class);
				model.add(createMenuItem(Messages.getMessage("msg.jsp.versions"), "versions-" + folder.getId(), null,
						"#{entry.versions}", null, style.getImagePath("versions.png"), true, "_blank", null));
				model.add(createMenuItem(" " + Messages.getMessage("document.discussions"), "discussions-"
						+ document.getId(), null, "#{entry.discussions}", null, style.getImagePath("comments.png"),
						true, "_blank", null));
				model.add(createMenuItem(Messages.getMessage("msg.jsp.sendasemail"), "sendasmail-" + folder.getId(),
						null, "#{entry.sendAsEmail}", null, style.getImagePath("editmail.png"), true, "_blank", null));
				model.add(createMenuItem(Messages.getMessage("msg.jsp.sendticket"), "sendticket-" + folder.getId(),
						null, "#{entry.sendAsTicket}", null, style.getImagePath("ticket.png"), true, "_blank", null));
				model.add(createMenuItem(Messages.getMessage("info"), "info-" + folder.getId(), null, "#{entry.info}",
						null, style.getImagePath("info.png"), true, "_blank", null));
				model.add(createMenuItem(Messages.getMessage("history"), "history-" + folder.getId(), null,
						"#{entry.history}", null, style.getImagePath("history.png"), true, "_blank", null));
			} catch (Throwable t) {
				return;
			}
		}
	}

	public void showDocumentPath() {
		this.showPath = true;
	}

	public boolean isShowPath() {
		return showPath;
	}

	private void openDocumentsPage() {
		NavigationBean navigation = ((NavigationBean) FacesUtil.accessBeanFromFacesContext("navigation", FacesContext
				.getCurrentInstance(), log));
		initCollections();
		MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		Menu docMenu = menuDao.findById(Menu.MENUID_DOCUMENTS);
		PageContentBean panel = new PageContentBean(docMenu.getId(), "document/browse");
		panel.setContentTitle(Messages.getMessage(docMenu.getText()));
		navigation.setSelectedPanel(panel);
	}

	public String info() {
		openDocumentsPage();
		return super.info();
	}

	public String history() {
		openDocumentsPage();
		return super.history();
	}

	public String sendAsEmail() {
		openDocumentsPage();
		return super.sendAsEmail();
	}

	public String sendAsTicket() {
		openDocumentsPage();
		return super.sendAsTicket();
	}

	public String versions() {
		openDocumentsPage();
		return super.versions();
	}

	public String discussions() {
		openDocumentsPage();
		return super.discussions();
	}

	public void setScore(Double score) {
	}

	@Override
	public void setTitle(String name) {
		throw new UnsupportedOperationException("setName method unsupported");
	}

	@Override
	public void createScore(float score) {
		throw new UnsupportedOperationException("createScore method unsupported");
	}

	@Override
	public void setDate(Date date) {
		throw new UnsupportedOperationException("setDate method unsupported");
	}

	@Override
	public void setSize(long sze) {
		throw new UnsupportedOperationException("setSize method unsupported");
	}

	@Override
	public void setSummary(String summary) {
		throw new UnsupportedOperationException("setSummary method unsupported");
	}

	@Override
	public void setType(String typ) {
		throw new UnsupportedOperationException("setType method unsupported");
	}

	@Override
	public boolean isRelevant(SearchOptions opt) {
		return result.isRelevant(opt);
	}

	@Override
	public void setSourceDate(Date date) {
		throw new UnsupportedOperationException("setSourceDate method unsupported");
	}

	public Date getCreation() {
		return result.getCreation();
	}

	public void setCreation(Date creation) {
		result.setCreation(creation);
	}

	@Override
	public void setCustomId(String customId) {
		result.setCustomId(customId);
	}

	@Override
	public void setDocId(long docId) {
		result.setDocId(docId);
	}

	@Override
	public void setPath(String path) {
		throw new UnsupportedOperationException("setPath method unsupported");
	}

	@Override
	public void setSource(String source) {
		throw new UnsupportedOperationException("setSource method unsupported");
	}
}