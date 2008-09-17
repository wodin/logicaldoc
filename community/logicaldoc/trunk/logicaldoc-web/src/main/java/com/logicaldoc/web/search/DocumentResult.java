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

import com.logicaldoc.web.StyleBean;
import com.logicaldoc.web.document.DocumentRecord;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.navigation.NavigationBean;
import com.logicaldoc.web.navigation.PageContentBean;
import com.logicaldoc.web.util.FacesUtil;
import com.logicaldoc.web.util.SnippetStripper;

/**
 * A result entry usable by the GUI.
 * 
 * @author Marco Meschieri
 * @version $Id:$
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
		initMenu(getMenuId());

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		this.document = docDao.findByMenuId(getMenu().getMenuId());
	}

	protected void initMenu(int menuId) {
		try {
			MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
			this.menu = mdao.findByPrimaryKey(menuId);
		} catch (RuntimeException e) {
			log.error(e.getMessage(), e);
		}
	}

	public Date getDate() {
		return result.getDate();
	}

	public int getDateCategory() {
		return result.getDateCategory();
	}

	public Integer getDocId() {
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

	public int getMenuId() {
		return result.getMenuId();
	}

	public String getName() {
		return result.getName();
	}

	public String getPath() {
		return result.getPath();
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

	public boolean isRelevant(SearchOptions arg0, String arg1) {
		return result.isRelevant(arg0, arg1);
	}

	/**
	 * Creates the context menu associated with this record
	 * 
	 * @see com.logicaldoc.web.navigation.MenuBarBean#createMenuItems()
	 */
	protected void createMenuItems() {
		model.clear();
		Menu menu = getMenu();
		if (menu.getMenuType() == Menu.MENUTYPE_FILE) {
			model.add(createMenuItem(Messages.getMessage("msg.jsp.versions"), "versions-" + menu.getMenuId(), null,
					"#{entry.versions}", null, StyleBean.getImagePath("versions.png"), true, "_blank", null));
			model.add(createMenuItem(Messages.getMessage("msg.jsp.discuss"), "articles-" + menu.getMenuId(), null,
					"#{entry.articles}", null, StyleBean.getImagePath("comments.png"), true, "_blank", null));
			model.add(createMenuItem(Messages.getMessage("msg.jsp.sendasemail"), "sendasmail-" + menu.getMenuId(),
					null, "#{entry.sendAsEmail}", null, StyleBean.getImagePath("editmail.png"), true, "_blank", null));
			model.add(createMenuItem(Messages.getMessage("msg.jsp.sendticket"), "sendticket-" + menu.getMenuId(), null,
					"#{entry.sendAsTicket}", null, StyleBean.getImagePath("ticket.png"), true, "_blank", null));
			model.add(createMenuItem(Messages.getMessage("msg.jsp.foldercontent.info"), "info-" + menu.getMenuId(),
					null, "#{entry.info}", null, StyleBean.getImagePath("info.png"), true, "_blank", null));
			model.add(createMenuItem(Messages.getMessage("msg.jsp.history"), "history-" + menu.getMenuId(), null,
					"#{entry.history}", null, StyleBean.getImagePath("history.png"), true, "_blank", null));
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
		MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		Menu docMenu = menuDao.findByPrimaryKey(Menu.MENUID_DOCUMENTS);
		PageContentBean panel = new PageContentBean("m-" + docMenu.getMenuId(), "document/browse");
		panel.setContentTitle(Messages.getMessage(docMenu.getMenuText()));
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

	public String articles() {
		openDocumentsPage();
		return super.articles();
	}

	public String versions() {
		openDocumentsPage();
		return super.versions();
	}

	public void setScore(Double score) {
	}

	@Override
	public void setIcon(String icon) {
		throw new UnsupportedOperationException("setIcon method unsupported");
	}

	@Override
	public void setMenuId(int menuId) {
		throw new UnsupportedOperationException("setMenuId method unsupported");
	}

	@Override
	public void setName(String name) {
		throw new UnsupportedOperationException("setName method unsupported");
	}

	@Override
	public void setPath(String path) {
		throw new UnsupportedOperationException("setPath method unsupported");
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
	public void setSize(int sze) {
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
}