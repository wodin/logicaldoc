
package com.logicaldoc.benchmark;

import com.logicaldoc.webservice.document.DocumentClient;
import com.logicaldoc.webservice.folder.FolderClient;
import com.logicaldoc.webservice.system.SystemClient;


public class LoaderServerProxy
{
    public final String rmiUrl;
    public final String ticket;
    public final DocumentClient documentClient;
    public final FolderClient folderClient;    
    public final SystemClient systemClient;
    
    public LoaderServerProxy(
            String rmiUrl,
            String ticket,
            FolderClient folderClient,
            DocumentClient documentClient, 
            SystemClient systemClient)
    {
        this.rmiUrl = rmiUrl;
        this.ticket = ticket;
        this.folderClient = folderClient;
        this.documentClient = documentClient;
        this.systemClient = systemClient;
    }
}

