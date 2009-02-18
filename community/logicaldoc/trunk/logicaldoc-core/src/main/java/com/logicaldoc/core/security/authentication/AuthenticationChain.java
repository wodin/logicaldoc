package com.logicaldoc.core.security.authentication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java.plugin.registry.Extension;

import com.logicaldoc.util.Context;
import com.logicaldoc.util.PluginRegistry;

/**
 * This authentication component implements a chain of possible authentication
 * sources that will be invoked sequentially.
 * 
 * @author Sebastian Wenzky
 * @since 4.5
 */
public class AuthenticationChain implements AuthenticationComponent {
	protected Log log = LogFactory.getLog(AuthenticationChain.class);

	private List<AuthenticationComponent> authenticationComponents = new ArrayList<AuthenticationComponent>();

	public void setAuthenticationComponents(List<AuthenticationComponent> authenticationComponents) {
		this.authenticationComponents = authenticationComponents;
	}

	@Override
	public boolean authenticate(String username, String password) {
		if (authenticationComponents == null || authenticationComponents.isEmpty())
			init();

		for (AuthenticationComponent cmp : authenticationComponents) {
			System.out.println("** " + cmp);

			// validate on user will be true, if a specific component manages
			// this user explicitally (e.g. admin is BasicAuthentication)
			if (cmp.validateOnUser(username)) {
				System.out.println("** validateOnUser");
				return cmp.authenticate(username, password);
			}

			boolean loggedIn = cmp.authenticate(username, password);

			System.out.println("** " + loggedIn);

			if (loggedIn)
				return true;
		}

		return false;
	}

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
			// Retrieve the task name
			authenticationComponents.add((AuthenticationComponent) context.getBean(extension.getParameter("providerId")
					.valueAsString()));
		}
		log.info("Authentication chain initialized");
	}

	@Override
	public boolean validateOnUser(String user) {
		return false;
	}
}