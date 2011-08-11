package com.logicaldoc.gui.common.server;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;

/**
 * This is an example of how to use UploadAction class.
 * 
 * This servlet saves all received files in a temporary folder, and deletes them
 * when the user sends a remove request.
 * 
 * @author Manolo Carrasco Moñino
 * 
 */
public class MockUploadServlet extends UploadAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Maintain a list with received files and their content types
	 */
	Hashtable<String, File> receivedFiles = new Hashtable<String, File>();

	Hashtable<String, String> receivedContentTypes = new Hashtable<String, String>();

	public MockUploadServlet() {
		super.maxSize = 123452423425342300L;
	}

	/**
	 * Override executeAction to save the received files in a custom place and
	 * delete this items from session.
	 */
	@Override
	public String executeAction(HttpServletRequest request, List<FileItem> sessionFiles) throws UploadActionException {
		System.out.println("** servlet session:" + request.getSession().getId());

		String path = getServletContext().getRealPath("/upload/" + request.getSession().getId());
		File uploadFolder = new File(path);

		// Google App Engine doesn't support disk writing
		// uploadFolder.mkdirs();
		// uploadFolder.mkdir();

		for (FileItem item : sessionFiles) {
			if (false == item.isFormField()) {
				OutputStream os = null;
				try {
					File file = new File("xxx.bin");
					// Google App Engine doesn't support disk writing
					// file=File.createTempFile("upload-", ".bin",
					// uploadFolder);
					// os = new FileOutputStream(file);
					// copyFromInputStreamToOutputStream(item.getInputStream(),
					// os);

					receivedFiles.put(item.getFieldName(), file);
					receivedContentTypes.put(item.getFieldName(), item.getContentType());

					System.out.println("** received file " + item.getFieldName());
				} catch (Throwable e) {
					e.printStackTrace();
					throw new UploadActionException(e.getMessage());
				} finally {
					if (os != null) {
						try {
							os.flush();
							os.close();
						} catch (IOException e) {
							log(e.getMessage());
						}

					}
				}
			}
			removeSessionFileItems(request);
		}
		return null;
	}

	/**
	 * Remove a file when the user sends a delete request
	 */
	@Override
	public void removeItem(HttpServletRequest request, String fieldName) throws UploadActionException {
		File file = receivedFiles.get(fieldName);
		receivedFiles.remove(fieldName);
		receivedContentTypes.remove(fieldName);
		if (file != null && file.exists())
			file.delete();
	}

	/**
	 * Get the content of an uploaded file
	 */
	@Override
	public void getUploadedFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
		super.getUploadedFile(request, response);
		String fieldName = request.getParameter(PARAM_SHOW);
		File f = receivedFiles.get(fieldName);
		if (f != null) {
			response.setContentType(receivedContentTypes.get(fieldName));
			FileInputStream is = new FileInputStream(f);
			copyFromInputStreamToOutputStream(is, response.getOutputStream());
		} else {
			renderXmlResponse(request, response, ERROR_ITEM_NOT_FOUND);
		}
	}
}