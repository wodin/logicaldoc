package com.logicaldoc.webdav.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavLocatorFactory;
import org.apache.jackrabbit.webdav.DavMethods;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavServletRequest;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.DavSessionProvider;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.WebdavRequest;
import org.apache.jackrabbit.webdav.WebdavRequestImpl;
import org.apache.jackrabbit.webdav.WebdavResponse;
import org.apache.jackrabbit.webdav.WebdavResponseImpl;
import org.apache.jackrabbit.webdav.io.InputContext;
import org.apache.jackrabbit.webdav.io.InputContextImpl;
import org.apache.jackrabbit.webdav.io.OutputContext;
import org.apache.jackrabbit.webdav.io.OutputContextImpl;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;

import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.webdav.AuthenticationUtil;
import com.logicaldoc.webdav.AuthenticationUtil.Credentials;
import com.logicaldoc.webdav.resource.DavResourceFactory;
import com.logicaldoc.webdav.session.DavSessionImpl;

/**
 * <code>AbstractWebdavServlet</code>
 * <p/>
 */
abstract public class AbstractWebdavServlet extends HttpServlet implements DavConstants {

	protected static Log log = LogFactory.getLog(AbstractWebdavServlet.class);
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -8726695805361483901L;

	private UserDAO userDAO;
	

    /**
     * Default value for the 'WWW-Authenticate' header, that is set, if request
     * results in a {@link DavServletResponse#SC_UNAUTHORIZED 401 (Unauthorized)}
     * error.
     *
     * @see #getAuthenticateHeaderValue()
     */
    public static final String DEFAULT_AUTHENTICATE_HEADER = "Basic realm=\"Jackrabbit Webdav Server\"";

    /**
     * Checks if the precondition for this request and resource is valid.
     *
     * @param request
     * @param resource
     * @return
     */
    abstract protected boolean isPreconditionValid(WebdavRequest request, DavResource resource);

    /**
     * Returns the <code>DavSessionProvider</code>.
     *
     * @return the session provider
     */
    abstract public DavSessionProvider getDavSessionProvider();

    /**
     * Returns the <code>DavSessionProvider</code>.
     *
     * @param davSessionProvider
     */
    abstract public void setDavSessionProvider(DavSessionProvider davSessionProvider);

    /**
     * Returns the <code>DavLocatorFactory</code>.
     *
     * @return the locator factory
     */
    abstract public DavLocatorFactory getLocatorFactory();

    /**
     * Sets the <code>DavLocatorFactory</code>.
     *
     * @param locatorFactory
     */
    abstract public void setLocatorFactory(DavLocatorFactory locatorFactory);

    /**
     * Returns the <code>DavResourceFactory</code>.
     *
     * @return the resource factory
     */
    abstract public DavResourceFactory getResourceFactory();

    /**
     * Sets the <code>DavResourceFactory</code>.
     *
     * @param resourceFactory
     */
    abstract public void setResourceFactory(DavResourceFactory resourceFactory);

    /**
     * Returns the value of the 'WWW-Authenticate' header, that is returned in
     * case of 401 error.
     *
     * @return value of the 'WWW-Authenticate' header
     */
    abstract public String getAuthenticateHeaderValue();

    
    
    public void init(){
    	this.userDAO = (UserDAO) Context.getInstance().getBean(UserDAO.class);
    }
    
