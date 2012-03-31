
package com.logicaldoc.bm;

import com.logicaldoc.webservice.auth.AuthClient;
import com.logicaldoc.webservice.document.DocumentClient;
import com.logicaldoc.webservice.folder.FolderClient;
import com.logicaldoc.webservice.search.SearchClient;
import com.logicaldoc.webservice.system.SystemClient;

/**
 * Helper class to store remote service connections.
 */
public class LoaderServerProxy
{
    public final String rmiUrl;
    public final String ticket;
    public AuthClient authClient;    
    public final DocumentClient documentClient;
    public final FolderClient folderClient;    
    public final SystemClient systemClient;
    public final SearchClient searchClient;	    
    
    public LoaderServerProxy(
            String rmiUrl,
            String ticket,
            AuthClient authClient,
            FolderClient folderClient,
            DocumentClient documentClient, 
            SystemClient systemClient,
            SearchClient searchClient)
    {
        this.rmiUrl = rmiUrl;
        this.ticket = ticket;
        this.authClient = authClient;
        this.folderClient = folderClient;
        this.documentClient = documentClient;
        this.systemClient = systemClient;
        this.searchClient = searchClient;
    }
}

