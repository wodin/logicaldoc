package com.logicaldoc.webdav.web;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.server.BasicCredentialsProvider;
import org.apache.jackrabbit.server.CredentialsProvider;
import org.apache.jackrabbit.server.SessionProvider;
import org.apache.jackrabbit.server.SessionProviderImpl;
import org.apache.jackrabbit.webdav.DavLocatorFactory;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.WebdavRequest;
import org.apache.jackrabbit.webdav.lock.LockManager;
import org.apache.jackrabbit.webdav.lock.SimpleLockManager;
import org.apache.jackrabbit.webdav.simple.LocatorFactoryImplEx;

import com.logicaldoc.util.Context;
import com.logicaldoc.webdav.resource.DavResourceFactory;

/**
 * For more informations, please visit
 * {@link org.apache.jackrabbit.webdav.simple.SimpleWebdavServlet}
 * 
 * @author Sebastian Wenzky
 *
 */
@SuppressWarnings("serial")
public class WebdavServlet extends AbstractWebdavServlet {

	protected static Log log = LogFactory.getLog(WebdavServlet.class);

	public static final String INIT_PARAM_RESOURCE_PATH_PREFIX = "resource-path-prefix";

	public static final String INIT_PARAM_AUTHENTICATE_HEADER = "authenticate-header";

	public final static String INIT_PARAM_MISSING_AUTH_MAPPING = "missing-auth-mapping";

	public static final String INIT_PARAM_RESOURCE_CONFIG = "resource-config";

	public static final String CTX_ATTR_RESOURCE_PATH_PREFIX = "jackrabbit.webdav.simple.resourcepath";

	private String resourcePathPrefix;

	private String authenticate_header;

	private LockManager lockManager;

	private DavResourceFactory resourceFactory;

	private DavLocatorFactory locatorFactory;

	private SessionProvider sessionProvider;

	private ResourceConfig config;

	public void init() {
		super.init();

		resourcePathPrefix = getInitParameter(INIT_PARAM_RESOURCE_PATH_PREFIX);
		if (resourcePathPrefix == null) {
			log.debug("Missing path prefix > setting to empty string.");
			resourcePathPrefix = "";
		} else if (resourcePathPrefix.endsWith("/")) {
			log.debug("Path prefix ends with '/' > removing trailing slash.");
			resourcePathPrefix = resourcePathPrefix.substring(0,
					resourcePathPrefix.length() - 1);
		}
		getServletContext().setAttribute(CTX_ATTR_RESOURCE_PATH_PREFIX,
				resourcePathPrefix);
		log.info(INIT_PARAM_RESOURCE_PATH_PREFIX + " = '" + resourcePathPrefix
				+ "'");

		authenticate_header = getInitParameter(INIT_PARAM_AUTHENTICATE_HEADER);
		if (authenticate_header == null) {
			authenticate_header = DEFAULT_AUTHENTICATE_HEADER;
		}
		log.info("WWW-Authenticate header = '" + authenticate_header + "'");

		String configParam = getInitParameter(INIT_PARAM_RESOURCE_CONFIG);
		if (configParam != null) {
			try {
				config = getResourceConfig();
			} catch (Exception e) {
				log.debug("Unable to build resource filter provider.");
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean isPreconditionValid(WebdavRequest request,
			DavResource resource) {
		return !resource.exists() || request.matchesIfHeader(resource);
	}

	public String getPathPrefix() {
		return resourcePathPrefix;
	}

	public static String getPathPrefix(ServletContext ctx) {
		return (String) ctx.getAttribute(CTX_ATTR_RESOURCE_PATH_PREFIX);
	}

	public DavLocatorFactory getLocatorFactory() {
		if (locatorFactory == null) {
			locatorFactory = new LocatorFactoryImplEx(resourcePathPrefix);
		}
		return locatorFactory;
	}

	public void setLocatorFactory(DavLocatorFactory locatorFactory) {
		this.locatorFactory = locatorFactory;
	}

	public LockManager getLockManager() {
		if (lockManager == null) {
			lockManager = new SimpleLockManager();
		}
		return lockManager;
	}

	public void setLockManager(LockManager lockManager) {
		this.lockManager = lockManager;
	}

	public DavResourceFactory getResourceFactory() {
		if (resourceFactory == null) {
			resourceFactory = new ResourceFactoryImpl(getLockManager(),
					getResourceConfig());
		}
		return resourceFactory;
	}

	public void setResourceFactory(DavResourceFactory resourceFactory) {
		this.resourceFactory = resourceFactory;
	}

	public synchronized SessionProvider getSessionProvider() {
		if (sessionProvider == null) {
			sessionProvider = new SessionProviderImpl(getCredentialsProvider());
		}
		return sessionProvider;
	}

	protected CredentialsProvider getCredentialsProvider() {
		return new BasicCredentialsProvider(
				getInitParameter(INIT_PARAM_MISSING_AUTH_MAPPING));
	}

	public synchronized void setSessionProvider(SessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	public String getAuthenticateHeaderValue() {
		return authenticate_header;
	}

	public ResourceConfig getResourceConfig() {
		// fallback if no config present
		if (config != null)
			return config;
		else
			return (ResourceConfig) Context.getInstance().getBean(
					"ResourceConfig");
	}

	public void setResourceConfig(ResourceConfig config) {
		this.config = config;
	}

	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.service(request, response);
	}
}
