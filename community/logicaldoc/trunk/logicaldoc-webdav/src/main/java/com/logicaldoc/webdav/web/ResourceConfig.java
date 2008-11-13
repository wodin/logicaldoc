package com.logicaldoc.webdav.web;

import org.apache.jackrabbit.server.io.MimeResolver;
import org.apache.jackrabbit.server.io.PropertyManager;
import org.apache.jackrabbit.server.io.PropertyManagerImpl;
import org.apache.jackrabbit.webdav.simple.DefaultItemFilter;
import org.apache.jackrabbit.webdav.simple.ItemFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.webdav.io.manager.IOManager;

public class ResourceConfig {

    private static Logger log = LoggerFactory.getLogger(ResourceConfig.class);

    private ItemFilter itemFilter;
    private IOManager ioManager;
    private PropertyManager propManager;
    private String[] nodetypeNames = new String[0];
    private boolean collectionNames = false;
    private MimeResolver mimeResolver;

    /**
     * 
     * @param ioManager
     */
    public void setIOManager(IOManager ioManager) {
		this.ioManager = ioManager;
	}
    
    /**
     *
     * @return
     */
    public IOManager getIOManager() {
        return ioManager;
    }

    /**
     *
     * @return
     */
    public PropertyManager getPropertyManager() {
        if (propManager == null) {
            log.debug("ResourceConfig: missing property-manager > building default.");
            propManager = PropertyManagerImpl.getDefaultManager();
        }
        return propManager;
    }

    /**
     * Returns the item filter specified with the configuration or {@link DefaultItemFilter}
     * if the configuration was missing the corresponding entry or the parser failed
     * to build a <code>ItemFilter</code> instance from the configuration.
     *
     * @return item filter as defined by the config or {@link DefaultItemFilter}
     */
    public ItemFilter getItemFilter() {
        if (itemFilter == null) {
            log.debug("ResourceConfig: missing resource filter > building DefaultItemFilter ");
            itemFilter = new DefaultItemFilter();
        }
        return itemFilter;
    }

    /**
     *
     * @return
     */
    public MimeResolver getMimeResolver() {
        return mimeResolver;
    }
}
