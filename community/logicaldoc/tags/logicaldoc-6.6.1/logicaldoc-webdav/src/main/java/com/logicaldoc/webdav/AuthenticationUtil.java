package com.logicaldoc.webdav;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.jackrabbit.util.Base64;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.WebdavResponse;

import com.logicaldoc.webdav.exception.WebDavAuthorisationException;
import com.logicaldoc.webdav.web.AbstractWebdavServlet;

/**
 * 
 * Authentication through WebDAV will be managed through a more simple way.
 * Therefore
 * 
 * <pre>
 * authentication
 * </pre>
 * 
 * causes an decode of the given credentials bason on base64. Decoded
 * credentials will be turned back to caller.
 * 
 * @author Sebastian Wenzky
 * 
 */
public final class AuthenticationUtil {

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
	 * @param request
	 *            The marshaled HttpServletRequest
	 * @return valide credentials
	 * @throws WebDavAuthorisationException
	 *             If given UserCredentials are corrupt or even missing
	 */
	public static Credentials authenticate(HttpServletRequest request)
			throws WebDavAuthorisationException {
		String authHeader = request
				.getHeader(DavConstants.HEADER_AUTHORIZATION);
		try {
			if (authHeader != null) {
				String[] authStr = authHeader.split(" ");
				if (authStr.length >= 2
						&& authStr[0]
								.equalsIgnoreCase(HttpServletRequest.BASIC_AUTH)) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					Base64.decode(authStr[1].toCharArray(), out);
					String decAuthStr = out.toString("ISO-8859-1");
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
				throw new WebDavAuthorisationException(
						"Invalid Authentication Header for ("
								+ DavConstants.HEADER_AUTHORIZATION
								+ ") found.");
			} else {
				throw new WebDavAuthorisationException(
						"No authorization header ("
								+ DavConstants.HEADER_AUTHORIZATION
								+ ") found.");
			}
		} catch (Exception e) {
			throw new WebDavAuthorisationException(
					"An error has been occured due authorisation time");
		}
	}

	/**
	 * Sends back via use of marshaled HttpServletResponse an 
	 * AuthenticationHeader
	 * @param response the {@link WebdavResponse}
	 */
	public static void sendAuthorisationCommand(WebdavResponse response) {
		try {
			response.setHeader("WWW-Authenticate",
					AbstractWebdavServlet.DEFAULT_AUTHENTICATE_HEADER);
			response.sendError(DavServletResponse.SC_UNAUTHORIZED);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
