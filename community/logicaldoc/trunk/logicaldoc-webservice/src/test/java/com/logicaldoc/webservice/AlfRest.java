package com.logicaldoc.webservice;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import com.logicaldoc.webservice.model.WSDocument;

public class AlfRest {

	public static String BASE_PATH = "http://localhost:8080/logicaldoc";

	public static void main(String[] args) throws IOException {

		String sid = loginJSON();

		//createFolderSimple(sid);
		//createFolderSimpleJSON(sid);

		createDocument( sid);
		// listChildrean(sid, 04L);
	}

	private static void listChildrean(String sid, long parentId) throws ClientProtocolException, IOException {

		System.out.println("sid: " + sid);

		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("sid", sid));
		params.add(new BasicNameValuePair("folderId", String.valueOf(parentId)));

		StringBuilder requestUrl = new StringBuilder(BASE_PATH + "/services/rest/folder/listChildren");
		String querystring = URLEncodedUtils.format(params, "utf-8");
		requestUrl.append("?");
		requestUrl.append(querystring);

		System.out.println(requestUrl);

		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet method = new HttpGet(requestUrl.toString());
		method.setHeader("Accept", "application/json");

		CloseableHttpResponse response1 = httpclient.execute(method);

		try {
			System.out.println(response1.getStatusLine());
			HttpEntity entity2 = response1.getEntity();

			String respoBody = EntityUtils.toString(entity2, "UTF-8");
			System.out.println(respoBody);

			// do something useful with the response body
			// and ensure it is fully consumed
			EntityUtils.consume(entity2);
		} finally {
			response1.close();
		}
	}

	private static void createDocument(String sid) throws IOException {

		CloseableHttpClient httpclient = HttpClients.createDefault();
		
        HttpPost httppost = new HttpPost(BASE_PATH + "/services/rest/document/create");

		File f = new File("C:/tmp/InvoiceProcessing01-workflow.png");
		System.out.println(f.getName());
		
		WSDocument wsDoc = new WSDocument();
		wsDoc.setId(0);
		wsDoc.setTitle("document test");
		wsDoc.setCustomId("CustomId-xxxxx4444");
		wsDoc.setTags(new String[] { "Invoice", "Processing", "workflow" });
		wsDoc.setFolderId(4L);
		wsDoc.setFileName(f.getName());

		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String jsonStr = ow.writeValueAsString(wsDoc);		
        
		StringBody sidPart = new StringBody(sid, ContentType.TEXT_PLAIN);
		StringBody jsonPart = new StringBody(jsonStr, ContentType.APPLICATION_JSON);
        FileBody binPart = new FileBody(f);        

        HttpEntity reqEntity = MultipartEntityBuilder.create()
        		.addPart("sid", sidPart)
                .addPart("document", jsonPart)
                .addPart("content", binPart)
                .build();
		
		httppost.setEntity(reqEntity);
		

		CloseableHttpResponse response = httpclient.execute(httppost);
		try {
			HttpEntity rent = response.getEntity();
			if (rent != null) {
				String respoBody = EntityUtils.toString(rent, "UTF-8");
				System.out.println(respoBody);
			}
		} finally {
			response.close();
		}
	}

	private static void createFolderSimple(String sid) throws UnsupportedEncodingException, IOException {

		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("sid", sid));
		formparams.add(new BasicNameValuePair("folderPath", "/LogicalDOC/USA/NJ/Fair Lawn/createSimple"));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
		HttpPost httppost = new HttpPost(BASE_PATH + "/services/rest/folder/createSimple");
		httppost.setEntity(entity);		

		CloseableHttpResponse response = httpclient.execute(httppost);
		try {
			HttpEntity rent = response.getEntity();
			if (rent != null) {
				String respoBody = EntityUtils.toString(rent, "UTF-8");
				System.out.println(respoBody);
			}
		} finally {
			response.close();
		}
	}

	private static void createFolderSimpleJSON(String sid)
			throws UnsupportedEncodingException, IOException {

		CloseableHttpClient httpclient = HttpClients.createDefault();

		String folderPath = "/LogicalDOC/USA/NJ/Fair Lawn/createSimple";
		String input = "{ \"sid\" : \"" + sid + "\", \"folderPath\" : \"" + folderPath + "\" }";
		System.out.println(input);

		HttpPost httppost = new HttpPost(BASE_PATH + "/services/rest/folder/createSimple");
		StringEntity entity = new StringEntity(input, ContentType.create("application/json", Consts.UTF_8));
		httppost.setEntity(entity);

		CloseableHttpResponse response = httpclient.execute(httppost);
		try {
			HttpEntity rent = response.getEntity();
			if (rent != null) {
				String respoBody = EntityUtils.toString(rent, "UTF-8");
				System.out.println(respoBody);
			}
		} finally {
			response.close();
		}
	}

	private static String loginJSON() throws UnsupportedEncodingException, IOException {

		CloseableHttpClient httpclient = HttpClients.createDefault();

		String input = "{ \"username\" : \"admin\", \"password\" : \"admin\" }";
		System.out.println(input);

		StringEntity entity = new StringEntity(input, ContentType.create("application/json", Consts.UTF_8));
		HttpPost httppost = new HttpPost(BASE_PATH + "/services/rest/auth/login");
		httppost.setEntity(entity);

		CloseableHttpResponse response = httpclient.execute(httppost);
		try {
			HttpEntity rent = response.getEntity();
			if (rent != null) {
				String respoBody = EntityUtils.toString(rent, "UTF-8");
				System.out.println(respoBody);
				return respoBody;
			}
		} finally {
			response.close();
		}

		return null;
	}

}
