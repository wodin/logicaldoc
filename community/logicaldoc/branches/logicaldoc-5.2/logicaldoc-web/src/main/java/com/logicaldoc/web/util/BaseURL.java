package com.logicaldoc.web.util;

import javax.faces.context.FacesContext;
import javax.servlet.ServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BaseURL {

    private static final Log log = LogFactory.getLog(BaseURL.class);

    private BaseURL() {
    }

    static ServletRequest getRequest() {
        final FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext == null) {
            return null;
        }
        ServletRequest request = (ServletRequest) facesContext.getExternalContext().getRequest();
        return request;
    }

    public static String getServerURL() {
        return getServerURL(getRequest(), false);
    }

    /**
     * @return Server URL as : protocol://serverName:port/
     */
    public static String getServerURL(ServletRequest request, boolean local) {
        return VirtualHostHelper.getServerURL(request, local);
    }

    /**
     * @return WebApp name : ie : logicaldoc
     */
    public static String getWebAppName() {
        final FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            String baseURL = facesContext.getExternalContext().getRequestContextPath();

            if (baseURL==null) {
                return "logicaldoc";
            }

            baseURL = baseURL.replace("/", "");
            return baseURL;
        } else {
            return "logicaldoc";
        }
    }

    /**
     * @return base URL as protocol://serverName:port/webappName/
     */
    public static String getBaseURL() {
        return getBaseURL(getRequest());
    }

    public static String getBaseURL(ServletRequest request) {
        return VirtualHostHelper.getBaseURL(request);
    }

    public static String getLocalBaseURL(ServletRequest request) {
        String localURL = null;
        String serverUrl = getServerURL(request, true);
        if (serverUrl != null) {
            localURL = serverUrl + getWebAppName() + '/';
        }
        if (localURL == null) {
            log.error("Could not retrieve loacl url correctly");
        }
        return localURL;
    }
}
