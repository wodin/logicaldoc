package com.logicaldoc.webservice.auth;

import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Auth Web Service definition interface
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
@WebService
public interface AuthService {
	/**
	 * Starts a new user session.
	 * 
	 * @param username The username
	 * @param password The password
	 * @return The newly created session identifier(sid)
	 */
	public String login(@WebParam(name = "username") String username, @WebParam(name = "password") String password)
			throws Exception;

	/**
	 * Closes a user session.
	 * 
	 * @param sid The session identifier
	 */
	public void logout(@WebParam(name = "sid") String sid);

	/**
	 * Checks if a SID is valid
	 */
	public boolean valid(@WebParam(name = "sid") String sid);

	/**
	 * Renews a session
	 */
	public void renew(@WebParam(name = "sid") String sid);
}
