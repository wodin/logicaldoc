package com.logicaldoc.benchmark;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.webservice.folder.WSFolder;


public class LoaderListFoldersThread extends AbstractLoaderThread
{
	private static Log log = LogFactory.getLog(LoaderListFoldersThread.class);
	
    private String messageRecord;

	private int totalFolders;

	public LoaderListFoldersThread(LoaderSession session, String loaderName, long testTotal) {
    	super(session, loaderName, testTotal);
    }

    /**
     * Go to a directory and get a listing of the folders beneath it.
     */
    @Override
    protected String doLoading(LoaderServerProxy serverProxy, long rootFolder) throws Exception 
    {
    	totalFolders = 0;
        listFoldersRecursive(serverProxy, rootFolder);        
        
        // Done
        String msg = String.format("Found %d folders below node %s", totalFolders, "Default Workspace");
        this.messageRecord = msg;
        return msg;
    }
    
    @Override
    public String getSummary()
    {
        return super.getSummary() + messageRecord;
    }    

	/**
	 * Recursive method to list all folders in the hierarchy.
	 */
	private void listFoldersRecursive(LoaderServerProxy serverProxy, long parentFolder) {

		WSFolder[] folders = new WSFolder[0];
		try {
			folders = serverProxy.folderClient.listChildren(serverProxy.ticket, parentFolder);
		} catch (Exception e) {
			log.warn("listFoldersRecursive(): ", e);
		}

		if (folders != null) {
			totalFolders +=  folders.length;
			for (WSFolder info : folders) {
				listFoldersRecursive(serverProxy, info.getId());
			}
		}
	}
}
