package com.logicaldoc.webdav.context;

import java.io.OutputStream;

import org.apache.jackrabbit.server.io.IOContext;
import org.apache.jackrabbit.server.io.MimeResolver;

import com.logicaldoc.webdav.resource.model.Resource;

public interface ExportContext extends IOContext {

    /**
     * Returns the item to be exported
     *
     * @return
     */
    public Resource getResource();

    /**
     * Return the output stream to be used for the export or <code>null</code>
     *
     * @return output stream or <code>null</code>
     */
    public OutputStream getOutputStream();

    /**
     * Return the <code>MimeResolver</code> defined for this export context.
     *
     * @return mimetype resolver defined for this export context.
     */
    public MimeResolver getMimeResolver();

    /**
     * Set the content type for the resource content
     *
     * @param mimeType
     * @param encoding
     */
    public void setContentType(String mimeType, String encoding);

    /**
     * Sets the content language.
     *
     * @param contentLanguage
     */
    public void setContentLanguage(String contentLanguage);

    /**
     * Sets the length of the data.
     *
     * @param contentLength the content length
     */
    public void setContentLength(long contentLength);

    /**
     * Sets the creation time of the resource. A successful properties export may
     * set this member.
     *
     * @param creationTime the creation time
     */
    public void setCreationTime(long creationTime);

    /**
     * Sets the modification time of the resource
     *
     * @param modificationTime the modification time
     */
    public void setModificationTime(long modificationTime);

    /**
     * Sets the ETag of the resource. A successfull export command
     * may set this member.
     *
     * @param etag the ETag
     */
    public void setETag(String etag);

    /**
     * Sets an arbitrary property to this export context.
     *
     * @param propertyName
     * @param propertyValue
     */
    public void setProperty(Object propertyName, Object propertyValue);
}
