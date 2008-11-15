package com.logicaldoc.webdav.io.manager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DefaultIOManager extends IOManagerImpl {

	protected static Log log = LogFactory.getLog(DefaultIOManager.class);

    /**
     * Creates a new <code>DefaultIOManager</code> and populates the internal
     * list of <code>IOHandler</code>s by the defaults.
     *
     * @see #init()
     */
    public DefaultIOManager() {
        init();
    }


    protected void init() {
        
    }
}
