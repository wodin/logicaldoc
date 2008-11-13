package com.logicaldoc.webdav.web;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.server.BasicCredentialsProvider;
import org.apache.jackrabbit.server.CredentialsProvider;
import org.apache.jackrabbit.server.SessionProvider;
import org.apache.jackrabbit.server.SessionProviderImpl;
import org.apache.jackrabbit.webdav.DavLocatorFactory;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavSessionProvider;
import org.apache.jackrabbit.webdav.WebdavRequest;
import org.apache.jackrabbit.webdav.lock.LockManager;
import org.apache.jackrabbit.webdav.lock.SimpleLockManager;
import org.apache.jackrabbit.webdav.simple.DavSessionProviderImpl;
import org.apache.jackrabbit.webdav.simple.LocatorFactoryImplEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.util.Context;
import com.logicaldoc.webdav.resource.DavResourceFactory;


public class WebdavServlet extends AbstractWebdavServlet{

    /**
     * the default logger
     */
	private static final Logger log = LoggerFactory.getLogger(WebdavServlet.class);

    /**
     * init param name of the repository prefix
     */
    public static final String INIT_PARAM_RESOURCE_PATH_PREFIX = "resource-path-prefix";

    /**
     * Name of the optional init parameter that defines the value of the
     * 'WWW-Authenticate' header.<p/>
     * If the parameter is omitted the default value
     * {@link #DEFAULT_AUTHENTICATE_HEADER "Basic Realm=Jackrabbit Webdav Server"}
     * is used.
     *
     * @see #getAuthenticateHeaderValue()
     */
    public static final String INIT_PARAM_AUTHENTICATE_HEADER = "authenticate-header";

    /** the 'missing-auth-mapping' init parameter */
    public final static String INIT_PARAM_MISSING_AUTH_MAPPING = "missing-auth-mapping";

    /**
     * Name of the init parameter that specify a separate configuration used
     * for filtering the resources displayed.
     */
    public static final String INIT_PARAM_RESOURCE_CONFIG = "resource-config";

    /**
     * Servlet context attribute used to store the path prefix instead of
     * having a static field with this servlet. The latter causes problems
     * when running multiple
     */
    public static final String CTX_ATTR_RESOURCE_PATH_PREFIX = "jackrabbit.webdav.simple.resourcepath";

    /**
     * the resource path prefix
     */
    private String resourcePathPrefix;

    /**
     * Header value as specified in the {@link #INIT_PARAM_AUTHENTICATE_HEADER} parameter.
     */
    private String authenticate_header;

    /**
     * Map used to remember any webdav lock created without being reflected
     * in the underlying repository.
     * This is needed because some clients rely on a successful locking
     * mechanism in order to perform properly (e.g. mac OSX built-in dav client)
     */
    private LockManager lockManager;

    /**
     * the resource factory
     */
    private DavResourceFactory resourceFactory;

    /**
     * the locator factory
     */
    private DavLocatorFactory locatorFactory;

    /**
     * the webdav session provider
     */
    private DavSessionProvider davSessionProvider;

    /**
     * the repository session provider
     */
    private SessionProvider sessionProvider;

