package com.logicaldoc.webdav.io.handler;

import java.io.IOException;

import org.apache.jackrabbit.webdav.DavResource;

import com.logicaldoc.webdav.context.ExportContext;
import com.logicaldoc.webdav.context.ImportContext;
import com.logicaldoc.webdav.io.manager.IOManager;

public interface IOHandler {

    /**
     * Returns the <code>IOManager</code> that called this handler or <code>null</code>.
     *
     * @return
     */
    public IOManager getIOManager();

    /**
     * Sets the <code>IOManager</code> that called this handler.
     */
    public void setIOManager(IOManager ioManager);

    /**
     * Returns a human readable name for this <code>IOHandler</code>
     *
     * @return
     */
    public String getName();

    /**
     * Returns true, if this handler can run a successful import based on the
     * specified context.
     *
     * @param context
     * @param isCollection
     * @return
     */
    public boolean canImport(ImportContext context, boolean isCollection);

    /**
     * Returns true, if this handler can run a successful import based on
     * the specified context and resource. A simple implementation may choose
     * to return the same as {@link IOHandler#canImport(ImportContext, boolean)}
     * where the isCollection flag is determined by
     * {@link DavResource#isCollection()}.
     *
     * @param context
     * @param resource
     * @return
     */
    public boolean canImport(ImportContext context, DavResource resource);

    /**
     * Runs the import for the given context and indicates by a boolean return
     * value, if the import could be completed successfully. If the specified
     * <code>ImportContext</code> does not provide a {@link ImportContext#hasStream() stream}
     * the implementation is free, to only import properties of to refuse the
     * import.<br>
     *
     * Please note, that it is the responsibility of the specified
     * <code>ImportContext</code> to assert, that its stream is not consumed
     * multiple times when being passed to a chain of <code>IOHandler</code>s.
     *
     * @param context
     * @param isCollection
     * @return true if the import was successful.
     * @throws IOException if an unexpected error occurs or if this method has
     * been called although {@link IOHandler#canImport(ImportContext, boolean)}
     * returns false.
     */
    public boolean importContent(ImportContext context, boolean isCollection) throws IOException;

    /**
     * Runs the import for the given context and resource. It indicates by a boolean return
     * value, if the import could be completed successfully. If the specified
     * <code>ImportContext</code> does not provide a {@link ImportContext#hasStream() stream}
     * the implementation is free, to only import properties of to refuse the
     * import. A simple implementation may return the same as
     * {@link IOHandler#importContent(ImportContext, boolean)} where the
     * isCollection flag is determined by {@link DavResource#isCollection()}<br>
     *
     * Please note, that it is the responsibility of the specified
     * <code>ImportContext</code> to assert, that its stream is not consumed
     * multiple times when being passed to a chain of <code>IOHandler</code>s.
     *
     * @param context
     * @param resource
     * @return
     * @throws IOException if an unexpected error occurs or if this method has
     * been called although {@link IOHandler#canImport(ImportContext, DavResource)}
     * returns false.
     * @see IOHandler#importContent(ImportContext, boolean)
     */
    public boolean importContent(ImportContext context, DavResource resource) throws IOException;

    /**
     * Returns true, if this handler can run a successful export based on the
     * specified context.
     *
     * @param context
     * @param isCollection
     * @return
     */
    public boolean canExport(ExportContext context, boolean isCollection);

    /**
     * Returns true, if this handler can run a successful export based on
     * the specified context and resource. A simple implementation may choose
     * to return the same as {@link IOHandler#canExport(ExportContext, boolean)}
     * where the isCollection flag is determined by
     * {@link DavResource#isCollection()}.
     *
     * @param context
     * @param resource
     * @return
     */
    public boolean canExport(ExportContext context, DavResource resource);

    /**
     * Runs the export for the given context. It indicates by a boolean return
     * value, if the export could be completed successfully. If the specified
     * <code>ExportContext</code> does not provide a {@link ExportContext#hasStream() stream}
     * the implementation should set the properties only and ignore the content to
     * be exported. A simple implementation may return the same as
     * {@link IOHandler#exportContent(ExportContext, boolean)} where the
     * isCollection flag is determined by {@link DavResource#isCollection()}<br>
     *
     * Please note, that it is the responsibility of the specified
     * <code>ExportContext</code> to assert, that its stream is not written
     * multiple times when being passed to a chain of <code>IOHandler</code>s.
     *
     * @param context
     * @param isCollection
     * @return
     * @throws IOException if an unexpected error occurs or if this method has
     * been called although {@link IOHandler#canExport(ExportContext, boolean)}
     * returns false.
     */
    public boolean exportContent(ExportContext context, boolean isCollection) throws IOException;

    /**
     * Runs the export for the given context and resource. It indicates by a boolean return
     * value, if the export could be completed successfully. If the specified
     * <code>ExportContext</code> does not provide a {@link ExportContext#hasStream() stream}
     * the implementation should set the properties only and ignore the content to
     * be exported. A simple implementation may return the same as
     * {@link IOHandler#exportContent(ExportContext, boolean)} where the
     * isCollection flag is determined by {@link DavResource#isCollection()}<br>
     *
     * Please note, that it is the responsibility of the specified
     * <code>ExportContext</code> to assert, that its stream is not written
     * multiple times when being passed to a chain of <code>IOHandler</code>s.
     *
     * @param context
     * @param resource
     * @return
     * @throws IOException if an unexpected error occurs or if this method has
     * been called although {@link IOHandler#canExport(ExportContext, DavResource)}
     * returns false.
     */
    public boolean exportContent(ExportContext context, DavResource resource) throws IOException;
}