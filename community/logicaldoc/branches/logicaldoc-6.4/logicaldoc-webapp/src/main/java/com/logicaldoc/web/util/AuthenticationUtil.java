package com.logicaldoc.web.util;

import java.io.IOException;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.logicaldoc.util.Base64Coder;

/**
 * 
 * Authentication through some download URLs will be managed through a more
 * simple way. Therefore
 * 
 * <pre>
 * authentication
 * </pre>
 * 
 * causes an decode of the given credentials bason on base64. Decoded
 * credentials will be turned back to caller.
 * 
 */
public final class AuthenticationUtil {

	public static final String DEFAULT_AUTHENTICATE_HEADER = "Basic realm=\"LogicalDOC\"";

	public static final String HEADER_AUTHORIZATION = "Authorization";

	public interface Credentials {
		public String getUserName();

		public String getPassword();
	};

	/**
	 * Decodes given Credentials through base64 decode and turns back a @link
	 * {@link Credentials} Objects with username and password. Please note, that
	 * this credentials are no valid login-informations against logicalDOC,
	 * instead its more an preparement for the final check.
	 * 
	 * @param request The marshaled HttpServletRequest
	 * @return valide credentials
	 * @throws AuthenticationException If given UserCredentials are corrupt or
	 *         even missing
	 */
	public static Credentials authenticate(HttpServletRequest request) throws AuthenticationException {
		String authHeader = request.getHeader(HEADER_AUTHORIZATION);
		try {
			if (authHeader != null) {
				String[] authStr = authHeader.split(" ");
				if (authStr.length >= 2 && authStr[0].equalsIgnoreCase(HttpServletRequest.BASIC_AUTH)) {
					String decAuthStr = Base64Coder.decodeString(authStr[1]);
					int pos = decAuthStr.indexOf(':');
					final String userid = decAuthStr.substring(0, pos);
					final String passwd = decAuthStr.substring(pos + 1);
					return new Credentials() {

						@Override
						public String getPassword() {
							return passwd;
						}

						@Override
						public String getUserName() {
							return userid;
						}

					};
				}
				throw new AuthenticationException("Invalid Authentication Header for (" + DEFAULT_AUTHENTICATE_HEADER
						+ ") found.");
			} else {
				throw new AuthenticationException("No authorization header (" + DEFAULT_AUTHENTICATE_HEADER
						+ ") found.");
			}
		} catch (Exception e) {
			throw new AuthenticationException("An error has been occured due authorisation time");
		}
	}

	/**
	 * Sends back via use of marshaled HttpServletResponse an
	 * AuthenticationHeader
	 * 
	 * @param response the {@link WebdavResponse}
	 */
	public static void sendAuthorisationCommand(HttpServletResponse response) {
		try {
			response.setHeader("WWW-Authenticate", DEFAULT_AUTHENTICATE_HEADER);
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
