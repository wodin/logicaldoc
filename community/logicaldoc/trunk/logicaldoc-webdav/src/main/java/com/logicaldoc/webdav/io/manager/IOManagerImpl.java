package com.logicaldoc.webdav.io.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.webdav.DavResource;

import com.logicaldoc.webdav.context.ExportContext;
import com.logicaldoc.webdav.context.ImportContext;
import com.logicaldoc.webdav.io.handler.IOHandler;

public class IOManagerImpl implements IOManager {

	protected static Log log = LogFactory.getLog(IOManagerImpl.class);

    private final List<IOHandler> ioHandlers = new ArrayList<IOHandler>();

    /**
     * Create a new <code>IOManager</code>.
     * Note, that this manager does not define any <code>IOHandler</code>s by
     * default. Use {@link #addIOHandler(IOHandler)} in order to populate the
     * internal list of handlers that are called for <code>importContent</code> and
     * <code>exportContent</code>.
     */
    public IOManagerImpl() {
    }

    /**
     * @see IOManager#addIOHandler(IOHandler)
     */
    public void addIOHandler(IOHandler ioHandler) {
        if (ioHandler == null) {
            throw new IllegalArgumentException("'null' is not a valid IOHandler.");
        }
        ioHandler.setIOManager(this);
        ioHandlers.add(ioHandler);
    }

    /**
     * @see IOManager#getIOHandlers()
     */
    public IOHandler[] getIOHandlers() {
        return (IOHandler[]) ioHandlers.toArray(new IOHandler[ioHandlers.size()]);
    }

    /**
     * @see IOManager#importContent(ImportContext, boolean)
     */
    public boolean importContent(ImportContext context, boolean isCollection) throws IOException {
        boolean success = false;
        if (context != null) {
           
            IOHandler[] ioHandlers = getIOHandlers();
            for (int i = 0; i < ioHandlers.length && !success; i++) {
                IOHandler ioh = ioHandlers[i];
                if (ioh.canImport(context, isCollection)) {
                   
                    success = ioh.importContent(context, isCollection);
                  
                }
            }
            context.informCompleted(success);
        }
        return success;
    }

    /**
     * @see IOManager#importContent(ImportContext, DavResource)
     */
    public boolean importContent(ImportContext context, DavResource resource) throws IOException {
        boolean success = false;
        if (context != null && resource != null) {
         
        	IOHandler[] ioHandlers = getIOHandlers();
            for (int i = 0; i < ioHandlers.length && !success; i++) {
                IOHandler ioh = ioHandlers[i];
                if (ioh.canImport(context, resource)) {
                    success = ioh.importContent(context, resource);
                }
            }
            context.informCompleted(success);
        }
        return success;
    }

    /**
     * @see IOManager#exportContent(ExportContext, boolean)
     */
    public boolean exportContent(ExportContext context, boolean isCollection) throws IOException {
        boolean success = false;
        if (context != null) {
            
            IOHandler[] ioHandlers = getIOHandlers();
            for (int i = 0; i < ioHandlers.length && !success; i++) {
                IOHandler ioh = ioHandlers[i];
                if (ioh.canExport(context, isCollection)) {
                   
                    success = ioh.exportContent(context, isCollection);
                   
                }
            }
            context.informCompleted(success);
        }
        return success;
    }

    /**
     * @see IOManager#exportContent(ExportContext, DavResource)
     */
    public boolean exportContent(ExportContext context, DavResource resource) throws IOException {
        boolean success = false;
        if (context != null && resource != null) {
           
            IOHandler[] ioHandlers = getIOHandlers();
            for (int i = 0; i < ioHandlers.length && !success; i++) {
                IOHandler ioh = ioHandlers[i];
                if (ioh.canExport(context, resource)) {
            
                    success = ioh.exportContent(context, resource);
                
                }
            }
            context.informCompleted(success);
        }
        return success;
    }

	@Override
	public void setIOHandler(List<IOHandler> handler) {
		for (IOHandler myIOHandler : handler) {
			myIOHandler.setIOManager(this);
			this.ioHandlers.add(myIOHandler);
		}
		
		
	}

}
