package com.logicaldoc.core.document;

/**
 * Registers an event on folder or document
 * 
 * @author Michael Scholz
 * @author Alessandro Gasparini - Logical Objects
 * @author Marco Meschieri - Logical Objects
 */
public class History extends AbstractHistory {

	@Override
	public Object clone() throws CloneNotSupportedException {
		History history = new History();
		history.setTenantId(getTenantId());
		history.setDate(getDate());
		history.setDocId(getDocId());
		history.setFolderId(getFolderId());
		history.setUser(getUser());
		history.setEvent(getEvent());
		history.setComment(getComment());
		history.setTitle(getTitle());
		history.setTitleOld(getTitleOld());
		history.setVersion(getVersion());
		history.setPath(getPath());
		history.setPathOld(getPathOld());
		history.setNotified(getNotified());
		history.setSessionId(getSessionId());
		history.setNew(getNew());
		history.setFilename(getFilename());
		history.setFilenameOld(getFilenameOld());
		history.setUserId(getUserId());
		history.setUserName(getUserName());
		history.setFile(getFile());
		history.setTenant(getTenant());

		return history;
	}
}
