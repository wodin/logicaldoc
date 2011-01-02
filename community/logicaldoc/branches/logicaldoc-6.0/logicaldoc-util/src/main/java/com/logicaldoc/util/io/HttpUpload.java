package com.logicaldoc.util.io;

import java.io.File;
import java.net.URLEncoder;
import java.util.Date;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpMethodParams;

/**
 * 
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class HttpUpload {

	private String url;

	private File file;

	private String fileName;

	public void UploadFile() {
	}

	public static void main(String[] args) throws Exception {
		File f = new File("C:/email.txt");
		HttpUpload upload = new HttpUpload();
		upload.setFile(f);
		upload.setFileName("test.jpg");
		upload.setURL("http://localhost:9080/logicaldoc/upload?sid=test");
		upload.upload();
	}

	public void upload() throws Exception {

		PostMethod filePost = new PostMethod(url);

		String f = fileName;
		if (f == null)
			f = file.getName();

		String name = "LDOC-" + new Date().getTime();

		filePost.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, false);
		filePost.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf8");
		filePost.getParams().setParameter(HttpMethodParams.HTTP_ELEMENT_CHARSET, "utf8");

		try {
			FilePart filePart = new FilePart(name, URLEncoder.encode(f, "UTF-8"), file);
			filePart.setCharSet("utf8");
			Part[] parts = { filePart };

			filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));

			HttpClient client = new HttpClient();

			client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);

			int status = client.executeMethod(filePost);

			if (status == HttpStatus.SC_OK) {
				System.out.println("Upload complete, response= " + filePost.getResponseBodyAsString());
			} else {
				String message = "Upload failed, response=" + HttpStatus.getStatusText(status);
				System.out.println(message);
				throw new Exception(message);
			}
		} finally {
			filePost.releaseConnection();
		}
	}

	public String getURL() {
		return url;
	}

	public void setURL(String url) {
		this.url = url;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}