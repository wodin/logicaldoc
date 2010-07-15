package com.logicaldoc.web.password;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserHistory;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.io.CryptUtil;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.i18n.Messages;

/**
 * Bean used to change an expired password
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class PasswordBean {
	protected static Log log = LogFactory.getLog(PasswordBean.class);

	private String password;

	private String repass;

	private String oldPassword;

	private User user;

	public String save() {
		try {
			if (!CryptUtil.cryptString(getOldPassword()).equals(user.getPassword())) {
				Messages.addLocalizedError("password.mismatch", "renewPassword:oldPassword");
				return null;
			}

			if (!getPassword().equals(getRepass())) {
				Messages.addLocalizedError("msg.jsp.adduser.repass");
				return null;
			}

			if (getOldPassword().equals(getPassword())) {
				Messages.addLocalizedError("password.usedifferent");
				return null;
			}

			UserHistory history = null;

			if (StringUtils.isNotEmpty(getPassword()) && !user.getPassword().equals(getPassword())) {
				// The password was changed
				user.setDecodedPassword(getPassword());
				user.setPasswordChanged(new Date());
				// Add a user history entry
				history = new UserHistory();
				history.setUser(user);
				history.setEvent(UserHistory.EVENT_USER_PASSWORDCHANGED);
				history.setComment("");
				history.setSessionId(SessionManagement.getCurrentUserSessionId());
			}

			user.setRepass("");

			UserDAO dao = (UserDAO) Context.getInstance().getBean(UserDAO.class);

			boolean stored = dao.store(user, history);

			if (!stored) {
				Messages.addLocalizedError("errors.action.saveuser.notstored");
			} else {
				Messages.addLocalizedInfo("msg.action.changeuser");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			Messages.addLocalizedError("errors.action.saveuser.notstored");
			return null;
		}

		Messages.addLocalizedInfo("password.usenew");
		return "login";
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRepass() {
		return repass;
	}

	public void setRepass(String repass) {
		this.repass = repass;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}