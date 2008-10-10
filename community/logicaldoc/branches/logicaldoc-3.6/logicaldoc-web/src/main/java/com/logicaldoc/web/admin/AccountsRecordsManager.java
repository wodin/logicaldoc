package com.logicaldoc.web.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.logicaldoc.core.communication.EMailAccount;
import com.logicaldoc.core.communication.dao.EMailAccountDAO;
import com.logicaldoc.util.Context;

import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.util.FacesUtil;

/**
 * <p>
 * The <code>AccountsManager</code> class is responsible for constructing the
 * list of <code>EMailAccount</code> beans which will be bound to a
 * ice:dataTable JSF component. <p/>
 * <p>
 * Large data sets could be handle by adding a ice:dataPaginator. Alternatively
 * the dataTable could also be hidden and the dataTable could be added to
 * scrollable ice:panelGroup.
 * </p>
 * 
 * @author Marco Meschieri
 * @version $Id: DocumentsRecordsManager.java,v 1.1 2007/06/29 06:28:29 marco
 *          Exp $
 * @since 3.0
 */
public class AccountsRecordsManager {
    protected static Log log = LogFactory.getLog(AccountsRecordsManager.class);

    private List<EMailAccount> accounts = new ArrayList<EMailAccount>();

    private String selectedPanel = "list";

    private EMailAccount selectedAccount = null;

    public AccountsRecordsManager() {
    }

    public String enable() {
        EMailAccount account = (EMailAccount) FacesContext.getCurrentInstance()
                .getExternalContext().getRequestMap().get("account");
        EMailAccountDAO accountDao = (EMailAccountDAO) Context.getInstance()
                .getBean(EMailAccountDAO.class);
        account.setEnabled(1);
        accountDao.store(account);

        return null;
    }

    public String disable() {
        EMailAccount account = (EMailAccount) FacesContext.getCurrentInstance()
                .getExternalContext().getRequestMap().get("account");
        EMailAccountDAO accountDao = (EMailAccountDAO) Context.getInstance()
                .getBean(EMailAccountDAO.class);
        account.setEnabled(0);
        accountDao.store(account);

        return null;
    }

    void reload() {
        accounts.clear();

        try {
            EMailAccountDAO accountDao = (EMailAccountDAO) Context
                    .getInstance().getBean(EMailAccountDAO.class);
            accounts = (List<EMailAccount>) accountDao.findAll();
            Collections.sort(accounts, new Comparator<EMailAccount>() {
                public int compare(EMailAccount arg0, EMailAccount arg1) {
                    if (arg0.getAccountId() > arg1.getAccountId()) {
                        return 1;
                    } else if (arg0.getAccountId() < arg1.getAccountId()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public String getSelectedPanel() {
        return selectedPanel;
    }

    public void setSelectedPanel(String panel) {
        this.selectedPanel = panel;
    }

    public String list() {
        selectedPanel = "list";
        reload();

        return null;
    }

    public String edit() {
        selectedPanel = "edit";

        AccountForm accountForm = ((AccountForm) FacesUtil
                .accessBeanFromFacesContext("accountForm", FacesContext
                        .getCurrentInstance(), log));

        EMailAccount selectedAccount = (EMailAccount) FacesContext
                .getCurrentInstance().getExternalContext().getRequestMap().get(
                        "account");
        accountForm.setAccount(selectedAccount);

        return null;
    }

    public String addAccount() {
        selectedPanel = "edit";
        AccountForm accountForm = ((AccountForm) FacesUtil
                .accessBeanFromFacesContext("accountForm", FacesContext
                        .getCurrentInstance(), log));
        EMailAccount account = new EMailAccount();
        account.setLanguage(SessionManagement.getLanguage());
        accountForm.setAccount(account);
        return null;
    }

    public String delete() {
        int accountId = Integer.parseInt((String) FacesContext
                .getCurrentInstance().getExternalContext()
                .getRequestParameterMap().get("accountId"));

        if (SessionManagement.isValid()) {
            try {
                EMailAccountDAO accountDao = (EMailAccountDAO) Context
                        .getInstance().getBean(EMailAccountDAO.class);
                boolean deleted = accountDao.delete(accountId);

                if (deleted) {
                    Messages.addLocalizedInfo("msg.action.deleteaccount");
                } else {
                    Messages.addLocalizedError("errors.action.deleteaccount");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                Messages.addLocalizedError("errors.action.deleteaccount");
            }

            reload();
        } else {
            return "login";
        }

        return null;
    }

    /**
     * Gets the list of EMailAccount which will be used by the ice:dataTable
     * component.
     */
    public Collection<EMailAccount> getAccounts() {
        if (accounts.isEmpty()) {
            reload();
        }

        return accounts;
    }

    public int getCount() {
        return getAccounts().size();
    }

    public EMailAccount getSelectedAccount() {
        return selectedAccount;
    }

    public void setSelectedGroup(EMailAccount selectedAccount) {
        this.selectedAccount = selectedAccount;
    }
}
