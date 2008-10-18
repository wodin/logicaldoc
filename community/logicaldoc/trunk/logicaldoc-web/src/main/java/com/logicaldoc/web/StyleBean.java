package com.logicaldoc.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
 * @since 0.3.0
 */
public class StyleBean {

	protected static Log log = LogFactory.getLog(StyleBean.class);

	// folder icons for the respective themes
	public static final String XP_BRANCH_EXPANDED_ICON = "xmlhttp/css/xp/css-images/tree_folder_open.gif";

	public static final String XP_BRANCH_CONTRACTED_ICON = "xmlhttp/css/xp/css-images/tree_folder_close.gif";

	public static final String XP_SPACER_ICON = "xmlhttp/css/xp/css-images/spacer.gif";

	private static Properties paths = new Properties();

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

	/**
	 * Creates a new instance of the StyleBean.
	 */
	public StyleBean() {
		super();
		// initialize the style list
		styleList = new ArrayList<SelectItem>();
		styleList.add(new SelectItem(XP, XP));
		styleList.add(new SelectItem(ROYALE, ROYALE));

		log.debug("StyleBean initialized");
	}

	public static Properties getPaths() {
		return paths;
	}

	public void setPaths(Properties newpaths) {
		paths = newpaths;
		log.debug("StyleBean: properties loaded");
		log.debug("StyleBean: properties = " + paths);
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
	 * Applies temp style to to the current style and image directory and
	 * manually refreshes the icons in the navigation tree. The page will reload
	 * based on navigation rules to ensure the theme is applied; this is
	 * necessary because of difficulties encountered by updating the stylesheet
	 * reference within the <HEAD> of the document.
	 * 
	 * @return the reload navigation attribute
	 */
	public String changeStyle() {
		currentStyle = tempStyle;
		imageDirectory = "./xmlhttp/css/" + currentStyle + "/css-images/";

		return "reload";
	}

	/**
	 * Gets a list of available theme names that can be applied.
	 * 
	 * @return available theme list
	 */
	public List<SelectItem> getStyleList() {
		return styleList;
	}

	public static String getPath(String name) {
		return paths.getProperty(name, "");
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

	public static String getImagePath(String imageName) {
		return getPath("images").substring(1) + "/" + imageName;
	}
}
