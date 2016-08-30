package com.logicaldoc.core.security.spring;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.logicaldoc.core.security.User;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;

/**
 * This Authentication provider simply checks if the provided password matches
 * the context property 'adminpswd'
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.5
 */
public class AdminPasswdAuthenticationProvider implements AuthenticationProvider {
	public AdminPasswdAuthenticationProvider() {
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) authentication;
		String username = String.valueOf(auth.getPrincipal());
		String password = String.valueOf(auth.getCredentials());

		if (!"admin".equals(username))
			throw new BadCredentialsException("badcredentials");

		ContextProperties config = Context.get().getProperties();
		if (StringUtils.isEmpty(config.getProperty("adminpasswd")))
			throw new BadCredentialsException("badcredentials");

		User user = new User();
		user.setDecodedPassword(password);

		if (!user.getPassword().equals(config.getProperty("adminpasswd")))
			throw new BadCredentialsException("badcredentials");

		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("admin"));

		// Return an authenticated token, containing user data and authorities
		LDAuthenticationToken a = new LDAuthenticationToken(user, null, authorities);
		return a;
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return true;
	}
}