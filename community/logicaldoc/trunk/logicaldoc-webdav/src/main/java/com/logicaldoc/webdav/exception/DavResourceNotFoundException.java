package com.logicaldoc.webdav.exception;

public class DavResourceNotFoundException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6258212946488552163L;

	public DavResourceNotFoundException(String s){
		super(s);
	}
}
