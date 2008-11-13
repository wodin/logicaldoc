package com.logicaldoc.webdav.exception;

public class WebDavAuthorisationException extends Exception{
	public WebDavAuthorisationException(String s){
		super(s);
	}
	
	public WebDavAuthorisationException(Exception e){
		super(e);
	}
}
