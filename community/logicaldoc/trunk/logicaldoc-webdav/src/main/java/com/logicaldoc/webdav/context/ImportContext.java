package com.logicaldoc.webdav.context;

import java.io.InputStream;

import org.apache.jackrabbit.server.io.IOContext;
import org.apache.jackrabbit.server.io.IOUtil;
import org.apache.jackrabbit.server.io.MimeResolver;

import com.logicaldoc.webdav.resource.model.Resource;

public interface ImportContext extends IOContext {

	/**
     * Returns the item to be exported
     *
     * @return
     */
    public Resource getResource();
    
    /**
     * Return the <code>MimeResolver</code> defined for this import context.
     *
     * @return mimetype resolver defined for this import context.
     */
    public MimeResolver getMimeResolver();

    /**
     * Returns the system id of the resource to be imported. This id depends on
     * the system the resource is comming from. it can be a filename, a
     * display name of a webdav resource, an URI, etc.
     *
     * @return the system id of the resource to import
     */
    public String getSystemId();

    /**
     * Returns the input stream of the data to import or <code>null</code> if
     * there are none.
     *
     * @return the input stream.
     * @see #hasStream()
     */
    public InputStream getInputStream();

    /**
     * Returns the modification time of the resource or the current time if
     * the modification time has not been set.
     *
     * @return the modification time.
     */
    public long getModificationTime();

    /**
     * Returns the content language or <code>null</code>
     *
     * @return contentLanguage
     */
    public String getContentLanguage();

    /**
     * Returns the length of the data or {@link IOUtil#UNDEFINED_LENGTH -1} if
     * the content length could not be determined.
     *
     * @return the content length
     */
    public long getContentLength();

    /**
     * Returns the main media type. It should be retrieved from a content type
     * (as present in a http request) or from the systemId. If either value
     * is indefined <code>null</code> should be returned.
     *
     * @return the mimetype of the resource to be imported
     */
    public String getMimeType();

    /**
     * Returns the encoding extracted from a content type as present in a
     * request header or <code>null</code>
     *
     * @return the encoding to be used for importing
     */
    public String getEncoding();

    /**
     *
     * @param propertyName
     * @return
     */
    public Object getProperty(Object propertyName);
}
