package com.logicaldoc.web.admin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.util.config.SettingsConfig;

import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.i18n.Messages;


/**
 * Form for directories editing
 *
 * @author Marco Meschieri
 * @version $Id:$
 * @since ###release###
 */
public class FoldersForm {
    protected static Log log = LogFactory.getLog(FoldersForm.class);
    private String docDir = "";
    private String indexDir = "";
    private String userDir = "";

    public FoldersForm() {
        reload();
    }

    public String getDocDir() {
        return docDir;
    }

    public void setDocDir(String docDir) {
        this.docDir = docDir;
    }

    public String getIndexDir() {
        return indexDir;
    }

    public void setIndexDir(String indexDir) {
        this.indexDir = indexDir;
    }

    public String getUserDir() {
        return userDir;
    }

    public void setUserDir(String userDir) {
        this.userDir = userDir;
    }

    private void reload() {
        SettingsConfig conf = (SettingsConfig) Context.getInstance()
                                                      .getBean(SettingsConfig.class);
        docDir = conf.getValue("docdir");
        indexDir = conf.getValue("indexdir");
        userDir = conf.getValue("userdir");
    }

    public String save() {
        if (SessionManagement.isValid()) {
            try {
                SettingsConfig conf = (SettingsConfig) Context.getInstance()
                                                              .getBean(SettingsConfig.class);
                conf.setValue("docdir", docDir);
                conf.setValue("indexdir", indexDir);
                conf.setValue("userdir", userDir);

                PropertiesBean pbean = new PropertiesBean(getClass().getClassLoader().getResource("context.properties"));
    			pbean.setProperty("conf.docdir", docDir);
    			pbean.setProperty("conf.indexdir", indexDir);
    			pbean.setProperty("conf.userdir", userDir);
    			pbean.write();
                
                Messages.addLocalizedInfo("msg.action.savesettings");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                Messages.addLocalizedError("errors.action.savesettings");
            }
        } else {
            return "login";
        }

        reload();

        return null;
    }
}
