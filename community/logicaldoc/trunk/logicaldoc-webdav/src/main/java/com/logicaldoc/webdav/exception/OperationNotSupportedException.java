package com.logicaldoc.webdav.exception;

/**
 * 
 * @author wenzkseb
 *
 */
@SuppressWarnings("serial")
public class OperationNotSupportedException extends RuntimeException{
	public OperationNotSupportedException(){
		super("This method is not supported.");
	}
}
