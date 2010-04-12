package com.logicaldoc.web.navigation;

import javax.faces.event.ActionEvent;

import com.icesoft.faces.component.tree.IceUserObject;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.web.StyleBean;

/**
 * <p>
 * The PageContentBean class is responsible for holding state information which
 * will allow a TreeNavigation and NavigationBean display dynamic content.
 * </p>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class PageContentBean extends IceUserObject {
	private Menu menu;

	private long menuId;

	// template, default panel to make visible in a panel stack
	private String contentName = "";

	// text to be displayed in navigation link
	private String displayText;

	// title information to be displayed
	private String contentTitle;

	private String template = "";

	private boolean pageContent = true;

	// view reference to control the visible content
	private NavigationBean navigationBean;

	public PageContentBean(long menuId) {
		this("m-" + menuId);
		this.menuId = menuId;
	}

	public PageContentBean(long menuId, String template) {
		this("m-" + menuId, template);
		this.menuId = menuId;
	}

	public PageContentBean(String contentName) {
		this();
		this.contentName = contentName;
	}

	public PageContentBean(String contentName, String template) {
		this(contentName);
		this.template = template;
	}

	public PageContentBean() {
		super(null);
		init();
	}

	public PageContentBean(Menu menu) {
		this();
		this.menu = menu;
	}

	/**
	 * Initialize internationalization.
	 */
	private void init() {
		setBranchContractedIcon(StyleBean.XP_BRANCH_CONTRACTED_ICON);
		setBranchExpandedIcon(StyleBean.XP_BRANCH_EXPANDED_ICON);
		setLeafIcon(StyleBean.XP_BRANCH_CONTRACTED_ICON);
		setExpanded(false);
		setLeaf(false);
		setIcon("skins/default/images/spacer.gif");
	}

	/**
	 * Gets the navigation callback.
	 * 
	 * @return NavigationBean.
	 */
	public NavigationBean getNavigationSelection() {
		return navigationBean;
	}

	/**
	 * Sets the navigation callback.
	 * 
	 * @param navigationBean controls selected panel state.
	 */
	public void setNavigationSelection(NavigationBean navigationBean) {
		this.navigationBean = navigationBean;
	}

	/**
	 * Gets the template name to display in the showcase.jspx. The template is a
	 * panel in a panel stack which will be made visible.
	 * 
	 * @return panel stack template name.
	 */
	public String getContentName() {
		return contentName;
	}

	/**
	 * Sets the template name to be displayed when selected in tree. Selection
	 * will only occur if pageContent is true.
	 * 
	 * @param contentName valid panel name in showcase.jspx
	 */
	public void setContentName(String templateName) {
		this.contentName = templateName;
	}

	/**
	 * Gets the menu display text. This text will be shown in the navigation
	 * control.
	 * 
	 * @return menu display text.
	 */
	public String getDisplayText() {
		return displayText;
	}

	/**
	 * Sets the text to be displayed in the menu. This text string must match a
	 * resource property in
	 * com.icesoft.icefaces.samples.showcase.resources.messages
	 * 
	 * @param displayText menu text to display
	 */
	public void setDisplayText(String menuDisplayText) {
		this.displayText = menuDisplayText;
		setText(getDisplayText());
	}

	/**
	 * Get the text to be displayed as the content title. This text string must
	 * match resource property in
	 * com.icesoft.icefaces.samples.showcase.resources.messages
	 * 
	 * @return menu content title
	 */
	public String getContentTitle() {
		return contentTitle;
	}

	/**
	 * Sets the menu content title.
	 * 
	 * @param contentTitle menu content title name.
	 */
	public void setContentTitle(String menuContentTitle) {
		this.contentTitle = menuContentTitle;
	}

	public String getTemplate() {
		return template;
	}

	/**
	 * This is necessary for the Facelets version of component-showcase. Unlike
	 * the JSP version of component-showcase, which uses static includes, the
	 * Facelets version uses dynamic inclusion tied to an EL expression, which
	 * will call getMenuContentInclusionFile().
	 * 
	 * @param template The server-side path to the file to be included
	 */
	public void setTemplate(String menuContentInclusionFile) {
		this.template = menuContentInclusionFile;
	}

	/**
	 * Does the node contain content.
	 * 
	 * @return true if the page contains content; otherwise, false.
	 */
	public boolean isPageContent() {
		return pageContent;
	}

	/**
	 * Sets the page content.
	 * 
	 * @param pageContent True if the page contains content; otherwise, false.
	 */
	public void setPageContent(boolean pageContent) {
		this.pageContent = pageContent;
	}

	public Menu getMenu() {
		return menu;
	}

	public long getMenuId() {
		if (menu != null) {
			menuId = menu.getId();
			return menuId;
		} else if (menuId != 0) {
			return menuId;
		} else
			return -1;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	public void setMenuId(long menuId) {
		this.menuId = menuId;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	/**
	 * Item selection handler
	 */
	public void onSelect(ActionEvent event) {

		if (isPageContent()) {
			if (navigationBean == null) {
				return;
			}

			// only toggle the branch expansion if we have already selected the
			// node
			if (navigationBean.getSelectedPanel().equals(this)) {
				// toggle the branch node expansion
				setExpanded(!isExpanded());
			}

			navigationBean.setSelectedPanel(this);
		}
		// Otherwise toggle the node visibility, only changes the state
		// of the nodes with children.
		else {
			setExpanded(!isExpanded());
		}
	}

	@Override
	public boolean equals(Object obj) {
		PageContentBean other = (PageContentBean) obj;

		if ((other != null) && (other.getContentName() != null) && (contentName != null)) {
			return this.contentName.equals(other.getContentName());
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return displayText;
	}
}
