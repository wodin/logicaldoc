package com.logicaldoc.web.util;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VirtualHostHelper {

    private static final Logger log = LoggerFactory.getLogger(VirtualHostHelper.class);

    private static final String X_FORWARDED_HOST = "x-forwarded-host";

    private static final String VH_HEADER = "logicaldoc-virtual-host";

    // Utility class.
    private VirtualHostHelper() {
    }

    private static HttpServletRequest getHttpServletRequest(
            ServletRequest request) {
        if (request instanceof HttpServletRequest) {
            return (HttpServletRequest) request;
        }
        return null;
    }

    /**
     * @return WebApp name : ie : logicaldoc
     */
    public static String getWebAppName(ServletRequest request) {
        HttpServletRequest httpRequest = getHttpServletRequest(request);
        if (httpRequest == null) {
            return "logicaldoc";
        } else {
            return httpRequest.getContextPath().replace("/", "");
        }
    }

    /**
     * @return Server URL as : protocol://serverName:port/
     */
    public static String getServerURL(ServletRequest request) {
        return getServerURL(request, false);
    }

    private static String getServerUrl(String scheme, String serverName,
            int serverPort) {
        StringBuilder sbaseURL = new StringBuilder();
        sbaseURL.append(scheme);
        sbaseURL.append("://");
        sbaseURL.append(serverName);
        if (serverPort != 0) {
            if ("http".equals(scheme) && serverPort != 80
                    || "https".equals(scheme) && serverPort != 443) {
                sbaseURL.append(':');
                sbaseURL.append(serverPort);
            }
        }
        sbaseURL.append('/');
        return sbaseURL.toString();
    }

    /**
     * @return Server URL as : protocol://serverName:port/
     */
    public static String getServerURL(ServletRequest request, boolean local) {
        String baseURL = null;
        HttpServletRequest httpRequest = getHttpServletRequest(request);
        if (httpRequest != null) {
            // Detect logicaldoc specific header for VH
            String logicaldocVH = httpRequest.getHeader(VH_HEADER);
            if (!local && logicaldocVH != null && logicaldocVH.contains("http")) {
                baseURL = logicaldocVH;
            } else {
                // default values
                String serverName = httpRequest.getServerName();
                int serverPort = httpRequest.getServerPort();
                if (!local) {
                    // Detect virtual hosting based in standard header
                    String forwardedHost = httpRequest
                            .getHeader(X_FORWARDED_HOST);
                    if (forwardedHost != null) {
                        if (forwardedHost.contains(":")) {
                            serverName = forwardedHost.split(":")[0];
                            serverPort = Integer.valueOf(forwardedHost
                                    .split(":")[1]);
                        } else {
                            serverName = forwardedHost;
                            serverPort = 80; // fallback
                        }
                    }
                }
                String scheme = httpRequest.getScheme();
                baseURL = getServerUrl(scheme, serverName, serverPort);
            }
        }
        if (baseURL == null) {
            log.error("Could not retrieve base url correctly");
        }
        return baseURL;
    }

    /**
     * @return base URL as protocol://serverName:port/webappName/
     */
    public static String getBaseURL(ServletRequest request) {
        String baseURL = null;
        String serverUrl = getServerURL(request, false);
        if (serverUrl != null) {
            baseURL = serverUrl + getWebAppName(request) + '/';
        }
        if (baseURL == null) {
            log.error("Could not retrieve base url correctly");
        }
        return baseURL;
    }

}
