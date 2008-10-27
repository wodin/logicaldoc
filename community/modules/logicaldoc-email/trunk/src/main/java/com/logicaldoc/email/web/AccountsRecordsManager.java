package com.logicaldoc.email.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.email.EmailAccount;
import com.logicaldoc.email.dao.EmailAccountDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.util.FacesUtil;

/**
 * <p>
 * The <code>AccountsManager</code> class is responsible for constructing the
 * list of <code>EmailAccount</code> beans which will be bound to a
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

	private List<EmailAccount> accounts = new ArrayList<EmailAccount>();

	private String selectedPanel = "list";

	private EmailAccount selectedAccount = null;

	public AccountsRecordsManager() {
	}

	public String enable() {
		EmailAccount account = (EmailAccount) FacesContext.getCurrentInstance().getExternalContext().getRequestMap()
				.get("account");
		EmailAccountDAO accountDao = (EmailAccountDAO) Context.getInstance().getBean(EmailAccountDAO.class);
		account.setEnabled(1);
		accountDao.store(account);

		return null;
	}

	public String disable() {
		EmailAccount account = (EmailAccount) FacesContext.getCurrentInstance().getExternalContext().getRequestMap()
				.get("account");
		EmailAccountDAO accountDao = (EmailAccountDAO) Context.getInstance().getBean(EmailAccountDAO.class);
		account.setEnabled(0);
		accountDao.store(account);

		return null;
	}

	void reload() {
		accounts.clear();

		try {
			EmailAccountDAO accountDao = (EmailAccountDAO) Context.getInstance().getBean(EmailAccountDAO.class);
			accounts = (List<EmailAccount>) accountDao.findAll();
			Collections.sort(accounts, new Comparator<EmailAccount>() {
				public int compare(EmailAccount arg0, EmailAccount arg1) {
					if (arg0.getId() > arg1.getId()) {
						return 1;
					} else if (arg0.getId() < arg1.getId()) {
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

		AccountForm accountForm = ((AccountForm) FacesUtil.accessBeanFromFacesContext("AccountForm", FacesContext
				.getCurrentInstance(), log));

		EmailAccount selectedAccount = (EmailAccount) FacesContext.getCurrentInstance().getExternalContext()
				.getRequestMap().get("account");
		accountForm.setAccount(selectedAccount);

		return null;
	}

	public String addAccount() {
		selectedPanel = "edit";
		AccountForm accountForm = ((AccountForm) FacesUtil.accessBeanFromFacesContext("AccountForm", FacesContext
				.getCurrentInstance(), log));
		EmailAccount account = new EmailAccount();
		account.setLanguage(SessionManagement.getLanguage());
		accountForm.setAccount(account);
		return null;
	}

	public String delete() {
		int accountId = Integer.parseInt((String) FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("accountId"));

		if (SessionManagement.isValid()) {
			try {
				EmailAccountDAO accountDao = (EmailAccountDAO) Context.getInstance().getBean(EmailAccountDAO.class);
				boolean deleted = accountDao.delete(accountId);

				if (deleted) {
					Messages.addLocalizedInfo("logicaldoc-email.account.delete");
				} else {
					Messages.addLocalizedError("logicaldoc-email.account.error");
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				Messages.addLocalizedError("logicaldoc-email.account.error");
			}

			reload();
		} else {
			return "login";
		}

		return null;
	}

	/**
	 * Gets the list of EmailAccount which will be used by the ice:dataTable
	 * component.
	 */
	public Collection<EmailAccount> getAccounts() {
		if (accounts.isEmpty()) {
			reload();
		}

		return accounts;
	}

	public int getCount() {
		return getAccounts().size();
	}

	public EmailAccount getSelectedAccount() {
		return selectedAccount;
	}

	public void setSelectedGroup(EmailAccount selectedAccount) {
		this.selectedAccount = selectedAccount;
	}
}
