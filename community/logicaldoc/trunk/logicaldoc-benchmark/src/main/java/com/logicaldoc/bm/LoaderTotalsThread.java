package com.logicaldoc.bm;

import com.logicaldoc.webservice.WSParameter;

/**
 * A loader thread that merely reports the size of the remote repository.
 * @since 6.5
 */
public class LoaderTotalsThread extends AbstractLoaderThread
{
    public LoaderTotalsThread(LoaderSession session, String loaderName, long testTotal, long testLoadDepth) {
		super(session, loaderName, testTotal, testLoadDepth);
    }

    /**
     * Gets the remote repository sizes and dumps those.
     */
    @Override
    protected String doLoading(LoaderServerProxy serverProxy, long rootFolder) throws Exception {
        return getTotalsMessage();
    }

    @Override
    public String getSummary()
    {
        return super.getSummary() + getTotalsMessage();
    }
    
    private String getTotalsMessage() 
    {
    	int docTotals = 0;
    	int folderTotals = 0;
    	 StringBuilder sb = new StringBuilder();
    	
        LoaderServerProxy serverProxy = session.getRemoteServer();
               
        try {
			// Get Statistics
			WSParameter[] statistics = serverProxy.systemClient.getStatistics(serverProxy.ticket);
			           
			// find the total number of documents
			for (WSParameter wsParameter : statistics) {
				if (wsParameter.getName().equals("docs_notindexed") || wsParameter.getName().equals("docs_indexed") || wsParameter.getName().equals("docs_trash")) {
					docTotals += Integer.parseInt(wsParameter.getValue()); 
				}
			}
			
			// find the total number of folders
			for (WSParameter wsParameter : statistics) {
				if (wsParameter.getName().equals("folder_withdocs") || wsParameter.getName().equals("folder_empty") || wsParameter.getName().equals("folder_trash")) {
					folderTotals += Integer.parseInt(wsParameter.getValue()); 
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        
        sb.append(String.format("Total documents=%d", docTotals));
        sb.append(", ").append(String.format("Total folders=%d", folderTotals));
        
        // Done
        return sb.toString();
    }
}
