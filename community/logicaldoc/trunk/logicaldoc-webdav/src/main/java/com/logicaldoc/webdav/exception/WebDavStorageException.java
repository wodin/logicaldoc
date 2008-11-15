package com.logicaldoc.webdav.exception;

@SuppressWarnings("serial")
public class WebDavStorageException extends Exception{
	public WebDavStorageException(String s){
		super(s);
	}
	
	public WebDavStorageException(Exception e){
		super(e);
	}
}
