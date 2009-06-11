package com.logicaldoc.web.document;

import java.util.Date;

import javax.faces.component.UIInput;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.dao.VersionDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.util.FacesUtil;

/**
 * Base form for version editing
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class VersionEditForm {
	protected static Log log = LogFactory.getLog(DocumentRecord.class);

	private String version;

	private String author;

	private Date versionDate;

	private String comment;

	private VersionsRecordsManager versionsManager;

	private UIInput commentInput = null;

	public void init(VersionRecord version) {
		this.version = version.getVersion();
		this.versionDate = version.getVersionDate();
		this.author = version.getUsername();
		this.comment = version.getComment();
		FacesUtil.forceRefresh(commentInput);
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
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
		try {
			VersionDAO vdao = (VersionDAO) Context.getInstance().getBean(VersionDAO.class);
			VersionRecord version = versionsManager.getVersion(getVersion());
			vdao.initialize(version.getWrappedVersion());
			version.setComment(getComment());
			vdao.store(version.getWrappedVersion());
			Messages.addLocalizedInfo(Messages.getMessage("msg.action.changeversion"));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			Messages.addLocalizedError("errors.action.changeversion");
		}

		versionsManager.backToList();

		return null;
	}

	public void setVersionsManager(VersionsRecordsManager versionsManager) {
		this.versionsManager = versionsManager;
	}

	public Date getVersionDate() {
		return versionDate;
	}

	public void setVersionDate(Date versionDate) {
		this.versionDate = versionDate;
	}

	public UIInput getCommentInput() {
		return commentInput;
	}

	public void setCommentInput(UIInput commentInput) {
		this.commentInput = commentInput;
	}
}