    /**
     * Service the given request.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    	
    	HttpSession session = request.getSession(true);

        WebdavRequest webdavRequest = new WebdavRequestImpl(request, getLocatorFactory());
     
        // DeltaV requires 'Cache-Control' header for all methods except 'VERSION-CONTROL' and 'REPORT'.
        int methodCode = DavMethods.getMethodCode(request.getMethod());
        boolean noCache = DavMethods.isDeltaVMethod(webdavRequest) && !(DavMethods.DAV_VERSION_CONTROL == methodCode || DavMethods.DAV_REPORT == methodCode);
        WebdavResponse webdavResponse = new WebdavResponseImpl(response, noCache);
        
        try {
    		if(session.getAttribute("name") == null){
        		if(request.getHeader(DavConstants.HEADER_AUTHORIZATION) != null){
        			Credentials credentials = AuthenticationUtil.authenticate(webdavRequest);
        			boolean isLoggedOn = userDAO.validateUser(credentials.getUserName(), credentials.getPassword());
        			if(isLoggedOn == false) {
        				AuthenticationUtil.sendAuthorisationCommand(webdavResponse);
        				return;
        			}
        			
        			session.setAttribute("name", credentials.getUserName());
        			
        		}
        		else {
        			AuthenticationUtil.sendAuthorisationCommand(webdavResponse);
    				return;
        		}
        	}
        	
                
        	DavSessionImpl davSession = new DavSessionImpl();
        	davSession.putObject("id", userDAO.findByUserName(session.getAttribute("name").toString()).getId());
        	davSession.putObject("name", session.getAttribute("name"));
        		
            //check matching if=header for lock-token relevant operations
            DavResource resource = getResourceFactory().createResource(webdavRequest.getRequestLocator(), webdavRequest, webdavResponse, davSession);

            if (!isPreconditionValid(webdavRequest, resource)) {
                webdavResponse.sendError(DavServletResponse.SC_PRECONDITION_FAILED);
                return;
            }
            if (!execute(webdavRequest, webdavResponse, methodCode, resource)) {
                super.service(request, response);
            }

        } catch (DavException e) {
            if (e.getErrorCode() == HttpServletResponse.SC_UNAUTHORIZED) {
            	e.printStackTrace();
            } else {
                webdavResponse.sendError(e);
            }
        } catch (Exception e) {
			e.printStackTrace();
		} finally {
            
        }
        
        response.getOutputStream().flush();
        response.getOutputStream().close();
    }

    /**
     * Executes the respective method in the given webdav context
     *
     * @param request
     * @param response
     * @param method
     * @param resource
     * @throws ServletException
     * @throws IOException
     * @throws DavException
     */
    protected boolean execute(WebdavRequest request, WebdavResponse response,
                              int method, DavResource resource)
            throws ServletException, IOException, DavException {
    
        switch (method) {
            case DavMethods.DAV_GET:
                doGet(request, response, resource);
                break;
            case DavMethods.DAV_HEAD:
                doHead(request, response, resource);
                break;
            case DavMethods.DAV_PROPFIND:
                doPropFind(request, response, resource);
                break;
            case DavMethods.DAV_PROPPATCH:
                doPropPatch(request, response, resource);
                break;
            case DavMethods.DAV_POST:
                doPost(request, response, resource);
                break;
            case DavMethods.DAV_PUT:
                doPut(request, response, resource);
                break;
            case DavMethods.DAV_DELETE:
                doDelete(request, response, resource);
                break;
            case DavMethods.DAV_COPY:
                doCopy(request, response, resource);
                break;
            case DavMethods.DAV_MOVE:
                doMove(request, response, resource);
                break;
            case DavMethods.DAV_MKCOL:
                doMkCol(request, response, resource);
                break;
            default:
                // any other method
                return false;
        }
        return true;
    }

    /**
     * The HEAD method
     *
     * @param request
     * @param response
     * @param resource
     * @throws IOException
     */
    protected void doHead(WebdavRequest request, WebdavResponse response,
                          DavResource resource) throws IOException {
        spoolResource(request, response, resource, false);
    }

    /**
     * The GET method
     *
     * @param request
     * @param response
     * @param resource
     * @throws IOException
     */
    protected void doGet(WebdavRequest request, WebdavResponse response,
                         DavResource resource) throws IOException {
        spoolResource(request, response, resource, true);
    }

