package com.logicaldoc.web.admin;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.searchengine.store.Storer;

import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.BackupConfig;
import com.logicaldoc.util.config.PropertiesBean;

import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.i18n.Messages;

import java.io.File;


/**
 * @author Michael Scholz
 */
public class BackupInfo {
	
    protected static Log log = LogFactory.getLog(BackupInfo.class);
    
    private boolean enabled;
    private String backupDir;

    public BackupInfo() {
        reload();
    }

    private void reload() {
        BackupConfig conf = (BackupConfig) Context.getInstance()
                                                  .getBean(BackupConfig.class);
        setEnabled(conf.isEnabled());

        String tmp = StrSubstitutor.replaceSystemProperties(conf.getLocation());
        this.backupDir = FilenameUtils.separatorsToSystem(tmp);
    }

    public String getBackupDir() {
        return backupDir;
    }

    public void setBackupDir(String backupDir) {
        this.backupDir = backupDir;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void save() {
        try {
            BackupConfig conf = (BackupConfig) Context.getInstance()
                                                      .getBean(BackupConfig.class);
            conf.setEnabled(enabled);
            conf.setLocation(backupDir);

            PropertiesBean pbean = new PropertiesBean(getClass().getClassLoader().getResource("context.properties"));
			pbean.setProperty("conf.backupdir", backupDir);
			pbean.setProperty("conf.backupenabled", new Boolean(enabled).toString());
			pbean.write();
            
			FileUtils.forceMkdir(new File(backupDir));
            Messages.addLocalizedInfo("msg.action.savesettings");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Messages.addLocalizedError("errors.action.savesettings");
        }
    }

    public String restore() {
        if (SessionManagement.isValid()) {
            try {
                Storer storer = (Storer) Context.getInstance()
                                                .getBean(Storer.class);
                storer.restoreAll();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

            reload();
        } else {
            return "login";
        }

        return null;
    }
}