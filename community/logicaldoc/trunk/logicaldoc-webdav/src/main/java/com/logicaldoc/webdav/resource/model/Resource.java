package com.logicaldoc.webdav.resource.model;

import java.io.InputStream;

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

}
