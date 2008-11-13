package com.logicaldoc.webdav.io.manager;

import java.io.IOException;
import java.util.List;

import org.apache.jackrabbit.webdav.DavResource;

import com.logicaldoc.webdav.context.ExportContext;
import com.logicaldoc.webdav.context.ImportContext;
import com.logicaldoc.webdav.io.handler.IOHandler;

public interface IOManager {

	/**
	 * 
	 * @param handler
	 */
	public void setIOHandler(List<IOHandler> handler);
	
	
    /**
     * Adds the specified handler to the list of handlers.
     *
     * @param ioHandler to be added
     */
    public void addIOHandler(IOHandler ioHandler);

    /**
     * Returns all handlers that have been added to this manager.
     *
     * @return Array of all handlers
     */
    public IOHandler[] getIOHandlers();

    /**
     * Passes the specified context and boolean value to the IOHandlers present
     * on this manager.
     * As soon as the first handler incidates success the import should be
     * considered completed. If none of the handlers can deal with the given
     * information this method must return false.
     *
     * @param context
     * @param isCollection
     * @return true if any of the handlers import the given context.
     * False otherwise.
     * @throws IOException
     * @see IOHandler#importContent(ImportContext, boolean)
     */
    public boolean importContent(ImportContext context, boolean isCollection) throws IOException;

    /**
     * Passes the specified information to the IOHandlers present on this manager.
     * As soon as the first handler incidates success the import should be
     * considered completed. If none of the handlers can deal with the given
     * information this method must return false.
     *
     * @param context
     * @param resource
     * @return true if any of the handlers import the information present on the
     * specified context.
     * @throws IOException
     * @see IOHandler#importContent(ImportContext, DavResource)
     */
    public boolean importContent(ImportContext context, DavResource resource) throws IOException;

    /**
     * Passes the specified information to the IOHandlers present on this manager.
     * As soon as the first handler incidates success the export should be
     * considered completed. If none of the handlers can deal with the given
     * information this method must return false.
     *
     * @param context
     * @param isCollection
     * @return true if any of the handlers could run the export successfully,
     * false otherwise.
     * @throws IOException
     * @see IOHandler#exportContent(ExportContext, boolean)
     */
    public boolean exportContent(ExportContext context, boolean isCollection) throws IOException;

    /**
     * Passes the specified information to the IOHandlers present on this manager.
     * As soon as the first handler incidates success the export should be
     * considered completed. If none of the handlers can deal with the given
     * information this method must return false.
     *
     * @param context
     * @param resource
     * @return true if any of the handlers could run the export successfully,
     * false otherwise.
     * @throws IOException
     * @see IOHandler#exportContent(ExportContext, DavResource)
     */
    public boolean exportContent(ExportContext context, DavResource resource) throws IOException;
}
