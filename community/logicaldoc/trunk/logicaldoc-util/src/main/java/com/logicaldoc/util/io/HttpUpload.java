package com.logicaldoc.util.io;

import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;

/**
 * 
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class HttpUpload {

	private static final int TIMEOUT = 15000;

	private String url;

	private File file;

	private String fileName;

	private CountingRequestEntity.ProgressListener listener;

	public void UploadFile() {
	}

	public static void main(String[] args) throws Exception {
		File f = new File("C:/tmp/buf.txt");
		HttpUpload upload = new HttpUpload();
		upload.setFile(f);
		upload.setFileName("buf.txt");
		upload.setURL("https://localhost:9443/logicaldoc/servlet.gupld?new_session=true&sid=39d38a5f-4a2f-4738-b8ee-7dd07b793a00");
		upload.setListener(new CountingRequestEntity.ProgressListener() {
			@Override
			public void transferred(long total, long increment) {
				System.out.println("Transfered " + increment);
				System.out.println("Total " + total);
			}
		});
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

			// Prepare the multipart request
			MultipartRequestEntity multipart = new MultipartRequestEntity(parts, filePost.getParams());

			RequestEntity request = multipart;

			if (listener != null) {
				// Wrap with the counter request in order to track the upload
				request = new CountingRequestEntity(multipart, listener);
			}

			filePost.setRequestEntity(request);

			if (url.toLowerCase().startsWith("https")) {
				URL _url = new URL(url);
				int port = _url.getPort();
				if (_url.getPort() <= 0)
					port = 443;

				Protocol.registerProtocol("https",
						new Protocol("https", new EasySSLProtocolSocketFactory(), port));
			}

			HttpClient client = new HttpClient();

			client.getHttpConnectionManager().getParams().setConnectionTimeout(TIMEOUT);

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

	public CountingRequestEntity.ProgressListener getListener() {
		return listener;
	}

	public void setListener(CountingRequestEntity.ProgressListener listener) {
		this.listener = listener;
	}
}