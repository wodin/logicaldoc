package com.logicaldoc.core.security.authentication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java.plugin.registry.Extension;

import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.PluginRegistry;

/**
 * This authentication component implements a chain of possible authentication
 * sources that will be invoked sequentially.
 * 
 * @author Sebastian Wenzky
 * @since 4.5
 */
public class AuthenticationChain implements AuthenticationProvider {
	protected Log log = LogFactory.getLog(AuthenticationChain.class);

	private List<AuthenticationProvider> providers = new ArrayList<AuthenticationProvider>();

	private static ThreadLocal<String> sessionId = new ThreadLocal<String>() {
		protected synchronized String initialValue() {
			return null;
		}
	};

	public static String getSessionId() {
		return (String) sessionId.get();
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
			String session = SessionManager.getInstance().newSession(username, userObject);
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

		boolean loggedIn = false;
		for (AuthenticationProvider cmp : providers) {
			if (!cmp.isEnabled())
				continue;

			// validates an user for valid login credentials if a specific
			// component handles this user explicitally (e.g. admin is
			// BasicAuthentication)
			if (cmp.validateOnUser(username)) {
				loggedIn = cmp.authenticate(username, password);
				break;
			}

			loggedIn = cmp.authenticate(username, password);

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
		log.info("Authentication chain initialized");
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}