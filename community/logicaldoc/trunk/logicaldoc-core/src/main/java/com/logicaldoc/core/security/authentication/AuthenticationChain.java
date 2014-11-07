package com.logicaldoc.core.security.authentication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.java.plugin.registry.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.Tenant;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.TenantDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.plugin.PluginRegistry;

/**
 * This authentication component implements a chain of possible authentication
 * sources that will be invoked sequentially.
 * 
 * @author Sebastian Wenzky
 * @since 4.5
 */
public class AuthenticationChain implements AuthenticationProvider {
	private static Logger log = LoggerFactory.getLogger(AuthenticationChain.class);

	private List<AuthenticationProvider> providers = new ArrayList<AuthenticationProvider>();

	private static ThreadLocal<String> sessionId = new ThreadLocal<String>() {
		protected synchronized String initialValue() {
			return null;
		}
	};

	protected boolean ignoreCaseLogin() {
		ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		return "true".equals(config.getProperty("login.ignorecase"));
	}

	public static String getSessionId() {
		String sid = (String) sessionId.get();
		sessionId.remove();
		return sid;
	}

	@Override
	public final boolean authenticate(String username, String password) {
		return authenticate(username, password, null);
	}

	@Override
	public final boolean authenticate(String username, String password, Object userObject) {
		boolean loggedIn = validate(username, password);

		if (loggedIn) {
			// Create a new session and store if into the current thread
			String session = SessionManager.getInstance().newSession(username, password, userObject);
			AuthenticationChain.sessionId.set(session);
		}

		return loggedIn;
	}

	/**
	 * Try to authenticate the user without creating a new session
	 * 
	 * @param username
	 * @param password
	 * @return True only on successful authentication
	 */
	public boolean validate(String username, String password) {
		if (providers == null || providers.isEmpty())
			init();

		/*
		 * Check the anonymous login
		 */
		{
			String tenant = Tenant.DEFAULT_NAME;
			UserDAO udao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
			User user = null;

			if (ignoreCaseLogin())
				user = udao.findByUserNameIgnoreCase(username);
			else
				user = udao.findByUserName(username);

			if (user != null) {
				TenantDAO tdao = (TenantDAO) Context.getInstance().getBean(TenantDAO.class);
				Tenant t = tdao.findById(user.getTenantId());
				if (t != null)
					tenant = t.getName();
			}

			ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
			if ("true".equals(config.getProperty(tenant + ".anonymous.enabled"))
					&& username.equals(config.getProperty(tenant + ".anonymous.user")))
				return true;
		}

		boolean loggedIn = false;
		for (AuthenticationProvider cmp : providers) {
			if (!cmp.isEnabled())
				continue;

			// validates an user for valid login credentials if a specific
			// component handles this user explicitly (e.g. admin is
			// BasicAuthentication)
			if (cmp.validateOnUser(username)) {
				loggedIn = cmp.authenticate(username, password);
			}

			if (loggedIn)
				break;
		}

		return loggedIn;
	}

	@Override
	public boolean validateOnUser(String user) {
		return false;
	}

	/**
	 * Populate the providers chain using the extension point Authentication
	 * declared in the core plug-in.
	 */
	private void init() {
		Context context = Context.getInstance();
		PluginRegistry registry = PluginRegistry.getInstance();
		Collection<Extension> exts = registry.getExtensions("logicaldoc-core", "Authentication");

		// Sort the extensions according to ascending position
		List<Extension> sortedExts = new ArrayList<Extension>();
		for (Extension extension : exts) {
			sortedExts.add(extension);
		}
		Collections.sort(sortedExts, new Comparator<Extension>() {
			public int compare(Extension e1, Extension e2) {
				int position1 = Integer.parseInt(e1.getParameter("position").valueAsString());
				int position2 = Integer.parseInt(e2.getParameter("position").valueAsString());
				if (position1 < position2)
					return -1;
				else if (position1 > position2)
					return 1;
				else
					return 0;
			}
		});

		for (Extension extension : sortedExts) {
			// Retrieve the provider bean id
			providers.add((AuthenticationProvider) context
					.getBean(extension.getParameter("providerId").valueAsString()));
		}

		if (sortedExts.isEmpty())
			providers.add((AuthenticationProvider) context.getBean("BasicAuthentication"));

		log.info("Authentication chain initialized");
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}