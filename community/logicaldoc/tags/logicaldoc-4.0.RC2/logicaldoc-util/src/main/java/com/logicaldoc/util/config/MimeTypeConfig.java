package com.logicaldoc.util.config;

import java.util.Properties;

/**
 * Collects all registered extension-mimetypes mammings
 * 
 * @author Marco Meschieri
 * @author Michael Scholz
 * @version $Id: MimeTypeConfig.java,v 1.1 2007/06/29 06:28:25 marco Exp $
 * @since 3.0
 */
public class MimeTypeConfig extends ContextConfigurator {
    private Properties mimeTypes = new Properties();

    private MimeTypeConfig() {
    }

    public Properties getMimeTypes() {
        return mimeTypes;
    }

    public void setMimeTypes(Properties mimeTypes) {
        this.mimeTypes = mimeTypes;
    }

    /**
     * This method selects the application for a mimetype.
     */
    public String getMimeApp(String extension) {
        return mimeTypes.getProperty(extension);
    }
}
