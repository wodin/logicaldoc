package com.logicaldoc.web.document;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.document.dao.DocumentDAO;

import com.logicaldoc.util.Context;

import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.i18n.Messages;

import java.util.Date;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;


/**
 * Base form for version editingo
 *
 * @author Marco Meschieri
 * @version $Id: VersionEditForm.java,v 1.2 2006/09/03 16:24:37 marco Exp $
 * @since 3.0
 */
public class VersionEditForm {
    protected static Log log = LogFactory.getLog(DocumentRecord.class);
    private String version;
    private String author;
    private Date date;
    private String comment;
    private VersionsRecordsManager versionsManager;

    public void init(VersionRecord version) {
        this.version = version.getVersion();
        this.date = version.getDate();
        this.author = version.getVersionUser();
        this.comment = version.getVersionComment();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String desdcription) {
        this.comment = desdcription;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String update() {
        FacesContext facesContext = FacesContext.getCurrentInstance();

        // Show the proper panel
        Document doc = versionsManager.getSelectedDocument();

        if (SessionManagement.isValid()) {
            try {
                DocumentDAO ddao = (DocumentDAO) Context.getInstance()
                                                        .getBean(DocumentDAO.class);
                Version version = doc.getVersion(getVersion());
                version.setVersionComment(getComment());

                boolean stored = ddao.store(doc);

                if (!stored) {
                    Messages.addLocalizedError("errors.action.changeversion");
                } else {
                    Messages.addLocalizedInfo(Messages.getMessage(
                            "msg.action.changeversion"));
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                Messages.addLocalizedError("errors.action.changeversion");
            }
        } else {
            return "login";
        }

        versionsManager.backToList();

        return null;
    }

    public void setVersionsManager(VersionsRecordsManager versionsManager) {
        this.versionsManager = versionsManager;
    }
}
