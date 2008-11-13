package com.logicaldoc.webdav.io.handler;

import org.apache.jackrabbit.server.io.IOContext;
import org.apache.jackrabbit.server.io.IOHandler;
import org.apache.jackrabbit.server.io.IOListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultIOListener implements IOListener {

    private static Logger log = LoggerFactory.getLogger(DefaultIOListener.class);

    private Logger ioLog;

    /**
     * Creates a new <code>DefaultIOListener</code>
     */
    public DefaultIOListener(Logger ioLog) {
        this.ioLog = (ioLog != null) ? ioLog : log;
    }

    /**
     * @see IOListener#onBegin(IOHandler, IOContext)
     */
    public void onBegin(IOHandler handler, IOContext ioContext) {
        ioLog.debug("Starting IOHandler (" + handler.getName() + ")");
    }

    /**
     * @see IOListener#onEnd(IOHandler, IOContext, boolean)
     */
    public void onEnd(IOHandler handler, IOContext ioContext, boolean success) {
        ioLog.debug("Result for IOHandler (" + handler.getName() + "): " + (success ? "OK" : "Failed"));
    }

    /**
     * @see IOListener#onError(IOHandler, IOContext, Exception)
     */
    public void onError(IOHandler ioHandler, IOContext ioContext, Exception e) {
        ioLog.debug("Error: " + e.getMessage());
    }
}
