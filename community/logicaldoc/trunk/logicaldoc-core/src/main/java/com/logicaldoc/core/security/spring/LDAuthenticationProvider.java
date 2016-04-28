package com.logicaldoc.core.security.spring;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.authentication.AuthenticationChain;
import com.logicaldoc.core.security.dao.UserDAO;

/**
 * This Authentication provider users the <code>AuthenticationChain</code> to
 * authenticate the users.
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.5
 */
public class LDAuthenticationProvider implements AuthenticationProvider {

	private static Logger log = LoggerFactory.getLogger(LDAuthenticationProvider.class);

	private AuthenticationChain authenticationChain;

	private UserDAO userDAO;

	public LDAuthenticationProvider() {
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) authentication;
		String username = String.valueOf(auth.getPrincipal());
		String password = String.valueOf(auth.getCredentials());

		log.debug("Authenticate user " + username);

		User user = userDAO.findByUserName(username);
		if (user == null) {
			String message = "Username " + username + " not found";
			log.warn(message);
			throw new UsernameNotFoundException("notfound");
		}

		if (user.getEnabled() == 0) {
			String message = "User " + username + " is disabled";
			log.warn(message);
			throw new DisabledException("disabled");
		}

		if (userDAO.isPasswordExpired(username)) {
			String message = "Bad Credentials for user " + username;
			log.warn(message);
			throw new CredentialsExpiredException("passwordexpired");
		}

		userDAO.initialize(user);

		HttpServletRequest httpReq = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();

		Object[] userObj = new Object[] { null, null, null, null };
		String key = null;
		if (httpReq != null) {
			userObj = new Object[] { httpReq.getRemoteAddr(), httpReq.getRemoteHost(),
					SessionManager.get().getCombinedUserId(httpReq), null };
			key = httpReq.getParameter("key");
		}

		// Check the passwords match
		if (!authenticationChain.authenticate(username, password, key, userObj)) {
			String message = "User " + username + " not authenticated";
			log.warn(message);
			throw new BadCredentialsException("badcredentials");
		}

		// Preferably clear the password in the user object before storing in
		// authentication object
		user.clearPassword();

		String[] groups = user.getGroupNames();
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for (String role : groups) {
			authorities.add(new SimpleGrantedAuthority(role));
		}

		// Return an authenticated token, containing user data and
		// authorities
		LDAuthenticationToken a = new LDAuthenticationToken(user, null, authorities);
		a.setSid(AuthenticationChain.getSessionId());

		return a;
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return true;
	}

	public void setAuthenticationChain(AuthenticationChain authenticationChain) {
		this.authenticationChain = authenticationChain;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
}