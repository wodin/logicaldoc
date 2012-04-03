package com.logicaldoc.bm;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.webservice.document.WSDocument;

/**
 * Loader thread that puts documents to the remote repository.
 * 
 * @author Alessandro Gasparini
 * @since 6.5
 */
public class LoaderUploadThread extends AbstractLoaderThread {

	private static Log log = LogFactory.getLog(LoaderUploadThread.class);
	
	public LoaderUploadThread(LoaderSession session, String loaderName, long testTotal, long testLoadDepth) {
		super(session, loaderName, testTotal, testLoadDepth);
	}

	@Override
	protected String doLoading(LoaderServerProxy serverProxy, long rootFolder) throws Exception {
        
    	// Get a random folder
        List<String> folderPath = super.chooseFolderPath();
     
        // Make sure the folder exists        
        Long folderID = makeFolders(serverProxy.ticket, serverProxy, rootFolder, folderPath);
		
		File file = super.getFile();
		String title = formatter.format(testCount);

		Long docId = createDocument(serverProxy.ticket, serverProxy, folderID, title, file);
		if (docId == null) {
			throw new Exception("Error creating document: " + file.getName());
		}        
		
        // Done
//        String msg = String.format("Uploaded 1 files to folder: %s", folderPath.toString());
//        return msg;
		return null;
	}

	private Long createDocument(String ticket, LoaderServerProxy serverProxy, long folderId, String title, File file) {
		
		String fileName = file.getName();

		WSDocument doc = new WSDocument();
		doc.setFolderId(folderId);
		doc.setTitle(title);
		doc.setFileName(fileName);
		doc.setLanguage(session.getLanguage());
		try {	
			doc = serverProxy.documentClient.create(ticket, doc, file);
			if (doc != null)
				log.debug("Created document " + fileName);
		} catch (Throwable ex) {
			log.error(ex.getMessage(), ex);
		}
		if (doc == null)
			return null;
		else
			return doc.getId();
	}	

}
