package com.logicaldoc.webdav.resource.model;

import java.io.InputStream;
import java.util.Date;

/**
 * 
 * @author Sebastian Wenzky
 *
 */
public interface Resource {
	
	public void setID(String ID);
	
	public String getID();
	
	public Long getContentLength();
	
	public String getName() ;
	
	public void setContentLength(Long contentLength);
	
	public void setName(String name);
	
	public boolean isFolder();
	
	public boolean isLocked();

	public void isLocked(boolean isLocked);
	
	public void isFolder(boolean isFolder);
	
	public String getPath();
	
	public void setPath(String path);
	
	public void setInputStream(InputStream is);
	
	public InputStream getInputStream();
	
	public void setRequestedPerson(long id);
	
	public long getRequestedPerson();

	public boolean getIsCheckedOut();
	
	public void setIsCheckedOut(boolean isCheckedOut);
	
	public void setVersionLabel(String versionLabel);
	
	public String getVersionLabel();

	public Date getLastModified();

	public void setLastModified(Date lastModified);

	public void setVersionDate(Date date);
	
	public Date getVersionDate();
	
	public String getAuthor();
	
	public void setAuthor(String author);
	
	public String getComment();
	
	public void setComment(String comment);
	
}
