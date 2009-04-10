package com.logicaldoc.web;

import java.io.File;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.authentication.AuthenticationChain;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.util.config.SettingsConfig;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.navigation.NavigationBean;
import com.logicaldoc.web.navigation.PageContentBean;
import com.logicaldoc.web.util.Constants;

/**
 * Executes user login
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class LoginForm {
	protected static Log logger = LogFactory.getLog(LoginForm.class);

	private String j_password;

	private String language;

	private String j_username;

	private NavigationBean navigation;

	private Boolean setupPerformed;

	/**
	 * Login handler
	 * 
	 * @return "success" if the user was successfully authenticated
	 */
	public String login() {
		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		AuthenticationChain authenticationChain = (AuthenticationChain) Context.getInstance().getBean(
				AuthenticationChain.class);

		if (authenticationChain.authenticate(j_username, j_password)) {
			User user = userDao.findByUserName(j_username);

			logger.info("User " + j_username + " logged in.");

			FacesContext facesContext = FacesContext.getCurrentInstance();
			Map<String, Object> session = facesContext.getExternalContext().getSessionMap();

			session.put(Constants.AUTH_USERID, user.getId());
			session.put(Constants.AUTH_USERNAME, j_username);
			session.put(Constants.AUTH_PASSWORD, j_password);

			// Gets language option pressed
			if (language.equals("default"))
				language = user.getLanguage();
			session.put(Constants.LANGUAGE, language);

			Locale locale = user.getLocale();
			session.put(Constants.LOCALE, locale);
			facesContext.getViewRoot().setLocale(locale);
			logger.info("Set locale to " + locale);

			String timezone = Calendar.getInstance().getTimeZone().getID();
			session.put(Constants.TIMEZONE, timezone);

			StyleBean style = (StyleBean) Context.getInstance().getBean(StyleBean.class);

			// Retrieve the entry page
			PageContentBean page = null;
			try {
				PropertiesBean config = new PropertiesBean();
				String entrypage = config.getProperty("gui.entrypage");
				if (StringUtils.isNotEmpty(entrypage) && Long.parseLong(entrypage) != 1L) {
					MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
					Menu menu = menuDao.findById(Long.parseLong(entrypage));
					if (menu != null && menuDao.isReadEnable(menu.getId(), user.getId())) {
						page = new PageContentBean("m-" + Long.toString(menu.getId()));
						if (StringUtils.isNotEmpty(menu.getRef())) {
							page.setTemplate(menu.getRef());
						}
						page.setContentTitle(Messages.getMessage(menu.getText()));
						page.setIcon(style.getImagePath(menu.getIcon()));
					} else {
						logger.warn("Menu " + entrypage + " not found");
					}
				}
			} catch (Throwable e) {
				logger.error(e.getMessage(), e);
			}

			if (page == null) {
				// Show the home page
				page = new PageContentBean("home", "home");
				page.setContentTitle(Messages.getMessage("home"));
				page.setDisplayText(Messages.getMessage("home"));
				page.setIcon(style.getImagePath("home.png"));
			}
			navigation.setSelectedPanel(page);

			return "loginSuccess";
		} else {
			logger.warn("User " + j_username + " is not valid.");
			// Messages.addError(Messages.getString("errors.action.password.mismatch"));
			Messages.addError("Invalid username or password");

			return "loginFailure";
		}
	}

	/**
	 * Logout handler
	 * 
	 * @return "login" if the user was succesfully logged off
	 */
	@SuppressWarnings("unchecked")
	public String logout() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);

		try {
			String authUsername = SessionManagement.getUsername();
			SettingsConfig conf = (SettingsConfig) Context.getInstance().getBean(SettingsConfig.class);
			FileUtils.deleteDirectory(new File(conf.getValue("userdir") + "/" + authUsername + "/temp"));

			logger.info("User " + authUsername + " logged out.");

			Enumeration enumeration = session.getAttributeNames();

			while (enumeration.hasMoreElements()) {
				session.removeAttribute((String) enumeration.nextElement());
			}

			// session.invalidate();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return "login";
	}

	/**
	 * @return the j_password
	 */
	public String getJ_password() {
		return j_password;
	}

	/**
	 * @param j_password the j_password to set
	 */
	public void setJ_password(String j_password) {
		this.j_password = j_password;
	}

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * @return the j_username
	 */
	public String getJ_username() {
		return j_username;
	}

	/**
	 * @param j_username the j_username to set
	 */
	public void setJ_username(String j_username) {
		this.j_username = j_username;
	}

	public void setNavigation(NavigationBean navigation) {
		this.navigation = navigation;
	}

	public boolean isNotInitialized() {

		if ((setupPerformed != null) && (setupPerformed.booleanValue() == true))
			return false;

		PropertiesBean pbean = (PropertiesBean) Context.getInstance().getBean("ContextProperties");
		String jdbcUrl = pbean.getProperty("jdbc.url");
		if (StringUtils.isNotEmpty(jdbcUrl)) {
			if (jdbcUrl.equals("jdbc:hsqldb:mem:logicaldoc")) {
				return true;
			} else {
				setupPerformed = new Boolean(true);
				return false;
			}
		}

		return true;
	}

}
