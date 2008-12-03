package com.logicaldoc.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.util.config.PropertiesBean;

/**
 * <p>
 * The StyleBean class is the backing bean which manages the demonstrations'
 * active theme. There are currently two themes supported by the bean; XP and
 * Royale.
 * </p>
 * <p/>
 * <p>
 * The webpages' style attributes are modified by changing link in the header of
 * the HTML document. The selectInputDate and tree components' styles are
 * changed by changing the location of their image src directories.
 * </p>
 * 
 * @since 3.0
 */
public class StyleBean {

	protected static Log log = LogFactory.getLog(StyleBean.class);

	// folder icons for the respective themes
	public static final String XP_BRANCH_EXPANDED_ICON = "xmlhttp/css/xp/css-images/tree_folder_open.gif";

	public static final String XP_BRANCH_CONTRACTED_ICON = "xmlhttp/css/xp/css-images/tree_folder_close.gif";

	public static final String XP_SPACER_ICON = "xmlhttp/css/xp/css-images/spacer.gif";

	// possible theme choices
	private final String XP = "xp";

	private final String ROYALE = "royale";

	// default theme
	private String currentStyle = XP;

	private String tempStyle = XP;

	// available style list
	private List<SelectItem> styleList;

	// default theme image directory for selectinputdate and theme.
	private String imageDirectory = "./xmlhttp/css/xp/css-images/";

	private TimeZone timeZone;

	private String skin = "default";

	private String productName;

	private String productUrl;

	private String productHelp;

	private String productRelease;

	private String productYear;

	/**
	 * Creates a new instance of the StyleBean.
	 */
	public StyleBean() {
		super();
		reload();
		log.debug("StyleBean initialized");
	}

	public void reload() {
		// initialize the style list
		styleList = new ArrayList<SelectItem>();
		styleList.add(new SelectItem(XP, XP));
		styleList.add(new SelectItem(ROYALE, ROYALE));
		initTimeZone();

		try {
			PropertiesBean context = new PropertiesBean();
			skin = context.getProperty("skin");
			productName = context.getProperty("skin." + skin + ".product.name");
			if (StringUtils.isEmpty(productName))
				productName = context.getProperty("product.name");
			productUrl = context.getProperty("skin." + skin + ".product.url");
			if (StringUtils.isEmpty(productUrl))
				productUrl = context.getProperty("product.url");
			productHelp = context.getProperty("skin." + skin + ".product.help");
			if (StringUtils.isEmpty(productHelp))
				productHelp = context.getProperty("product.help");
			productYear = context.getProperty("skin." + skin + ".product.year");
			if (StringUtils.isEmpty(productYear))
				productYear = context.getProperty("product.year");
			productHelp = context.getProperty("product.release");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	public String getProductYear() {
		return productYear;
	}

	public void setProductYear(String productYear) {
		this.productYear = productYear;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductUrl() {
		return productUrl;
	}

	public void setProductUrl(String productUrl) {
		this.productUrl = productUrl;
	}

	public String getProductHelp() {
		return productHelp;
	}

	public void setProductHelp(String productHelp) {
		this.productHelp = productHelp;
	}

	public String getProductRelease() {
		return productRelease;
	}

	public void setProductRelease(String productRelease) {
		this.productRelease = productRelease;
	}

	public String getSkin() {
		return skin;
	}

	public void setSkin(String skin) {
		this.skin = skin;
	}

	private void initTimeZone() {
		Calendar now = Calendar.getInstance();
		this.timeZone = now.getTimeZone();
	}

	/**
	 * Gets the current style.
	 * 
	 * @return current style
	 */
	public String getCurrentStyle() {
		return currentStyle;
	}

	/**
	 * Sets the current style of the application to one of the predetermined
	 * themes.
	 * 
	 * @param currentStyle
	 */
	public void setCurrentStyle(String currentStyle) {
		this.tempStyle = currentStyle;
	}

	/**
	 * Gets the html needed to insert a valid css link tag.
	 * 
	 * @return the tag information needed for a valid css link tag
	 */
	public String getStyle() {
		String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();

		return "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + contextPath + "/xmlhttp/css/" + currentStyle
				+ "/" + currentStyle + ".css" + "\"></link>";
	}

	/**
	 * Gets the image directory to use for the selectinputdate and tree theming.
	 * 
	 * @return image directory used for theming
	 */
	public String getImageDirectory() {
		return imageDirectory;
	}

	/**
	 * Gets a list of available theme names that can be applied.
	 * 
	 * @return available theme list
	 */
	public List<SelectItem> getStyleList() {
		return styleList;
	}

	public String getPath(String name) {
		System.out.println("getPath " + name + " - " + skin);
		return "/skins/" + skin + "/" + name;
	}

	public String getImagesPath() {
		return getPath("images");
	}

	public String getHelpPath() {
		return getPath("help");
	}

	public String getCssPath() {
		return getPath("css");
	}

	public String getImagePath(String imageName) {
		return getPath("images").substring(1) + "/" + imageName;
	}

	public String getTimeZoneID() {
		String tzDisplayName = timeZone.getDisplayName(true, TimeZone.SHORT);
		log.debug("tzDisplayName = " + tzDisplayName);
		return tzDisplayName;
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}
}