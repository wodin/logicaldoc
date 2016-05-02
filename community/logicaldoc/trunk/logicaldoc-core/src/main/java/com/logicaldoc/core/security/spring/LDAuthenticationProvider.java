package com.logicaldoc.core.security.spring;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
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

import com.logicaldoc.core.security.Client;
import com.logicaldoc.core.security.Session;
import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.authentication.AccountDisabledException;
import com.logicaldoc.core.security.authentication.AccountNotFoundException;
import com.logicaldoc.core.security.authentication.PasswordExpiredException;
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

	private UserDAO userDAO;

	public LDAuthenticationProvider() {
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) authentication;
		String username = String.valueOf(auth.getPrincipal());
		String password = String.valueOf(auth.getCredentials());

		log.debug("Authenticate user " + username);

		HttpServletRequest httpReq = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();

		String key = null;
		if (httpReq != null)
			key = httpReq.getParameter("key");

		Client client = SessionManager.get().buildClient(httpReq);

		// Check the passwords match
		try {
			Session session = SessionManager.get().newSession(username, password, key, client);

			// Preferably clear the password in the user object before storing
			// in
			// authentication object
			session.getUser().clearPassword();

			String[] groups = session.getUser().getGroupNames();
			Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
			for (String role : groups) {
				authorities.add(new SimpleGrantedAuthority(role));
			}

			// Return an authenticated token, containing user data and
			// authorities
			LDAuthenticationToken a = new LDAuthenticationToken(session.getUser(), null, authorities);
			a.setSid(session.getId());

			return a;
		} catch (AccountNotFoundException nf) {
			String message = "Username " + username + " not found";
			log.warn(message);
			throw new UsernameNotFoundException(nf.getMessage());
		} catch (AccountDisabledException ad) {
			String message = "User " + username + " is disabled";
			log.warn(message);
			throw new DisabledException(ad.getMessage());
		} catch (PasswordExpiredException pe) {
			String message = "Credentials expired for user " + username;
			log.warn(message);
			throw new CredentialsExpiredException(pe.getMessage());
		} catch (com.logicaldoc.core.security.authentication.AuthenticationException ae) {
			String message = "Bad Credentials for user " + username;
			log.warn(message);
			throw new CredentialsExpiredException("badcredentials");
		}
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return true;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
}