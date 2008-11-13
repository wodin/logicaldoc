package com.logicaldoc.webdav.context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.jackrabbit.server.io.ExportContext;
import org.apache.jackrabbit.server.io.IOUtil;
import org.apache.jackrabbit.server.io.MimeResolver;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.io.OutputContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.webdav.resource.model.Resource;

public class ExportContextImpl extends AbstractExportContext {

    private static Logger log = LoggerFactory.getLogger(ExportContextImpl.class);

    private final Map properties = new HashMap();
    private final OutputContext outputCtx;

    private File outFile;
    private OutputStream outStream;

    public ExportContextImpl(Resource resource, OutputContext outputCtx) throws IOException {
        this(resource, outputCtx, null);
    }

    public ExportContextImpl(Resource resource, OutputContext outputCtx,
                             MimeResolver mimeResolver) throws IOException {
        super(resource, (outputCtx != null) ? outputCtx.hasStream() : false, null, mimeResolver);
        this.outputCtx = outputCtx;
        if (hasStream()) {
            // we need a tmp file, since the export could fail
            outFile = File.createTempFile("__exportcontext", "tmp");
        }
    }

    /**
     * Returns a new <code>OutputStream</code> to the temporary file or
     * <code>null</code> if this context provides no stream.
     *
     * @see ExportContext#getOutputStream()
     * @see #informCompleted(boolean)
     */
    public OutputStream getOutputStream() {
        checkCompleted();
        if (hasStream()) {
            try {
                // clean up the stream retrieved by the preceeding handler, that
                // did not behave properly and failed to export although initially
                // willing to handle the export.
                if (outStream != null) {
                    outStream.close();
                }
                outStream = new FileOutputStream(outFile);
                return outStream;
            } catch (IOException e) {
                // unexpected error... ignore and return null
            }
        }
        return null;
    }

    /**
     * @see ExportContext#setContentLanguage(String)
     */
    public void setContentLanguage(String contentLanguage) {
        properties.put(DavConstants.HEADER_CONTENT_LANGUAGE, contentLanguage);
    }

    /**
     * @see ExportContext#setContentLength(long)
     */
    public void setContentLength(long contentLength) {
        properties.put(DavConstants.HEADER_CONTENT_LENGTH, contentLength + "");
    }

    /**
     * @see ExportContext#setContentType(String,String)
     */
    public void setContentType(String mimeType, String encoding) {
        properties.put(DavConstants.HEADER_CONTENT_TYPE, IOUtil.buildContentType(mimeType, encoding));
    }

    /**
     * Does nothing since the wrapped output context does not understand
     * creation time
     *
     * @param creationTime
     * @see ExportContext#setCreationTime(long)
     */
    public void setCreationTime(long creationTime) {
        // ignore since output-ctx does not understand creation time
    }

    /**
     * @see ExportContext#setModificationTime(long)
     */
    public void setModificationTime(long modificationTime) {
        if (modificationTime <= IOUtil.UNDEFINED_TIME) {
            modificationTime = new Date().getTime();
        }
        String lastMod = IOUtil.getLastModified(modificationTime);
        properties.put(DavConstants.HEADER_LAST_MODIFIED, lastMod);
    }

    /**
     * @see ExportContext#setETag(String)
     */
    public void setETag(String etag) {
        properties.put(DavConstants.HEADER_ETAG, etag);
    }

    /**
     * @see ExportContext#setProperty(Object, Object) 
     */
    public void setProperty(Object propertyName, Object propertyValue) {
        properties.put(propertyName, propertyValue);
    }

    /**
     * If success is true, the properties set before an the output stream are
     * written to the wrapped <code>OutputContext</code>.
     *
     * @param success
     * @see ExportContext#informCompleted(boolean)
     */
    public void informCompleted(boolean success) {
        checkCompleted();
        completed = true;
        // make sure the outputStream gets closed (and don't assume the handlers
        // took care of this.
        if (outStream != null) {
            try {
                outStream.close();
            } catch (IOException e) {
                // ignore
            }
        }
        if (success) {
            // write properties and data to the output-context
            if (outputCtx != null) {
                boolean hasContentLength = false;
                Iterator it = properties.keySet().iterator();
                while (it.hasNext()) {
                    Object name = it.next();
                    Object value = properties.get(name);
                    if (name != null && value != null) {
                        outputCtx.setProperty(name.toString(), value.toString());
                        // check for content-length
                        hasContentLength = DavConstants.HEADER_CONTENT_LENGTH.equals(name.toString());
                    }
                }

                if (outputCtx.hasStream() && outFile != null) {
                    OutputStream out = outputCtx.getOutputStream();
                    try {
                        // make sure the content-length is set
                        if (!hasContentLength) {
                            outputCtx.setContentLength(outFile.length());
                        }
                        FileInputStream in = new FileInputStream(outFile);
                        IOUtil.spool(in, out);
                    } catch (IOException e) {
                        log.error(e.toString());
                    }
                }
            }
        }
        if (outFile != null) {
            outFile.delete();
        }
    }
}
