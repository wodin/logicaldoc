package com.logicaldoc.webdav.context;

import org.apache.jackrabbit.server.io.DefaultIOListener;
import org.apache.jackrabbit.server.io.IOListener;
import org.apache.jackrabbit.server.io.IOUtil;
import org.apache.jackrabbit.server.io.MimeResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.webdav.resource.model.Resource;

public abstract class AbstractExportContext implements ExportContext {

    private static Logger log = LoggerFactory.getLogger(AbstractExportContext.class);

    private final IOListener ioListener;
    private final Resource resource;
    private final boolean hasStream;
    private final MimeResolver mimeResolver;

    protected boolean completed;

    public AbstractExportContext(Resource resource, boolean hasStream,
                                 IOListener ioListener) {
        this(resource, hasStream, ioListener, null);
    }

    public AbstractExportContext(Resource resource, boolean hasStream,
                                 IOListener ioListener, MimeResolver mimeResolver) {
        this.resource = resource;
        this.hasStream = hasStream;
        this.ioListener = (ioListener != null) ? ioListener : new DefaultIOListener(log);
        this.mimeResolver = (mimeResolver != null) ? mimeResolver : IOUtil.MIME_RESOLVER;
    }

    public IOListener getIOListener() {
        return ioListener;
    }
    
    public Resource getResource() {
		return resource;
	}
    
    public MimeResolver getMimeResolver() {
        return mimeResolver;
    }

    public boolean hasStream() {
        return hasStream;
    }

    public void informCompleted(boolean success) {
        completed = true;
    }

    public boolean isCompleted() {
        return completed;
    }

    protected void checkCompleted() {
        if (completed) {
            throw new IllegalStateException("ExportContext has already been finalized.");
        }
    }
}
