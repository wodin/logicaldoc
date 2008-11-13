package com.logicaldoc.webdav.io.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultIOManager extends IOManagerImpl {

    private static Logger log = LoggerFactory.getLogger(DefaultIOManager.class);

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
