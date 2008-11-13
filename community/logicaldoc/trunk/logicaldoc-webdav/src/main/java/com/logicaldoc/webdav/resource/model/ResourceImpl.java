package com.logicaldoc.webdav.resource.model;

import java.io.InputStream;

/**
 * 
 * @author Sebastian Wenzky
 *
 */
public class ResourceImpl implements Resource{
	
	private String id;
	private String name;
	private Long contentLength;
	private boolean isFolder;
	private boolean isLocked;
	private String path;
	private InputStream is;
	private long personRequest;
	
	public Long getContentLength() {
		return contentLength;
	}
	
	public String getName() {
		return name;
	}
	
	public void setContentLength(Long contentLength) {
		this.contentLength = contentLength;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void isFolder(boolean isFolder){
		this.isFolder = isFolder;
	}
	
	public boolean isFolder(){
		return this.isFolder;
	}
	
	public void isLocked(boolean isLocked){
		this.isLocked = isLocked;
	}
	
	public boolean isLocked(){
		return this.isLocked;
	}

	public String getPath() {		
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public String getID() {
		return this.id;
	}
	
	public void setID(String id) {
		this.id = id;
	}

	@Override
	public InputStream getInputStream() {
		return this.is;
	}

	@Override
	public void setInputStream(InputStream is) {
		this.is = is;
	}

	@Override
	public long getRequestedPerson() {
		return this.personRequest;
	}

	@Override
	public void setRequestedPerson(long id) {
		this.personRequest = id;
	}
}
