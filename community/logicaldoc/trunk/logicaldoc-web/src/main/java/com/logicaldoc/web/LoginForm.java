package com.logicaldoc.web;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.authentication.AuthenticationChain;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.LocaleUtil;
import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.util.config.SettingsConfig;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.navigation.MenuBarBean;
import com.logicaldoc.web.navigation.PageContentBean;
import com.logicaldoc.web.password.PasswordBean;
import com.logicaldoc.web.util.Constants;
import com.logicaldoc.web.util.FacesUtil;

/**
 * Executes user login
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class LoginForm {
	protected static Log log = LogFactory.getLog(LoginForm.class);

	private String j_password;

	private String language;

	private String j_username;

	private MenuBarBean menuBar;

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

		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();

		if (authenticationChain.authenticate(j_username, j_password, request.getRemoteAddr())) {
			User user = userDao.findByUserName(j_username);
			log.info("User " + j_username + " logged in.");

			Map<String, Object> session = facesContext.getExternalContext().getSessionMap();

			session.put(Constants.AUTH_USERID, user.getId());
			session.put(Constants.AUTH_USERNAME, j_username);
			session.put(Constants.AUTH_PASSWORD, j_password);

			// Gets language option pressed

			Locale locale = user.getLocale();
			if (language == null || language.equals("default")) {
				language = user.getLanguage();
			} else {
				locale = LocaleUtil.toLocale(language);
			}

			session.put(Constants.LANGUAGE, language);
			session.put(Constants.LOCALE, locale);
			facesContext.getViewRoot().setLocale(locale);
			log.info("Set locale to " + locale);

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
						page = new PageContentBean(menu.getId());
						if (StringUtils.isNotEmpty(menu.getRef())) {
							page.setTemplate(menu.getRef());
						}
						page.setContentTitle(Messages.getMessage(menu.getText()));
						page.setIcon(style.getImagePath(menu.getIcon()));
					} else {
						log.warn("Menu " + entrypage + " not found");
					}
				}
			} catch (Throwable e) {
				log.error(e.getMessage(), e);
			}

			if (page == null) {
				// Show the home page
				page = new PageContentBean("home", "home");
				page.setContentTitle(Messages.getMessage("dashboard"));
				page.setDisplayText(Messages.getMessage("dashboard"));
				page.setIcon(style.getImagePath("home.png"));
			}
			menuBar.getNavigation().setSelectedPanel(page);
			menuBar.getModel().clear();

			return "loginSuccess";
		} else if (userDao.isPasswordExpired(j_username)) {
			User user = userDao.findByUserName(j_username);
			PasswordBean bean = ((PasswordBean) FacesUtil.accessBeanFromFacesContext("passwordBean", FacesContext
					.getCurrentInstance(), log));
			bean.setUser(user);
			log.info("User " + j_username + " password expired.");
			return "passwordExpired";
		} else {
			log.warn("User " + j_username + " is not valid.");
			Messages.addLocalizedError("errors.login.notvalid");
			return "loginFailure";
		}
	}

	/**
	 * Logout handler
	 * 
	 * @return "login" if the user was succesfully logged off
	 */
	public String logout() {
		FacesContext facesContext = FacesContext.getCurrentInstance();

		try {
			String authUsername = SessionManagement.getUsername();
			SettingsConfig conf = (SettingsConfig) Context.getInstance().getBean(SettingsConfig.class);
			FileUtils.deleteDirectory(new File(conf.getValue("userdir") + "/" + authUsername + "/temp"));

			log.info("User " + authUsername + " logged out.");

			HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
			if (session != null && session.getAttribute(Constants.USER_SESSION) != null) {
				SessionManager.getInstance().kill((String) session.getAttribute(Constants.USER_SESSION));
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
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

	public MenuBarBean getMenuBar() {
		return menuBar;
	}

	public void setMenuBar(MenuBarBean menuBar) {
		this.menuBar = menuBar;
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

	public boolean isAdmin() {
		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		User user = userDao.findByUserName(SessionManagement.getUsername());
		for (Group group : user.getGroups()) {
			if (group.getName().equals("admin"))
				return true;
		}
		return false;
	}

}