    /**
     * @param request
     * @param response
     * @param resource
     * @param sendContent
     * @throws IOException
     */
    private void spoolResource(WebdavRequest request, WebdavResponse response,
                               DavResource resource, boolean sendContent)
            throws IOException {

        if (!resource.exists()) {
        	response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        long modSince = request.getDateHeader("If-Modified-Since");
        if (modSince > UNDEFINED_TIME) {
            long modTime = resource.getModificationTime();
            // test if resource has been modified. note that formatted modification
            // time lost the milli-second precision
            if (modTime != UNDEFINED_TIME && (modTime / 1000 * 1000) <= modSince) {
                // resource has not been modified since the time indicated in the
                // 'If-Modified-Since' header.
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }
        }

        //spool resource properties and ev. resource content.
        OutputStream out = (sendContent) ? response.getOutputStream() : null;
        resource.spool(getOutputContext(response, out));
        response.flushBuffer();
    }

    /**
     * The PROPFIND method
     *
     * @param request
     * @param response
     * @param resource
     * @throws IOException
     */
    protected void doPropFind(WebdavRequest request, WebdavResponse response,
                              DavResource resource) throws IOException, DavException {

    	if(log.isDebugEnabled())
    		log.debug("[READ] FINDING " +(resource.isCollection()?"DOCUMENTS WITHING THE FOLDER ":"JUST THE DOCUMENT ") + resource.getDisplayName());
    	
        if (!resource.exists()) {
            response.sendError(DavServletResponse.SC_NOT_FOUND);
            return;
        }

        int depth = request.getDepth(DEPTH_INFINITY);
        DavPropertyNameSet requestProperties = request.getPropFindProperties();
        int propfindType = request.getPropFindType();

        MultiStatus mstatus = new MultiStatus();
        
        mstatus.addResourceProperties(resource, requestProperties, propfindType, depth);
        response.sendMultiStatus(mstatus);
    }

    /**
     * The PROPPATCH method
     *
     * @param request
     * @param response
     * @param resource
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    protected void doPropPatch(WebdavRequest request, WebdavResponse response,
                               DavResource resource)
            throws IOException, DavException {

        List changeList = request.getPropPatchChangeList();
        if (changeList.isEmpty()) {
            response.sendError(DavServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        MultiStatus ms = new MultiStatus();
        MultiStatusResponse msr = resource.alterProperties(changeList);
        ms.addResponse(msr);
        response.sendMultiStatus(ms);
    }

    /**
     * The POST method. Delegate to PUT
     *
     * @param request
     * @param response
     * @param resource
     * @throws IOException
     * @throws DavException
     */
    protected void doPost(WebdavRequest request, WebdavResponse response,
                          DavResource resource) throws IOException, DavException {
        doPut(request, response, resource);
    }

    /**
     * The PUT method
     *
     * @param request
     * @param response
     * @param resource
     * @throws IOException
     * @throws DavException
     */
    protected void doPut(WebdavRequest request, WebdavResponse response,
                         DavResource resource) throws IOException, DavException {
    	
    	if(log.isDebugEnabled())
    		log.debug("[ADD] Document " + resource.getDisplayName());
    	
        DavResource parentResource = resource.getCollection();
        if (parentResource == null || !parentResource.exists()) {
            // parent does not exist
            response.sendError(DavServletResponse.SC_CONFLICT);
            return;
        }

        int status;
        // test if resource already exists
        if (resource.exists()) {
            status = DavServletResponse.SC_NO_CONTENT;
        } else {
            status = DavServletResponse.SC_CREATED;
        }

       
        
        
        parentResource.addMember(resource, getInputContext(request, request
				.getInputStream()));
        response.setStatus(status);
    }

    /**
     * The MKCOL method
     *
     * @param request
     * @param response
     * @param resource
     * @throws IOException
     * @throws DavException
     */
    protected void doMkCol(WebdavRequest request, WebdavResponse response,
                           DavResource resource) throws IOException, DavException {

    	if(log.isDebugEnabled())
    		log.debug("[ADD] Directory " + resource.getDisplayName());
        DavResource parentResource = resource.getCollection();
        if (parentResource == null || !parentResource.exists() || !parentResource.isCollection()) {
            // parent does not exist or is not a collection
            response.sendError(DavServletResponse.SC_CONFLICT);
            return;
        }
        // shortcut: mkcol is only allowed on deleted/non-existing resources
        if (resource.exists()) {
            response.sendError(DavServletResponse.SC_METHOD_NOT_ALLOWED);
        }

        if (request.getContentLength() > 0 || request.getHeader("Transfer-Encoding") != null) {
            parentResource.addMember(resource, getInputContext(request, request.getInputStream()));
        } else {
            parentResource.addMember(resource, getInputContext(request, null));
        }
        response.setStatus(DavServletResponse.SC_CREATED);
    }

    /**
     * The DELETE method
     *
     * @param request
     * @param response
     * @param resource
     * @throws IOException
     * @throws DavException
     */
    protected void doDelete(WebdavRequest request, WebdavResponse response,
                            DavResource resource) throws IOException, DavException {
        
    	if(log.isDebugEnabled())
    		log.debug("[DELETE]" + (resource.isCollection()?" FOLDER":" DOCUMENT") + " " + resource.getDisplayName());
    	
    	DavResource parent = resource.getCollection();
        if (parent != null) {
            parent.removeMember(resource);
            response.setStatus(DavServletResponse.SC_NO_CONTENT);
        } else {
            response.sendError(DavServletResponse.SC_FORBIDDEN, "Cannot remove the root resource.");
        }
    }

    /**
     * The COPY method
     *
     * @param request
     * @param response
     * @param resource
     * @throws IOException
     * @throws DavException
     */
    protected void doCopy(WebdavRequest request, WebdavResponse response,
                          DavResource resource) throws IOException, DavException {

        // only depth 0 and infinity is allowed
        int depth = request.getDepth(DEPTH_INFINITY);
        if (!(depth == DEPTH_0 || depth == DEPTH_INFINITY)) {
            response.sendError(DavServletResponse.SC_BAD_REQUEST);
            return;
        }

        DavResource destResource = getResourceFactory().createResource(request.getDestinationLocator(), request, response);
        int status = validateDestination(destResource, request);
        if (status > DavServletResponse.SC_NO_CONTENT) {
            response.sendError(status);
            return;
        }

        resource.copy(destResource, depth == DEPTH_0);
        response.setStatus(status);
    }

    /**
     * The MOVE method
     *
     * @param request
     * @param response
     * @param resource
     * @throws IOException
     * @throws DavException
     */
    protected void doMove(WebdavRequest request, WebdavResponse response,
                          DavResource resource) throws IOException, DavException {

        DavResource destResource = getResourceFactory().createResource(request.getDestinationLocator(), request, response);
        int status = validateDestination(destResource, request);
        if (status > DavServletResponse.SC_NO_CONTENT) {
            response.sendError(status);
            return;
        }

        resource.move(destResource);
        response.setStatus(status);
    }

    /**
     * Validate the given destination resource and return the proper status
     * code: Any return value greater/equal than {@link DavServletResponse#SC_NO_CONTENT}
     * indicates an error.
     *
     * @param destResource destination resource to be validated.
     * @param request
     * @return status code indicating whether the destination is valid.
     */
    private int validateDestination(DavResource destResource, WebdavRequest request)
            throws DavException {

        String destHeader = request.getHeader(HEADER_DESTINATION);
        if (destHeader == null || "".equals(destHeader)) {
            return DavServletResponse.SC_BAD_REQUEST;
        }
        if (destResource.getLocator().equals(request.getRequestLocator())) {
            return DavServletResponse.SC_FORBIDDEN;
        }

        int status;
        if (destResource.exists()) {
            if (request.isOverwrite()) {
                // matching if-header required for existing resources
                if (!request.matchesIfHeader(destResource)) {
                    return DavServletResponse.SC_PRECONDITION_FAILED;
                } else {
                    // overwrite existing resource
                    destResource.getCollection().removeMember(destResource);
                    status = DavServletResponse.SC_NO_CONTENT;
                }
            } else {
                // cannot copy/move to an existing item, if overwrite is not forced
                return DavServletResponse.SC_PRECONDITION_FAILED;
            }
        } else {
            // destination does not exist >> copy/move can be performed
            status = DavServletResponse.SC_CREATED;
        }
        return status;
    }

    /**
     * Return a new <code>InputContext</code> used for adding resource members
     *
     * @param request
     * @param in
     * @return
     * @see #spoolResource(WebdavRequest, WebdavResponse, DavResource, boolean)
     */
    protected InputContext getInputContext(DavServletRequest request, InputStream in) {
        return new InputContextImpl(request, in);
    }

    /**
     * Return a new <code>OutputContext</code> used for spooling resource properties and
     * the resource content
     *
     * @param response
     * @param out
     * @return
     * @see #doPut(WebdavRequest, WebdavResponse, DavResource)
     * @see #doPost(WebdavRequest, WebdavResponse, DavResource)
     * @see #doMkCol(WebdavRequest, WebdavResponse, DavResource)
     */
    protected OutputContext getOutputContext(DavServletResponse response, OutputStream out) {
        return new OutputContextImpl(response, out);
    }
}

