package com.logicaldoc.gui.common.client;

/**
 * Thrown in case of invalid session
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class InvalidSessionException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidSessionException() {
	}

	public InvalidSessionException(String message) {
		super(message);
	}
}
