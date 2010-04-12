package com.logicaldoc.webdav.io.manager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * For more informations, please visit
 * {@link org.apache.jackrabbit.server.io.DefaultIOManager}
 * 
 * @author Sebastian Wenzky
 * 
 */
public class DefaultIOManager extends IOManagerImpl {

	protected static Log log = LogFactory.getLog(DefaultIOManager.class);

    public DefaultIOManager() {
        init();
    }

    protected void init() {
    }
}
