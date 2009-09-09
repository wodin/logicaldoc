package com.logicaldoc.web.settings;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.io.CryptUtil;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.i18n.Messages;


/**
 * Change password form
 *
 * @author Marco Meschieri
 * @version $Id:$
 * @since ###release###
 */
public class PasswordForm {
    protected static Log log = LogFactory.getLog(PasswordForm.class);
    String oldPassword;
    String password;
    String repass;

    public PasswordForm() {
        oldPassword = "";
        password = "";
        repass = "";
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public String getPassword() {
        return password;
    }

    public String getRepass() {
        return repass;
    }

    public void setOldPassword(String opwd) {
        oldPassword = opwd;
    }

    public void setPassword(String npwd) {
        password = npwd;
    }

    public void setRepass(String re) {
        repass = re;
    }

    public String save() {
        if (SessionManagement.isValid()) {
            try {
                if (password.equals(repass)) {
                    long userId = SessionManagement.getUserId();
                    UserDAO udao = (UserDAO) Context.getInstance()
                                                    .getBean(UserDAO.class);
                    User user = udao.findById(userId);
                    String opwd = CryptUtil.cryptString(oldPassword);

                    if (opwd.equals(user.getPassword())) {
                        user.setDecodedPassword(password);
                        udao.store(user);
                        Messages.addLocalizedInfo("msg.action.passwordchanged");
                    } else {
                        Messages.addLocalizedError(
                            "errors.action.password.mismatch");
                    }
                } else {
                    Messages.addLocalizedError("errors.val.password");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                Messages.addLocalizedError("errors.action.changepassword");
            }

            return null;
        } else {
            return "login";
        }
    }
}