    /**
     * The config
     */
    private ResourceConfig config;

    
    public void init() {
        super.init();
        
        resourcePathPrefix = getInitParameter(INIT_PARAM_RESOURCE_PATH_PREFIX);
        if (resourcePathPrefix == null) {
            log.debug("Missing path prefix > setting to empty string.");
            resourcePathPrefix = "";
        } else if (resourcePathPrefix.endsWith("/")) {
            log.debug("Path prefix ends with '/' > removing trailing slash.");
            resourcePathPrefix = resourcePathPrefix.substring(0, resourcePathPrefix.length() - 1);
        }
        getServletContext().setAttribute(CTX_ATTR_RESOURCE_PATH_PREFIX, resourcePathPrefix);
        log.info(INIT_PARAM_RESOURCE_PATH_PREFIX + " = '" + resourcePathPrefix + "'");

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

    /**
     * Returns the configured path prefix
     *
     * @return resourcePathPrefix
     * @see #INIT_PARAM_RESOURCE_PATH_PREFIX
     */
    public String getPathPrefix() {
        return resourcePathPrefix;
    }

    /**
     * Returns the configured path prefix
     *
     * @return resourcePathPrefix
     * @see #INIT_PARAM_RESOURCE_PATH_PREFIX
     */
    public static String getPathPrefix(ServletContext ctx) {
        return (String) ctx.getAttribute(CTX_ATTR_RESOURCE_PATH_PREFIX);
    }

    /**
     * Returns the <code>DavLocatorFactory</code>. If no locator factory has
     * been set or created a new instance of {@link org.apache.jackrabbit.webdav.simple.LocatorFactoryImpl} is
     * returned.
     *
     * @return the locator factory
     * @see AbstractWebdavServlet#getLocatorFactory()
     */
    public DavLocatorFactory getLocatorFactory() {
        if (locatorFactory == null) {
            locatorFactory = new LocatorFactoryImplEx(resourcePathPrefix);
        }
        return locatorFactory;
    }

    /**
     * Sets the <code>DavLocatorFactory</code>.
     *
     * @param locatorFactory
     * @see AbstractWebdavServlet#setLocatorFactory(DavLocatorFactory)
     */
    public void setLocatorFactory(DavLocatorFactory locatorFactory) {
        this.locatorFactory = locatorFactory;
    }

    /**
     * Returns the <code>LockManager</code>. If no lock manager has
     * been set or created a new instance of {@link SimpleLockManager} is
     * returned.
     *
     * @return the lock manager
     */
    public LockManager getLockManager() {
        if (lockManager == null) {
            lockManager = new SimpleLockManager();
        }
        return lockManager;
    }

    /**
     * Sets the <code>LockManager</code>.
     *
     * @param lockManager
     */
    public void setLockManager(LockManager lockManager) {
        this.lockManager = lockManager;
    }

    /**
     * Returns the <code>DavResourceFactory</code>. If no request factory has
     * been set or created a new instance of {@link ResourceFactoryImpl} is
     * returned.
     *
     * @return the resource factory
     * @see AbstractWebdavServlet#getResourceFactory()
     */
    public DavResourceFactory getResourceFactory() {
        if (resourceFactory == null) {
            resourceFactory = new ResourceFactoryImpl(getLockManager(), getResourceConfig());
        }
        return resourceFactory;
    }

    /**
     * Sets the <code>DavResourceFactory</code>.
     *
     * @param resourceFactory
     * @see AbstractWebdavServlet#setResourceFactory(org.apache.jackrabbit.webdav.DavResourceFactory)
     */
    public void setResourceFactory(DavResourceFactory resourceFactory) {
        this.resourceFactory = resourceFactory;
    }

    /**
     * Returns the <code>SessionProvider</code>. If no session provider has been
     * set or created a new instance of {@link SessionProviderImpl} that extracts
     * credentials from the request's <code>Authorization</code> header is
     * returned.
     *
     * @return the session provider
     */
    public synchronized SessionProvider getSessionProvider() {
        if (sessionProvider == null) {
            sessionProvider = new SessionProviderImpl(getCredentialsProvider());
        }
        return sessionProvider;
    }

    /**
     * Factory method for creating the credentials provider to be used for
     * accessing the credentials associated with a request. The default
     * implementation returns a {@link BasicCredentialsProvider} instance,
     * but subclasses can override this method to add support for other
     * types of credentials.
     *
     * @return the credentilas provider
     * @since 1.3
     */
    protected CredentialsProvider getCredentialsProvider() {
    	return new BasicCredentialsProvider(getInitParameter(INIT_PARAM_MISSING_AUTH_MAPPING));
    }

    /**
     * Sets the <code>SessionProvider</code>.
     *
     * @param sessionProvider
     */
    public synchronized void setSessionProvider(SessionProvider sessionProvider) {
        this.sessionProvider = sessionProvider;
    }

    /**
     * Returns the <code>DavSessionProvider</code>. If no session provider has
     * been set or created a new instance of {@link DavSessionProviderImpl}
     * is returned.
     *
     * @return the session provider
     * @see AbstractWebdavServlet#getDavSessionProvider()
     */
    public synchronized DavSessionProvider getDavSessionProvider() {
        if (davSessionProvider == null) {
            davSessionProvider =
                new DavSessionProviderImpl(null, getSessionProvider());
        }
        return davSessionProvider;
    }

    /**
     * Sets the <code>DavSessionProvider</code>.
     *
     * @param sessionProvider
     * @see AbstractWebdavServlet#setDavSessionProvider(org.apache.jackrabbit.webdav.DavSessionProvider)
     */
    public synchronized void setDavSessionProvider(DavSessionProvider sessionProvider) {
        this.davSessionProvider = sessionProvider;
    }

    /**
     * Returns the header value retrieved from the {@link #INIT_PARAM_AUTHENTICATE_HEADER}
     * init parameter. If the parameter is missing, the value defaults to
     * {@link #DEFAULT_AUTHENTICATE_HEADER}.
     *
     * @return the header value retrieved from the corresponding init parameter
     * or {@link #DEFAULT_AUTHENTICATE_HEADER}.
     * @see AbstractWebdavServlet#getAuthenticateHeaderValue()
     */
    public String getAuthenticateHeaderValue() {
        return authenticate_header;
    }

    /**
     * Returns the resource configuration to be applied
     *
     * @return the resource configuration.
     */
    public ResourceConfig getResourceConfig() {
        // fallback if no config present
    	return (ResourceConfig) Context.getInstance().getBean("ResourceConfig");
    }

    /**
     * Set the resource configuration
     *
     * @param config
     */
    public void setResourceConfig(ResourceConfig config) {
        this.config = config;
    }
    
    public void service(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    	super.service(request, response);
    }
}
